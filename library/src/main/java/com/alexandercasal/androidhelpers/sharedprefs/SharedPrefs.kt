package com.alexandercasal.androidhelpers.sharedprefs

import android.content.Context
import androidx.lifecycle.LiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.BooleanSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.FloatSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.IntSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.LongSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.StringSetSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.StringSharedPrefsLiveData
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class SharedPrefs(private val context: Context, private val fileName: String) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    fun intPref(defValue: Int = 0, key: String) = Pref(defValue, key)
    fun booleanPref(defValue: Boolean = false, key: String) = Pref(defValue, key)
    fun floatPref(defValue: Float = 0f, key: String) = Pref(defValue, key)
    fun longPref(defValue: Long = 0L, key: String) = Pref(defValue, key)
    fun stringPref(defValue: String = "", key: String) = Pref(defValue, key)

    @Suppress("MemberVisibilityCanBePrivate")
    inner class Pref<T>(val defValue: T, val key: String) : ReadWriteProperty<SharedPrefs, T> {
        var value: T = defValue

        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        override fun getValue(thisRef: SharedPrefs, property: KProperty<*>): T {
            return when (defValue) {
                is Boolean -> sharedPreferences.getBoolean(key, defValue)
                is Float -> sharedPreferences.getFloat(key, defValue)
                is Int -> sharedPreferences.getInt(key, defValue)
                is Long -> sharedPreferences.getLong(key, defValue)
                is String -> sharedPreferences.getString(key, defValue)
                else -> error("Invalid type for SharedPreferences: $defValue")
            } as T
        }

        override fun setValue(thisRef: SharedPrefs, property: KProperty<*>, value: T) {
            if (this.value != value) {
                this.value = value
                val editor = sharedPreferences.edit()

                when (value) {
                    is Boolean -> editor.putBoolean(key, value)
                    is Float -> editor.putFloat(key, value)
                    is Int -> editor.putInt(key, value)
                    is Long -> editor.putLong(key, value)
                    is String -> editor.putString(key, value)
                    else -> error("Invalid type for SharedPreferences: $value")
                }
                editor.apply()
            }
        }

        @Suppress("UNCHECKED_CAST")
        fun toLiveData(): LiveData<T> {
            return when (defValue) {
                is Int -> IntSharedPrefsLiveData(sharedPreferences, defValue, key)
                is Boolean -> BooleanSharedPrefsLiveData(sharedPreferences, defValue, key)
                is Float -> FloatSharedPrefsLiveData(sharedPreferences, defValue, key)
                is Long -> LongSharedPrefsLiveData(sharedPreferences, defValue, key)
                is String -> StringSharedPrefsLiveData(sharedPreferences, defValue, key)
                else -> throw IllegalArgumentException("Unsupported type for SharedPrefsLiveData: $defValue")
            } as LiveData<T>
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    inner class StringSetPref(val defValue: Set<String>?, val key: String) : ReadWriteProperty<SharedPrefs, Set<String>?> {
        var value: Set<String>? = defValue

        override fun getValue(thisRef: SharedPrefs, property: KProperty<*>): Set<String>? {
            return sharedPreferences.getStringSet(key, defValue)
        }

        override fun setValue(thisRef: SharedPrefs, property: KProperty<*>, value: Set<String>?) {
            if (this.value != value) {
                this.value = value
                sharedPreferences.edit().putStringSet(key, value).apply()
            }
        }

        fun toLiveData(): LiveData<Set<String>?> = StringSetSharedPrefsLiveData(sharedPreferences, defValue, key)
    }
}