package app.com.timbuktu.collections;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import app.com.timbuktu.MediaItem;
import app.com.timbuktu.SyncCache;
import app.com.timbuktu.collage.CollageHelper;

public class Collection implements Parcelable {
    private ArrayList<Integer> mCollection = new ArrayList<>();

    private long startTime = 0;
    private long endTime = 0;

    private ArrayList<String> mPlaces = new ArrayList<>();
    //private Bitmap mCollage = null;
    /**
     * Constructs a Collection from a Parcel
     */
    public Collection() {
        mCollection = new ArrayList<>();
    }

    public Collection(Collection copy) {
        mCollection = new ArrayList<>();
        for (Integer i: copy.get())
            mCollection.add(i);
        startTime = copy.getStartTime();
        endTime = copy.getEndTime();

        mPlaces = copy.getPlaces();
    }

    public Collection (Parcel parcel) {
        mCollection = parcel.readArrayList(Collection.class.getClassLoader());
        startTime = parcel.readLong();
        endTime = parcel.readLong();
        parcel.readStringList(mPlaces);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mCollection);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeStringList(mPlaces);
    }

    // Method to recreate a Question from a Parcel
    public static Creator<Collection> CREATOR = new Creator<Collection>() {

        @Override
        public Collection createFromParcel(Parcel source) {
            return new Collection(source);

        }

        @Override
        public Collection[] newArray(int size) {
            return new Collection[size];
        }

    };

    public ArrayList<Integer> get() {
        return mCollection;
    }

    public void set(ArrayList<Integer> newVal) {
        ArrayList<Integer> copy = (ArrayList<Integer>) newVal.clone();
        mCollection = copy;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
       this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public ArrayList<String> getPlaces() {
        return this.mPlaces;
    }

    public void setPlaces(ArrayList<String> newPlaces) {
        if (newPlaces == null || newPlaces.size() == 0)
            return;

        mPlaces = new ArrayList<>();
        for (String place: newPlaces) {
            if (!mPlaces.contains(place)) {
                mPlaces.add(place);
            }
        }
    }

    public void add(Integer val) {
        mCollection.add(val);
    }

    public void addWithTS(Integer val, long timeStamp) {
        mCollection.add(val);
        startTime = (startTime == 0) ? timeStamp : Math.min(startTime, timeStamp);
        endTime = (endTime == 0) ? timeStamp : Math.max(endTime, timeStamp);
    }

    public Integer at(int index) {
        if (index > mCollection.size()) {
            return -1;
        }
        return mCollection.get(index);
    }

    public void reset() {
        mCollection.clear();
        //if (mCollage != null && !mCollage.isRecycled())
        //    mCollage.recycle();
        //collage();
    }

    public int size() {
        return mCollection.size();
    }

    //public Bitmap getBitmap() {
    //    return mCollage;
    //}

}
