package com.example.weatherapp.view

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DetailsFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherDTO
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

//private const val API_WEATHER_KEY = "f140efeb-3d4c-4af4-98c9-66c3af7f143b"

class DetailsFragment : BottomSheetDialogFragment() {

    companion object {
        const val BUNDLE_EXTRA = "weather"
        val bundle = Bundle()
        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle: Weather

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadWeather() {
        try {
            val uri =
                URL("https://api.weather.yandex.ru/v2/forecast?lat=${weatherBundle.city.lat}&lon=${weatherBundle.city.lon}")
            val handler = Handler()
            Thread(Runnable {
                lateinit var urlConnection: HttpsURLConnection
                try {
                    urlConnection = uri.openConnection() as HttpsURLConnection
                    urlConnection.requestMethod = "GET"
                    urlConnection.addRequestProperty( "X-Yandex-API-Key", BuildConfig.API_WEATHER_KEY)
                    urlConnection.readTimeout = 10000
                    val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val weatherDTO: WeatherDTO = Gson().fromJson(getLines(bufferedReader), WeatherDTO::class.java)
                    handler.post { displayWeather(weatherDTO) }
                } catch (e: Exception) {
                    Log.e("", "Fail connection", e)
                    e.printStackTrace() //Обработка ошибки
                } finally {
                    urlConnection.disconnect()
                }
            }).start()
        } catch (e: MalformedURLException) {
            Log.e("", "Fail URI", e)
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }

    private fun displayWeather(weatherDTO: WeatherDTO) {
        with(binding) {
            val city = weatherBundle.city
            itemViewCityName.text = city.city
            itemViewCityCondition.text = weatherDTO.fact?.condition
            itemViewCityTemp.text = weatherDTO.fact?.temp.toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()
        loadWeather()

        binding.bottomNavigationMenu.setOnItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.inrussia_weather_navigation -> {
                    bundle.putString(ListFragment.BUNDLE_EXTRA_MENU, "isRussian")
                    activity?.supportFragmentManager?.popBackStack()
                    true
                }

                R.id.inworld_weather_navigation -> {
                    bundle.putString(ListFragment.BUNDLE_EXTRA_MENU, "isWorld")
                    activity?.supportFragmentManager?.popBackStack()
                    true
                }

                R.id.settings_navigation -> {
                    true
                }

                else -> false
            }
        }
    }
}