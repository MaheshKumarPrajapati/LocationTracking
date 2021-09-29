package com.mahesh_location_tracking.ui

import android.Manifest
import android.content.Intent
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mahesh_location_tracking.R
import com.mahesh_location_tracking.util.GetLatLng
import com.mahesh_location_tracking.util.Utils

class SplashActivity : AppCompatActivity() {
    var permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    )
    private var getLatLng: GetLatLng? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        if (Utils.checkPermissions(this, this@SplashActivity, permissions)) {
            val manager = getSystemService(LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Utils.buildAlertMessageNoGps(this)
            } else {
                val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val ni = connectivityManager.activeNetworkInfo
                if (ni != null && ni.isAvailable && ni.isConnectedOrConnecting) {
                    if (getLatLng != null) getLatLng!!.getLatLng(this) else {
                        getLatLng = GetLatLng()
                        getLatLng!!.getLatLng(this)
                    }
                    Handler().postDelayed({
                        if (!this@SplashActivity.isDestroyed && !this@SplashActivity.isFinishing) {
                            startActivity(Intent(this@SplashActivity, TrackMeActivity::class.java))
                            finish()
                        }
                    }, 1000)
                } else {
                    Utils.checkInternetSnackbar(this, this@SplashActivity, findViewById(android.R.id.content))
                }
            }
        } else Utils.checkPermissions(this, this@SplashActivity, permissions)
    }
}