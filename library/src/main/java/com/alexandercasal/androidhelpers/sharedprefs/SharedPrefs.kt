package com.alexandercasal.androidhelpers.sharedprefs

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class SharedPrefs(private val context: Context, private val fileName: String) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    private fun getPrefKey(key: String?, property: KProperty<*>): String {
        return when {
            key != null -> key
            else -> property.name
        }
    }

    fun intPref(defValue: Int = 0, key: String? = null) = Pref(defValue, key)
    fun booleanPref(defValue: Boolean = false, key: String? = null) = Pref(defValue, key)
    fun floatPref(defValue: Float = 0f, key: String? = null) = Pref(defValue, key)
    fun longPref(defValue: Long = 0L, key: String? = null) = Pref(defValue, key)
    fun stringPref(defValue: String = "", key: String? = null) = Pref(defValue, key)

    inner class Pref<T>(private val defValue: T, val key: String? = null) : ReadWriteProperty<SharedPrefs, T> {
        var value: T = defValue

        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        override fun getValue(thisRef: SharedPrefs, property: KProperty<*>): T {
            val prefKey = getPrefKey(key, property)

            return when (defValue) {
                is Boolean -> sharedPreferences.getBoolean(prefKey, defValue)
                is Float -> sharedPreferences.getFloat(prefKey, defValue)
                is Int -> sharedPreferences.getInt(prefKey, defValue)
                is Long -> sharedPreferences.getLong(prefKey, defValue)
                is String -> sharedPreferences.getString(prefKey, defValue)
                else -> error("Invalid type for SharedPreferences: $defValue")
            } as T
        }

        override fun setValue(thisRef: SharedPrefs, property: KProperty<*>, value: T) {
            this.value = value
            val prefKey = getPrefKey(key, property)
            val editor = sharedPreferences.edit()

            when (value) {
                is Boolean -> editor.putBoolean(prefKey, value)
                is Float -> editor.putFloat(prefKey, value)
                is Int -> editor.putInt(prefKey, value)
                is Long -> editor.putLong(prefKey, value)
                is String -> editor.putString(prefKey, value)
                else -> error("Invalid type for SharedPreferences: $value")
            }
            editor.apply()
        }
    }

}