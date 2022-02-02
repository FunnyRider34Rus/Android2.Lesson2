package com.example.weatherapp.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager.CONNECTIVITY_ACTION
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DetailsFragmentBinding
import com.example.weatherapp.model.FactDTO
import com.example.weatherapp.model.Weather
import com.example.weatherapp.model.WeatherDTO
import com.example.weatherapp.model.WeatherLoader
import com.example.weatherapp.service.DetailsService
import com.example.weatherapp.service.LATITUDE_EXTRA
import com.example.weatherapp.service.LONGITUDE_EXTRA
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
const val DETAILS_LOAD_RESULT_EXTRA = "LOAD RESULT"
const val DETAILS_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
const val DETAILS_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
const val DETAILS_RESPONSE_EMPTY_EXTRA = "RESPONSE IS EMPTY"
const val DETAILS_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
const val DETAILS_REQUEST_ERROR_MESSAGE_EXTRA = "REQUEST ERROR MESSAGE"
const val DETAILS_URL_MALFORMED_EXTRA = "URL MALFORMED"
const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
const val DETAILS_TEMP_EXTRA = "TEMPERATURE"
const val DETAILS_CONDITION_EXTRA = "CONDITION"
private const val TEMP_INVALID = -100
private const val PROCESS_ERROR = "Обработка ошибки"

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

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Snackbar.make(view!!, "Изменилось состояние сети", Snackbar.LENGTH_LONG).show()
        }
    }

    private val loadResultsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(DETAILS_LOAD_RESULT_EXTRA)) {
                DETAILS_INTENT_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_DATA_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_RESPONSE_EMPTY_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_REQUEST_ERROR_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_REQUEST_ERROR_MESSAGE_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_URL_MALFORMED_EXTRA -> TODO(PROCESS_ERROR)
                DETAILS_RESPONSE_SUCCESS_EXTRA -> renderData(
                    WeatherDTO(
                        FactDTO(
                            intent.getIntExtra(DETAILS_TEMP_EXTRA, TEMP_INVALID),
                            intent.getStringExtra(DETAILS_CONDITION_EXTRA)
                        )
                    )
                )
                else -> TODO(PROCESS_ERROR)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
        context?.let {
            LocalBroadcastManager.getInstance(it)
                .registerReceiver(loadResultsReceiver, IntentFilter(DETAILS_INTENT_FILTER))
        }
        context?.registerReceiver(networkReceiver, IntentFilter("android.net.ConnectivityManager.CONNECTIVITY_CHANGE"))
    }

    override fun onDestroy() { context?.let {
        LocalBroadcastManager.getInstance(it).unregisterReceiver(loadResultsReceiver) }
        context?.unregisterReceiver(networkReceiver)
        super.onDestroy()
    }

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
        getWeather()

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

    private fun getWeather() {
        context?.let {
            it.startService(Intent(it, DetailsService::class.java).apply {
                putExtra(
                    LATITUDE_EXTRA,
                    weatherBundle.city.lat
                )
                putExtra(
                    LONGITUDE_EXTRA, weatherBundle.city.lon
                )
            })
        }
    }

    private fun renderData(weatherDTO: WeatherDTO) {
        val fact = weatherDTO.fact
        val temp = fact!!.temp
        val condition = fact.condition
        if (temp == TEMP_INVALID || condition
            == null) {
            TODO(PROCESS_ERROR) } else {
            val city = weatherBundle.city
            binding.itemViewCityName.text = city.city
            binding.itemViewCityTemp.text = temp.toString()
            binding.itemViewCityCondition.text = condition
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}