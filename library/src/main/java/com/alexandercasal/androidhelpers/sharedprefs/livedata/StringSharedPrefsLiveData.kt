package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences

class StringSharedPrefsLiveData(
    sharedPreferences: SharedPreferences,
    defaultValue: String,
    key: String
) : SharedPrefsLiveData<String?>(sharedPreferences, defaultValue, key) {

    override fun getPrefsValue(key: String, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }
}