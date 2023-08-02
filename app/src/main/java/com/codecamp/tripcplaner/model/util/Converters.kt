package com.codecamp.tripcplaner.model.util

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
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
    fun fromMap(map: MutableMap<String,Boolean>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun toMap(map: String): MutableMap<String,Boolean> {
        val gson = Gson()
        val type = object : TypeToken<MutableMap<String,Boolean>>() {}.type
        return gson.fromJson(map, type)
    }

    @TypeConverter
    fun fromLatLng(latLng: LatLng): String {
        val gson = Gson()
        return gson.toJson(latLng)
    }
    @TypeConverter
    fun toLatLng(latLng: String): LatLng {
        val gson = Gson()
        val type = object : TypeToken<LatLng>() {}.type
        return gson.fromJson(latLng, type)
    }

    @TypeConverter
    fun fromPair(pair: Pair<LatLng,LocalDateTime>): String {
        val gson = Gson()
        return gson.toJson(pair)
    }

    @TypeConverter
    fun toPair(pair: String): Pair<LatLng,LocalDateTime> {
        val gson = Gson()
        val type = object : TypeToken<Pair<LatLng,LocalDateTime>>() {}.type
        return gson.fromJson(pair, type)
    }

    @TypeConverter
    fun fromMapPair(map: MutableMap<String,Pair<LatLng,LocalDateTime>>): String {
        var mapJson = JSONObject()
        map.entries.forEach {
            var cityJson = JSONObject()
            var cityLatLngJson = JSONObject()
            cityLatLngJson.put("latitude",it.value.first.latitude)
            cityLatLngJson.put("longitude",it.value.first.longitude)
            cityJson.put("first",cityLatLngJson)
            cityJson.put("second",it.value.second)
            mapJson.put(it.key,cityJson)
        }
        return mapJson.toString()
    }

    @TypeConverter
    fun toMapPair(map: String): MutableMap<String,Pair<LatLng,LocalDateTime>> {

        val mapJson = JSONObject(map)
        val map = mutableMapOf<String,Pair<LatLng,LocalDateTime>>()
        mapJson.keys().forEach { cityJsonObject ->
            val cityJson = mapJson.getJSONObject(cityJsonObject)
            val cityLatLngJson = cityJson.getJSONObject("first")
            val cityLatLng = LatLng(cityLatLngJson.getDouble("latitude"),cityLatLngJson.getDouble("longitude"))
            val cityDateTime = LocalDateTime.parse(cityJson.getString("second"), DateTimeFormatter.ISO_DATE_TIME)
            map[cityJsonObject] = Pair(cityLatLng,cityDateTime)
        }
        return map
    }
}
