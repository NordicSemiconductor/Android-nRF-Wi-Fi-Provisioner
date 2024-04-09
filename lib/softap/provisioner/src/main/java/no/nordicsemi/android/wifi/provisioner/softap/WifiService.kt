package no.nordicsemi.android.wifi.provisioner.softap

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Created by Roshan Rajaratnam on 19/02/2024.
 */
interface WifiService {

    @Headers("Content-Type: text/plain")
    @GET("prov/networks")
    suspend fun listSsids(): Response<ResponseBody>

    @Headers("Content-Type: text/plain")
    @PUT("prov/configure")
    suspend fun provision(@Body credentials: String): Response<ResponseBody>

    @Headers("Content-Type: text/plain")
    @PUT("led/{led}")
    suspend fun blink(@Path("led") led : Int, @Body toggle: String): Response<ResponseBody>
}