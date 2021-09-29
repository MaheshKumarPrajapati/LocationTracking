package com.mahesh_location_tracking.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mahesh_location_tracking.R
import com.mahesh_location_tracking.adapter.LocationListAdapter
import com.mahesh_location_tracking.databinding.ActivityLocationsListBinding
import com.mahesh_location_tracking.model.LocationModel
import com.mahesh_location_tracking.pref.ReadLocation
import com.mahesh_location_tracking.pref.SaveLocation
import com.mahesh_location_tracking.util.Utils
import java.util.*

class LocationsListActivity : AppCompatActivity(), View.OnClickListener {
    var mBinding: ActivityLocationsListBinding? = null
    private var saveLocation: SaveLocation? = null
    private var dialog: Dialog? = null
    private var mAdapter: LocationListAdapter? = null
    private var readLocation: ReadLocation? = null
    private var locationModel: LocationModel? = null
    private val subLoc = ArrayList<LocationModel.Location>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_locations_list)
        initi()
    }

    private fun initi() {
        saveLocation = SaveLocation(this)
        readLocation = ReadLocation(this)
        setOther()
        setLocationData()
    }

    private fun setLocationData() {
        if (readLocation!!.location != "") {
            locationModel = Gson().fromJson(readLocation!!.location, LocationModel::class.java)
            subLoc.addAll(locationModel!!.locations!!)
            setData()
        }
    }

    private fun setOther() {
        mBinding!!.header.ivBack.setOnClickListener(this)
        mBinding!!.header.ivDelete.setOnClickListener(this)
    }

    private fun setData() {
        mBinding!!.tvId.text = getString(R.string.trip_id) + locationModel!!.tripId
        mBinding!!.tvStartTime.text = getString(R.string.trip_start_time) + locationModel!!.startTime
        mBinding!!.tvEndTime.text = getString(R.string.trip_end_time) + locationModel!!.endTime
        mAdapter = LocationListAdapter(this, subLoc)
        mBinding!!.rvLoc.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding!!.rvLoc.adapter = mAdapter
    }

    override fun onClick(v: View) {
        if (v == mBinding!!.header.ivBack) {
            finish()
        } else if (v == mBinding!!.header.ivDelete) {
            if (!readLocation!!.isService) showDeleteDialog() else Utils.showSnackbarMessage(this, findViewById(android.R.id.content), resources.getString(R.string.err_cant_delete))
        }
    }

    private fun showDeleteDialog() {
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val inflater = LayoutInflater.from(this)
        val v = inflater.inflate(R.layout.trip_delete, null)
        dialog!!.setContentView(v)
        dialog!!.setCancelable(true)
        val ok = dialog!!.findViewById<TextView>(R.id.tvBtnDelete)
        val cancel = dialog!!.findViewById<TextView>(R.id.tvBtnCancel)
        ok.setOnClickListener {
            dialog!!.dismiss()
            saveLocation!!.clearLocation(this@LocationsListActivity)
            setLocationData()
        }
        cancel.setOnClickListener { dialog!!.dismiss() }
        dialog!!.show()
    }
}