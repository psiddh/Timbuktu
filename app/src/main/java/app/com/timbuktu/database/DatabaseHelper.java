package app.com.timbuktu.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String TAG = "DatabaseHelper";
    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, boolean persistent, int version) {
        // if the database name is null, it will be created in-memory
        super(context, persistent ? DatabaseContract.DATABASE_NAME : null, null, version);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.MediaTable.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(final SQLiteDatabase db, int oldVersion, final int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("ALTER TABLE " + DatabaseContract.MediaTable.DELETE_TABLE + " RENAME TO DROP_" + DatabaseContract.MediaTable.DELETE_TABLE);

        // TBD: Need to figure Update strategy later!
        Thread dropThread = new Thread() {
            @Override
            public void run() {
                db.execSQL(DatabaseContract.MediaTable.DELETE_TABLE);
                Log.i(TAG, "Done removing existing data");
                db.needUpgrade(newVersion);
                // TBD: For now!
                onCreate(db);
            };
        };
        dropThread.start();
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }
}