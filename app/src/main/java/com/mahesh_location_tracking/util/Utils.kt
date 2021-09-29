package com.mahesh_location_tracking.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mahesh_location_tracking.R
import java.util.*

object Utils {
    private const val WIFI_ENABLE_REQUEST = 0x1006

    // show snackbar messages
    fun showSnackbarMessage(context: Context?, parentView: View?, msg: String?) {
        val snackbar = Snackbar.make(parentView!!, msg!!, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        val textView = snackbarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        textView.maxLines = 5
        snackbar.show()
    }

    fun checkPermissions(context: Context?, activity: Activity?, permissions: Array<String>): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(context!!, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity!!, listPermissionsNeeded.toTypedArray(), 100)
            return false
        }
        return true
    }

    fun buildAlertMessageNoGps(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(context.resources.getString(R.string.gps_disable))
                .setCancelable(false)
                .setPositiveButton(context.resources.getString(R.string.Yes)) { dialog: DialogInterface?, id: Int -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
        val alert = builder.create()
        alert.show()
    }

    // show snackbar messages
    fun checkInternetSnackbar(context: Context?, activity: Activity, parentView: View?): Snackbar {
        val snackbar = Snackbar.make(parentView!!, R.string.internet_msg, Snackbar.LENGTH_INDEFINITE)
        if (parentView is CoordinatorLayout) {
            val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
            params.setMargins(0, 0, 0, 130)
            snackbar.view.layoutParams = params
        }
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        val textView = snackbarView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        textView.maxLines = 5
        snackbar.setAction("RETRY") { view: View? ->
            val gpsOptionsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
            activity.startActivityForResult(gpsOptionsIntent, WIFI_ENABLE_REQUEST)
        }
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        snackbar.show()
        return snackbar
    }
}