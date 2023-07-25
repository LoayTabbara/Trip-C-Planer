package com.codecamp.tripcplaner.model

import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApi {
    @GET("maps/api/place/autocomplete/json")
    suspend fun getPredictions(
        @Query("key") key: String = MAPS_API_KEY,
    @Query("types") types: String = "address",
    @Query("input") input: String
    ): GooglePredictionsResponse

    companion object{
        const val BASE_URL = "https://maps.googleapis.com/"
    }
}