package com.codecamp.tripcplaner.model.data

import com.squareup.moshi.Json

data class CityInfo(
    @Json(name = "Activities") val activities: List<String>,
    @Json(name = "Arrival Time") val arrivalTime: String
)

data class ItineraryInfo(
    @Json(name = "Packing List") val packingList: List<String>,
    @Json(name = "Itinerary") val itinerary: Map<String, CityInfo>

)
