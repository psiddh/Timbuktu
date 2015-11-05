package app.com.timbuktu;

import android.location.Address;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import app.com.timbuktu.database.DatabaseHelper;
import app.com.timbuktu.database.DatabaseManager;

public class SyncCache {

    private HashSet<String> mUniqueCities = new HashSet<>();
    private HashSet<String> mUniqueStates = new HashSet<>();
    private HashSet<String> mUniqueCountries = new HashSet<>();
    
    private static ConcurrentHashMap<Integer, MediaItem> mMap;
    private static SyncCache sInstance = null;
    public static SyncCache getInstance() {
        if (sInstance == null) {
            mMap = new ConcurrentHashMap<>();
            sInstance = new SyncCache();
        }
        return sInstance;
    }

    private SyncCache() {
    }

    public void addMediaItem(int id, MediaItem item) {
        mMap.put(id, item);

        ArrayList<String> places = item.getPlaces();
        if (places.size() == 0 || places == null)
            return;
        //String address = item.getAddress().get(0).getAddressLine(0);
        String city = places.get(0);
        String state = places.get(1);
        String country = places.get(2);
        //String postalCode = item.getAddress().get(0).getPostalCode();
        //String knownName = item.getAddress().get(0).getFeatureName();

        mUniqueCities.add(city);
        mUniqueStates.add(state);
        mUniqueCountries.add(country);

        Log.d("DEBUG: ", "Check!" + mUniqueCities.size() + " - " + mUniqueStates.size() + " - " + mUniqueCountries.size());
    }

    public MediaItem getMediaItem(int id) {
        MediaItem item = mMap.get(id);
        return item;
    }


    public ConcurrentHashMap<Integer, MediaItem> getMap() {
        return mMap;
    }

    // Util function
    public ArrayList<String> getMatchingPlacesFromUserFilter(String UserFilter) {
        ArrayList<String> places = new ArrayList<>();
        StringBuilder concat = new StringBuilder();

        String filter = UserFilter;
        String [] words = filter.split("\\s+");

        for (int i = 0; i < words.length; i++) {
            concat.setLength(0);
            concat.append(words[i] + ((i + 1 == words.length) ? "" : " "));
            for (int j = i+1; j < words.length; j++) {
                concat.append(words[j] + ((j + 1 == words.length) ? "" : " "));

                if (mUniqueCities.contains(concat.toString()))
                    places.add(concat.toString());
                if (mUniqueStates.contains(concat.toString()))
                    places.add(concat.toString());
                if (mUniqueCountries.contains(concat.toString()))
                    places.add(concat.toString());
            }
        }
        return places;
    }
}