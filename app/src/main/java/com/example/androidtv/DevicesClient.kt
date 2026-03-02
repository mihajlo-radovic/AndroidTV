package com.example.androidtv

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DevicesClient {
    val instance: DevicesService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DevicesService::class.java)
    }
}