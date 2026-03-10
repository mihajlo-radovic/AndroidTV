package com.example.androidtv

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DevicesService {

    @GET("devices")
    suspend fun getDevices(@Query("searchInput") searchInput: String?): Response<List<Devices>>

    @POST("devices")
    suspend fun createDevice(@Body device: CreateDeviceRequest): Response<Devices>

    @PATCH("devices/{id}/active")
    suspend fun setActive(@Path("id") id: Int, @Body request: ActiveRequest): Response<Devices>

    @PATCH("devices/{id}")
    suspend fun updateDevices(@Path("id") id: Int, @Body req: UpdateDeviceReq): Response<Devices>
}

data class CreateDeviceRequest(
    val name: String,
    val type: String
)

data class ActiveRequest(
    val active: Boolean
)

data class UpdateDeviceReq(
    val resolution: String? = null,
    val capsLock: Boolean? = null,
    val volume: Int? = null,
    val orientation: String? = null
)