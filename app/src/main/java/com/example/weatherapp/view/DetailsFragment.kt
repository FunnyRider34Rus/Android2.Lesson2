package com.example.weatherapp.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DetailsFragmentBinding
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherDTO
import com.example.weatherapp.model.WeatherLoader
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = arguments?.getParcelable(BUNDLE_EXTRA) ?: Weather()
        var isFirstError = true
        val onLoadListener: WeatherLoader.WeatherLoaderListener =
            object : WeatherLoader.WeatherLoaderListener {
                override fun onLoaded(weatherDTO: WeatherDTO) { displayWeather(weatherDTO) }
                override fun onFailed(throwable: Throwable, weatherDTO: WeatherDTO) {
                    if (isFirstError) {
                        isFirstError = !isFirstError
                        displayWeather(weatherDTO)
                    } else {
                        Snackbar
                            .make(view, "Не удаётся загрузить данные", Snackbar.LENGTH_LONG)
                            .setAction("Отменить") { activity?.supportFragmentManager?.popBackStack() }
                            .show()
                    }
                }
            }

        val loader = WeatherLoader(onLoadListener, weatherBundle.city.lat, weatherBundle.city.lon)
        loader.loadWeather()

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

    private fun displayWeather(weatherDTO: WeatherDTO) {
        with(binding) {
            val city = weatherBundle.city
            itemViewCityName.text = city.city
            itemViewCityCondition.text = weatherDTO.fact?.condition
            itemViewCityTemp.text = weatherDTO.fact?.temp.toString()
        }
    }
}