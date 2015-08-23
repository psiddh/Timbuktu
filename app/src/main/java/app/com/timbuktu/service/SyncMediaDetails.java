package app.com.timbuktu.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

import java.util.List;
import java.util.Locale;

import app.com.timbuktu.MediaItem;
import app.com.timbuktu.SyncCache;


public class SyncMediaDetails extends AsyncTask<Object, MediaItem, Integer> {

    private int mPictureId = -1;
    private int mPictureDisplayName = -1;
    private int mPictureDateColumn = -1;
    private int mPictureTitleColumn = -1;
    private int mPictureDataColumn = -1;
    private int mPictureLatitudeColumn = -1;
    private int mPictureLongitudeColumn = -1;

    private int mVideoId = -1;
    private int mVideoMimeType = -1;
    private int mVideoLatitudeColumn = -1;
    private int mVideoLongitudeColumn = -1;
    private int mVideoDataColumn = -1;
    private int mVideoDateColumn = -1;
    private int mVideoDuration = -1;
    private int mVideoSize = -1;
    private int mVideoDescription = -1;
    private int mVideoTag = -1;
    private int mFileMimeType = -1;

    private Context mContext;
    private Cursor mCursor;
    private Geocoder mGeocoder;
    private SyncCache mSyncCache;
    private ProgressDialog mDialog;


    private static final String TAG = "SyncMediaDetails";

    public SyncMediaDetails(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mGeocoder = new Geocoder(mContext, Locale.getDefault());
        mSyncCache = new SyncCache();
        setupCursor(mCursor);
        setupDialog();
    }

    @Override
    protected Integer doInBackground(Object... params) {
        try {
            startClustering(mCursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Start Clustering!");
        return mCursor.getCount();
    }

    @Override
    protected void onProgressUpdate(MediaItem... params) {
        mDialog.incrementProgressBy(params.length);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Integer result) {
        mDialog.dismiss();
    }

    private void setupCursor(Cursor cur) {
        if (!cur.isClosed()) {
            mPictureId = cur.getColumnIndex(
                    MediaStore.Images.Media._ID);
            mPictureDisplayName = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            mPictureDateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);
            mPictureTitleColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.TITLE);
            mPictureDataColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATA);
            mPictureLatitudeColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.LATITUDE);
            mPictureLongitudeColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.LONGITUDE);

            mVideoId = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns._ID);
            mVideoMimeType = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.MIME_TYPE);
            mVideoLatitudeColumn = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.LATITUDE);
            mVideoLongitudeColumn = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.LONGITUDE);
            mVideoDataColumn = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.DATA);
            mVideoDateColumn = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.DATE_TAKEN);
            mVideoDuration = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.DURATION);
            mVideoSize = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.SIZE);
            mVideoDescription = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.DESCRIPTION);
            mVideoTag = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.TAGS);

            mFileMimeType = cur.getColumnIndex(
                    MediaStore.Files.FileColumns.MEDIA_TYPE );
        }
        // Work your way from the latest pics!
        cur.moveToFirst();
    }

    private void setupDialog() {
        mDialog = new ProgressDialog(mContext);
        mDialog.setTitle("Hola!");
        mDialog.setMessage("Please wait while we setup few things");
        mDialog.setIndeterminate(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setMax(mCursor.getCount());
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void startClustering(final Cursor cur) {
        setupCursor(mCursor);
        tryClustering(cur);
    }

    private void stopClustering() {
    }


    private void tryClustering(Cursor cur) {
        if (cur.isClosed() || isCancelled()) return;

        long timestamp = 0;
        int id = -1;
        String currentDate = null;
        String path = null;
        boolean isVideo = false;
        double lat = 0.0;
        double lng = 0.0;
        List<Address> addresses = null;

        do {
            if (isCursorPosAtTypeVideo(cur)) {
                if (mVideoDateColumn != -1) {
                    isVideo = true;
                    timestamp = cur.getLong(mVideoDateColumn);
                    id = cur.getInt(mVideoId);
                    lat = cur.getDouble(mVideoLatitudeColumn);
                    lng = cur.getDouble(mVideoLongitudeColumn);
                    path = cur.getString(mVideoDataColumn);
                }
            } else  {
                currentDate = cur.getString(mPictureDateColumn);
                if (currentDate != null) {
                    timestamp = Long.parseLong(currentDate);
                    id = cur.getInt(mPictureId);
                    lat = cur.getDouble(mPictureLatitudeColumn);
                    lng = cur.getDouble(mPictureLongitudeColumn);
                    path = cur.getString(mPictureDataColumn);
                }
            }
            addresses = getAddress(lat, lng);

            MediaItem item = new MediaItem(id, path, timestamp, lat, lng, addresses, isVideo);
            mSyncCache.addMediaItem(id, item);
            publishProgress(item);
            cur.moveToNext();

        } while (!cur.isClosed() && !cur.isLast() && !cur.isAfterLast());
    }

    public List<Address> getAddress(double lat, double lng) {
        List<Address> addresses = null;
        try {
            addresses = mGeocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() <= 0) {
                return addresses;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        finally {
            return addresses;
        }
    }

    private boolean isCursorPosAtTypeVideo(Cursor cur) {
        if (cur.isClosed() || isCancelled() || cur.getCount() <= 0 || mFileMimeType == -1)
            return false;

        return (cur.getInt(mFileMimeType) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
    }
}
