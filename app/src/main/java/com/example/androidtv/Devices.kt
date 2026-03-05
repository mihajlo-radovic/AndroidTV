package com.example.androidtv

data class Devices(
    val id: Int,
    val name: String,
    val type: String,
    val active: Boolean,
    val resolution: String?,
    val capsLock: Boolean?,
    val volume: Int?,
    val orientation: String?,
    val createdAt: String,
    val updatedAt: String
)
