package com.gelo.weatherforecast.viewmodel

import androidx.lifecycle.ViewModel
import com.gelo.weatherforecast.injection.component.DaggerViewModelInjector
import com.gelo.weatherforecast.injection.component.ViewModelInjector
import com.gelo.weatherforecast.injection.module.NetworkModule

abstract class BaseViewModel : ViewModel(){

    private val injector: ViewModelInjector = DaggerViewModelInjector
        .builder()
        .networkModule(NetworkModule)
        .build()

    init {
        inject()
    }

    private fun inject(){
        when(this){
            is WeatherAppViewModel -> injector.inject(this)
        }
    }
}