package mne.seva.mnereceipt.presentation.settingsActivity

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import mne.seva.mnereceipt.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

}