package willjuste.com.sunshine;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);

        //Add some preferences that are defined by the XML file
        addPreferencesFromResource(R.xml.pref_general);

        //Attach an OnPreferenceChangeListenerso the UI summary can update when the preference changes.
        bindPreference(findPreference(getString(R.string.pref_location_key)));
        bindPreference(findPreference(getString(R.string.pref_temperature_unit_key)));
    }

    private void bindPreference(Preference preference) {
        //Watch for value changes in the listener
        preference.setOnPreferenceChangeListener(this);

//The value of the preference

        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            //For list preferences, look up the correct display value int the 'entries'
            ListPreference listPreference = (ListPreference) preference;
            int preferenceIndex = listPreference.findIndexOfValue(stringValue);
            if (preferenceIndex >=0){
                preference.setSummary(listPreference.getEntries()[preferenceIndex]);
            }
        } else {
            //For other preferences that aren't in a ListPreference
            preference.setSummary(stringValue);
        }
        return true;
    }




/**
 * A placeholder fragment containing a simple view.
 */
//public static class SettingsFragment extends Fragment {
//
//    public SettingsFragment() {
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup settings_container,
//                             Bundle savedInstanceState) {
//
//        View rootView = inflater.inflate(R.layout.fragment_settings, settings_container, false);
//
//        Intent settingsActivity = getActivity().getIntent();
//        if (settingsActivity != null && settingsActivity.hasExtra(settingsActivity.EXTRA_TEXT)) {
//            String infoStr = settingsActivity.getStringExtra(settingsActivity.EXTRA_TEXT);
//
//            ((TextView) rootView.findViewById(R.id.settings_text)).setText(infoStr);
//        }
//
//        return rootView;
//    }
//}
}
