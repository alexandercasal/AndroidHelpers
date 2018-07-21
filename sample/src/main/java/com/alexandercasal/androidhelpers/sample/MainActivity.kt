package com.alexandercasal.androidhelpers.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.alexandercasal.androidhelpers.sample.databinding.ActivityMainBinding
import com.alexandercasal.androidhelpers.sample.prefs.SamplePrefs

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var prefs: SamplePrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = SamplePrefs(this, "sample")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            togglePref = prefs.togglePref
            countPref = prefs.countPref
            decimalPref = prefs.decimalPref
            wordPref = prefs.wordPref
        }

        updatePrefs()
    }

    // Changes reflected in sample.xml located in the shared_prefs dir
    private fun updatePrefs() {
        prefs.countPref = 10
        prefs.decimalPref = 0f
        prefs.togglePref = true
        prefs.wordPref = "saved"
    }
}
