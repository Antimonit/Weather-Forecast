package com.gelo.weatherforecast.adapter

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gelo.weatherforecast.R
import com.gelo.weatherforecast.api.response.WeatherListResponse
import com.gelo.weatherforecast.viewmodel.WeatherAppViewModel

class CityListAdapter(private val weatherListResponse: WeatherListResponse, context: Context, private val listener: OnItemCLickListener
) : RecyclerView.Adapter<CityListAdapter.CityViewHolder>(){

    private var weatherAppViewModel: WeatherAppViewModel = WeatherAppViewModel()
    var sharedPreferences: SharedPreferences? = null
    var isClicked = true

    inner class CityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener  {
        val textTemperature: TextView
        val textCityName: TextView
        val textWeather: TextView
        val item: LinearLayout
        val favorite: ImageView

        init {
            textTemperature = itemView.findViewById(R.id.text_temperature)
            textCityName = itemView.findViewById(R.id.text_location)
            textWeather = itemView.findViewById(R.id.text_weather)
            item = itemView.findViewById(R.id.item_weather)
            favorite = itemView.findViewById(R.id.image_favorite)
            itemView.setOnClickListener(this)

            favorite.setOnClickListener(View.OnClickListener {
                Log.d("adapter image", "clickkkk")
                if (isClicked){
                    favorite.setImageResource(R.drawable.unselect)
                    isClicked = false
                    sharedPreferences?.edit()?.putBoolean(weatherListResponse.list?.get(adapterPosition)!!.id, false)!!.apply()
                }else{
                    favorite.setImageResource(R.drawable.selected)
                    isClicked = true
                    sharedPreferences?.edit()?.putBoolean(weatherListResponse.list?.get(adapterPosition)!!.id, true)!!.apply()
                }
            })
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }

        }

    }

    fun updateData(weatherResponse: WeatherListResponse){
        this.weatherListResponse.list?.clear()
        this.weatherListResponse.list?.addAll(weatherResponse.list!!)
        notifyDataSetChanged()
    }

    interface OnItemCLickListener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        sharedPreferences = view.getContext().getSharedPreferences("WeatherPreferences", Context.MODE_PRIVATE)
        return CityViewHolder(view)

    }

    override fun getItemCount(): Int {
        Log.d("Fragment Adapter", weatherListResponse?.list!!.size.toString())

        return weatherListResponse?.list!!.size
    }


    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val weather = weatherListResponse.list?.get(position)
        val rounded = String.format("%.1f", weather?.main?.temp)
        holder.textTemperature.text = rounded + " " + holder.itemView.context.getString(R.string.temperature)
        holder.textCityName.text = weather?.name
        holder.textWeather.text = weather?.weather?.get(0)!!.main

        if (sharedPreferences?.getBoolean(weather.id, false) == null){
            holder.favorite.setImageResource(R.drawable.unselect)
        }
        if (sharedPreferences?.getBoolean(weather.id, false) == false){
            holder.favorite.setImageResource(R.drawable.unselect)
        }else if (sharedPreferences?.getBoolean(weather.id, false)== true){
            holder.favorite.setImageResource(R.drawable.selected)
        }


        //background color per temperature
        if (weather?.main?.temp!! < 0){
            holder.item.setBackgroundResource(R.color.freezing)
        } else if (weather?.main?.temp!! > 0 && weather?.main?.temp!! < 15){
            holder.item.setBackgroundResource(R.color.cold)
        } else if (weather?.main?.temp!! > 15 && weather?.main?.temp!! < 30){
            holder.item.setBackgroundResource(R.color.warm)
        }else if (weather?.main?.temp!! > 30){
            holder.item.setBackgroundResource(R.color.hot)
        }

//        holder.itemView.setOnClickListener(View.OnClickListener {
//            Log.d("Current ID", weather.id)
//            weatherAppViewModel.getWeatherByCityID(weather.id)
//        })
    }

}