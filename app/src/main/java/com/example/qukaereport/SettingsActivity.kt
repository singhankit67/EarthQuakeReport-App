package com.example.qukaereport

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
    class EarthquakePreferenceFragment: PreferenceFragment(), Preference.OnPreferenceChangeListener {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings_main)
            var minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key))
            bindPreferenceSummaryToValue(minMagnitude)
            var orderBy = findPreference(getString(R.string.settings_order_by_key))
            bindPreferenceSummaryToValue(orderBy)
        }

        override fun onPreferenceChange(preference: Preference, value:Any):Boolean {
            val stringValue = value.toString()
            //preference.summary = stringValue
                if (preference is ListPreference)
                {
                    val listPreference = preference as ListPreference
                    val prefIndex = listPreference.findIndexOfValue(stringValue)
                    if (prefIndex >= 0)
                    {
                        val labels = listPreference.entries
                        preference.setSummary(labels[prefIndex])
                    }
                }
                else
                {
                    preference.summary = stringValue
                }
                return true
            }


        private fun bindPreferenceSummaryToValue(preference:Preference) {
            preference.onPreferenceChangeListener = this
            val preferences = PreferenceManager.getDefaultSharedPreferences(preference.context)
            val preferenceString = preferences.getString(preference.key, "")
            onPreferenceChange(preference, preferenceString)
        }

    }
}
