package com.example.qukaereport
import android.text.TextUtils
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.util.ArrayList
    /**
     * Helper methods related to requesting and receiving earthquake data from USGS.
     */
    object QueryUtils {
        fun fetchEarthQuakeData(requestUrl : String):List<Earthquake> ? {
//            try {
//                Thread.sleep(2000)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }

            val url = createURL(requestUrl)
            var jsonResponse1:String? = null
            try{
                jsonResponse1 = makeHttpRequest(url)
            }catch (e:IOException){


            }
            val earthquakes:List<Earthquake>? = jsonResponse1?.let { extractEarthquakes(it) }
            return earthquakes
        }
        //Returns new url object from the given String url
        private fun createURL(StringUrl: String):URL?{//this method is to convert a string into url the input that we get is a string
            var url:URL? = null //over here we have declared  a variable url to store only the url values and show error if it stores anything else
            try{//We are putting our statements in try catch block so that it dosent shows any error
                url = URL(StringUrl) //this is the main step where a string gets converted to a url and is stored in url variable
            }
            catch (e: MalformedURLException){

            }
            return url

        }
        private fun makeHttpRequest(url:URL?):String?{
            var jsonResponse = "" //to store the url value here in the jsonResponse variable
            if(url == null){
                return jsonResponse
                //if no information is provided then it woudnt crash the app
            }
            var urlConnetion:HttpURLConnection? = null //to handel the url connection part
            var inputStream :InputStream? = null //this is basically use to parse the json data/to read the input data
            try {
                //putting it in try block so that if any error occurs the system would be able to catch it
                urlConnetion =
                    url!!.openConnection() as HttpURLConnection //to begin the desired url connection procedure
                urlConnetion.requestMethod =
                    "GET" //out of the many http request we have requested for the get method coz we want to get the values there is
                //one more method other than this that is write which helps up in writing somethin specific in the json request
                urlConnetion.readTimeout = 10000
                urlConnetion.connectTimeout = 15000
                //basically the time for the read and connection and is in millis
                urlConnetion.connect()//this is the final step after this the connection would be established
                if (urlConnetion.responseCode == 200) {
                    inputStream = urlConnetion.inputStream //to convert the  data into binary values
                    jsonResponse = readFromStream(inputStream) // basically a function to read the binary values and covert them into human readable format
                }
            }
                catch(e: IOException){
                }finally{
                if(urlConnetion != null){
                    urlConnetion.disconnect()
                }
                if(inputStream != null)
                {
                    inputStream.close()
                }

                }
            return jsonResponse

        }
        private fun readFromStream(inputStream: InputStream?): String {
            //over here we receive the inputStream from above and try to convert it in human readable form
            val output = StringBuilder() //we could have used string or a string builder but here we have used an string builder coz string is immutable once updated
            //cannot be changed whereas a string builder is mutable and we use string builder when the inital value needs to be updated continously or if there is any change
            if(inputStream != null){
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8")) //to convert the values in human readable text
                val reader = BufferedReader(inputStreamReader)
                //the input stream reader only reads single charecters at a time which would take a lot of time so we just wrap it in buffer reader so that so that large
                //chunks of data can be read at one time
                var line = reader.readLine()
                while(line != null){ //while line variable contain any word this loop runs
                    output.append(line) //this will just append the data at the end
                    line = reader.readLine()

                }
            }
            return output.toString()


        }

        //in this we first go to the usgs webpage all this happens in the backend then we go to type then to properties and take out only mag,time,place and url
        fun extractEarthquakes(earthquakeJSON:String): List<Earthquake>? {
            if(TextUtils.isEmpty(earthquakeJSON)){
                return null
            }

            // Create an empty ArrayList that we can start adding earthquakes to
            val earthquakes:ArrayList<Earthquake> = ArrayList()
            // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
            // is formatted, a JSONException exception object will be thrown.
            // Catch the exception so the app doesn't crash, and print the error message to the logs.
            try
            {//we have used a try catch block so that the app does not crashes
                // Create a JSONObject from the SAMPLE_JSON_RESPONSE string
                val baseJsonResponse = JSONObject(earthquakeJSON)
//                // Extract the JSONArray associated with the key called "features",
//                // which represents a list of features (or earthquakes).
                val earthquakeArray = baseJsonResponse.getJSONArray("features")
//                // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
                for (i in 0 until earthquakeArray.length())//0 to the length of the earthquake array
                {
//                    // Get a single earthquake at position i within the list of earthquakes
                    val currentEarthquake = earthquakeArray.getJSONObject(i)

//                    // For a given earthquake, extract the JSONObject associated with the
//                    // key called "properties", which represents a list of all properties
//                    // for that earthquake.
                    val properties = currentEarthquake.getJSONObject("properties")
//                    // Extract the value for the key called "mag"
                    val magnitude = properties.getDouble("mag")
//                    // Extract the value for the key called "place"
                    val location = properties.getString("place")
//                    // Extract the value for the key called "time"
                    val date = properties.getLong("time")
                    val time = properties.getLong("time")
//                    // Extract the value for the key called "url"
                    val url = properties.getString("url")
//                    // Create a new {@link Earthquake} object with the magnitude, location, time,
//                    // and url from the JSON response.
                    val earthquake = Earthquake(magnitude, location, date, time, url)
//                    // Add the new {@link Earthquake} to the list of earthquakes.
                    earthquakes.add(earthquake)
                }
            }
            catch (e:JSONException) {
                // If an error is thrown when executing any of the above statements in the "try" block,
                // catch the exception here, so the app doesn't crash. Print a log message
                // with the message from the exception.
                Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e)
            }
            // Return the list of earthquakes
            return earthquakes
        }
    }/**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */