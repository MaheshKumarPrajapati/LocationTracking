package com.mahesh_location_tracking.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.mahesh_location_tracking.R
import com.mahesh_location_tracking.databinding.ActivityTrackMeBinding
import com.mahesh_location_tracking.directionhelpers.FetchURL
import com.mahesh_location_tracking.directionhelpers.TaskLoadedCallback
import com.mahesh_location_tracking.model.LocationModel
import com.mahesh_location_tracking.pref.ReadLocation
import com.mahesh_location_tracking.pref.SaveLocation
import com.mahesh_location_tracking.service.MyLocationService
import com.mahesh_location_tracking.util.GetLatLng
import java.text.SimpleDateFormat
import java.util.*

class TrackMeActivity : AppCompatActivity(), View.OnClickListener, OnMapReadyCallback, OnCameraIdleListener, TaskLoadedCallback {
    var mBinding: ActivityTrackMeBinding? = null
    private var isAllFabsVisible = false
    var permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var isLocationRun = false
    private var saveLocation: SaveLocation? = null
    private var readLocation: ReadLocation? = null
    private var dateFormat: SimpleDateFormat? = null
    private val subLoc = ArrayList<LocationModel.Location>()
    private var getLatLng: GetLatLng? = null
    private var locationModel: LocationModel? = null
    private var currentPolyline: Polyline? = null
    var mapFragment: SupportMapFragment? = null
    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_track_me)
        initi()
    }

    private fun initi() {
        saveLocation = SaveLocation(this)
        readLocation = ReadLocation(this)
        setOther()
        setMap()
        closeFab()
        dateFormat = SimpleDateFormat("yyyy-MM-dd 'T' HH:mm:ss 'Z'")
        getLatLng = GetLatLng()
        getLatLng!!.getLatLng(this)
    }

    private fun setOther() {
        mBinding!!.addFab.setOnClickListener(this)
        mBinding!!.startFab.setOnClickListener(this)
        mBinding!!.stopFab.setOnClickListener(this)
        mBinding!!.listFab.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        if (view == mBinding!!.addFab) {
            if (!isAllFabsVisible) {
                mBinding!!.addFab.icon = resources.getDrawable(R.drawable.ic_close)
                mBinding!!.startFab.show()
                mBinding!!.stopFab.show()
                mBinding!!.listFab.show()
                mBinding!!.tvStart.visibility = View.VISIBLE
                mBinding!!.tvStop.visibility = View.VISIBLE
                mBinding!!.tvList.visibility = View.VISIBLE
                mBinding!!.addFab.extend()
                isAllFabsVisible = true
            } else {
                closeFab()
            }
        } else if (view == mBinding!!.startFab) {
            if (checkPermissions()) {
                if (!isLocationRun) {
                    startLocationService()
                    isLocationRun = true
                    closeFab()
                }
            }
        } else if (view == mBinding!!.stopFab) {
            if (isLocationRun) {
                stopLocationService()
                isLocationRun = false
                closeFab()
            }
        } else if (view == mBinding!!.listFab) {
            closeFab()
            startActivity(Intent(this, LocationsListActivity::class.java))
        }
    }

    private fun closeFab() {
        mBinding!!.addFab.icon = resources.getDrawable(R.drawable.ic_add)
        mBinding!!.startFab.hide()
        mBinding!!.stopFab.hide()
        mBinding!!.listFab.hide()
        mBinding!!.tvStart.visibility = View.GONE
        mBinding!!.tvStop.visibility = View.GONE
        mBinding!!.tvList.visibility = View.GONE
        mBinding!!.addFab.shrink()
        isAllFabsVisible = false
    }

    private fun startLocationService() {
        saveLocation!!.saveIsService(true)
        getLatLng!!.getLatLng(this)
        locationModel = LocationModel()
        saveLocation!!.saveId((readLocation!!.id.toInt() + 1).toString())
        locationModel!!.tripId = readLocation!!.id
        locationModel!!.startTime = dateFormat!!.format(Date())
        val loc = LocationModel.Location()
        loc.latitude = readLocation!!.latitude.toDouble()
        loc.longitide = readLocation!!.longitude.toDouble()
        loc.accuracy = readLocation!!.accuracy.toFloat()
        loc.timestamp = dateFormat!!.format(Date())
        subLoc.add(loc)
        locationModel!!.locations = subLoc
        Log.d("activity loc:- ", readLocation!!.latitude + ",  " + readLocation!!.longitude)
        saveLocation!!.saveLocation(Gson().toJson(locationModel))
        val intent = Intent(this, MyLocationService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun stopLocationService() {
        saveLocation!!.saveIsService(false)
        if (readLocation!!.location != "") {
            locationModel = Gson().fromJson(readLocation!!.location, LocationModel::class.java)
            if (locationModel != null) {
                locationModel!!.endTime = dateFormat!!.format(Date())
                saveLocation!!.saveLocation(Gson().toJson(locationModel))
            }
        }
        val intent = Intent(this, MyLocationService::class.java)
        stopService(intent)
    }

    // PERMISSION
    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), 100)
            return false
        }
        return true
    }

    // SET MAP
    private fun setMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in center and move the camera
        val yourlocation = LatLng(readLocation!!.latitude.toDouble(), readLocation!!.longitude.toDouble())
        currentMarker = mMap!!.addMarker(MarkerOptions().position(yourlocation).draggable(true).title("Starting Point"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(yourlocation, 15f))
        val mapUiSettings = mMap!!.uiSettings
        mapUiSettings.isCompassEnabled = true
        mapUiSettings.isRotateGesturesEnabled = true
        mapUiSettings.isMyLocationButtonEnabled = true
        mapUiSettings.setAllGesturesEnabled(true)
        mapUiSettings.isZoomControlsEnabled = true // Enable the zoom controls for the map
        mMap!!.setOnCameraIdleListener(this)
    }

    override fun onCameraIdle() {}
    private val m: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, i: Intent) {
            // TODO Auto-generated method stub
            val run = i.getStringExtra("run")
            val startLatLng: LatLng
            val endLatLng: LatLng
            if (run != null) {
                if (run.equals("run", ignoreCase = true)) {
                    if (readLocation!!.location != "") {
                        locationModel = Gson().fromJson(readLocation!!.location, LocationModel::class.java)
                        if (locationModel!!.locations!!.size == 0) {
                            startLatLng = LatLng(locationModel!!.locations!![0].latitude!!, locationModel!!.locations!![0].longitide!!)
                            endLatLng = LatLng(locationModel!!.locations!![0].latitude!!, locationModel!!.locations!![0].longitide!!)
                        } else {
                            startLatLng = LatLng(locationModel!!.locations!![0].latitude!!, locationModel!!.locations!![0].longitide!!)
                            endLatLng = LatLng(locationModel!!.locations!![locationModel!!.locations!!.size - 1].latitude!!, locationModel!!.locations!![locationModel!!.locations!!.size - 1].longitide!!)
                        }
                        val url = getUrl(startLatLng, endLatLng, "driving")
                        FetchURL(this@TrackMeActivity).execute(url, "driving")
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(m, IntentFilter("run"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(m)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(m)
    }

    private fun getUrl(origin: LatLng, dest: LatLng, directionMode: String): String {
        // Origin of route
        val str_origin = "origin=${origin.latitude},${origin.longitude}"
        // Destination of route
        val str_dest = "destination=${dest.latitude},${dest.longitude}"
        // Mode
        val mode = "mode=$directionMode"
        // Building the parameters to the web service
        val parameters = "$str_origin&$str_dest&$mode"
        // Output format
        val output = "json"
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/${output}?${parameters}&key=${getString(R.string.google_maps_key)}"
    }

    override fun onTaskDone(vararg values: Any?) {
        if (currentPolyline != null) currentPolyline!!.remove()
        currentPolyline = mMap!!.addPolyline(values[0] as PolylineOptions)
    }
}