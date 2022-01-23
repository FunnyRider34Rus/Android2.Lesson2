package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DetailsFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.weatherapp.model.Weather
import com.example.weatherapp.viewmodel.MainViewModel

class DetailsFragment : BottomSheetDialogFragment() {

    companion object {

        const val BUNDLE_EXTRA = "weather"
        const val BUNDLE_EXTRA_MENU = "menu"

        val bundle = Bundle()

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weather = arguments?.getParcelable<Weather>(BUNDLE_EXTRA)
        if (weather != null) {
            val city = weather.city
            binding.itemViewCityName.text = city.city
            //binding.cityCoordinates.text = "${getString(R.string.city_coordinates)} ${city.lat} ${city.lon}"
            binding.itemViewCityTemp.text = weather.temperature.toString()
            //binding.feelsLikeValue.text = weather.feelsLike.toString()
        }

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