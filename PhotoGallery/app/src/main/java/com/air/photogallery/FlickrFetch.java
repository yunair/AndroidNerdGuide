package com.air.photogallery;

import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Air on 15/7/29.
 */
public class FlickrFetch {
    public static final String TAG = "FlickrFetchr";
    public static final String PREF_SEARCH_QUERY = "searchQuery";

    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "b49634b1a68d767b4a2dd3f7a4c38807";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String PARAM_EXTRAS = "extras";
    private static final String PARAM_TEXT = "text";
    // Specifying the url_s extra tells Flickr to include the URL for the small version of the picture if it is available.
    private static final String EXTRA_SMALL_URL = "url_s";

    private static final String XML_PHOTO = "photo";

    byte[] getUrlBytes(String urlSpec) throws IOException {
        HttpsURLConnection urlConnection = null;
//        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(urlSpec);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000 * 1000 * 60);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = urlConnection.getInputStream();

            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) return  null;

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }  finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    public List<GalleryItem> downloadGalleryItems(String url){
        List<GalleryItem> items = new ArrayList<GalleryItem>();

        try {
            Log.d(TAG, url);
            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));

            parseItems(items, parser);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        return items;
    }
    public List<GalleryItem> fetchItems() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .build().toString();

        return downloadGalleryItems(url);
    }

    public List<GalleryItem> search(String query) {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_TEXT, query)
                .build().toString();
        return downloadGalleryItems(url);
    }

    void parseItems(List<GalleryItem> items, XmlPullParser parser)
            throws XmlPullParserException, IOException {
        int eventType = parser.next();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG &&
                    XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                GalleryItem item = new GalleryItem();
                item.setId(id);
                item.setCaption(caption);
                item.setUrl(smallUrl);
                items.add(item);
            }
            eventType = parser.next();
        }
    }

}
