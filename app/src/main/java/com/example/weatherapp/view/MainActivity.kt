package com.example.weatherapp.view

import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.weatherapp.R
import com.example.weatherapp.databinding.MainActivityBinding
import com.example.weatherapp.view.DetailsFragment.Companion.bundle

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListFragment.newInstance(bundle))
                .commitNow()
        }
    }
}