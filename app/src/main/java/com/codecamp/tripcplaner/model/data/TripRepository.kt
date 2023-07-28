package com.codecamp.tripcplaner.model.data

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class TripRepository @Inject constructor(private val tripDao: TripDao) {
    fun getAllItems() = tripDao.getAll()

    suspend fun insertAll(trips: List<Trip>) {
        withContext(Dispatchers.IO)
        {
            tripDao.insertAll(trips)
        }
    }

    suspend fun insertTrip(trip: Trip) {
        withContext(Dispatchers.IO)
        {
            tripDao.insertTrip(trip)
        }
    }

    fun deleteAll() {
        tripDao.deleteAll()
    }

    fun deleteById(id: Int) {
        tripDao.deleteById(id)
    }

    fun deleteByTitle(title: String) {
        tripDao.deleteByTitle(title)
    }

    fun getByTitle(title: String): Trip {
        return tripDao.getByTitle(title)
    }

    fun getByStartDate(startDate: String): Trip {
        return tripDao.getByStartDate(startDate)
    }

    fun getByEndDate(endDate: String): Trip {
        return tripDao.getByEndDate(endDate)
    }

    fun getById(id: Int): Trip {
        return tripDao.getById(id)
    }

    fun getCount(): Int {
        return tripDao.getCount()
    }

    fun populateTrips(_trips: StateFlow<List<Trip>>): MutableList<Trip> {
        val trips = mutableListOf<Trip>()
        trips.addAll(_trips.value)
        return trips
    }

    suspend fun saveData(
        title: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        cities: List<String>,
        packingList: List<String>,
        latLngList: List<LatLng>,
        activities: List<String>,
        dates: List<LocalDateTime>,
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

