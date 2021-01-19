package com.gelo.weatherforecast.api

import com.gelo.weatherforecast.api.response.WeatherDetailResponse
import com.gelo.weatherforecast.api.response.WeatherListResponse
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherAPI {

    @POST("group")
    fun getWeatherByCityIDS(@Query("id") cityIDs: String, @Query("units") unit: String, @Query("appid") apiKey: String): Call<WeatherListResponse>

    @POST("group")
    fun getWeatherDetail(@Query("id") cityID: String, @Query("units") unit: String, @Query("appid") apiKey: String): Call<WeatherDetailResponse>
}