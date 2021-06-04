package net.smartgekko.mygooglemap.model

import com.google.gson.annotations.SerializedName

data class FeatureMember(
    @SerializedName("GeoObject") val geoObjects:GeoObject
)
