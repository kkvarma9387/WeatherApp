package com.example.weatherapp.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("main")
    val main: MainDto,

    @SerializedName("weather")
    val weather: List<WeatherInfoDto>
)


