package app.com.timbuktu.service;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Collection implements Parcelable{
    private ArrayList<Integer> mCollection;
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
    }

    public Collection (Parcel parcel) {
        mCollection = parcel.readArrayList(null);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeList(mCollection);
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
    }

    public int size() {
        return mCollection.size();
    }
}
