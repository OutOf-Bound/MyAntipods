package net.smartgekko.mygooglemap.model

import com.google.gson.annotations.SerializedName

data class GetLocationByAddress
    (
    @SerializedName("response") val response: ApiResponse
)