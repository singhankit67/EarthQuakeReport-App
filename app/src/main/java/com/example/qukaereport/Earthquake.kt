package com.example.qukaereport
class Earthquake(magnitude: Double, location:String, timeInMilliseconds:Long,time:Long,url:String) {
    var mMagnitude:Double
    var lLocation:String? =null
    var tTimeInMilliseconds:Long
    var mtime:Long
    var uUrl:String? = null
    init{
        this.mMagnitude = magnitude
        this.lLocation = location
        this.tTimeInMilliseconds = timeInMilliseconds
        this.uUrl = url
        this.mtime = time
    }
}
//over here we made our own class because in a default class you can take only two parameters at a time but here we need more than two thats why we created our
//own class in which we have 4 parameters magnitude,location,time and url and in the init block we are passing the init method to call all the gettter functions
// to get the values
