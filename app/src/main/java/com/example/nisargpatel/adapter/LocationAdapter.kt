package com.example.nisargpatel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nisargpatel.*
import com.example.nisargpatel.`interface`.AllItemClickListener
import com.example.nisargpatel.`interface`.DeleteItemClickListener
import com.example.nisargpatel.`interface`.MyItemClickListener
import com.example.nisargpatel.databinding.ListItemBinding
import com.example.nisargpatel.model.LocationData
import kotlin.math.roundToInt

class LocationAdapter(
    var locationList: ArrayList<LocationData>,
    private val listener: MyItemClickListener,
    private val deletelistener: DeleteItemClickListener,
    private val allItemClickListener: AllItemClickListener
) :
    RecyclerView.Adapter<ProductAdapterViewHolder>() {

    private var filteredList = locationList?.toMutableList()
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapterViewHolder {
        val binding: ListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item, parent, false
        )
        context = parent.context
        return ProductAdapterViewHolder(binding).also {
            it.binding.root.setOnClickListener {
            }
        }
    }

    override fun onBindViewHolder(holder: ProductAdapterViewHolder, position: Int) {
        val user = locationList[position]
        holder.binding.locationAddress.text = user.address ?: ""
        holder.binding.locationName.text = user.City ?: ""
        holder.binding.locationUpdate.setOnClickListener {
            if (user != null) {
                listener.onItemClicked(user)
            }
        }
        holder.binding.locationDelete.setOnClickListener {
            if (user != null) {
                deletelistener.onDeleteClicked(user.place_id)
            }
        }

        holder.binding.locationDistance.text = "Distance :" + user.distance + " Kms"

        holder.itemView.setOnClickListener {
            allItemClickListener.allItemCLick(locationList, user.place_id)
        }
    }

    override fun getItemCount() = filteredList!!.size

}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371 // Radius of the earth in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    val roundoff = (r * c * 100.0).roundToInt() / 100.0
    return roundoff // Distance in km
}

class ProductAdapterViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)