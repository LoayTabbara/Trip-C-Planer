package com.codecamp.tripcplaner.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "transport_type") val transportType: String,
    @ColumnInfo(name = "start_date") val startDate: LocalDateTime,
    @ColumnInfo(name = "end_date") val endDate: LocalDateTime,
    @ColumnInfo(name = "cities") val cities: MutableMap<String, Pair<LatLng, LocalDateTime>>,
    @ColumnInfo(name = "activities") val activities: MutableList<String>,
    @ColumnInfo(name = "packing_list") val packingList: MutableMap<String, MutableList<Boolean>>,//first one for checked, second one for reminder
)
