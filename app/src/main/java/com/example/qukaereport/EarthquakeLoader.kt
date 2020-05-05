package com.example.qukaereport

import android.content.Context
import android.util.Log
import androidx.loader.content.AsyncTaskLoader

class EarthquakeLoader (context: Context, url:String) :
    AsyncTaskLoader<List<Earthquake>>(context) {
    val LOG_TAG =MainActivity::class.java!!.getName()
    var mUrl:String? = null
    init {
        mUrl = url
    }

    override fun onStartLoading() {
        forceLoad()
        Log.e(LOG_TAG,"onStartIsLoading")
    }

    override fun loadInBackground(): List<Earthquake>? {
        if (mUrl == null)
        {
            return null
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        val earthquakes = QueryUtils.fetchEarthQuakeData(mUrl!!)
        Log.e(LOG_TAG,"loadInBackground")
        return earthquakes

    }
}