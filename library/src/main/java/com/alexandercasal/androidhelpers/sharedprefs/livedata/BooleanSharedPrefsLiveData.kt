package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences

class BooleanSharedPrefsLiveData(
    sharedPreferences: SharedPreferences,
    defaultValue: Boolean,
    key: String
) : SharedPrefsLiveData<Boolean>(sharedPreferences, defaultValue, key) {

    override fun getPrefsValue(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }
}