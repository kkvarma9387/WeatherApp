package com.example.weatherapp.data.remote

import com.google.gson.annotations.SerializedName


data class MainDto(
    @SerializedName("temp")
    val temp: Double
)
