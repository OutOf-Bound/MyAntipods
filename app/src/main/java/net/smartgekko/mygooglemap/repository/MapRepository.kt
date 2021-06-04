package net.smartgekko.mygooglemap.model

import net.smartgekko.mygooglemap.API_BASE_URL
import net.smartgekko.mygooglemap.GEO_API_FORMAT_JSON
import net.smartgekko.mygooglemap.GEO_API_KEY
import net.smartgekko.mygooglemap.GEO_API_RESULTS_NUMBER
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class UserAgentInterceptor(private val userAgent: String) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val originRequest: Request = chain.request()
        val requestWithUserAgent: Request = originRequest.newBuilder()
            .header("User-Agent", userAgent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }
}

object MapRepository {
    private val api: Api
    private var okHttp: OkHttpClient

    init {
        //  val uA = System.getProperty("http.agent")
        val uA = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:85.0) Gecko/20100101 Firefox/85.0"
        okHttp = OkHttpClient.Builder().addInterceptor(UserAgentInterceptor(uA)).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp)
            .build()
        api = retrofit.create(Api::class.java)
    }

    fun getLocationbyAddress(
        addressdLine: String,
        onSuccess: (mapObject: MapObject) -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        api.getLocationByAddress(GEO_API_KEY, addressdLine, GEO_API_FORMAT_JSON, GEO_API_RESULTS_NUMBER)
            .enqueue(object : Callback<GetLocationByAddress> {
                override fun onResponse(
                    call: Call<GetLocationByAddress>,
                    response: Response<GetLocationByAddress>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null && responseBody.response.geoObjectCollection.featureMember.isNotEmpty()) {
                            val objectName =
                                responseBody.response.geoObjectCollection.featureMember[0].geoObjects.description +
                                        ", " + responseBody.response.geoObjectCollection.featureMember[0].geoObjects.name
                            val coordinates =
                                (responseBody.response.geoObjectCollection.featureMember[0].geoObjects.point.pos).split(
                                    " "
                                ).toTypedArray()
                            val mapObject = MapObject(
                                coordinates[0].toDouble(),
                                coordinates[1].toDouble(),
                                objectName
                            )
                            onSuccess.invoke(mapObject)
                        } else {
                            val t = Throwable("Empty set")
                            onError.invoke(t)
                        }
                    } else {
                        val t = Throwable("Request failed")
                        onError.invoke(t)
                    }
                }

                override fun onFailure(call: Call<GetLocationByAddress>, t: Throwable) {
                    onError.invoke(t)
                }
            })
    }
}
