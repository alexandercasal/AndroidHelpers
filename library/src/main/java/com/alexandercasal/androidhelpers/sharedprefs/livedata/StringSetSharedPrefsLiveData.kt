package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences

class StringSetSharedPrefsLiveData(
    sharedPreferences: SharedPreferences,
    defaultValue: Set<String>?,
    key: String
) : SharedPrefsLiveData<Set<String>?>(sharedPreferences, defaultValue, key) {

    override fun getPrefsValue(key: String, defValue: Set<String>?): Set<String>? {
        return sharedPreferences.getStringSet(key, defValue)
    }
}