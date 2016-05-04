package willjuste.com.sunshine;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
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
