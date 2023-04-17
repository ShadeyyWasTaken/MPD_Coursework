package com.example.karastoyanov_martin_s2031121;

import android.graphics.Color;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EarthquakeData {
    private String title;
    private String description;
    private String link;
    private String pubDate;
    private String category;
    private double geoLat;
    private double geoLong;

    private String location;
    private int depth;
    private double magnitude;
    private Date date;

    private int colour = -1;


    // Constructor
    public EarthquakeData(String title, String description, String link, String pubDate, String category, Double geoLat, Double geoLong) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = pubDate;
        this.category = category;
        this.geoLat = geoLat;
        this.geoLong = geoLong;
    }

    public EarthquakeData() {
        this.title = "";
        this.description = "";
        this.link = "";
        this.pubDate = "";
        this.category = "";
        this.geoLat = 0.0;
        this.geoLong = 0.0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getGeoLat() {
        return geoLat;
    }

    public void setGeoLat(Double geoLat) {
        this.geoLat = geoLat;
    }

    public double getGeoLong() {
        return geoLong;
    }

    public void setGeoLong(Double geoLong) {
        this.geoLong = geoLong;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation() {
        this.location = extractValue(description, "Location:");
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth() {
        try {
            this.depth = Integer.parseInt(extractValue(description, "Depth:"));
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude() {
        try{
            this.magnitude = Double.parseDouble(extractValue(description, "Magnitude:"));
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }


    public Date getDate()
    {
        return this.date;
    }
    public void setDate() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            this.date = dateFormat.parse(extractValueDate(description, "Origin date/time"));
        } catch (ParseException e) {
            System.err.println("Failed to parse date");
            e.printStackTrace();
        }
    }

    public static String extractValue(String description, String keyword) {
        String[] lines = description.split(";");

        for (String line : lines) {
            if (line.contains(keyword)) {
                // Extract the value from the line
                String[] parts = line.split(":");
                if (parts.length > 1) {
                    // If the keyword is "Depth", remove "km" from the value
                    String value = parts[1].trim().replace("km", "").trim();
                    return value;
                }
            }
        }
        System.err.println(keyword + " not found in description");
        return null;
    }

    public int getColour() {
        return colour;
    }

    public void setColour() {
        if (magnitude >= 0 && magnitude < 1 ) {
            this.colour = Color.parseColor("#ADD8E6"); //Blue
        } else if (magnitude >= 1 && magnitude < 2) {
            this.colour = Color.parseColor("#FFC0CB"); //Pink
        } else if (magnitude >= 2 && magnitude < 3) {
            this.colour = Color.parseColor("#008000"); //Green
        } else if (magnitude >= 3 && magnitude < 4) {
            this.colour = Color.parseColor("#FFA500"); //Orange
        } else if (magnitude >= 4) {
            this.colour = Color.parseColor("#FF0000"); //Red
        }
    }

    public void setColour(int colour)
    {
        this.colour = colour;
    }

    public static String extractValueDate(String description, String keyword) {
        // Split the description string into lines
        String[] lines = description.split(";");

        // Iterate through the lines to find the line containing the keyword
        for (String line : lines) {
            if (line.contains(keyword)) {
                // Extract the value from the line
                String[] parts = line.split(":");
                if (parts.length > 1) {
                    String value = parts[1].trim();
                    if (keyword.equalsIgnoreCase("Origin date/time")) {
                        // If the keyword is "Origin date/time", parse the date value
                        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH");                        try {
                            Date date = format.parse(value);
                            // Format the date to desired output
                            format.applyPattern("dd/MM/yyyy");
                            value = format.format(date);
                        } catch (ParseException e) {
                            System.err.println("Failed to parse date: " + value);
                            e.printStackTrace();
                        }
                    }
                    return value;
                }
            }
        }
        return null;
    }

    public String dateToString() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return format.format(this.date);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}

