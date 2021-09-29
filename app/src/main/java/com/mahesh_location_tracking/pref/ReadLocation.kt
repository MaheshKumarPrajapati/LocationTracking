package com.mahesh_location_tracking.pref

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class ReadLocation(var ctx: Context) {
    var myprefs = "Location"
    var mode = Activity.MODE_PRIVATE
    var res = ""
    private val prefs: SharedPreferences
    val location: String
        get() {
            res = ""
            res = prefs.getString("loc", "")!!
            return res
        }
    val latitude: String
        get() {
            res = ""
            res = prefs.getString("latitude", "28.594790")!!
            return res
        }
    val longitude: String
        get() {
            res = ""
            res = prefs.getString("longitude", "77.323977")!!
            return res
        }
    val accuracy: String
        get() {
            res = ""
            res = prefs.getString("acc", "0.0")!!
            return res
        }
    val id: String
        get() {
            res = ""
            res = prefs.getString("id", "0")!!
            return res
        }
    val isService: Boolean
        get() {
            var r = false
            r = prefs.getBoolean("is_service_running", false)
            return r
        }

    init {
        prefs = ctx.getSharedPreferences(myprefs, mode)
    }
}