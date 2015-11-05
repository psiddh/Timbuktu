package app.com.timbuktu;

import android.location.Address;

import java.util.ArrayList;
import java.util.List;

public class MediaItem {

    private int mMediaID; // Handle to picture in Media d/b (also acts as a primary key)
    private String mPath;
    private long mTimeStamp;
    private double mLat;
    private double mLng;
    //private List<Address> mAddresses;
    private ArrayList<String> mPlaces;
    private boolean mIsVideo;

    public MediaItem(int mediaId, String path, long timeStamp, double lat, double lng, ArrayList<String> places, boolean isVideo) {
        this.mMediaID = mediaId;
        this.mPath = path;
        this.mTimeStamp = timeStamp;
        this.mLat = lat;
        this.mLng = lng;
        this.mIsVideo = isVideo;
        mPlaces = new ArrayList<>(places);
    }

    public int getMediaID() {
        return mMediaID;
    }

    public String getPath() {
        return mPath;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public double getLat() {
        return mLat;
    }

    public double getLng() {
        return mLng;
    }

    public ArrayList<String> getPlaces() {
        return mPlaces;
    }

    public boolean isVideo() {
        return mIsVideo;
    }
}