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

    // TODO: medium: Should use ViewModelProvider to initialize ViewModels! In this situation the instance created here is immediately overriden from `startViewModelObserver` method.
    private var weatherAppViewModel: WeatherAppViewModel = WeatherAppViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startViewModelObserver()
        weatherAppViewModel.getWeatherByCityIDS(resources.getString(R.string.city_ids))

    }

    // TODO: low: Redundant overriding method
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    // TODO: low: Redundant overriding method
    override fun onResume() {
        super.onResume()
    }

    // TODO: low: Function 'showWeatherList' could be private
    // TODO: low: Actual value of parameter 'tag' is always 'main_to_weather_list'
    // TODO: low: Actual value of parameter 'addToBackStack' is always 'false'
    fun showWeatherList(weatherListResponse: WeatherListResponse, tag: String, addToBackStack: Boolean){
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        val weatherListFragment = WeatherListFragment.newInstance(weatherListResponse)
        if (addToBackStack) {
            ft.addToBackStack(tag)
        }
        ft.replace(R.id.main, weatherListFragment, tag)
        // TODO: low: It is rather uncommon to depend on `(commit|commitNow)AllowingStateLoss`.
        ft.commitAllowingStateLoss()
    }

    private fun startViewModelObserver(){
        weatherAppViewModel = ViewModelProviders.of(this).get(WeatherAppViewModel::class.java)
        // TODO: high: Why do we replace whole fragment when we receive new data??
        weatherAppViewModel.mutableWeatherListResponse.observe(this, Observer {
            if (it != null) {
                if (it.list != null) {
                    showWeatherList(it, "main_to_weather_list", false)
                }
            }
        })
    }
}