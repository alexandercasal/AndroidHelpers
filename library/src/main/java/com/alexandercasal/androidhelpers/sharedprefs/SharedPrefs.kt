package com.alexandercasal.androidhelpers.sharedprefs

import android.content.Context
import androidx.lifecycle.LiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.BooleanSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.FloatSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.IntSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.LocalTimePrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.LongSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.StringSetSharedPrefsLiveData
import com.alexandercasal.androidhelpers.sharedprefs.livedata.StringSharedPrefsLiveData
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class SharedPrefs(private val context: Context, private val fileName: String) {

    private val sharedPreferences by lazy {
        context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }

    protected fun intPref(defValue: Int = 0, key: String) = Pref(defValue, key)
    protected fun booleanPref(defValue: Boolean = false, key: String) = Pref(defValue, key)
    protected fun floatPref(defValue: Float = 0f, key: String) = Pref(defValue, key)
    protected fun longPref(defValue: Long = 0L, key: String) = Pref(defValue, key)
    protected fun stringPref(defValue: String = "", key: String) = Pref(defValue, key)

    @Suppress("MemberVisibilityCanBePrivate")
    inner class Pref<T>(val defValue: T, val key: String) : ReadWriteProperty<SharedPrefs, T> {
        private var cacheValue: T = getValueInternal()

        override fun getValue(thisRef: SharedPrefs, property: KProperty<*>): T {
            return getValueInternal()
        }

        @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
        private fun getValueInternal(): T {
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
            if (this.cacheValue != value) {
                this.cacheValue = value
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
        private var cacheValue: Set<String>? = sharedPreferences.getStringSet(key, defValue)

        override fun getValue(thisRef: SharedPrefs, property: KProperty<*>): Set<String>? {
            return sharedPreferences.getStringSet(key, defValue)
        }

        override fun setValue(thisRef: SharedPrefs, property: KProperty<*>, value: Set<String>?) {
            if (this.cacheValue != value) {
                this.cacheValue = value
                sharedPreferences.edit().putStringSet(key, value).apply()
            }
        }

        fun toLiveData(): LiveData<Set<String>?> = StringSetSharedPrefsLiveData(sharedPreferences, defValue, key)
    }

    /**
     * Manages storing and retrieving a [LocalTime][org.threeten.bp.LocalTime] directly as a shared preference.
     * The LocalTime will be stored as a [String] formatted in [ISO_LOCAL_TIME][org.threeten.bp.format.DateTimeFormatter.ISO_LOCAL_TIME].
     *
     * While malformatted time strings will be guarded against, any indirect operations on this preference that result in a differently
     * formatted String may result in unexpected behavior.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    inner class LocalTimePref(val defValue: LocalTime?, val key: String) : ReadWriteProperty<SharedPrefs, LocalTime?> {
        private var cacheValue: LocalTime? = getValueInternal(defValue)

        override fun getValue(thisRef: SharedPrefs, property: KProperty<*>): LocalTime? {
            return getValueInternal(cacheValue)
        }

        private fun getValueInternal(fallback: LocalTime?): LocalTime? {
            val timeString: String? = sharedPreferences.getString(key, fallback?.format(DateTimeUtil.localTimeFormatter))
            timeString ?: return null

            return DateTimeUtil.toLocalTime(timeString, fallback)
        }

        override fun setValue(thisRef: SharedPrefs, property: KProperty<*>, value: LocalTime?) {
            if (this.cacheValue != value) {
                this.cacheValue = value
                sharedPreferences.edit().putString(key, value?.format(DateTimeUtil.localTimeFormatter)).apply()
            }
        }

        fun toLiveData(): LiveData<LocalTime?> = LocalTimePrefsLiveData(sharedPreferences, defValue, key)
    }

    internal object DateTimeUtil {

        val localTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

        fun toLocalTime(time: String?, fallback: LocalTime?): LocalTime? {
            return try {
                LocalTime.parse(time, localTimeFormatter)
            } catch (e: Exception) {
                Timber.e(e, "Malformatted LocalTime string: $time")
                fallback // fall back to the last known good LocalTime
            }
        }
    }
}