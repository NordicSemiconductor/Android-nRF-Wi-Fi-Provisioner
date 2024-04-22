package no.nordicsemi.android.wifi.provisioner.softap

import no.nordicsemi.android.wifi.provisioner.softap.proto.ScanResults
import no.nordicsemi.android.wifi.provisioner.softap.proto.WifiConfig
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Roshan Rajaratnam on 19/02/2024.
 */
interface WifiService {

    @Headers("Content-Type: application/x-protobuf")
    @GET("prov/networks")
    suspend fun listSsids(): ScanResults

    @Headers("Content-Type: application/x-protobuf")
    @POST("prov/configure")
    suspend fun provision(@Body config: WifiConfig): Response<ResponseBody>
}