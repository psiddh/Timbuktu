package app.com.timbuktu;

import android.location.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SyncCache {

    private ConcurrentHashMap<Integer, MediaItem> mMap;

    public SyncCache() {
        mMap = new ConcurrentHashMap<>();
    }

    public void addMediaItem(int id, MediaItem item) {
        mMap.put(id, item);
    }
}