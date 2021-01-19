package com.gelo.weatherforecast.injection.component

import com.gelo.weatherforecast.injection.module.NetworkModule
import com.gelo.weatherforecast.viewmodel.WeatherAppViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [(NetworkModule::class)])
interface ViewModelInjector{

    fun inject(weatherAppViewModel: WeatherAppViewModel)


    @Component.Builder
    interface Builder{
        fun build(): ViewModelInjector

        fun networkModule(networkModule: NetworkModule): Builder
    }
}