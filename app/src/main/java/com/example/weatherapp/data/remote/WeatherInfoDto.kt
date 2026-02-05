package com.example.weatherapp.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherInfoDto(
    @SerializedName("description")
    val description: String,

    @SerializedName("icon")
    val icon: String
)