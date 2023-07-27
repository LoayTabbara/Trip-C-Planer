package com.codecamp.tripcplaner.model.remote

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface LatLngService {
    @Headers("Content-Type: application/json")
    @POST("geocode/json")
    suspend fun generateResponse(
        @Query("key") apikey: String,
        @Query("address") address: String
    ): Response<JsonObject>
}