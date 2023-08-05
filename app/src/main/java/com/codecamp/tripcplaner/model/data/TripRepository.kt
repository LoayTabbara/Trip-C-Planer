package com.codecamp.tripcplaner.model.data

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
interface TripRepository {
    fun getAllItems(): MutableList<Trip>
    suspend fun updatePackingList(id: Int, packingList: MutableMap<String, MutableList<Boolean>>)
    suspend fun insertAll(trips: MutableList<Trip>)
    suspend fun insertTrip(trip: Trip)
    fun deleteAll()
    fun deleteById(id: Int)
    fun deleteByTitle(title: String)
    fun getByTitle(title: String): Trip
    fun getByStartDate(startDate: String): Trip
    fun getByEndDate(endDate: String): Trip
    fun getById(id: Int): Trip
    fun getCount(): Int
    fun populateTrips(_trips: StateFlow<List<Trip>>): MutableList<Trip>
    suspend fun saveData(
        title: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        cities: MutableList<String>,
        packingList: MutableList<String>,
        latLngList: MutableList<LatLng>,
        activities: MutableList<String>,
        dates: MutableList<LocalDateTime>,
        transportType: String
    )
}
