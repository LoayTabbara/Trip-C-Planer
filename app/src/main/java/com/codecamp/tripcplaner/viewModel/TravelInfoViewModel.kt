package com.codecamp.tripcplaner.viewModel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.codecamp.tripcplaner.MAPS_API_KEY
import com.codecamp.tripcplaner.model.data.Message
import com.codecamp.tripcplaner.model.data.Trip
import com.codecamp.tripcplaner.model.data.TripRepositoryImplementation
import com.codecamp.tripcplaner.model.navigation.TripCPlanerScreens
import com.codecamp.tripcplaner.model.remote.LatLngService
import com.codecamp.tripcplaner.model.remote.OpenAIRequestBody
import com.codecamp.tripcplaner.model.remote.RetrofitInit
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.time.LocalDateTime
import javax.inject.Inject

data class CityInfo(
    @Json(name = "Activities") val activities: List<String>,
    @Json(name = "Arrival Time") val arrivalTime: String
)

data class ItineraryInfo(
    @Json(name = "Packing List") val packingList: List<String>,
    @Json(name = "Itinerary") val itinerary: Map<String, CityInfo>

)

@HiltViewModel
class TravelInfoViewModel @Inject constructor(

    tripRepository: TripRepositoryImplementation
) : ViewModel() {
    private var localDateTimeList = mutableListOf<LocalDateTime>()
    private var meansOfTransport = ""
    var savedTrips = mutableListOf<Trip>()
    var latLngList = mutableListOf<LatLng>()
    var tripRepo = tripRepository
    var hasResult = mutableStateOf(false)

    var startDate: LocalDateTime = LocalDateTime.now()
    var endDate: LocalDateTime = LocalDateTime.now()
    private var messages = mutableStateListOf<Message>()
    private var packingListJson = mutableStateOf(listOf<String>())
    private var activitiesJson = mutableStateOf(mapOf<String, CityInfo>())
    var citiesWithActivity: Map<String, List<String>> = mapOf()
    private var arrivalTimesInCities = mutableStateOf<Map<String, String>>(mapOf())
    var times = mutableStateOf<List<String>>(listOf()) // added this line
    var packingList: MutableList<String> = mutableListOf()
    var generatePseudo = false


    fun sendMessage(
        startEndCities: List<String>, duration: Int, context: Context, season: String
    ) {
        generatePseudo = false
        val startCity = startEndCities.first()
        val endCity = startEndCities.last()
        val packingMessageContent = """
            Generate a JSON response with: 10 travel items based on $season; itinerary from $startCity to $endCity with imagined intermediate stops for $duration days; 2 activities per city; a proposed arrival time in each city. The start date is $startDate. The transportation type is $meansOfTransport. Follow this format:
            {
              "Packing List": ["item1", "item2", ...],
              "Itinerary": {
                "$startCity": {
                    "Activities": ["activity1", "activity2"],
                    "Arrival Time": "$startDate"
                },
                "city2": {
                    "Activities": ["activity1", "activity2"],
                    "Arrival Time": "${startDate.plusDays(1)}"
                },
                ...
                "$endCity": {
                    "Activities": ["activity1", "activity2"],
                    "Arrival Time": "${startDate.plusDays(1).plusHours(7)}"
                }
              }
            }
            """.trimIndent()
        messages.add(Message(packingMessageContent, "user"))


        val packingBody = OpenAIRequestBody(messages = messages)
        viewModelScope.launch {
            try {
                val packingResponse = RetrofitInit.openAIChatApi.generateResponse(packingBody)
                messages.add(packingResponse.choices.first().message)

                // Parse JSON response
                val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                val jsonAdapter = moshi.adapter(ItineraryInfo::class.java)
                val itineraryInfo =
                    jsonAdapter.fromJson(packingResponse.choices.first().message.content)

                // Save the parsed information to the state variables
                packingListJson.value = itineraryInfo?.packingList ?: listOf()
                activitiesJson.value = itineraryInfo?.itinerary ?: mapOf()

                // Copy the state to the publicly accessible variables
                packingList.addAll(packingListJson.value)
                if (citiesWithActivity.keys.contains("City A") || citiesWithActivity.keys.contains("CityA") || citiesWithActivity.keys.contains(
                        "City1"
                    ) || citiesWithActivity.keys.contains("City 1") || citiesWithActivity.keys.contains(
                        "Stopover 1"
                    )
                ) {
                    Toast.makeText(
                        context,
                        "Faulty result, generating standard answer",
                        Toast.LENGTH_LONG
                    ).show()
                    generatePseudo = true
                } else {

                    citiesWithActivity =
                        activitiesJson.value.mapValues { entry -> entry.value.activities }
                    arrivalTimesInCities.value =
                        activitiesJson.value.mapValues { entry -> entry.value.arrivalTime }
                    packingList = packingListJson.value.toMutableList()
                    times.value = arrivalTimesInCities.value.values.toList()
                }
            } catch (e: SocketTimeoutException) {
                Toast.makeText(
                    context,
                    "Connection timeout error, generating standard answer",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.e("Error", e.message.toString())
                generatePseudo = true

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Unknown Error! ${e.message}, generating standard answer",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.e("Error", e.message.toString())
                citiesWithActivity = mapOf()

                generatePseudo = true

            }
            if (generatePseudo) {
                packingList = mutableListOf(
                    "Passport",
                    "Clothes",
                    "Toiletries",
                    "Camera",
                    "Phone charger",
                    "Snacks",
                    "Water bottle",
                    "Maps",
                    "Travel guidebook",
                    "Sunglasses"
                )
                citiesWithActivity = mapOf(
                    "Kassel" to listOf(
                        "Visit Bergpark Wilhelmshöhe",
                        "Explore Museum Fridericianum"
                    ),
                    "Paderborn" to listOf("Visit Paderborn Cathedral", "Explore Pader Springs"),
                    "Münster" to listOf(
                        "Visit Münster Cathedral",
                        "Explore Münster Botanical Garden"
                    ),
                    "Dortmund" to listOf("Visit Dortmund U-Tower", "Explore Westfalenpark"),
                )
                latLngList = mutableListOf(
                    LatLng(51.3128, 9.4815),
                    LatLng(51.71, 8.766),
                    LatLng(51.9615, 7.6282),
                    LatLng(51.5142, 7.4684)
                )
                times = mutableStateOf(
                    listOf(
                        startDate.toString(),
                        startDate.plusDays(1).toString(),
                        startDate.plusDays(2).toString(),
                        startDate.plusDays(3).toString(),
                        startDate.plusDays(5).toString()
                    )
                )
            }
            hasResult.value = true
        }
    }

    suspend fun getLatLng(locationName: String): LatLng {
        val serviceLatLng: LatLngService =
            Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(LatLngService::class.java)
        val response = serviceLatLng.generateResponse(MAPS_API_KEY, locationName)
        val results = response.body()?.get("results") as JsonArray
        if (results.size() == 0) {
            return LatLng(52.3128, 9.5815)
        }
        val geometry = results[0].asJsonObject.get("geometry") as JsonObject
        val location = geometry.get("location") as JsonObject
        return LatLng(location.get("lat").asDouble, location.get("lng").asDouble)

    }

    fun addToPackingList(item: String) {
        packingList.add(item)
    }

    fun removeFromPackingList(item: String) {
        packingList.remove(item)
    }

    fun sendDateToSave(
        title: String,
        activities: MutableList<String>,
        transportType: String,
    ) {

        times.value.forEach {
            localDateTimeList.add(LocalDateTime.parse(it))
        }
        viewModelScope.launch {
            tripRepo.saveData(
                title = title,
                startDate = LocalDateTime.parse(times.value.first()),
                endDate = endDate,
                cities = citiesWithActivity.keys.toMutableList(),
                packingList = packingList,
                latLngList = latLngList,
                activities = activities,
                dates = localDateTimeList,
                transportType = transportType,
            )
        }
    }

    fun generateBodyForSharing(item: Trip): JSONObject {
        val packingListJsonArray = JSONArray()
        item.packingList.keys.forEach { packingListObject ->
            packingListJsonArray.put(packingListObject)
        }
        val latLngJsonArray = JSONArray()
        val timesJsonArray = JSONArray()
        val citiesJsonArray = JSONArray()
        val activitiesJsonArray = JSONArray()
        item.cities.values.forEach { pair ->
            latLngJsonArray.put(pair.first.latitude.toString() + "," + pair.first.longitude.toString())
            timesJsonArray.put(pair.second.toString())
        }
        item.cities.keys.forEach { city ->
            citiesJsonArray.put(city)
        }
        item.activities.forEach { activity ->
            activitiesJsonArray.put(activity)
        }
        val bodyForPostRequestForSharing = JSONObject().apply {
            put("message from developers", "You do not have our app 'TripCPlaner' installed. Please install it to make use ot the shared trip.")
            put("packingList", packingListJsonArray)
            put("latLng", latLngJsonArray)
            put("times", timesJsonArray)
            put("cities", citiesJsonArray)
            put("activities", activitiesJsonArray)
            put("transport", item.transportType)
            put("end_date", item.endDate.toString())
        }
        return bodyForPostRequestForSharing
    }

    fun shareTrip(item: Trip, context: Context) {
        val body = generateBodyForSharing(item)
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL("https://extendsclass.com/api/json-storage/bin")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.doOutput = true

            connection.outputStream.use { os ->
                val input = body.toString()
                val bytes = input.toByteArray(Charsets.UTF_8)
                os.write(bytes, 0, bytes.size)
            }

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                connection.inputStream.use { stream ->
                    val reader = stream.reader()
                    val response = reader.readText()
                    val jsonResponse = JSONObject(response)
                    val id = jsonResponse.getString("id")

                    withContext(Dispatchers.Main) {

                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Shared Trip ID", id)
                        clipboard.setPrimaryClip(clip)


                        Toast.makeText(
                            context,
                            "copied trip id: $id to clipboard",
                            Toast.LENGTH_LONG
                        ).show()


                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Use the code \n\n$id\n\ninside the TripCPlaner App to see my awesome trip or simply click the link: https://pink-trudy-95.tiiny.site/?id=$id"
                            )
                            type = "text/plain"
                        }

                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                }
            } else {
                val errorStream = connection.errorStream
                val errorMessage = errorStream?.reader()?.readText() ?: "Unknown error"
                Toast.makeText(context, "We couldn't generate share link", Toast.LENGTH_LONG)
            }
        }
    }

    fun fetchTrip(
        sharedCode: String,
        detailsViewModel: DetailViewModel,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = URL("https://extendsclass.com/api/json-storage/bin/$sharedCode")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/json")

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { stream ->
                    val reader = stream.reader()
                    val response = reader.readText()
                    val jsonResponse = JSONObject(response)


                    val packingListJsonArray = jsonResponse.getJSONArray("packingList")
                    val latLngJsonArray = jsonResponse.getJSONArray("latLng")
                    val timesJsonArray = jsonResponse.getJSONArray("times")
                    val citiesJsonArray = jsonResponse.getJSONArray("cities")
                    val activitiesJsonArray = jsonResponse.getJSONArray("activities")


                    val latLng = List(latLngJsonArray.length()) { latLngJsonArray.getString(it) }
                    val cities = List(citiesJsonArray.length()) { citiesJsonArray.getString(it) }
                    val activities =
                        List(activitiesJsonArray.length()) { activitiesJsonArray.getString(it) }
                    val timesArray = List(timesJsonArray.length()) { timesJsonArray.getString(it) }
                    val tempCitiesWithActivityMap = mutableMapOf<String, List<String>>()
                    for (i in cities.indices) {
                        tempCitiesWithActivityMap[cities[i]] =
                            listOf(activities[2 * i], activities[2 * i + 1])
                    }
                    meansOfTransport = jsonResponse.getString("transport")
                    val endDateString = jsonResponse.getString("end_date")
                    citiesWithActivity = tempCitiesWithActivityMap.toMap()
                    packingList = MutableList(packingListJsonArray.length()) {
                        packingListJsonArray.getString(it)
                    }
                    latLngList = mutableListOf()
                    latLng.forEach {
                        val latLngSplit = it.split(",")
                        latLngList.add(LatLng(latLngSplit[0].toDouble(), latLngSplit[1].toDouble()))
                    }
                    times = mutableStateOf(timesArray)
                    startDate = LocalDateTime.parse(times.value.first())
                    endDate = LocalDateTime.parse(endDateString)
                    detailsViewModel.setDates(startDate, endDate)
                    detailsViewModel.setMeansOfTransport(meansOfTransport)
                    withContext(Dispatchers.Main) {

                        navController.navigate(TripCPlanerScreens.PackScreen.name)
                    }


                }
            } else {
                val errorStream = connection.errorStream
                val errorMessage = errorStream?.reader()?.readText() ?: "Unknown error"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "A trip with  id: $sharedCode  wasn't found ", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

}