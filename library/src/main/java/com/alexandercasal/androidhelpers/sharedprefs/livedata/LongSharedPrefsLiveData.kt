package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences

class LongSharedPrefsLiveData(
    sharedPreferences: SharedPreferences,
    defaultValue: Long,
    key: String
) : SharedPrefsLiveData<Long>(sharedPreferences, defaultValue, key) {

    override fun getPrefsValue(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }
}