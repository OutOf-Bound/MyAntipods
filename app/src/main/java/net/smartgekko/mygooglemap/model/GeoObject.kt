package net.smartgekko.mygooglemap.model

import com.google.gson.annotations.SerializedName

data class GeoObject(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description:String,
    @SerializedName("Point") val point:Point
)
