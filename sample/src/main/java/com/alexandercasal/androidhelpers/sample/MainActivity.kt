package com.alexandercasal.androidhelpers.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.alexandercasal.androidhelpers.livedata.observe
import com.alexandercasal.androidhelpers.sample.databinding.ActivityMainBinding
import com.alexandercasal.androidhelpers.sample.prefs.SamplePrefs
import java.util.Random

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var prefs: SamplePrefs
    val viewModel by lazy { ViewModelProviders.of(this).get(SampleViewModel::class.java) }

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
        initViewModelObservers()

        binding.counter.setOnClickListener {
            viewModel.changeLiveNumber(Random().nextInt(2))
            //viewModel.postLiveNumber(Random().nextInt(2))
        }
    }

    private fun initViewModelObservers() {
        viewModel.dummyLiveData.observe(this) {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // Changes reflected in sample.xml located in the shared_prefs dir
    private fun updatePrefs() {
        prefs.countPref = 10
        prefs.decimalPref = 0f
        prefs.togglePref = true
        prefs.wordPref = "saved"
    }
}
