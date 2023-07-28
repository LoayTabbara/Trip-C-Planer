package com.codecamp.tripcplaner.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codecamp.tripcplaner.MAPS_API_KEY
import com.codecamp.tripcplaner.model.data.Message
import com.codecamp.tripcplaner.model.data.Trip
import com.codecamp.tripcplaner.model.data.TripRepository
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import javax.inject.Inject

data class ItineraryInfo(
    @Json(name = "Packing List") val packingList: List<String>,
    @Json(name = "Itinerary") val itinerary: Map<String, List<String>>
)

@HiltViewModel
class TravelInfoViewModel @Inject constructor(

    private val tripRepository: TripRepository
) : ViewModel() {

    var dates: List<LocalDateTime> = listOf()
    private var messages = mutableStateListOf<Message>()
    private var packingListJson = mutableStateOf(listOf<String>())
    private var activitiesJson = mutableStateOf(mapOf<String, List<String>>())
    var citiesWithActivity: Map<String, List<String>> = mutableMapOf()
    var packingList: MutableList<String> = mutableListOf("")
    var hasResult = mutableStateOf(false)
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    private val trips: StateFlow<List<Trip>> = _trips
    var savedTrips = mutableStateListOf<Trip>()
    var tripRepo = tripRepository
    var latLngList = mutableListOf<LatLng>()
    fun sendMessage(
        startEnd: List<String>, duration: Int, context: Context, season: String
    ) {
        val startCity = startEnd.first()
        val endCity = startEnd.last()
        val packingMessageContent = """
Generate a JSON response with: 10 travel items; itinerary from $startCity to $endCity with imagined intermediate stops for $duration days in the $season; 2 activities per city. Follow this format:
{
  "Packing List": ["item1", "item2", ...],
  "Itinerary": {
    "$startCity": ["activity1", "activity2"],
    "city2": ["activity1", "activity2"],
    ...
    "$endCity": ["activity1", "activity2"]
  }
}
""".trimIndent()
        messages.add(Message(packingMessageContent, "user"))

        viewModelScope.launch {
            _trips.emit(tripRepo.getAllItems())
            savedTrips = tripRepo.populateTrips(_trips) as SnapshotStateList<Trip>
            val packingBody = OpenAIRequestBody(messages = messages)

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
                citiesWithActivity = activitiesJson.value
                packingList = packingListJson.value as MutableList<String>
                if (citiesWithActivity.keys.contains("City A") || citiesWithActivity.keys.contains("CityA") || citiesWithActivity.keys.contains(
                        "City1"
                    ) || citiesWithActivity.keys.contains("City 1") || citiesWithActivity.keys.contains(
                        "Stopover 1"
                    )
                ) {
                    Toast.makeText(context, "Faulty result, retrying", Toast.LENGTH_LONG).show()
                    citiesWithActivity = mapOf()
                    sendMessage(
                        startEnd, duration, context, season
                    )
                } else {
                    hasResult.value = true
                }

            } catch (e: SocketTimeoutException) {
                Toast.makeText(context, "Connection timeout error, retrying", Toast.LENGTH_LONG)
                    .show()
                Log.e("Error", e.message.toString())
                citiesWithActivity = mapOf()
                sendMessage(
                    startEnd, duration, context, season
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Unknown Error!, retrying", Toast.LENGTH_LONG)
                    .show()
                Log.e("Error", e.message.toString())
                citiesWithActivity = mapOf()
                sendMessage(
                    startEnd, duration, context, season
                )
            }
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
            return LatLng(0.0, 0.0)
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
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        activities: MutableList<String>,
        transportType: String
    ) {
        viewModelScope.launch {
            tripRepo.saveData(
                title = title,
                startDate = startDate!!,
                endDate = endDate!!,
                cities = citiesWithActivity.keys as List<String>,
                packingList = packingList,
                latLngList = latLngList,
                activities = activities,
                dates = dates,
                transportType = transportType,
            )
        }
    }
}



