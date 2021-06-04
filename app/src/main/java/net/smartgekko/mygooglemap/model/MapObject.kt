package net.smartgekko.mygooglemap.model

import com.google.gson.annotations.SerializedName

data class MapObject (
        @SerializedName("lon") val lon: Double,
        @SerializedName("lat") val lat: Double,
        @SerializedName("display_name") val displayName: String
    )
