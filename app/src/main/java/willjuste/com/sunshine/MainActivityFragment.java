package willjuste.com.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {


    private static final String OPEN_WEATHER_MAP_API_KEY = "e3502db9a75431a5d3c4b4c108cfdd9a";

    private ArrayAdapter<String> mForecastAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_mainactivity_fragment, menu);
    }


    //This will handle the items in the actionbar when they are clicked. Parent activity needs
    // to be specified in Android Manifest in order for back and home buttons to work.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            GetWeatherTask weatherTask = new GetWeatherTask();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String location = preferences.getString(
                    getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));

            weatherTask.execute(location);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] forecastArray = {
                "Mon ",
                "Tue ",
                "Wed ",
                "Thurs ",
                "Fri ",
                "Sat",
                "Sun"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(forecastArray));
        mForecastAdapter = new ArrayAdapter<String>(
                //The current context (this fragments's parent activity)
                getActivity(),
                //ID of list item layout
                R.layout.list_item_forecast,
                // ID of textview to populate
                R.id.list_item_forecast_textview,
                //Data
                weekForecast);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Intent detailedActivity = new Intent(getActivity(), DetailedActivity.class);
                detailedActivity.putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(detailedActivity);

            }
        });

        return rootView;
    }

    public class GetWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String TAG = MainActivityFragment.class.getSimpleName();

        /* This date/time code will be moved outside the Async Task
        but are just breaking down individual methods
        */
        private String parsedDateString(long time) {
            //API returns a in unix timestamp measured in seconds
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MM dd");
            return dateFormat.format(time);
        }

        // High/Low weathers

        private String parsedTemperatures(double high, double low) {
            long highTemperature = Math.round(high);
            long lowTemperature = Math.round(low);

            String highLowString = highTemperature + "/" + lowTemperature;
            return highLowString;
        }

        /**
         * Pull data from the Json String and parse it
         **/

        private String[] parseJsonData(String forecastJsonStr, int dayNumbers)
                throws JSONException {

            //JSON Objects that need to be parsed.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            Time dayTime = new Time();
            dayTime.setToNow();

            //get the julian start day
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            //work with UTC
            dayTime = new Time();


            String[] resultStrs = new String[dayNumbers];
            for (int i = 0; i < weatherArray.length(); i++) {
                // Using the format "Day, description, hi/low"+

                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day

                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".

                long dateTime;
                dateTime = dayTime.setJulianDay(julianStartDay + i);
                day = parsedDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.


                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = parsedTemperatures(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

            //Check to see if there is a zip code. Verify the size of the parameter.
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.

            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            int dayNumbers = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(dayNumbers))
                        .appendQueryParameter(APPID_PARAM, OPEN_WEATHER_MAP_API_KEY)
                        .build();

                URL url = new URL(buildUri.toString());


                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                forecastJsonStr = buffer.toString();


            } catch (IOException e) {
                Log.e(TAG, "Error ", e);


                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }

                    try {
                        return parseJsonData(forecastJsonStr, dayNumbers);

                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }
            //Will happen if there is an error getting or parsing the datagit pu
            return null;
        }

        @Override
        protected void onPostExecute(String[] feedToUi) {
            if (feedToUi != null) {
                mForecastAdapter.clear();
                for (String dailyForecastStr : feedToUi) {
                    mForecastAdapter.add(dailyForecastStr);
                }
            }
            super.onPostExecute(feedToUi);
        }
    }
}

