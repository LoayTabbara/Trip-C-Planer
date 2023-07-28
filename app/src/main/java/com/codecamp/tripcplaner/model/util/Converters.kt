package com.codecamp.tripcplaner.model.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Converters {

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): String =
        localDateTime.format(DateTimeFormatter.ISO_DATE_TIME)

    @TypeConverter
    fun toLocalDateTime(t: String): LocalDateTime =
        LocalDateTime.parse(t, DateTimeFormatter.ISO_DATE_TIME)

    @TypeConverter
    fun fromList(list: List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
    @TypeConverter
    fun toList(list: String): List<String> {
        val gson = Gson()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(list, type)
    }

    @TypeConverter
    fun fromMap(map: Map<String,Boolean>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun toMap(map: String): Map<String,Boolean> {
        val gson = Gson()
        val type = object : TypeToken<Map<String,Boolean>>() {}.type
        return gson.fromJson(map, type)
    }

}
