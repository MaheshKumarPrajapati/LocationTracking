package com.mahesh_location_tracking.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LocationModel {
    @SerializedName("trip_id")
    @Expose
    var tripId: String? = null

    @SerializedName("start_time")
    @Expose
    var startTime: String? = null

    @SerializedName("end_time")
    @Expose
    var endTime: String? = null

    @SerializedName("locations")
    @Expose
    var locations: List<Location>? = null

    //    -----------------------------------com.example.Location.java-----------------------------------
    class Location {
        @SerializedName("latitude")
        @Expose
        var latitude: Double? = null

        @SerializedName("longitide")
        @Expose
        var longitide: Double? = null

        @SerializedName("timestamp")
        @Expose
        var timestamp: String? = null

        @SerializedName("accuracy")
        @Expose
        var accuracy: Float? = null
    }
}