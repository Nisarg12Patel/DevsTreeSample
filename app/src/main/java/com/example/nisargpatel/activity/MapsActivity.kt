package com.example.nisargpatel.activity

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.nisargpatel.R
import com.example.nisargpatel.database.ContentResolverPOIRepository
import com.example.nisargpatel.database.DatabaseOpenHelper
import com.example.nisargpatel.databinding.ActivityMapsBinding
import com.example.nisargpatel.model.LocationData
import com.example.nisargpatel.model.MapData
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var mLatLong: LatLng? = null
    private var city: String? = null
    private var address: String? = null
    private var gcd: Geocoder? = null
    private var myObject: LocationData? = null
    lateinit var locationList: ArrayList<LocationData>
    var flag: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DatabaseOpenHelper(this, null, null, 1).getDatabase(this)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.api_key), Locale.US);
        }
        mapFragment.getMapAsync(this)

        if (intent.getIntExtra("count", 1) == 1) {
            flag = 1
        } else if (intent.getIntExtra("count", 2) == 2) {
            flag = 2
        }

        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                mLatLong = place.latLng
                val cameraPosition = CameraPosition.Builder()
                    .target(
                        LatLng(
                            mLatLong!!.latitude,
                            mLatLong!!.longitude
                        )
                    )
                    .zoom(12f)
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                gcd = Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val address1: List<Address>? =
                        gcd!!.getFromLocation(mLatLong!!.latitude, mLatLong!!.longitude, 1)
                    city = address1!![0].locality
                    address = address1[0].getAddressLine(0)

                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val currentMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(mLatLong!!)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        .title(address)
                )
                currentMarker!!.showInfoWindow()
                binding.layoutSave.visibility = View.VISIBLE
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })

        binding.btnLocationSave.setOnClickListener {
            if (flag == 1) {
                myObject = (intent.getSerializableExtra("item_data") as LocationData?)!!
                val count = ContentResolverPOIRepository(this, database).updatePOI(
                    myObject!!.place_id,
                    address!!,
                    mLatLong!!.latitude,
                    mLatLong!!.longitude,
                    city!!
                )
            } else {
                ContentResolverPOIRepository(this, database).insertPOI(
                    0, address!!,
                    mLatLong!!.latitude,
                    mLatLong!!.longitude,
                    city!!
                )
            }

            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (flag == 1) {
            myObject = (intent.getSerializableExtra("item_data") as LocationData?)!!
            mapdraw(myObject!!)
        } else if (flag == 2) {
            locationList = (intent.getSerializableExtra("item_data") as ArrayList<LocationData>?)!!
            val newList = arrayListOf<LatLng>()
            for (item in locationList) {
                newList.add(LatLng(item.latitude!!, item.longitude!!))
                val currentMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(item.latitude!!, item.longitude!!))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
                currentMarker!!.showInfoWindow()
                if (item.place_id == intent.getIntExtra("place_id", 1)) {
                    val cameraPosition = CameraPosition.Builder()
                        .target(
                            LatLng(
                                item.latitude!!,
                                item.longitude!!
                            )
                        )
                        .zoom(12f)
                        .build()
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
            for ((index, value) in newList.withIndex()) {
                println("the element at $index is $value")
                if(index != newList.size-1){
                    val urll = getDirectionURL(LatLng(newList[index].latitude,newList[index].longitude), LatLng(newList[index+1].latitude,newList[index+1].longitude),"AIzaSyBSNyp6GQnnKlrMr7hD2HGiyF365tFlK5U")
                    GetDirection(urll).execute()
                }
            }
        }
    }

    fun mapdraw(newdata: LocationData) {
        val cameraPosition = CameraPosition.Builder()
            .target(
                LatLng(
                    newdata.latitude!!,
                    newdata.longitude!!
                )
            )
            .zoom(12f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        val currentMarker = mMap.addMarker(
            MarkerOptions()
                .position(LatLng(newdata.latitude!!, newdata.longitude!!))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(newdata.address)
        )
        currentMarker!!.showInfoWindow()
    }

    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }
}