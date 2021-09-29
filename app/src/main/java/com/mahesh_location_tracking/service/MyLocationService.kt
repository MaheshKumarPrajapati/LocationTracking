package com.mahesh_location_tracking.service

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.mahesh_location_tracking.R
import com.mahesh_location_tracking.base.MyApp
import com.mahesh_location_tracking.model.LocationModel
import com.mahesh_location_tracking.pref.ReadLocation
import com.mahesh_location_tracking.pref.SaveLocation
import com.mahesh_location_tracking.ui.TrackMeActivity
import java.text.SimpleDateFormat
import java.util.*

class MyLocationService : Service() {
    private var mLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocationCB: LocationCallback? = null
    private var saveLocation: SaveLocation? = null
    private var readLocation: ReadLocation? = null
    private var locationModel: LocationModel? = null
    private var dateFormat: SimpleDateFormat? = null
    private val subLoc = ArrayList<LocationModel.Location>()
    override fun onCreate() {
        super.onCreate()
        saveLocation = SaveLocation(this)
        readLocation = ReadLocation(this)
        dateFormat = SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss 'Z'")
        mLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        if (readLocation!!.location != "") locationModel = Gson().fromJson(readLocation!!.location, LocationModel::class.java)
        subLoc.addAll(locationModel!!.locations!!)
        mLocationCB = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    Log.d("Service", "location error")
                    return
                }
                val locations = locationResult.lastLocation
                Log.d("Service locations : ", locations.latitude.toString() + ", " + locations.longitude)
                if (readLocation!!.location != null) locationModel = Gson().fromJson(readLocation!!.location, LocationModel::class.java)
                if (locationModel != null) {
                    if (locationModel!!.locations != null) {
                        val loc = LocationModel.Location()
                        loc.latitude = locations.latitude
                        loc.longitide = locations.longitude
                        loc.accuracy = locations.accuracy
                        loc.timestamp = dateFormat!!.format(Date())
                        subLoc.add(loc)
                        locationModel!!.locations = subLoc
                        saveLocation!!.saveLocation(Gson().toJson(locationModel))
                        sendBroadcast("run")
                        Log.d("service 1st loc:- ", subLoc[0].latitude.toString() + ",  " + subLoc[0].longitide)
                        Log.d("JSON:- ", readLocation!!.location)
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1001, showNotification())
        myLocation
        return START_STICKY
    }

    private val myLocation: Unit
        private get() {
            val locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000)
                    .setFastestInterval(5000)
                    .setMaxWaitTime(10000)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                stopSelf()
                return
            }
            mLocationProviderClient!!.requestLocationUpdates(locationRequest, mLocationCB, Looper.myLooper())
        }

    private fun showNotification(): Notification {
        val notiIntent = Intent(applicationContext, TrackMeActivity::class.java)
        val pIntent = PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificatoinBuilder = NotificationCompat.Builder(applicationContext,
                MyApp.NotificationChannelId)
                .setContentTitle("Location Updates")
                .setContentText("You Current Location Tracker Start")
                .setSmallIcon(R.drawable.ic_start)
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pIntent)
        return notificatoinBuilder.build()
    }

    fun sendBroadcast(run: String?) {
        val intent = Intent("run")
        intent.putExtra("run", run)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        mLocationProviderClient!!.removeLocationUpdates(mLocationCB)
    }
}