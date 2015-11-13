package app.com.timbuktu.collections;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import app.com.timbuktu.MediaItem;
import app.com.timbuktu.SyncCache;
import app.com.timbuktu.collage.CollageHelper;

public class Collection implements Parcelable {
    private ArrayList<Integer> mCollection = new ArrayList<>();
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
        /*Bitmap original = copy.getBitmap();
        if (original != null)
            mCollage = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());
        else
            mCollage = null;*/
    }

    public Collection (Parcel parcel) {
        mCollection = parcel.readArrayList(Collection.class.getClassLoader());
        //mCollage = parcel.readParcelable(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mCollection);
        //dest.writeParcelable(mCollage, flags);
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

    public void add(Integer val) {
        mCollection.add(val);
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
