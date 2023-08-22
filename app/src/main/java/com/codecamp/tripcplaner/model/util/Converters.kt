package com.codecamp.tripcplaner.model.util

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
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
    fun fromList(list: MutableList<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(mutableList: String): MutableList<String> {
        val gson = Gson()
        val type = object : TypeToken<MutableList<String>>() {}.type
        return gson.fromJson(mutableList, type)
    }

    @TypeConverter
    fun fromMap(map: MutableMap<String, MutableList<Boolean>>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun toMap(map: String): MutableMap<String, MutableList<Boolean>> {
        val gson = Gson()
        val type = object : TypeToken<MutableMap<String, MutableList<Boolean>>>() {}.type
        return gson.fromJson(map, type)
    }

    @TypeConverter
    fun fromMapPair(map: MutableMap<String, Pair<LatLng, LocalDateTime>>): String {

        val mapJson = JSONObject()
        map.entries.forEach {
            val cityJson = JSONObject()
            val cityLatLngJson = JSONObject()
            cityLatLngJson.put("latitude", it.value.first.latitude)
            cityLatLngJson.put("longitude", it.value.first.longitude)
            cityJson.put("first", cityLatLngJson)
            cityJson.put("second", it.value.second)
            mapJson.put(it.key, cityJson)
        }
        return mapJson.toString()
    }

    @TypeConverter
    fun toMapPair(map: String): MutableMap<String, Pair<LatLng, LocalDateTime>> {

        val mapJson = JSONObject(map)
        val pairMutableMap = mutableMapOf<String, Pair<LatLng, LocalDateTime>>()
        mapJson.keys().forEach { cityJsonObject ->
            val cityJson = mapJson.getJSONObject(cityJsonObject)
            val cityLatLngJson = cityJson.getJSONObject("first")
            val cityLatLng =
                LatLng(cityLatLngJson.getDouble("latitude"), cityLatLngJson.getDouble("longitude"))
            val cityDateTime =
                LocalDateTime.parse(cityJson.getString("second"), DateTimeFormatter.ISO_DATE_TIME)
            pairMutableMap[cityJsonObject] = Pair(cityLatLng, cityDateTime)
        }
        return pairMutableMap
    }
}
