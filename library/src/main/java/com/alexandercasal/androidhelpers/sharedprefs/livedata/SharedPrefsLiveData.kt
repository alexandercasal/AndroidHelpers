package com.alexandercasal.androidhelpers.sharedprefs.livedata

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

@Suppress("MemberVisibilityCanBePrivate")
abstract class SharedPrefsLiveData<T>(
    protected val sharedPreferences: SharedPreferences,
    private val defaultValue: T,
    val key: String
): LiveData<T>() {

    private val changeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == this.key) {
            value = getPrefsValue(key, defaultValue)
        }
    }

    abstract fun getPrefsValue(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getPrefsValue(key, defaultValue)
        sharedPreferences.registerOnSharedPreferenceChangeListener(changeListener)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(changeListener)
        super.onInactive()
    }
}