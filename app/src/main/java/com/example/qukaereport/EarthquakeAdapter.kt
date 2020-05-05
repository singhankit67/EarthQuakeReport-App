package com.example.qukaereport
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat

import java.text.DecimalFormat
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class EarthquakeAdapter(context: Context, earthquakess:List<Earthquake>):
    ArrayAdapter<Earthquake>(context, 0, earthquakess) {
    override fun getView(position:Int, convertView: View?, parent: ViewGroup): View? {
        var listItemView = convertView
        if (listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate( R.layout.earthquake_list_item ,parent ,false)
            //earthquake layout file is the format in which we want our data to be
            //the above line is the layout or the format in which the data should be represented it is described in the layout
            val currentEarthquake = getItem(position)
            //to get the current position of the pointer or in simple words to iterate through the words as in if we are at 1 then do the below mentioned things
//            for 1 then do the same things for 2 and so on..
//            val magnitudeView = listItemView.findViewById(R.id.magnitude) as TextView
//            val formattedMagnitude = formatMagnitude(currentEarthquake?.mMagnitude)
            //magnitudeView.setText(formattedMagnitude)
            var magnitudeView = listItemView.findViewById(R.id.magnitude) as TextView
            //what ever the value is to be stored should be stored in the xml file where magnitude is id as magnitude keeps on changing it should be updated continously
            //it has a specific in the xml file only there these changes should take place and u can tell that this is basically the address where the changes are to take place
            val formattedMagnitude = formatMagnitude(currentEarthquake!!.mMagnitude)
            val magnitudeCircle = magnitudeView.background as GradientDrawable
            val magnitudeColor = getMagnitudeColor(currentEarthquake.mMagnitude)
            magnitudeCircle.setColor(magnitudeColor)
            //basically this converts the double value that we retrive from web into string using a formatMagnitude function which we have described below
            magnitudeView.text = formattedMagnitude
            //over here we are just calling that method
            //rest are same as above
            val originalLocation = currentEarthquake.lLocation
            val LOCATION_SEPARATOR = " of "
            val primaryLocation:String
            val locationOffset:String
            if (originalLocation!!.contains(LOCATION_SEPARATOR))
            {
                // Split the string into different parts (as an array of Strings)
                // based on the " of " text. We expect an array of 2 Strings, where
                // the first String will be "5km N" and the second String will be "Cairo, Egypt".
                val parts = originalLocation.split((LOCATION_SEPARATOR).toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                // Location offset should be "5km N " + " of " --> "5km N of"
                locationOffset = parts[0] + LOCATION_SEPARATOR
                // Primary location should be "Cairo, Egypt"
                primaryLocation = parts[1]
            }
            else
            {
                // Otherwise, there is no " of " text in the originalLocation string.
                // Hence, set the default location offset to say "Near the".
                locationOffset = context.getString(R.string.near_the)
                // The primary location will be the full location string "Pacific-Antarctic Ridge".
                primaryLocation = originalLocation
            }
            val primaryLocationView = listItemView.findViewById(R.id.location1) as TextView
            primaryLocationView.text = primaryLocation
            val locationOffsetView = listItemView.findViewById(R.id.location) as TextView
            locationOffsetView.text = locationOffset
//            val locationView = listItemView.findViewById(R.id.location) as TextView
//            locationView.text = currentEarthquake!!.lLocation
            val dateView = listItemView.findViewById(R.id.date) as TextView
            val formattedDate = formatDate(currentEarthquake.tTimeInMilliseconds)
            dateView.text = formattedDate
            val timeView = listItemView.findViewById(R.id.time) as TextView
            val formattedTime = formatTime(currentEarthquake.mtime)
            timeView.text = formattedTime

        }
        return listItemView
    }
    /**
     * Return the formatted magnitude string showing 1 decimal place (i.e. "3.2")
     * from a decimal magnitude value.
     */
    //this are the functions that perform the required conversions for us
    private fun formatMagnitude(magnitude:Double):String {
        val magnitudeFormat = DecimalFormat("0.0")
        return magnitudeFormat.format(magnitude)
    }
    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */

//    private fun formatDate(dateObject:Date):String {
//        val dateFormat = SimpleDateFormat("LLL dd, yyyy")
//        return dateFormat.format(dateObject)
//    }
    @SuppressLint("SimpleDateFormat")
    fun formatDate(timeInMilliseconds:Long):String{
        val dateFormat = SimpleDateFormat("LLL dd, yyyy")
        return dateFormat.format(timeInMilliseconds)
    }
    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    @SuppressLint("SimpleDateFormat")
    private fun formatTime(time: Long):String {
        val timeFormat = SimpleDateFormat("h:mm a")
        return timeFormat.format(time)
    }
    private fun getMagnitudeColor(magnitude: Double): Int {
        val magnitudeResourceColorId:Int = when(floor(magnitude).toInt()){
            0,1 -> R.color.magnitude1

            2-> R.color.magnitude2
            3-> R.color.magnitude3
            4-> R.color.magnitude4
            5-> R.color.magnitude5
            6-> R.color.magnitude6
            7-> R.color.magnitude7
            8-> R.color.magnitude8
            9-> R.color.magnitude9
            else -> R.color.magnitude10plus
        }
        return ContextCompat.getColor(context, magnitudeResourceColorId)

    }
}
//basically this is the adapter that does the work of storing the values as we want in our application
