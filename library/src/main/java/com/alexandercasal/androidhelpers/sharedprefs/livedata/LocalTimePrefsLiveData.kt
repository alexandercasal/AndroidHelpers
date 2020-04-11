package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences
import com.alexandercasal.androidhelpers.sharedprefs.SharedPrefs
import java.time.LocalTime

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