package com.example.androidtv

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface DevicesService {

//    @Headers(
//        "Content-Type: application/json",
//        "{\"name\":\"Camera 1\",\"type\":\"CAMERA\"}"
//    )
    @GET("devices")
    suspend fun getDevices(): Response<List<Devices>>

    @POST("devices")
    suspend fun createDevice(@Body device: CreateDeviceRequest): Response<Devices>

    @PATCH("devices/{id}/active")
    suspend fun setActive(@Path("id") id: Int, @Body request: ActiveRequest): Response<Devices>
}

data class CreateDeviceRequest(
    val name: String,
    val type: String
)

data class ActiveRequest(
    val active: Boolean
)