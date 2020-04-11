package com.alexandercasal.androidhelpers.sample

import android.content.Context
import com.alexandercasal.androidhelpers.sharedprefs.SharedPrefs
import java.time.LocalTime

class SamplePrefs(context: Context, filename: String) : SharedPrefs(context, filename) {

    private val delegates = Delegates()

    var demoIntPref by delegates.demoIntPrefDelegate
    val demoIntPrefLiveData = delegates.demoIntPrefDelegate.toLiveData()

    var demoBooleanPref by delegates.demoBooleanPrefDelegate
    val demoBooleanPrefLiveData = delegates.demoBooleanPrefDelegate.toLiveData()

    var demoFloatPref by delegates.demoFloatPrefDelegate
    val demoFloatPrefLiveData = delegates.demoFloatPrefDelegate.toLiveData()

    var demoLongPref by delegates.demoLongPrefDelegate
    val demoLongPrefLiveData = delegates.demoLongPrefDelegate.toLiveData()

    var demoStringPref by delegates.demoStringPrefDelegate
    val demoStringPrefLiveData = delegates.demoStringPrefDelegate.toLiveData()

    var demoStringSetPref by delegates.demoStringSetPrefDelegate
    val demoStringSetPrefLiveData = delegates.demoStringSetPrefDelegate.toLiveData()

    var demoLocalTimePref by delegates.demoLocalTimePrefDelegate
    val demoLocalTimePrefLiveData = delegates.demoLocalTimePrefDelegate.toLiveData()

    inner class Delegates {
        val demoIntPrefDelegate = intPref(0, "demoIntPref)")
        val demoBooleanPrefDelegate = booleanPref(false, "demoBooleanPref")
        val demoFloatPrefDelegate = floatPref(0f, "demoFloatPref")
        val demoLongPrefDelegate = longPref(0L, "demoLongPref")
        val demoStringPrefDelegate = stringPref("", "demoStringPref")
        val demoStringSetPrefDelegate = StringSetPref(emptySet(), "demoStringSetPref")
        val demoLocalTimePrefDelegate = LocalTimePref(LocalTime.now(), "demoLocalTimePref")
    }
}