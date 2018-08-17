package com.alexandercasal.androidhelpers.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alexandercasal.androidhelpers.livedata.postIfChanged
import com.alexandercasal.androidhelpers.livedata.updateIfChanged

class SampleViewModel : ViewModel() {

    private var _dummyLiveData = MutableLiveData<Int>()
    val dummyLiveData: LiveData<Int> = _dummyLiveData

    fun changeLiveNumber(value: Int) {
        _dummyLiveData.updateIfChanged(value)
    }

    fun postLiveNumber(value: Int) {
        _dummyLiveData.postIfChanged(value)
    }
}