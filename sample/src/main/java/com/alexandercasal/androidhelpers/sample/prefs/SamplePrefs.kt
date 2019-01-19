package com.alexandercasal.androidhelpers.sample.prefs

import android.content.Context
import com.alexandercasal.androidhelpers.sharedprefs.SharedPrefs

class SamplePrefs(context: Context, filename: String) : SharedPrefs(context, filename) {

    var togglePref by booleanPref(true, "togglePref")
    var countPref by intPref(5, "countPref")
    var decimalPref by floatPref(5.5f, "decimalPref")
    var wordPref by stringPref("Word", "wordPref")
}