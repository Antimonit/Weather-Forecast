package com.gelo.weatherforecast.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.gelo.weatherforecast.R
import com.gelo.weatherforecast.api.response.WeatherDetailResponse
import com.gelo.weatherforecast.api.response.WeatherListResponse
import com.gelo.weatherforecast.network.NetworkConnection
import com.gelo.weatherforecast.utils.EXTRA_WEATHERS_RESPONSE
import com.gelo.weatherforecast.viewmodel.WeatherAppViewModel
import kotterknife.bindView
import kotlin.math.roundToInt

class DetailFragment : Fragment(){

    private lateinit var weatherAppViewModel: WeatherAppViewModel
    private var weatherDetailResponse: WeatherDetailResponse? = null

    private val location: TextView by bindView(R.id.detail_location)
    private val temperature: TextView by bindView(R.id.detail_temperature)
    private val weather: TextView by bindView(R.id.detail_weather)
    private val temp_high: TextView by bindView(R.id.detail_high_temp)
    private val temp_low: TextView by bindView(R.id.detail_low_temp)
    private val favorite: ImageView by bindView(R.id.detail_favorite)
    private val toolbar: androidx.appcompat.widget.Toolbar by bindView(R.id.detail_toolbar)


    private var isClicked : Boolean = true
    private var sharedPreferences: SharedPreferences? = null

    companion object {
        fun newInstance(weatherDetailResponse: WeatherDetailResponse): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_WEATHERS_RESPONSE, weatherDetailResponse)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null){
            weatherDetailResponse = arguments!!.getSerializable(EXTRA_WEATHERS_RESPONSE) as WeatherDetailResponse
        }
        sharedPreferences = activity?.getSharedPreferences("WeatherPreferences", Context.MODE_PRIVATE)
        startViewModelObserver()
        val networkConnection = NetworkConnection(context!!)
        networkConnection.observe(this, Observer { isConnected->

            if (!isConnected){
                checkConnectivity()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setNavigationIcon(R.drawable.arrow);
        toolbar.setTitle(resources.getString(R.string.app_name))
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            weatherAppViewModel.getWeatherByCityIDS(resources.getString(R.string.city_ids))
        })
        view?.isFocusableInTouchMode = true
        view?.requestFocus()

        view!!.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(
                v: View?,
                keyCode: Int,
                event: KeyEvent
            ): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        weatherAppViewModel.getWeatherByCityIDS(resources.getString(R.string.city_ids))
                        return true
                    }
                }
                return false
            }
        })

        favorite.setOnClickListener(View.OnClickListener {
            if (isClicked){
                favorite.setImageResource(R.drawable.unselect)
                isClicked = false
                sharedPreferences?.edit()?.putBoolean(weatherDetailResponse?.list!![0].id, false)!!.apply()

            }else{
                favorite.setImageResource(R.drawable.selected)
                isClicked = true
                sharedPreferences?.edit()?.putBoolean(weatherDetailResponse?.list!![0].id, true)!!.apply()
            }
        })

    }

    private fun populateData(){
        if (sharedPreferences?.getBoolean(weatherDetailResponse?.list?.get(0)?.id, false) == null || sharedPreferences?.getBoolean(weatherDetailResponse?.list?.get(0)?.id, false) == false){
            isClicked = false
            favorite.setImageResource(R.drawable.unselect)
        }else if (sharedPreferences?.getBoolean(weatherDetailResponse?.list?.get(0)?.id, false)== true){
            isClicked = true
            favorite.setImageResource(R.drawable.selected)
        }

        for (i:Int in 0 until  weatherDetailResponse?.list!!.size){
            val rounded = String.format("%.1f", weatherDetailResponse?.list?.get(i)?.main!!.temp)

            location.text = weatherDetailResponse?.list?.get(i)!!.name
            temperature.text = rounded + resources.getString(R.string.temperature)
            weather.text = weatherDetailResponse?.list?.get(i)?.weather!![0].main
            temp_high.text = resources.getString(R.string.high) + " "+ weatherDetailResponse?.list?.get(i)?.main!!.temp_max.roundToInt() + resources.getString(R.string.temperature) + " /"
            temp_low.text = resources.getString(R.string.low) + " "+ weatherDetailResponse?.list?.get(i)?.main!!.temp_min.roundToInt() + resources.getString(R.string.temperature)
        }
    }

    fun showWeatherList(weatherListResponse: WeatherListResponse, tag: String, addToBackStack: Boolean){
        val manager = activity?.supportFragmentManager
        val ft = manager?.beginTransaction()
        val weatherListFragment = WeatherListFragment.newInstance(weatherListResponse)
        if (addToBackStack) {
            ft?.addToBackStack(tag)
        }
        ft?.replace(R.id.main, weatherListFragment, tag)
        ft?.commitAllowingStateLoss()
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

    private fun checkConnectivity() {
            val dialogBuilder = AlertDialog.Builder(activity)

            dialogBuilder.setMessage(resources.getString(R.string.network_error_message))
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                })

            val alert = dialogBuilder.create()
            alert.setTitle("No Internet Connection")
            alert.show()
    }


}