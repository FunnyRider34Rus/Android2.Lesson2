package com.example.weatherapp.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.example.weatherapp.viewmodel.AppState
import com.example.weatherapp.databinding.MainFragmentBinding
import com.example.weatherapp.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val observer = Observer<AppState> { state ->
            renderData(state)
        }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getWeather()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(state: AppState) {
        when(state) {
            is AppState.Success -> {
                val weatherData = state.weatherData
                binding.loadingLayout.isVisible = false
                binding.mainView.isVisible = true
                binding.city.text = state.weatherData.name
                binding.temp.text = state.weatherData.temp.toString()
            }
            is AppState.Error -> {
                Snackbar
                    .make(binding.root, state.toString(), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload") {viewModel.getWeather()}
                    .show()
            }
            is AppState.Loading -> {
                binding.loadingLayout.isVisible = true
                binding.mainView.isVisible = false
            }
        }
    }

}