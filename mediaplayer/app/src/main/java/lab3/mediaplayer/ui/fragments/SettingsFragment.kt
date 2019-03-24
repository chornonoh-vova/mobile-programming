package lab3.mediaplayer.ui.fragments

import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import lab3.mediaplayer.R
import lab3.mediaplayer.ui.MainActivity


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        findPreference("pref_dark_theme").setOnPreferenceClickListener {
            TaskStackBuilder.create(activity)
                .addNextIntent(Intent(activity, MainActivity::class.java))
                .addNextIntent(activity?.intent)
                .startActivities()

            false
        }
    }
}