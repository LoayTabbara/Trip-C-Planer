package com.codecamp.tripcplaner.model.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

}

