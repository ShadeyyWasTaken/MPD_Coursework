package com.example.karastoyanov_martin_s2031121;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private boolean initialFetch = false;
    private EarthquakeDataParser earthquakeDataParser;
    private ArrayList<EarthquakeData> currentEarthquakesDisplayed = new ArrayList<>();
    private GoogleMap mMap;
    private ViewFlipper viewFlipper;
    private ListView rawDataDisplay, extremeEarthquakesDisplay;

    private Handler handler = new Handler();
    private static final long FETCH_DATA_DELAY = 100000;
    private Button goBackButton, goSearchViewButton, searchSpecificEarthquakesButton, toHomeViewButton
            , shallowestButton, deepestButton, largestMagnitudeButton, extremeEarthquakesButton;
    private final String urlSource="http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";
    private final Context context = this;
    private TextView depthTextView, magnitudeTextView, dateTextView, categoryTextView;

    private EditText editTextDate, editTextSpecificEarthquakeDate, editTextSpecificEarthquakeLocation;
    private Marker currentMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up the raw links to the graphical components
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        rawDataDisplay = (ListView) findViewById(R.id.rawDataDisplay);
        extremeEarthquakesDisplay = (ListView) findViewById(R.id.extremeEarthquakesDisplay);


        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextSpecificEarthquakeDate = (EditText) findViewById(R.id.editTextSpecificEarthquakeDate);
        editTextSpecificEarthquakeLocation = (EditText) findViewById(R.id.editTextSpecificEarthquakeLocation);

        depthTextView = (TextView) findViewById(R.id.depthTextView);
        magnitudeTextView = (TextView) findViewById(R.id.magnitudeTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        categoryTextView = (TextView) findViewById(R.id.categoryTextView);

        Button allEarthquakesButton = (Button) findViewById(R.id.allEarthquakesButton);
        allEarthquakesButton.setOnClickListener(allEarthquakesButtonListener);
        Button searchEarthquakeButton = (Button) findViewById(R.id.searchEarthquakesButton);
        searchEarthquakeButton.setOnClickListener(searchEarthquakesButtonListener);
        Button specificEarthquakesButton = (Button) findViewById(R.id.searchSpecificEarthquakesButton);
        specificEarthquakesButton.setOnClickListener(searchSpecificEarthquakesButtonListener);
        Button toHomeViewButton = (Button) findViewById(R.id.toHomeViewButton);
        toHomeViewButton.setOnClickListener(goBackButtonListener);
        Button shallowestButton = (Button) findViewById(R.id.shallowestButton);
        shallowestButton.setOnClickListener(shallowestButtonListener);
        Button deepestButton = (Button) findViewById(R.id.deepestButton);
        deepestButton.setOnClickListener(deepestButtonListener);
        Button largestMagnitudeButton = (Button) findViewById(R.id.largestMagnitudeButton);
        largestMagnitudeButton.setOnClickListener(largestMagnitudeButtonListener);
        Button extremeEarthquakesButton = (Button) findViewById(R.id.extremeEarthquakesButton);
        extremeEarthquakesButton.setOnClickListener(extremeEarthquakesButtonListener);
        goBackButton = (Button)findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(goBackButtonListener);
        goSearchViewButton = (Button)findViewById(R.id.goSearchViewButton);
        goSearchViewButton.setOnClickListener(goSearchViewButtonListener);

        earthquakeDataParser = new EarthquakeDataParser(new EarthquakeAdapter(context, new ArrayList<>()));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // More Code goes here
    }

    private final View.OnClickListener allEarthquakesButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            currentEarthquakesDisplayed =EarthquakeAdapter.removeColour(currentEarthquakesDisplayed);
            startPeriodicDataFetching();
        }
    };

    private final View.OnClickListener searchEarthquakesButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!initialFetch)
            {
                startPeriodicDataFetching();
                initialFetch = true;
            }
            viewFlipper.setDisplayedChild(2);
        }
    };

    private final View.OnClickListener extremeEarthquakesButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ArrayList<EarthquakeData> extremeEarthquakesNearGlasgow = EarthquakeAdapter.findExtremeEarthquakes(earthquakeDataParser.getEarthquakeDataList());
            EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(context, extremeEarthquakesNearGlasgow);
            extremeEarthquakesDisplay.setAdapter(earthquakeAdapter);
            displayExtremeEarthquakeData(extremeEarthquakesNearGlasgow);
            viewFlipper.setDisplayedChild(3);
        }
    };

    private final View.OnClickListener shallowestButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            EarthquakeData earthquake = EarthquakeAdapter.findEarthquakeWithLowestDepth(earthquakeDataParser.getEarthquakeDataList());
            if (earthquake != null)
            {
                setMapLocation(earthquake);
                depthTextView.setText("Depth: " + earthquake.getDepth() + " km");
                magnitudeTextView.setText("Magnitude: " + earthquake.getMagnitude());
                dateTextView.setText("Date: " + earthquake.dateToString());
                categoryTextView.setText("Category: " + earthquake.getCategory());
                viewFlipper.setDisplayedChild(1);
            }
        }
    };

    private final View.OnClickListener deepestButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            EarthquakeData earthquake = EarthquakeAdapter.findEarthquakeWithHighestDepth(earthquakeDataParser.getEarthquakeDataList());
            if (earthquake != null)
            {
                setMapLocation(earthquake);
                depthTextView.setText("Depth: " + earthquake.getDepth() + " km");
                magnitudeTextView.setText("Magnitude: " + earthquake.getMagnitude());
                dateTextView.setText("Date: " + earthquake.dateToString());
                categoryTextView.setText("Category: " + earthquake.getCategory());
                viewFlipper.setDisplayedChild(1);
            }
        }
    };

    private final View.OnClickListener largestMagnitudeButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            EarthquakeData earthquake = EarthquakeAdapter.findEarthquakeWithLargestMagnitude(earthquakeDataParser.getEarthquakeDataList());
            if (earthquake != null)
            {
                setMapLocation(earthquake);
                depthTextView.setText("Depth: " + earthquake.getDepth() + " km");
                magnitudeTextView.setText("Magnitude: " + earthquake.getMagnitude());
                dateTextView.setText("Date: " + earthquake.dateToString());
                categoryTextView.setText("Category: " + earthquake.getCategory());
                viewFlipper.setDisplayedChild(1);
            }
        }
    };
    private final View.OnClickListener searchSpecificEarthquakesButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            EarthquakeData earthquake = EarthquakeAdapter.findEarthquakeByDateAndLocation(earthquakeDataParser.getEarthquakeDataList(),
                    editTextSpecificEarthquakeDate.getText().toString(),
                    editTextSpecificEarthquakeLocation.getText().toString());

            if (earthquake != null)
            {
                setMapLocation(earthquake);
                depthTextView.setText("Depth: " + earthquake.getDepth() + " km");
                magnitudeTextView.setText("Magnitude: " + earthquake.getMagnitude());
                dateTextView.setText("Date: " + earthquake.dateToString());
                categoryTextView.setText("Category: " + earthquake.getCategory());
                viewFlipper.setDisplayedChild(1);
            }

            else
            {
                Toast.makeText(MainActivity.this, "City or Date not found", Toast.LENGTH_SHORT).show();
            }

        }
    };


    private View.OnClickListener goSearchViewButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            viewFlipper.setDisplayedChild(2);
        }
    };
    private View.OnClickListener goBackButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (currentMarker != null)
            {
                removeMarker();
            }
            viewFlipper.setDisplayedChild(0);
        }
    };

    private AdapterView.OnItemClickListener rawDataDisplayListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            EarthquakeData earthquake = (EarthquakeData) rawDataDisplay.getItemAtPosition(position);
            setMapLocation(earthquake);
            depthTextView.setText("Depth: " + earthquake.getDepth() + " km");
            magnitudeTextView.setText("Magnitude: " + earthquake.getMagnitude());
            dateTextView.setText("Date: " + earthquake.dateToString());
            categoryTextView.setText("Category: " + earthquake.getCategory());
            viewFlipper.setDisplayedChild(1);
        }
    };

    private AdapterView.OnItemClickListener extremeEarthquakesDisplayListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            EarthquakeData earthquake = (EarthquakeData) extremeEarthquakesDisplay.getItemAtPosition(position);
            setMapLocation(earthquake);
            depthTextView.setText("Depth: " + earthquake.getDepth() + " km");
            magnitudeTextView.setText("Magnitude: " + earthquake.getMagnitude());
            dateTextView.setText("Date: " + earthquake.dateToString());
            categoryTextView.setText("Category: " + earthquake.getCategory());
            viewFlipper.setDisplayedChild(1);
        }
    };


    protected void displayExtremeEarthquakeData(ArrayList<EarthquakeData> earthquakeDataList) {
        EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(context, earthquakeDataList);
        extremeEarthquakesDisplay.setAdapter(earthquakeAdapter);
        extremeEarthquakesDisplay.setOnItemClickListener(extremeEarthquakesDisplayListener);
    }

    protected void displayEarthquakeData(ArrayList<EarthquakeData> earthquakeDataList) {
        EarthquakeAdapter earthquakeAdapter = new EarthquakeAdapter(context, earthquakeDataList);
        rawDataDisplay.setAdapter(earthquakeAdapter);
        rawDataDisplay.setOnItemClickListener(rawDataDisplayListener);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng auckLand = new LatLng(-36.9, 174.8);
        mMap.addMarker(new MarkerOptions().position(auckLand).title("Marker in Auckland"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(auckLand));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
    public void setMapLocation(EarthquakeData earthquake)
    {
        LatLng earthquakeLocation = new LatLng(earthquake.getGeoLat(), earthquake.getGeoLong());
        currentMarker = mMap.addMarker(new MarkerOptions().position(earthquakeLocation).title(earthquake.getLocation()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(earthquakeLocation));
    }

    private void removeMarker()
    {
        currentMarker.remove();
    }

    private void fetchEarthquakeData() {
        // Call parse method and provide the URL and a listener for parsing completion
        earthquakeDataParser.parse(urlSource, new EarthquakeDataParser.OnParsingCompleteListener() {
            @Override
            public void onParsingComplete(ArrayList<EarthquakeData> earthquakeDataList) {
                // This callback will be called when parsing is complete
                if (earthquakeDataList != null) {
                    Log.e("EditText", editTextDate.getText().toString());
                    if (editTextDate.getText().toString().matches(""))
                    {
                        displayEarthquakeData(earthquakeDataList);
                    }
                    else
                    {
                        currentEarthquakesDisplayed =EarthquakeAdapter.findEarthquakesByDate(earthquakeDataList, editTextDate.getText().toString());
                        currentEarthquakesDisplayed =EarthquakeAdapter.changeColour(currentEarthquakesDisplayed);
                        displayEarthquakeData(currentEarthquakesDisplayed);

                    }

                }
                else {
                    // Parsing failed
                    Log.e("Error", "Failed to Parse");
                    Toast.makeText(MainActivity.this, "Failed to parse earthquake data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Runnable fetchDataRunnable = new Runnable() {
        @Override
        public void run() {
            // Fetch earthquake data
            fetchEarthquakeData();
            // Schedule the next fetch after the desired delay
            Log.e("AutoFetching", "Started");
            handler.postDelayed(fetchDataRunnable, FETCH_DATA_DELAY);
            Log.e("AutoFetching", "Ended");
        }
    };

    // Call this method to start periodic data fetching
    private void startPeriodicDataFetching() {
        // Post the first fetch immediately
        handler.removeCallbacks(fetchDataRunnable);
        handler.post(fetchDataRunnable);
    }
}