package com.mahesh_location_tracking.pref

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SaveLocation(var ctx: Context) {
    var myprefs = "Location"
    var mode = Activity.MODE_PRIVATE
    var result = false
    private var prefs: SharedPreferences
    fun clearLocation(ctx: Context) {
        prefs = ctx.getSharedPreferences(myprefs, mode)
        prefs.edit().clear().commit()
    }

    fun saveLocation(s: String?) {
        result = false
        val editor = prefs.edit()
        editor.putString("loc", s)
        result = editor.commit()
    }

    fun saveLatitude(s: String?) {
        result = false
        val editor = prefs.edit()
        editor.putString("latitude", s)
        result = editor.commit()
    }

    fun saveLongitude(s: String?) {
        result = false
        val editor = prefs.edit()
        editor.putString("longitude", s)
        result = editor.commit()
    }

    fun saveAccuracy(s: String?) {
        result = false
        val editor = prefs.edit()
        editor.putString("add", s)
        result = editor.commit()
    }

    fun saveId(s: String?) {
        result = false
        val editor = prefs.edit()
        editor.putString("id", s)
        result = editor.commit()
    }

    fun saveIsService(s: Boolean) {
        result = false
        val editor = prefs.edit()
        editor.putBoolean("is_service_running", s)
        result = editor.commit()
    }

    init {
        prefs = ctx.getSharedPreferences(myprefs, mode)
    }
}