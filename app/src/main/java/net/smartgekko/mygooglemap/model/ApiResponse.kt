package net.smartgekko.mygooglemap.model

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("GeoObjectCollection") val geoObjectCollection: GeoObjectCollection
)
