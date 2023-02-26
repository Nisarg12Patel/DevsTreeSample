package com.example.nisargpatel.database

interface POIRepository {
    fun insertPOI(placeid:Int,address:String,lattitude:Double,longitude:Double,City:String)
    fun updatePOI(placeid:Int,address:String,lattitude:Double,longitude:Double,City:String): Int
    fun deletePOI(placeid:Int): Int
}