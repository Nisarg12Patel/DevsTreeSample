package com.example.nisargpatel.activity

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nisargpatel.R
import com.example.nisargpatel.`interface`.AllItemClickListener
import com.example.nisargpatel.`interface`.DeleteItemClickListener
import com.example.nisargpatel.`interface`.MyItemClickListener
import com.example.nisargpatel.adapter.LocationAdapter
import com.example.nisargpatel.database.ContentResolverPOIRepository
import com.example.nisargpatel.database.DatabaseOpenHelper
import com.example.nisargpatel.databinding.ActivityLocationListBinding
import com.example.nisargpatel.model.LocationData
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

class LocationListActivity : AppCompatActivity(), MyItemClickListener, DeleteItemClickListener,
    AllItemClickListener {

    lateinit var locationAdapter: LocationAdapter
    private var TABLE_NAME: String = "poi"
    lateinit var db: SQLiteDatabase
    var locationListBinding: ActivityLocationListBinding? = null
    var selecteditem = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)

        db = DatabaseOpenHelper(this, null, null, 1).getDatabase(this)

        locationListBinding = DataBindingUtil.setContentView(this, R.layout.activity_location_list);

        val buttonClick = findViewById<Button>(R.id.add_button)

        buttonClick.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("count", 0)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (readCourses().size > 0) {
            locationAdapter = LocationAdapter(readCourses(), this, this, this)
            locationListBinding?.list?.layoutManager = LinearLayoutManager(this)
            locationListBinding?.list?.adapter = locationAdapter
        }
    }

    private fun readCourses(): ArrayList<LocationData> {

        val cursorCourses: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val courseModalArrayList: ArrayList<LocationData> = ArrayList()

        if (cursorCourses.moveToFirst()) {
            do {
                courseModalArrayList.add(LocationData(
                    cursorCourses.getInt(0),
                        cursorCourses.getString(1),
                        cursorCourses.getDouble(2),
                        cursorCourses.getDouble(3),
                        cursorCourses.getString(4),
                    )
                )
            } while (cursorCourses.moveToNext())
        }
        cursorCourses.close()
        for ((index, value) in courseModalArrayList.withIndex()) {
            courseModalArrayList[index].distance=distance(courseModalArrayList[0].latitude,
                courseModalArrayList[0].longitude,
                value.latitude,
                value.longitude)
        }
        return courseModalArrayList
    }

    override fun onItemClicked(data: LocationData) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("item_data", data)
        intent.putExtra("count", 1)
        startActivity(intent)
    }

    override fun onDeleteClicked(position: Int) {
        val count = ContentResolverPOIRepository(this, db).deletePOI(position)
        if (count >= 1) {
        filldata(readCourses())
        }
    }

    fun filldata(locationdata:ArrayList<LocationData>)
    {

        locationAdapter = LocationAdapter(locationdata, this, this, this)
        locationListBinding?.list?.adapter = locationAdapter;
        locationAdapter.notifyDataSetChanged()
    }



    override fun allItemCLick(position: ArrayList<LocationData>, placeid: Int) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra("item_data", position)
        intent.putExtra("place_id", placeid)
        intent.putExtra("count", 2)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.locationsort -> {
                // Create and show the BottomSheetDialog
                val dialog = BottomSheetDialog(this)
                dialog.setContentView(R.layout.bottom_sheet_dialog_layout)
                dialog.show()

                val closeButton = dialog.findViewById<ImageView>(R.id.closebtn)
                val radioAsc = dialog.findViewById<RadioButton>(R.id.radioAsc)
                val radioDesc = dialog.findViewById<RadioButton>(R.id.radioDesc)
                closeButton?.setOnClickListener {
                    dialog.dismiss()
                }
                radioAsc?.setOnClickListener {
                    val yourList: List<LocationData> = readCourses()
                    val sortedlocationdata: List<LocationData> = yourList.sortedBy { it.distance }
                    val arrayList = ArrayList(sortedlocationdata)
                    filldata(arrayList)
                    selecteditem = "ascending"
                    dialog.dismiss()
                }
                radioDesc?.setOnClickListener {
                    selecteditem = "descending"
                    val yourList: List<LocationData> = readCourses()
                    val sortedlocationdata: List<LocationData> = yourList.sortedByDescending { it.distance }
                    val arrayList = ArrayList(sortedlocationdata)
                    filldata(arrayList)

                    dialog.dismiss()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun distance(latitude: Double?, longitude: Double?, latitude1: Double?, longitude1: Double?): Double? {
        return calculateDistance(
            latitude!!,
            longitude!!,
            latitude1!!,
            longitude1!!
        )
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) *
                    Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return (r * c * 100.0).roundToInt() / 100.0 // Distance in km
    }
}