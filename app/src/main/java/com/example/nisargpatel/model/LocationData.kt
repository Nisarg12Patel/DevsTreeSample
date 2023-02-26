package com.example.nisargpatel.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class LocationData: Serializable {
    @SerializedName("place_id")
    @Expose
    var place_id: Int = 0

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null

    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null

    @SerializedName("City")
    @Expose
    var City: String? = null

    @SerializedName("City")
    @Expose
    var distance: Double? = null

    // constructor
    constructor(
        place_id: Int,
        address: String,
        latitude: Double,
        longitude: Double,
        City: String
    ) {
        this.place_id = place_id
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.City = City
    }
}
