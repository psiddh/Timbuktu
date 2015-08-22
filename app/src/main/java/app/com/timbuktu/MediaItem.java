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
    private List<Address> mAddresses;
    private boolean mIsVideo;

    public MediaItem(int mediaId, String path, long timeStamp, double lat, double lng, List<Address> addresses, boolean isVideo) {
        this.mMediaID = mediaId;
        this.mPath = path;
        this.mTimeStamp = timeStamp;
        this.mLat = lat;
        this.mLng = lng;
        this.mIsVideo = isVideo;
        mAddresses = new ArrayList<Address>(addresses);
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

    public List<Address> getAddress() {
        return mAddresses;
    }

    public boolean isVideo() {
        return mIsVideo;
    }
}