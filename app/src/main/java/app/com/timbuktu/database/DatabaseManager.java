package app.com.timbuktu.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseManager {

    private static String TAG = "DatabaseManager";
    private AtomicInteger mRefCount = new AtomicInteger();

    private static DatabaseManager mInstance;
    private static DatabaseHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized DatabaseManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseManager();
            mDbHelper = new DatabaseHelper(context, true, 1);
        }

        return mInstance;
    }

    public synchronized SQLiteDatabase open(boolean bWritable) {
        if (mRefCount.incrementAndGet() == 1) {
            // open new d/b connection
            mDatabase = bWritable ? mDbHelper.getWritableDatabase() : mDbHelper.getReadableDatabase();
        }

        return mDatabase;
    }


    public synchronized void close() {
        if (mRefCount.decrementAndGet() == 0) {
            // Indeed close the d/b
            mDatabase.close();
        }
    }
}