package com.gelo.weatherforecast.api.response

import com.squareup.moshi.Json
import java.io.Serializable

class WeatherListResponse: Serializable {

    @Json(name = "cnt")
    var cnt: Int = 0

    @Json(name = "list")
    var list: ArrayList<List>? = null

    class List : Serializable{

        @Json(name = "coord")
        var coord: Coord? = null

        @Json(name = "weather" )
        var weather: kotlin.collections.List<Weather>? = null

        @Json(name = "main")
        var main: Main? = null

        @Json(name = "id")
        var id: String = ""

        @Json(name = "name")
        var name: String = ""
    }

    class Coord: Serializable{

        @Json(name = "lon")
        var lon: String = ""

        @Json(name = "lat")
        var lat: String = ""

    }


    class Weather: Serializable{

            @Json(name = "id")
            var id: String = ""

            @Json(name = "main")
            var main: String = ""

            @Json(name = "description")
            var description: String = ""

            @Json(name = "icon")
            var icon: String = ""

        }

        class Main : Serializable{
            @Json(name = "temp")
            var temp: Double = 0.0

            @Json(name = "feels_like")
            var feels_like: String = ""

            @Json(name = "temp_min")
            var temp_min: Double = 0.0

            @Json(name = "temp_max")
            var temp_max: Double = 0.0

            @Json(name = "pressure")
            var pressure: String = ""

            @Json(name = "humidity")
            var humidity: String = ""

        }


}