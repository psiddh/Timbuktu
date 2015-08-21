package app.com.timbuktu;

import android.net.Uri;

public class TravelItem {

    private int mPictureId; // Handle to picture in Media d/b (also acts as a primary key)
    private int mClusterId; // Cluster ID it belongs to!
    private String mPath;
    private long mDate;
    private Uri mURI;
    private double lat;
    private double lng;
    private String place;

    public TravelItem(int pictureId, int clusterId, String path, long date, double lat, double lng,Uri uri, String place) {
        this.mPictureId = pictureId;
        this.mClusterId = clusterId;
        this.mPath = path;
        this.mDate = date;
        this.mURI = uri;
        this.place = place;
        this.lat = lat;
        this.lng = lng;
    }
}