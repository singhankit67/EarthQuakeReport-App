package com.example.qukaereport
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import java.util.ArrayList
class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<Earthquake>>,
    SharedPreferences.OnSharedPreferenceChangeListener {


    var mAdapter: EarthquakeAdapter? = null
    private val EARTHQUAKE_LOADER_ID = 1
    val LOG_TAG = MainActivity::class.java.name
    var EARTHQUAKE_REQUEST_URL:String = "https://earthquake.usgs.gov/fdsnws/event/1/query"
    var mEmptyStateTextView:TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Create a fake list of earthquake locations.
        val earthquakeListView = findViewById(R.id.list) as ListView
        mEmptyStateTextView = findViewById(R.id.empty_view)
        earthquakeListView.emptyView = mEmptyStateTextView

        mAdapter = EarthquakeAdapter(this, ArrayList())
        earthquakeListView.adapter = mAdapter
        //val task = EarthquakeAsyncTask()
        //task.execute(EARTHQUAKE_REQUEST_URL)
        val loaderManager = supportLoaderManager
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null,this)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)

        //this gives us the data that we want from a particular website by json parsing (getting the data of a website in a way we want) we do not have to manually enter the data
//        earthquakes.add(Earthquake("4.7","San Francisco","June 23,2014"))
//        earthquakes.add(Earthquake("6.1","London","April 13,2014"))
//        earthquakes.add(Earthquake("3.9","Tokyo","Jan 30,2015"))
//        earthquakes.add(Earthquake("4.5","Mexico City","Feb 2,2016"))
//        earthquakes.add(Earthquake("5.7","Moscow","May 31,2017"))
//        earthquakes.add(Earthquake("6.8","Rio De Janerio","May 24,2018"))
//        earthquakes.add(Earthquake("6.0","Paris","Feb 29,2020"))
        // Find a reference to the {@link ListView} in the layout
        //the data that we get gets appended(added) one by one and is visble to the user the format of how the data should be is given in the adapter
        // Create a new {@link ArrayAdapter} of earthquakes

        earthquakeListView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                adapterView: AdapterView<*>,
                view: View,
                position: Int,
                l: Long
            ) {
                // Find the current earthquake that was clicked on
                val currentEarthquake = mAdapter!!.getItem(position)
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                val earthquakeUri = Uri.parse(currentEarthquake!!.uUrl)
                // Create a new intent to view the earthquake URI
                val websiteIntent = Intent(Intent.ACTION_VIEW, earthquakeUri)
                // Send the intent to launch a new activity
                startActivity(websiteIntent)
            }
        })

    }
    override fun onSharedPreferenceChanged(prefs:SharedPreferences, key:String) {
        if ((key == getString(R.string.settings_min_magnitude_key) || key == getString(R.string.settings_order_by_key)))
        {
            // Clear the ListView as a new query will be kicked off
            mAdapter?.clear()
            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView?.visibility = View.GONE
            // Show the loading indicator while new data is being fetched
            val loadingIndicator = findViewById(R.id.loading_indicator) as ProgressBar
            loadingIndicator.setVisibility(View.VISIBLE)
            // Restart the loader to requery the USGS as the query settings have been updated
            supportLoaderManager.restartLoader(EARTHQUAKE_LOADER_ID, null, this)
        }
    }
    override fun onCreateLoader(i:Int, bundle:Bundle?):Loader<List<Earthquake>> {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val minMagnitude = sharedPrefs.getString(
            getString(R.string.settings_min_magnitude_key),
            getString(R.string.settings_min_magnitude_default)
        )
        var orderBy = sharedPrefs.getString(
            getString(R.string.settings_order_by_key),
            getString(R.string.settings_order_by_default)
        )
        val baseUri = Uri.parse(EARTHQUAKE_REQUEST_URL)
        val uriBuilder = baseUri.buildUpon()
        uriBuilder.appendQueryParameter("format", "geojson")
        uriBuilder.appendQueryParameter("limit", "20")
        uriBuilder.appendQueryParameter("minmag", minMagnitude)
        uriBuilder.appendQueryParameter("orderby", orderBy)
        return EarthquakeLoader(this, uriBuilder.toString())
    }

//    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Earthquake>> {
//        // Create a new loader for the given
//        Log.e(LOG_TAG,"CreateMethod")
//        return EarthquakeLoader(this,EARTHQUAKE_REQUEST_URL)
//
//    }
    override fun onLoadFinished(loader: Loader<List<Earthquake>>, earthquakes:List<Earthquake>) {
        val loadingIndicator = findViewById(R.id.loading_indicator) as ProgressBar
    loadingIndicator.visibility = View.GONE
        mEmptyStateTextView?.setText(R.string.no_earthquakes)
        // Clear the adapter of previous earthquake data
        mAdapter?.clear()

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes.isNotEmpty())
        {
           mAdapter?.addAll(earthquakes)
        }
        Log.e(LOG_TAG,"LoadMethod")
    }

    override fun onLoaderReset(loader: Loader<List<Earthquake>>) {
        // Loader reset, so we can clear out our existing data.
        mAdapter?.clear()
        Log.e(LOG_TAG,"ResetMethod")
    }
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        val id = item.itemId
        if (id == R.id.action_settings)
        {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

            /**
             * This method runs on the main UI thread after the background work has been
             * completed. This method receives as input, the return value from the doInBackground()
             * method. First we clear out the adapter, to get rid of earthquake data from a previous
             * query to USGS. Then we update the adapter with the new list of earthquakes,
             * which will trigger the ListView to re-populate its list items.
             */
            /*override fun onPostExecute(data:List<Earthquake>) {
                // Clear the adapter of previous earthquake data
                mAdapter!!.clear()
                // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
                // data set. This will trigger the ListView to update.
                if (data != null && !data.isEmpty())
                {
                    mAdapter!!.addAll(data)
                }
            }*/




}


