package com.mahesh_location_tracking.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mahesh_location_tracking.R
import com.mahesh_location_tracking.databinding.LayoutLocationTripDataBinding
import com.mahesh_location_tracking.model.LocationModel
import java.util.*


class LocationListAdapter(private val context: Context, private val subLoc: ArrayList<LocationModel.Location>) : RecyclerView.Adapter<LocationListAdapter.LocationListVH>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LocationListVH {
        val mBinding: LayoutLocationTripDataBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.context),
                R.layout.layout_location_trip_data, viewGroup, false)
        return LocationListVH(mBinding)
    }

    override fun onBindViewHolder(holder: LocationListVH, position: Int) {
        if (subLoc[position].latitude != null) {
            holder.mBinding.tvLLat.text = "${context.resources.getString(R.string.loc_lat)} ${subLoc[position].latitude}"
        }
        if (subLoc[position].longitide != null) {
            holder.mBinding.tvLLong.text = "${context.resources.getString(R.string.loc_long)} ${subLoc[position].longitide}"
        }
        if (subLoc[position].accuracy != null) {
            holder.mBinding.tvLAcc.text = "${context.resources.getString(R.string.loc_acc) } ${subLoc[position].accuracy}"
        }
        if (subLoc[position].timestamp != null) {
            holder.mBinding.tvLTimeS.text = "${context.resources.getString(R.string.loc_time_stamp) } ${ subLoc[position].timestamp}"
        }
    }

    override fun getItemCount(): Int {
        return subLoc.size
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    class LocationListVH(var mBinding: LayoutLocationTripDataBinding) : RecyclerView.ViewHolder(mBinding.root)
}
