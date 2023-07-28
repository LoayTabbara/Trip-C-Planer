package com.codecamp.tripcplaner.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "trips", primaryKeys = ["id"])
data class Trip(
    @PrimaryKey @ColumnInfo(name = "id")val id: Int,
    @ColumnInfo(name = "title")val title: String,
    @ColumnInfo(name = "start_date")val startDate: LocalDateTime,
    @ColumnInfo(name = "end_date")val endDate: LocalDateTime,
    @ColumnInfo(name = "cities")val cities: List<String>,
    @ColumnInfo(name = "activities")val activities: List<String>,
    @ColumnInfo(name = "packing_list")val packingList: Map<String,Boolean>,
)
