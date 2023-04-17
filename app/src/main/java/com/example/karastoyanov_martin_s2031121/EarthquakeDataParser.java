    package com.example.karastoyanov_martin_s2031121;
    import android.os.Handler;
    import android.os.Looper;
    import android.util.Log;

    import org.xmlpull.v1.XmlPullParser;
    import org.xmlpull.v1.XmlPullParserException;
    import org.xmlpull.v1.XmlPullParserFactory;

    import java.io.IOException;
    import java.io.InputStream;
    import java.net.URL;
    import java.net.URLConnection;
    import java.util.ArrayList;
    import java.util.concurrent.Executor;
    import java.util.concurrent.Executors;

    public class EarthquakeDataParser {
        private ArrayList<EarthquakeData> earthquakeDataList;
        private EarthquakeAdapter adapter;
        private Handler handler;

        public EarthquakeDataParser(EarthquakeAdapter earthquakeAdapter) {
            earthquakeDataList = new ArrayList<>();
            this.adapter = earthquakeAdapter;
            handler = new Handler(Looper.getMainLooper());
        }

        public void parse(final String urlSource, final OnParsingCompleteListener listener) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute((new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(urlSource);
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xpp = factory.newPullParser();
                        xpp.setInput(url.openStream(), null);
                        int eventType = xpp.getEventType();
                        EarthquakeData earthquakeData = null;

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG) {
                                String tagName = xpp.getName();
                                if (tagName.equals("item")) {
                                    earthquakeData = new EarthquakeData();
                                } else if (earthquakeData != null) {
                                    if (tagName.equals("title")) {
                                        earthquakeData.setTitle(xpp.nextText());
                                    } else if (tagName.equals("description")) {
                                        earthquakeData.setDescription(xpp.nextText());
                                    } else if (tagName.equals("link")) {
                                        earthquakeData.setLink(xpp.nextText());
                                    } else if (tagName.equals("pubDate")) {
                                        earthquakeData.setPubDate(xpp.nextText());
                                    } else if (tagName.equals("category")) {
                                        earthquakeData.setCategory(xpp.nextText());
                                    } else if (tagName.equals("lat")) {
                                        earthquakeData.setGeoLat(Double.parseDouble(xpp.nextText()));
                                    } else if (tagName.equals("long")) {
                                        earthquakeData.setGeoLong(Double.parseDouble(xpp.nextText()));
                                    }}
                            } else if (eventType == XmlPullParser.END_TAG) {
                                String tagName = xpp.getName();
                                if (tagName.equals("item") && earthquakeData != null) {
                                    boolean exists = false;
                                    for (EarthquakeData existingData : earthquakeDataList) {
                                        if (existingData.getTitle().equals(earthquakeData.getTitle()))
                                        {
                                            exists = true;
                                            break;
                                        }
                                    }
                                    if (!exists) {
                                        earthquakeData.setDepth();
                                        earthquakeData.setMagnitude();
                                        earthquakeData.setLocation();
                                        earthquakeData.setDate();
                                        earthquakeDataList.add(earthquakeData);
                                    }
                                    earthquakeData = null;
                                }
                            }
                            eventType = xpp.next();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    adapter.notifyDataSetChanged();
                                    listener.onParsingComplete(earthquakeDataList);
                                }
                            }
                        });

                    } catch (XmlPullParserException | IOException e) {
                        e.printStackTrace();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onParsingComplete(null);
                                }
                            }
                        });
                    }
                }
            }));
        }

        public interface OnParsingCompleteListener {
            void onParsingComplete(ArrayList<EarthquakeData> earthquakeDataList);
        }

        public ArrayList<EarthquakeData> getEarthquakeDataList() {
            return earthquakeDataList;
        }

    }