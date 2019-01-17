package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences

class FloatSharedPrefsLiveData(
    sharedPreferences: SharedPreferences,
    defaultValue: Float,
    key: String
) : SharedPrefsLiveData<Float>(sharedPreferences, defaultValue, key) {

    override fun getPrefsValue(key: String, defValue: Float): Float {
        return sharedPreferences.getFloat(key, defValue)
    }
}