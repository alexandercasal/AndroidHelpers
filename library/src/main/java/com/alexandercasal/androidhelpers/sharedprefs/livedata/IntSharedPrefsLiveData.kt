package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences

class IntSharedPrefsLiveData(
    sharedPreferences: SharedPreferences,
    defaultValue: Int,
    key: String
) : SharedPrefsLiveData<Int>(sharedPreferences, defaultValue, key) {

    override fun getPrefsValue(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }
}