package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences
import com.alexandercasal.androidhelpers.sharedprefs.SharedPrefs
import org.threeten.bp.LocalTime

class StringSharedPrefsLiveData(
    sharedPreferences: SharedPreferences,
    defaultValue: String,
    key: String
) : SharedPrefsLiveData<String?>(sharedPreferences, defaultValue, key) {

    override fun getPrefsValue(key: String, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }
}

class LocalTimePrefsLiveData(
    sharedPreferences: SharedPreferences,
    defaultValue: LocalTime?,
    key: String
) : SharedPrefsLiveData<LocalTime?>(sharedPreferences, defaultValue, key) {

    override fun getPrefsValue(key: String, defValue: LocalTime?): LocalTime? {
        val timeString: String? = sharedPreferences.getString(key, defValue?.format(SharedPrefs.DateTimeUtil.localTimeFormatter))
        timeString ?: return null

        return SharedPrefs.DateTimeUtil.toLocalTime(timeString, defValue)
    }
}