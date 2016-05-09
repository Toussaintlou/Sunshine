package willjuste.com.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailedFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
//
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailedFragment extends Fragment {

        private static final String TAG = DetailedFragment.class.getSimpleName();
        private static final String SHARE_HASHTAG = " #SunshineApp";

        private String infoStr;

        public DetailedFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detailed, container, false);

            Intent detailedActivity = getActivity().getIntent();
            if (detailedActivity != null && detailedActivity.hasExtra(detailedActivity.EXTRA_TEXT)) {
                infoStr = detailedActivity.getStringExtra(detailedActivity.EXTRA_TEXT);

                ((TextView) rootView.findViewById(R.id.detailed_text)).setText(infoStr);
            }

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);

            //Get the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);

            //Get the provider and hold it to set/change the share intent.
            ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            //Attach an intent to the ShareActionProvider
            if (shareActionProvider != null) {
                shareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(TAG, "Action Provider is null");
            }
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            //This resets the hierarchy of the Intent of the new application
            //that you're opening with to be sure the back button returns to this application
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            //Tells android what type of data is being shared
            shareIntent.setType("text/plain");
            //Data to input
            shareIntent.putExtra(Intent.EXTRA_TEXT, infoStr + SHARE_HASHTAG);
            return shareIntent;
        }
    }
}


