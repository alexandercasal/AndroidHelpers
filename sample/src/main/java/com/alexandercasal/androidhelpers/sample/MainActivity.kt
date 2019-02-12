package com.alexandercasal.androidhelpers.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.alexandercasal.androidhelpers.livedata.observe
import com.alexandercasal.androidhelpers.livedata.observeNonNull
import com.alexandercasal.androidhelpers.sample.databinding.ActivityMainBinding
import com.jakewharton.threetenabp.AndroidThreeTen

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by lazy { ViewModelProviders.of(this).get(DemoViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setupDemoClickListeners()
        setupLiveDataObservers()
    }

    private fun setupDemoClickListeners() {
        binding.demoDistinctLiveData.setOnClickListener {viewModel.demoDistinctLiveData() }
        binding.demoChangeDistinctLiveDataValue.setOnClickListener { viewModel.demoChangeDistinctLiveDataValue() }
        binding.demoPostChangeDistinctLiveDataValue.setOnClickListener { viewModel.postChangeDistinctLiveDataValue() }
        binding.demoSingleLiveEvent.setOnClickListener { viewModel.callSingleLiveEvent() }

        // Prefs
        binding.demoIntPref.setOnClickListener { viewModel.setIntPref() }
        binding.demoBooleanPref.setOnClickListener { viewModel.setBooleanPref() }
        binding.demoFloatPref.setOnClickListener { viewModel.setFloatPref() }
        binding.demoLongPref.setOnClickListener { viewModel.setLongPref() }
        binding.demoStringPref.setOnClickListener { viewModel.setStringPref() }
        binding.demoStringSetPref.setOnClickListener { viewModel.setStringSetPref() }
        binding.demoLocalTimePref.setOnClickListener { viewModel.setLocalTimePref() }
    }

    private fun setupLiveDataObservers() {
        viewModel.distinctLiveData.observeNonNull(this) {
            Toast.makeText(this, "Distinct Value: $it", Toast.LENGTH_LONG).show()
        }

        viewModel.singleLiveEvent.observe(this) {
            Toast.makeText(this, "Single live event triggered", Toast.LENGTH_LONG).show()
        }

        // Prefs
        viewModel.prefs.demoIntPrefLiveData.observeNonNull(this) {
            binding.demoIntPref.text = "Int Pref: $it"
        }

        viewModel.prefs.demoBooleanPrefLiveData.observeNonNull(this) {
            binding.demoBooleanPref.text = "Boolean Pref: $it"
        }

        viewModel.prefs.demoFloatPrefLiveData.observeNonNull(this) {
            binding.demoFloatPref.text = "Float Pref: $it"
        }

        viewModel.prefs.demoLongPrefLiveData.observeNonNull(this) {
            binding.demoLongPref.text = "Long Pref: $it"
        }

        viewModel.prefs.demoStringPrefLiveData.observeNonNull(this) {
            binding.demoStringPref.text = "String Pref: $it"
        }

        viewModel.prefs.demoStringSetPrefLiveData.observeNonNull(this) {
            binding.demoStringSetPref.text = "String Set Pref: $it"
        }

        viewModel.prefs.demoLocalTimePrefLiveData.observeNonNull(this) {
            binding.demoLocalTimePref.text = "LocalTime Pref: $it"
        }
    }
}
