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

    private static final int LOADER_ID = 50001;
    private static final String TAG = "TimbuktuService";
    // TBD: We may not need this as we will never ever try to stop the service from thread (Async Task)
    private static Stack<Integer> mServiceIds = new Stack<>();

    private List<Integer> mEventIds = new ArrayList<>();

    private CursorLoader mCursorLoader = null;

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

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
        new TravelDetailsTask(this, data).execute();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mServiceIds.size() > 1) {
            Log.d(TAG, "At-least one instance of the service is running " + mServiceIds.size());
        }
        int what = (intent == null) ? 0 : intent.getIntExtra("start-task-what", 0);
        if (what == 0) {
            // start all

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
        if (mServiceIds.size() > 0)
            stopId = mServiceIds.pop();
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

        mEventIds.clear();
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