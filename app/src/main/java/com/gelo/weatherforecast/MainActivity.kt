package com.gelo.weatherforecast

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gelo.weatherforecast.api.response.WeatherListResponse
import com.gelo.weatherforecast.fragment.WeatherListFragment
import com.gelo.weatherforecast.viewmodel.WeatherAppViewModel

class MainActivity : AppCompatActivity() {

    private var weatherAppViewModel: WeatherAppViewModel = WeatherAppViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startViewModelObserver()
        weatherAppViewModel.getWeatherByCityIDS(resources.getString(R.string.city_ids))

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onResume() {
        super.onResume()
    }

    fun showWeatherList(weatherListResponse: WeatherListResponse, tag: String, addToBackStack: Boolean){
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        val weatherListFragment = WeatherListFragment.newInstance(weatherListResponse)
        if (addToBackStack) {
            ft.addToBackStack(tag)
        }
        ft.replace(R.id.main, weatherListFragment, tag)
        ft.commitAllowingStateLoss()
    }

    private fun startViewModelObserver(){
        weatherAppViewModel = ViewModelProviders.of(this).get(WeatherAppViewModel::class.java)
        weatherAppViewModel.mutableWeatherListResponse.observe(this, Observer {
            if (it != null){
                if (it.list != null){
                    showWeatherList(it, "main_to_weather_list", false)
                }
            }
        })
    }
}