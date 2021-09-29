package com.mahesh_location_tracking.util

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.mahesh_location_tracking.pref.SaveLocation

class GetLatLng : LocationListener {
    var context: Context? = null
    fun getLatLng(context: Context) {
        this.context = context
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps(context)
        } else {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                } else manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        if (location != null) {
            val lat = location.latitude
            val lon = location.longitude
            val acc = location.accuracy
            Log.e("Base Location" + " loc", "$lat $lon $acc")
            val saveLocation = SaveLocation(context!!)
            saveLocation.saveLatitude(lat.toString())
            saveLocation.saveLongitude(lon.toString())
            saveLocation.saveAccuracy(acc.toString())
        }
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {
        Toast.makeText(context, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {
        private fun buildAlertMessageNoGps(context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            val alert = builder.create()
            alert.show()
        }
    }
}