package com.codecamp.tripcplaner.model.data
import com.codecamp.tripcplaner.model.data.TripRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class TripRepositoryImplementation @Inject constructor(private val tripDao: TripDao) : TripRepository {

    override fun getAllItems() = tripDao.getAll()

    override suspend fun insertAll(trips: MutableList<Trip>) {
        withContext(Dispatchers.IO)
        {
            tripDao.insertAll(trips)
        }
    }

    override suspend fun insertTrip(trip: Trip) {
        withContext(Dispatchers.IO)
        {
            tripDao.insertTrip(trip)
        }
    }

    override fun deleteAll() {
        tripDao.deleteAll()
    }

    override fun deleteById(id: Int) {
        tripDao.deleteById(id)
    }

    override fun deleteByTitle(title: String) {
        tripDao.deleteByTitle(title)
    }

    override fun getByTitle(title: String): Trip {
        return tripDao.getByTitle(title)
    }

    override fun getByStartDate(startDate: String): Trip {
        return tripDao.getByStartDate(startDate)
    }

    override fun getByEndDate(endDate: String): Trip {
        return tripDao.getByEndDate(endDate)
    }

    override fun getById(id: Int): Trip {
        return tripDao.getById(id)
    }

    override fun getCount(): Int {
        return tripDao.getCount()
    }

    override fun populateTrips(_trips: StateFlow<List<Trip>>): MutableList<Trip> {
        val trips = mutableListOf<Trip>()
        trips.addAll(_trips.value)
        return trips
    }

    override suspend fun saveData(
        title: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        cities: MutableList<String>,
        packingList: MutableList<String>,
        latLngList: MutableList<LatLng>,
        activities: MutableList<String>,
        dates: MutableList<LocalDateTime>,
        transportType: String
    ) {
        val citiesMap: MutableMap<String, Pair<LatLng, LocalDateTime>> = mutableMapOf()
        val checkedPackingList: MutableMap<String, Boolean> = mutableMapOf()
        packingList.forEach {
            checkedPackingList[it] = false
        }
        for (i in cities.indices) {
            citiesMap[cities[i]] = Pair(latLngList[i], dates[i])
        }
        getById(getCount() + 100)
        val trip = Trip(
            title = title,
            startDate = startDate,
            endDate = endDate,
            packingList = checkedPackingList,
            cities = citiesMap,
            activities = activities,
            id = getCount() + 100,
            transportType = transportType
        )
        withContext(Dispatchers.IO){

            insertTrip(trip)
        }
    }
}