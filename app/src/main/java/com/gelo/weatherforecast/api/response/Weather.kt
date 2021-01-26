package com.gelo.weatherforecast.api.response

import com.squareup.moshi.Json
import java.io.Serializable

// TODO: medium: Class "Weather" is never used
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