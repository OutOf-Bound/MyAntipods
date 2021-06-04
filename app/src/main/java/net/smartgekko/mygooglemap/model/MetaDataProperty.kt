package net.smartgekko.mygooglemap.model

import com.google.gson.annotations.SerializedName

data class MetaDataProperty(
    @SerializedName("GeocoderResponseMetaData") val geocoderResponseMetaData:GeocoderResponseMetaData
)
