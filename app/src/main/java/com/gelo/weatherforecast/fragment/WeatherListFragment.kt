package com.gelo.weatherforecast.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.gelo.weatherforecast.R
import com.gelo.weatherforecast.adapter.CityListAdapter
import com.gelo.weatherforecast.api.response.WeatherDetailResponse
import com.gelo.weatherforecast.api.response.WeatherListResponse
import com.gelo.weatherforecast.network.NetworkConnection
import com.gelo.weatherforecast.utils.EXTRA_WEATHERS_RESPONSE
import com.gelo.weatherforecast.viewmodel.WeatherAppViewModel
import kotterknife.bindView

class WeatherListFragment : Fragment(), CityListAdapter.OnItemCLickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var weatherAppViewModel: WeatherAppViewModel
    private var weatherListResponse: WeatherListResponse? = null

    private val weather_list_recyclerview: RecyclerView by bindView(R.id.weather_list_recyclerview)
    private val swipe_refresh_layout: SwipeRefreshLayout by bindView(R.id.swipe_container)
    private var cityListAdapter:  CityListAdapter? = null
    private val toolbar: androidx.appcompat.widget.Toolbar by bindView(R.id.weather_list_toolbar)

    companion object {
        fun newInstance(weatherListResponse: WeatherListResponse): WeatherListFragment {
            val fragment = WeatherListFragment()
            val args = Bundle()
            args.putSerializable(EXTRA_WEATHERS_RESPONSE, weatherListResponse)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null){
            weatherListResponse = arguments!!.getSerializable(EXTRA_WEATHERS_RESPONSE) as WeatherListResponse
        }
        startViewModelObserver()
        val networkConnection = NetworkConnection(context!!)
        networkConnection.observe(this, Observer { isConnected->

            if (!isConnected){
                checkConnectivity()
            }
        })
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather_list, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipe_refresh_layout.setOnRefreshListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar.setTitle(resources.getString(R.string.app_name))
        cityListAdapter = CityListAdapter(weatherListResponse!!, context!!, this)
        weather_list_recyclerview.layoutManager = LinearLayoutManager(context)
        weather_list_recyclerview.adapter = cityListAdapter
    }

    private fun startViewModelObserver(){
        weatherAppViewModel = ViewModelProviders.of(this).get(WeatherAppViewModel::class.java)
        weatherAppViewModel.mutableWeatherListResponse.observe(this, Observer {
            if (it != null){
                cityListAdapter?.updateData(it)
                cityListAdapter?.notifyDataSetChanged()
                swipe_refresh_layout.isRefreshing = false

            }
        })

        weatherAppViewModel.mutableWeatherDetailResponse.observe(this, Observer {
            if (it != null){
                swipe_refresh_layout.isRefreshing = false
                if (it.list != null){
                    Log.d("Weather Detail", "not null..")
                     showWeatherList(it, "weatherlist_to_weatherdetail", false)

                }
            }
        })
    }
    private fun showWeatherList(weatherDetailResponse: WeatherDetailResponse, tag: String, addToBackStack: Boolean){
        val manager = activity?.supportFragmentManager
        val ft = manager?.beginTransaction()
        val detailFragment = DetailFragment.newInstance(weatherDetailResponse)
        if (addToBackStack) {
            ft?.addToBackStack(tag)
        }
        ft?.replace(R.id.main, detailFragment, tag)
        ft?.commitAllowingStateLoss()
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


    override fun onItemClick(position: Int) {
        Log.d("SELECTED ID", weatherListResponse?.list!![position].name)
        weatherAppViewModel.getWeatherDetail(weatherListResponse?.list!![position].id)
    }

    override fun onRefresh() {
        Log.d("REFRESH", "refreshing..")

        weatherAppViewModel.getWeatherByCityIDS(resources.getString(R.string.city_ids))
        swipe_refresh_layout.isRefreshing = true

    }



}