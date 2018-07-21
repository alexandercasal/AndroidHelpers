package com.alexandercasal.androidhelpers.sample.prefs

import android.content.Context
import com.alexandercasal.androidhelpers.sharedprefs.SharedPrefs

class SamplePrefs(context: Context, filename: String) : SharedPrefs(context, filename) {

    var togglePref by booleanPref(true)
    var countPref by intPref(5)
    var decimalPref by floatPref(5.5f)
    var wordPref by stringPref("Word")
}