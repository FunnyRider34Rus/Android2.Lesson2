package com.example.weatherapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ListFragmentBinding
import com.example.weatherapp.model.Weather
import com.example.weatherapp.viewmodel.AppState
import com.example.weatherapp.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class ListFragment : BottomSheetDialogFragment() {

    companion object {

        const val BUNDLE_EXTRA_MENU = "menu"

        fun newInstance(bundle: Bundle): ListFragment {
            val fragment = ListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel
    private val adapter = ListFragmentAdapter(object : OnItemViewClickListener {
        override fun onItemViewClick(weather: Weather) {
            val manager = activity?.supportFragmentManager
            if (manager != null) {
                val bundle = Bundle()
                bundle.putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                manager.beginTransaction()
                    .replace(R.id.container, DetailsFragment.newInstance(bundle))
                    .addToBackStack("")
                    .commit()
            }
        }
    })

    private var isDataSetRus: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.getLiveData().observe(viewLifecycleOwner) { renderData(it) }
        viewModel.getWeatherFromLocalSourceRus()

        isDataSetRus = when (arguments?.getString(BUNDLE_EXTRA_MENU)) {
            "isRussian" -> {
                viewModel.getWeatherFromLocalSourceRus()
                true
            }
            "isWorld" -> {
                viewModel.getWeatherFromLocalSourceWorld()
                false
            }
            else -> {
                viewModel.getWeatherFromLocalSourceRus()
                true
            }
        }

        binding.recyclerView.adapter = adapter
        binding.bottomNavigationMenu.setOnItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.inrussia_weather_navigation -> {
                    viewModel.getWeatherFromLocalSourceRus()
                    true
                }

                R.id.inworld_weather_navigation -> {
                    viewModel.getWeatherFromLocalSourceWorld()
                    true
                }

                R.id.settings_navigation -> {
                    true
                }
                else -> { true }
            }
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                adapter.setWeather(appState.weatherData)
            }
            is AppState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
            is AppState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.listFragmentRootView.showSnackBarWithAction(
                    getString(R.string.error),
                    getString(R.string.reload),
                    { viewModel.getWeatherFromLocalSourceRus() }
                )
            }
        }
    }

    private fun View.showSnackBarWithAction(
        text: String,
        actionText: String,
        action: (View) -> Unit,
        length: Int = Snackbar.LENGTH_INDEFINITE
    ){
        Snackbar.make(this, text, length).setAction(actionText, action).show()
    }

    override fun onDestroy() {
        adapter.removeListener()
        super.onDestroy()
    }
}

interface OnItemViewClickListener {
    fun onItemViewClick(weather: Weather)
}
