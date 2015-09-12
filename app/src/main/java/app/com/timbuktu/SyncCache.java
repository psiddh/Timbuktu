package app.com.timbuktu;

import android.location.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SyncCache {

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
    }

    public MediaItem getMediaItem(int id) {
        return mMap.get(id);
    }

}