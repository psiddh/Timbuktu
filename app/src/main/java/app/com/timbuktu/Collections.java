package app.com.timbuktu;

import java.util.ArrayList;

public class Collections {

    private ArrayList<ArrayList<MediaItem>> mCollections;

    public Collections() {
        mCollections = new ArrayList<ArrayList<MediaItem>>();
    }

    public int addCollection(ArrayList<MediaItem> item) {
        mCollections.add(item);
        return mCollections.size();
    }

    public void removeCollection(int index) {
        mCollections.remove(index);
    }

    public int addItemsToCollection(int index, ArrayList<MediaItem> item) {
        return 0;
    }

    public void getSize(int id, MediaItem item) {
        mCollections.size();
    }
}