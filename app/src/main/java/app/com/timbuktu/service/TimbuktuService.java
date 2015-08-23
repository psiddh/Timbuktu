package app.com.timbuktu.service;

import android.app.Service;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TimbuktuService extends Service implements Loader.OnLoadCompleteListener<Cursor> {

    private static final int LOADER_ID = 10001;
    private static final String TAG = "TimbuktuService";
    private int startWhat = 0;
    private CursorLoader mCursorLoader = null;
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    public static final String SCAN_MEDIA = "app.com.timbuktu.ACTION-SCAN-MEDIA";

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        TimbuktuService getService() {
            return TimbuktuService.this;
        }
    }


    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "DBG: In on onLoadComplete");

        if (startWhat == 1) {
            new SyncMediaDetails(this, data).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, null);
            Log.d(TAG, "DBG: Start SyncMediaDetails");
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "DBG: In on create");
        if (intent.getAction().equalsIgnoreCase(SCAN_MEDIA)) {
            startWhat = 1;
        }
        createCursorLoader();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    public void stop() {
        clear();
        int stopId = -1;
        Log.d(TAG, "Stopping the service #: " + stopId);
        if (stopId != -1)
            stopSelf(stopId);
        else
            stopSelf();
    }

    private void clear() {
        if (mCursorLoader != null) {
            if (!mCursorLoader.isReset()) {
                mCursorLoader.unregisterListener(this);
                mCursorLoader.reset();
            }
            mCursorLoader.cancelLoad();
            mCursorLoader.stopLoading();
        }
    }

    public void createCursorLoader() {
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.MIME_TYPE,
                // Video columns
                MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.DESCRIPTION,
                MediaStore.Video.VideoColumns.TAGS,
                MediaStore.Video.VideoColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.LATITUDE,
                MediaStore.Video.VideoColumns.LONGITUDE,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.Video.VideoColumns.SIZE,

                // Generic column for both Video and Image
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };

        // Return only video and image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String sortOder = MediaStore.Images.Media.DATE_TAKEN + " ASC, " + MediaStore.Video.VideoColumns.DATE_TAKEN  + " ASC";
        Uri files = MediaStore.Files.getContentUri("external");//MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        mCursorLoader = new CursorLoader(this, files,
                projection,                                                 // Which columns to return
                selection,                                                  // Which rows to return
                null,                                                       // Selection arguments (none)
                sortOder                                                    // Ordering
        );

        mCursorLoader.registerListener(LOADER_ID, this);
        mCursorLoader.startLoading();
    }
}