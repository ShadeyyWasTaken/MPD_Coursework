package com.example.karastoyanov_martin_s2031121;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarthquakeAdapter extends ArrayAdapter<EarthquakeData> {

    private ArrayList<EarthquakeData> earthquakeList;
    private Context context;

    public EarthquakeAdapter(Context context, ArrayList<EarthquakeData> earthquakeList) {
        super(context, 0, earthquakeList);
        this.context = context;
        this.earthquakeList = earthquakeList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_earthquake_xml, parent, false);
        }

        EarthquakeData earthquake = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.textViewTitle);
        TextView pubDateTextView = convertView.findViewById(R.id.textViewPubDate);
        TextView latitudeTextView = convertView.findViewById(R.id.textViewLatitude);
        TextView longitudeTextView = convertView.findViewById(R.id.textViewLongitude);

        titleTextView.setText(earthquake.getTitle());
        pubDateTextView.setText("Date: " + earthquake.getPubDate());
        latitudeTextView.setText("Latitude: " + Double.toString(earthquake.getGeoLat()));
        longitudeTextView.setText("Longitude: " + Double.toString(earthquake.getGeoLong()));

        if (earthquake.getColour() != -1)
        {
            convertView.setBackgroundColor(earthquake.getColour());
        }
        return convertView;
    }

    public static ArrayList<EarthquakeData> findEarthquakesByDate(ArrayList<EarthquakeData> earthquakeDataList, String targetDate) {
        ArrayList<EarthquakeData> earthquakesOnDay = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date target = format.parse(targetDate); // Parse the target date
            for (EarthquakeData earthquakeData : earthquakeDataList) {
                // Compare the date of each EarthquakeData object with the target date
                if (earthquakeData.getDate() != null && format.format(earthquakeData.getDate()).equals(targetDate)) {
                    earthquakesOnDay.add(earthquakeData); // If the dates match, add the EarthquakeData object to the result list
                }
            }
        } catch (ParseException e) {
            System.err.println("Failed to parse target date: " + targetDate);
            e.printStackTrace();
        }
        return earthquakesOnDay;
    }

    public static EarthquakeData findEarthquakeByDateAndLocation(ArrayList<EarthquakeData> earthquakeDataList, String targetDate, String targetLocation) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date target = format.parse(targetDate); // Parse the target date
            for (EarthquakeData earthquakeData : earthquakeDataList) {
                // Compare the date of each EarthquakeData object with the target date
                if (earthquakeData.getDate() != null && format.format(earthquakeData.getDate()).equals(targetDate)) {
                    if (earthquakeData.getLocation() != null && earthquakeData.getLocation().toLowerCase().contains(targetLocation.toLowerCase())) {
                        return earthquakeData;
                    }
                }
            }
        } catch (ParseException e) {
            System.err.println("Failed to parse target date: " + targetDate);
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<EarthquakeData> changeColour(ArrayList<EarthquakeData> earthquakeDataList)
    {
        for (EarthquakeData earthquakeData: earthquakeDataList)
        {
            earthquakeData.setColour();
        }
        return earthquakeDataList;
    }

    public static ArrayList<EarthquakeData> removeColour(ArrayList<EarthquakeData> earthquakeDataList)
    {
        for (EarthquakeData earthquakeData: earthquakeDataList)
        {
            earthquakeData.setColour(-1);
        }
        return earthquakeDataList;
    }

    public static EarthquakeData findEarthquakeWithLowestDepth(ArrayList<EarthquakeData> earthquakeDataList) {
        EarthquakeData lowestDepthEarthquake = null;
        double lowestDepth = Double.MAX_VALUE;

        for (EarthquakeData earthquakeData : earthquakeDataList) {
            if (earthquakeData.getDepth() < lowestDepth) {
                lowestDepth = earthquakeData.getDepth();
                lowestDepthEarthquake = earthquakeData;
            }
        }

        return lowestDepthEarthquake;
    }

    public static EarthquakeData findEarthquakeWithHighestDepth(ArrayList<EarthquakeData> earthquakeDataList) {
        EarthquakeData highestDepthEarthquake = null;
        int highestDepth = Integer.MIN_VALUE;

        for (EarthquakeData earthquakeData : earthquakeDataList) {
            if (earthquakeData.getDepth() > highestDepth) {
                highestDepth = earthquakeData.getDepth();
                highestDepthEarthquake = earthquakeData;
            }
        }

        return highestDepthEarthquake;
    }

    public static EarthquakeData findEarthquakeWithLargestMagnitude(ArrayList<EarthquakeData> earthquakeDataList) {
        EarthquakeData largestMagnitudeEarthquake = null;
        double largestMagnitude = Double.MIN_VALUE;

        for (EarthquakeData earthquakeData : earthquakeDataList) {
            if (earthquakeData.getMagnitude() > largestMagnitude) {
                largestMagnitude = earthquakeData.getMagnitude();
                largestMagnitudeEarthquake = earthquakeData;
            }
        }

        return largestMagnitudeEarthquake;
    }

    private static EarthquakeData findNearestEarthquakeNorthOfGlasgow(ArrayList<EarthquakeData> earthquakeDataList) {
        double glasgowLatitude = 55.864; // Glasgow's latitude
        EarthquakeData nearestEarthquake = null;

        for (EarthquakeData earthquakeData : earthquakeDataList) {
            double earthquakeLatitude = earthquakeData.getGeoLat();

            if (earthquakeLatitude > glasgowLatitude) {
                if (nearestEarthquake == null || earthquakeLatitude < nearestEarthquake.getGeoLat()) {
                    nearestEarthquake = earthquakeData;
                }
            }
        }

        return nearestEarthquake;
    }

    private static EarthquakeData findNearestEarthquakeSouthOfGlasgow(ArrayList<EarthquakeData> earthquakeDataList) {
        double glasgowLatitude = 55.864; // Glasgow's latitude
        EarthquakeData nearestEarthquake = null;

        for (EarthquakeData earthquakeData : earthquakeDataList) {
            double earthquakeLatitude = earthquakeData.getGeoLat();

            if (earthquakeLatitude < glasgowLatitude) {
                if (nearestEarthquake == null || earthquakeLatitude > nearestEarthquake.getGeoLat()) {
                    nearestEarthquake = earthquakeData;
                }
            }
        }

        return nearestEarthquake;
    }

    private static EarthquakeData findNearestEarthquakeWestOfGlasgow(ArrayList<EarthquakeData> earthquakeDataList) {
        double glasgowLongitude = -4.252; // Glasgow's longitude
        EarthquakeData nearestEarthquake = null;

        for (EarthquakeData earthquakeData : earthquakeDataList) {
            double earthquakeLongitude = earthquakeData.getGeoLong();

            if (earthquakeLongitude < glasgowLongitude) {
                if (nearestEarthquake == null || earthquakeLongitude > nearestEarthquake.getGeoLong()) {
                    nearestEarthquake = earthquakeData;
                }
            }
        }

        return nearestEarthquake;
    }

    private static EarthquakeData findNearestEarthquakeEastOfGlasgow(ArrayList<EarthquakeData> earthquakeDataList) {
        double glasgowLongitude = -4.252; // Glasgow's longitude
        EarthquakeData nearestEarthquake = null;

        for (EarthquakeData earthquakeData : earthquakeDataList) {
            double earthquakeLongitude = earthquakeData.getGeoLong();

            if (earthquakeLongitude > glasgowLongitude) {
                if (nearestEarthquake == null || earthquakeLongitude < nearestEarthquake.getGeoLong()) {
                    nearestEarthquake = earthquakeData;
                }
            }
        }

        return nearestEarthquake;
    }

    public static ArrayList<EarthquakeData> findExtremeEarthquakes(ArrayList<EarthquakeData> earthquakeDataList) {


        // Find the nearest earthquake for each direction
        EarthquakeData northEarthquake = findNearestEarthquakeNorthOfGlasgow(earthquakeDataList);
        EarthquakeData southEarthquake = findNearestEarthquakeSouthOfGlasgow(earthquakeDataList);
        EarthquakeData westEarthquake = findNearestEarthquakeWestOfGlasgow(earthquakeDataList);
        EarthquakeData eastEarthquake = findNearestEarthquakeEastOfGlasgow(earthquakeDataList);


        // Store the extreme earthquakes in an ArrayList
        ArrayList<EarthquakeData> extremeEarthquakes = new ArrayList<>();
        extremeEarthquakes.add(northEarthquake);
        extremeEarthquakes.add(southEarthquake);
        extremeEarthquakes.add(westEarthquake);
        extremeEarthquakes.add(eastEarthquake);

        return extremeEarthquakes;
    }
}
