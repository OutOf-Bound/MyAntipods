package net.smartgekko.mygooglemap.model

import net.smartgekko.mygooglemap.GEO_API_KEY
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("/1.x/")
    fun getLocationByAddress(
        @Query("apikey") apikey: String = GEO_API_KEY,
        @Query("geocode") geocode: String = "",
        @Query("format") format: String="json",
        @Query("results") results: Int = 1
    ): Call<GetLocationByAddress>
}