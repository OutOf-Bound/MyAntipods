package net.smartgekko.mygooglemap.model

import com.google.gson.annotations.SerializedName

data class GeocoderResponseMetaData(
    @SerializedName("request") val request:String,
    @SerializedName("results") val results:String,
    @SerializedName("found") val found:String
)
