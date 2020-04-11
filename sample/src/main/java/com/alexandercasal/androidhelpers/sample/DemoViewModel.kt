package com.alexandercasal.androidhelpers.sample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.alexandercasal.androidhelpers.livedata.SingleLiveEvent
import com.alexandercasal.androidhelpers.livedata.getDistinct
import com.alexandercasal.androidhelpers.livedata.postIfChanged
import com.alexandercasal.androidhelpers.livedata.updateIfChanged
import java.time.LocalTime
import kotlin.random.Random

class DemoViewModel(application: Application) : AndroidViewModel(application) {

    val prefs: SamplePrefs = SamplePrefs(application, "demo_prefs")
    private val _distinctLiveData = MutableLiveData<Int>()
    val distinctLiveData = _distinctLiveData.getDistinct()

    val singleLiveEvent = SingleLiveEvent<Unit>()

    fun demoDistinctLiveData() {
        _distinctLiveData.value = 1
    }

    fun demoChangeDistinctLiveDataValue() {
        _distinctLiveData.updateIfChanged((_distinctLiveData.value ?: 0) + 1)
    }

    fun postChangeDistinctLiveDataValue() {
        Thread {
            _distinctLiveData.postIfChanged((_distinctLiveData.value ?: 0) + 1)
        }.start()
    }

    fun callSingleLiveEvent() {
        singleLiveEvent.call()
    }

    fun setIntPref() {
        prefs.demoIntPref++
    }

    fun setBooleanPref() {
        prefs.demoBooleanPref = !prefs.demoBooleanPref
    }

    fun setFloatPref() {
        prefs.demoFloatPref += 0.5f
    }

    fun setLongPref() {
        prefs.demoLongPref++
    }

    fun setStringPref() {
        prefs.demoStringPref = System.currentTimeMillis().toString()
    }

    fun setStringSetPref() {
        val one = Random.nextInt().toString()
        val two = Random.nextInt().toString()
        val three = two
        prefs.demoStringSetPref = setOf(one, two, three)
    }

    fun setLocalTimePref() {
        prefs.demoLocalTimePref = LocalTime.now()
    }
}