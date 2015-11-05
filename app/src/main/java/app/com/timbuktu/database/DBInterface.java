package app.com.timbuktu.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.com.timbuktu.MediaItem;

public class DBInterface {

    private static String TAG = "DBInterface";
    private static DatabaseManager mDBManager;
    private SQLiteDatabase mDatabase;

    public DBInterface(Context context) {
        mDBManager = DatabaseManager.getInstance(context);
    }

    public void open(boolean writeMode) {
        mDatabase = mDBManager.open(writeMode);
    }

    public void close() {
        mDBManager.close();
    }

    public void addMediaItem(int id, MediaItem item) throws IOException {
        throwIfNotOpen();
        // check if this id already exists in d/b
        if (isMediaIDAlreadyExistInDB(id)) {
            return;
        }
        addMediaRow(id, item);
    }

    public ArrayList<String> getPlacesById(int id) throws IOException {
        throwIfNotOpen();

        ArrayList<String> places = new ArrayList<>();
        mDatabase.beginTransaction();
        try {
            Cursor cursor = mDatabase.rawQuery(DatabaseContract.MediaTable.SELECT_ALL_BY_MEDIA_ID + id, null);
            if (cursor.getCount() == 0) {
                return places;
            } else {
                int COLUMN_CITY = cursor.getColumnIndexOrThrow(DatabaseContract.MediaTable.MEDIA_CITY);
                int COLUMN_STATE = cursor.getColumnIndexOrThrow(DatabaseContract.MediaTable.MEDIA_STATE);
                int COLUMN_COUNTRY = cursor.getColumnIndexOrThrow(DatabaseContract.MediaTable.MEDIA_COUNTRY);
                int COLUMN_LAT = cursor.getColumnIndexOrThrow(DatabaseContract.MediaTable.MEDIA_LAT);
                int COLUMN_LNG = cursor.getColumnIndexOrThrow(DatabaseContract.MediaTable.MEDIA_LONG);

                cursor.moveToFirst();
                String city = cursor.getString(COLUMN_CITY);
                String state = cursor.getString(COLUMN_STATE);
                String country = cursor.getString(COLUMN_COUNTRY);
                double lat = cursor.getDouble(COLUMN_LAT);
                double lng = cursor.getDouble(COLUMN_LNG);

                if (isValidLatLng(lat, lng) && city == "" && state == "" && country == "")
                    return places;

                places.add(city);
                places.add(state);
                places.add(country);
            }
            mDatabase.setTransactionSuccessful();
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            mDatabase.endTransaction();
            //close();
            return places;
        }
    }

    private void addMediaRow (int id, MediaItem item) {
        if (item == null)
            return;

        //open(true);

        String city = "";
        String state = "";
        String country = "";
        if (item.getPlaces() != null) {
            ArrayList<String> places = item.getPlaces();
            if (places.size() != 0) {
                city = places.get(0);
                state = places.get(1);
                country = places.get(2);
            }
        }
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MediaTable.MEDIA_ID,id);
        values.put(DatabaseContract.MediaTable.MEDIA_TYPE,item.isVideo()); // Video : true - for now!
        values.put(DatabaseContract.MediaTable.MEDIA_PATH,item.getPath());
        values.put(DatabaseContract.MediaTable.MEDIA_CITY,city);
        values.put(DatabaseContract.MediaTable.MEDIA_STATE,state);
        values.put(DatabaseContract.MediaTable.MEDIA_COUNTRY,country);
        values.put(DatabaseContract.MediaTable.MEDIA_LAT,item.getLat());
        values.put(DatabaseContract.MediaTable.MEDIA_LONG,item.getLng());

        mDatabase.beginTransaction();
        try {
            long insertRow = mDatabase.insert(DatabaseContract.MediaTable.TABLE_NAME, null, values);
            if (-1 == insertRow)
                Log.d(TAG, "Failed to Insert Row");
            else {

            }
            mDatabase.setTransactionSuccessful();
        } catch (Exception e) {

        }

        finally {
            mDatabase.endTransaction();
            //close();
        }
    }

    private boolean isMediaIDAlreadyExistInDB (int id) {
        boolean bIsFound = false;
        //open(true);

        mDatabase.beginTransaction();
        try {
            Cursor cursor = mDatabase.rawQuery(DatabaseContract.MediaTable.ROW_MEDIA_ID_EXISTS + id, null);
            if (cursor.getCount() > 0) {
                bIsFound = true;
            }
            mDatabase.setTransactionSuccessful();
            cursor.close();
        } catch (Exception e) {

        }

        finally {
            mDatabase.endTransaction();
            //close();
            return bIsFound;
        }
    }

    private void throwIfNotOpen() throws IOException{
        if (!mDatabase.isOpen()) {
            throw new IOException("Database is not open! First call open() before doing any DB operations!");
        }
    }

    private boolean isValidLatLng(double lat, double lng){
        if (lat == 0 && lng == 0)
            return false; // Special case!
        if(lat < -90 || lat > 90) {
            return false;
        } else if(lng < -180 || lng > 180) {
            return false;
        }
        return true;
    }
}