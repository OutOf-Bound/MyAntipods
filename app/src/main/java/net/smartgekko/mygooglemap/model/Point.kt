package net.smartgekko.mygooglemap.model

import com.google.gson.annotations.SerializedName

data class Point(
    @SerializedName("pos")val pos:String
)
