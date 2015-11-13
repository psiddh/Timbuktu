package app.com.timbuktu.collections;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Collections implements Parcelable {
    private ArrayList<Collection> mCollections = new ArrayList<>();

    protected Collections(Parcel in) {
        /*Collection[] collections = (Collection[]) in.readParcelableArray(Collections.class.getClassLoader());
        mCollections = new ArrayList<>();
        for (Collection coll: collections) {
            mCollections.add(coll);
        }*/

        //mCollections = in.readArrayList(Collections.class.getClassLoader());

        in.readList(mCollections, Collections.class.getClassLoader());

    }

    public static final Creator<Collections> CREATOR = new Creator<Collections>() {
        @Override
        public Collections createFromParcel(Parcel in) {
            return new Collections(in);
        }

        @Override
        public Collections[] newArray(int size) {
            return new Collections[size];
        }
    };

    public Collections() {
        mCollections = new ArrayList<Collection>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /*Collection[] collectionArray = new Collection[mCollections.size()];
        int index = 0;
        for (Collection collection : mCollections) {
            collectionArray[index] =  mCollections.get(index);
            index++;
        }
        dest.writeParcelableArray(collectionArray, flags);*/
        dest.writeList(mCollections);
    }

    public ArrayList<Collection> get() {
        return mCollections;
    }

    public void set(ArrayList<Collection> newVal) {
        ArrayList<Collection> copy = (ArrayList<Collection>) newVal.clone();
        mCollections = copy;
    }

    public void add(Collection val) {
        Collection copyVal = new Collection(val);
        mCollections.add(copyVal);
    }

    public Collection at(int index) {
        if (index >= mCollections.size()) {
            return null;
        }
        return mCollections.get(index);
    }

    public void reset() {
        mCollections.clear();
    }

    public int size() {
        return mCollections.size();
    }
}
