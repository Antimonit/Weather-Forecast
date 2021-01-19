package com.gelo.weatherforecast.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gelo.weatherforecast.api.WeatherAPI
import com.gelo.weatherforecast.api.response.WeatherDetailResponse
import com.gelo.weatherforecast.api.response.WeatherListResponse
import com.gelo.weatherforecast.utils.API_KEY
import com.gelo.weatherforecast.utils.UNITS
import retrofit2.Call
import javax.inject.Inject
import retrofit2.Callback
import retrofit2.Response

class WeatherAppViewModel : BaseViewModel(){

    @Inject
    lateinit var weatherAPI: WeatherAPI

    var mutableWeatherListResponse: MutableLiveData<WeatherListResponse> = MutableLiveData()
    var mutableWeatherDetailResponse: MutableLiveData<WeatherDetailResponse> = MutableLiveData()

    fun getWeatherByCityIDS(cityIDS: String){
        var weatherListResponse: WeatherListResponse? = null

        weatherAPI.getWeatherByCityIDS(cityIDS, UNITS, API_KEY).enqueue(object : Callback<WeatherListResponse>{
            override fun onResponse(call: Call<WeatherListResponse>, listResponse: Response<WeatherListResponse>) {
                if (listResponse.isSuccessful){
                    weatherListResponse = listResponse.body()

                    Log.d("Weather API", "success")
                    mutableWeatherListResponse.postValue(weatherListResponse)
                }
            }

            override fun onFailure(call: Call<WeatherListResponse>, t: Throwable) {
                Log.d("Weather API failed", t.message)}

        })
    }

    fun getWeatherDetail(cityID: String){
        var weatherDetailResponse: WeatherDetailResponse? = null

        weatherAPI.getWeatherDetail(cityID, UNITS, API_KEY).enqueue(object : Callback<WeatherDetailResponse>{
            override fun onResponse(call: Call<WeatherDetailResponse>, response: Response<WeatherDetailResponse>) {
                if (response.isSuccessful){
                    weatherDetailResponse = response.body()

                    Log.d("Weather Detail API", "success")
                    mutableWeatherDetailResponse.postValue(weatherDetailResponse)
                }
            }

            override fun onFailure(call: Call<WeatherDetailResponse>, t: Throwable) {
                Log.d("Weather Detail failed", t.message)
            }

        })
    }
}