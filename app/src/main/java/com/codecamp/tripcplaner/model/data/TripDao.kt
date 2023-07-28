package com.codecamp.tripcplaner.model.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripDao {
    @Query("SELECT * FROM trips")
    fun getAll(): List<Trip>

    @Query("SELECT * FROM trips WHERE id = :id")
    fun getById(id: Int): Trip

    @Query("SELECT * FROM trips WHERE title = :title")
    fun getByTitle(title: String): Trip

    @Query("SELECT * FROM trips WHERE start_date = :startDate")
    fun getByStartDate(startDate: String): Trip

    @Query("SELECT * FROM trips WHERE end_date = :endDate")
    fun getByEndDate(endDate: String): Trip


    @Query("DELETE FROM trips")
    fun deleteAll()

    @Query("DELETE FROM trips WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM trips WHERE title = :title")
    fun deleteByTitle(title: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trips: List<Trip>)

    @Query("SELECT COUNT(*) FROM trips")
    fun getCount(): Int

}