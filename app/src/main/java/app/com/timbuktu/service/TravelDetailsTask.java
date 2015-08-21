package app.com.timbuktu.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import app.com.timbuktu.GeoDecoder;
import app.com.timbuktu.TravelItem;
import app.com.timbuktu.UserFilterAnalyzer;

public class TravelDetailsTask extends AsyncTask<Object, TravelItem, Object> {

    private int mPictureId = -1;
    private int mPictureDisplayName = -1;
    private int mPictureDateColumn = -1;
    private int mPictureTitleColumn = -1;
    private int mPictureDataColumn = -1;

    private int mVideoId = -1;
    private int mVideoMimeType = -1;
    private int mVideoLatitude = -1;
    private int mVideoLongitude = -1;
    private int mVideoDataColumn = -1;
    private int mVideoDateColumn = -1;
    private int mVideoDuration = -1;
    private int mVideoSize = -1;
    private int mVideoDescription = -1;
    private int mVideoTag = -1;

    private int mFileMimeType = -1;

    private Context mContext;
    private Cursor mCursor;

    private UserFilterAnalyzer mAnalyzer;
    private HashMap<Integer, String> mHashMaLocation = new HashMap<>();

    Calendar mTitleCalendar = Calendar.getInstance((Locale.getDefault()));
    Pair<Long, Long> mPairRange = null;
    int mMatchState = -1;
    String mUserFilter = "";
    boolean mIsTitleDate = false;
    boolean bMatchPictureIDsOnly = false;

    private static final String TAG = "TravelDetailsTask";


    public TravelDetailsTask(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        setupCursor(mCursor);
        mAnalyzer = new UserFilterAnalyzer(mContext, mUserFilter);
        mPairRange = mAnalyzer.getDateRange(mUserFilter);
        String title = getTitleFromPair(mPairRange);
        mMatchState = mAnalyzer.getMatchState();
    }

    @Override
    protected Object doInBackground(Object... params) {
        try {
            startClustering(mCursor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Start Clustering!");
        return null;
    }

    @Override
    public void onProgressUpdate(TravelItem... params) {

    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Object result) {

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

            mVideoId = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns._ID);
            mVideoMimeType = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.MIME_TYPE);
            mVideoLatitude = cur.getColumnIndex(
                    MediaStore.Video.VideoColumns.LATITUDE);
            mVideoLongitude = cur.getColumnIndex(
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
        cur.moveToLast();
    }

    private void startClustering(final Cursor cur) {
        setupCursor(mCursor);
        tryClustering(cur);
    }

    private void stopClustering() {
    }

    private String getLocationByPath(int pictureId, String currentPath) {
        String location = mHashMaLocation.get(new Integer(pictureId));

        if (location == null) {
            location = GeoDecoder.getLocationByGeoDecoding(mContext, currentPath);
            if (location != null) {
                mHashMaLocation.put(new Integer(pictureId), location);
            }
        }

        return location;
    }

    private String getLocationByGeoCoordinates(int id, double latitude, double longitude) {
        String location = mHashMaLocation.get(new Integer(id));

        if (location == null) {
            location = GeoDecoder.getLocationByGeoDecoding(mContext, latitude, longitude);

            if (location != null) {
                mHashMaLocation.put(new Integer(id), location);
            }
        }

        return location;
    }

    private void tryClustering(Cursor cur) {
        if (cur.isClosed() || isCancelled()) return;


        long clusterStartTime = 0;
        long clusterEndTime = 0;
        int currentPicId = -1;
        String currentDate = null;
        String currentPath = null;
        String[] places = new String[16];
        String clusterItemDateTime = null;

        Uri uri = null;

        if (isCursorPosAtTypeVideo(cur)) {
            if (mVideoDateColumn != -1) {
                long date = cur.getLong(mVideoDateColumn);
            }
        } else if (mPictureDateColumn != -1 && !cur.isClosed() && cur.getCount() > 0) {
            currentDate = cur.getString(mPictureDateColumn);
            if (currentDate != null) {
                clusterStartTime = Long.parseLong(currentDate);
                //clusterEndTime = clusterStartTime - mCriteria.getMinimumDurationForTimeCriteria() * 60000; // Convert minutes to MS
            }

        }
        currentDate = cur.getString(mPictureDateColumn);
        if (currentDate != null) {
            clusterStartTime = Long.parseLong(currentDate);
        }

        if (mPictureId != -1) {
            currentPicId = cur.getInt(mPictureId);
        }
        if (mPictureDataColumn != -1) {
            currentPath = cur.getString(mPictureDataColumn);
        }

        do {


        } while (!cur.isClosed() && !cur.isFirst() && !cur.isBeforeFirst());
    }


    private String getDate(long milliSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);

        return cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR);
    }

    private String getTime(long milliSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);
        String AM_PM = (cal.get(Calendar.AM_PM) == 1) ? "PM" : "AM";

        int Minute = cal.get(Calendar.MINUTE);
        String MINUTE = (Minute < 10) ? "0" + Minute : "" + Minute;

        return cal.get(Calendar.HOUR) + ":" + MINUTE + " " + AM_PM;
    }

    private String getDateAndTime(long milliSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliSeconds);

        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);

        return "" + cal.get(Calendar.YEAR) +
                (month < 10 ? "0" + (month + 1) : (month + 1)) +
                (day < 10 ? "0" + day : day) +
                (hour < 10 ? "0" + hour : hour) +
                (minute < 10 ? "0" + minute : minute);
    }

    /*private void notifyNewCluster(PictureCluster newCluster) {
        if (MainActivity.class == null || mMainUIHandler == null) {
            return;
        }
        MainActivity.ServiceHandler uiThreadHandler = mMainUIHandler.get();
        if (uiThreadHandler == null) return;

        Message msg = new Message();
        msg.what = MSG_CLUSTER_ADDED;
        uiThreadHandler.sendMessage(msg);

    }

    private void notifyStateChange(int what) {
        if (MainActivity.class == null || mMainUIHandler == null) {
            return;
        }
        MainActivity.ServiceHandler uiThreadHandler = mMainUIHandler.get();
        if (uiThreadHandler == null) return;

        Message msg = new Message();
        msg.what = what;
        uiThreadHandler.sendMessage(msg);

    }

    private void doCommit(final PictureCluster newCluster) {
        Uri[] uris = newCluster.getImagesUri();
        List<Media> images = new ArrayList<>(uris.length);
        for (Uri uri : uris) {
            images.add(new Media(Media.IMAGE, uri));
        }

        EventHandler.getInstance(mContext).createEventAsync(
                EventHandler.PHOTO,
                newCluster.getBaseTime(),
                newCluster.getBaseTimeOffset(),
                null,
                null,
                null,
                new Location[]{newCluster.getLocation()},
                null,
                images,
                null,
                null,
                null,
                new EventHandler.EventAsyncListener() {
                    @Override
                    public void onFinish(int... ids) {
                        int id = ids[0];

                        if (id > 0) {
                            newCluster.setEventId(id);
                        }
                    }
                }
        );
    }*/


    private boolean isCursorPosAtTypeVideo(Cursor cur) {
        if (cur.isClosed() || isCancelled() || cur.getCount() <= 0 || mFileMimeType == -1)
            return false;

        return (cur.getInt(mFileMimeType) == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
    }

    private void doCommitVideoEntryAndPersist(Cursor cur, final boolean bPersist) {

        mVideoId = cur.getColumnIndex(
                MediaStore.Video.VideoColumns._ID);
        mVideoMimeType = cur.getColumnIndex(
                MediaStore.Video.VideoColumns.MIME_TYPE);
        mVideoLatitude = cur.getColumnIndex(
                MediaStore.Video.VideoColumns.LATITUDE);
        mVideoLongitude = cur.getColumnIndex(
                MediaStore.Video.VideoColumns.LONGITUDE);
        mVideoDataColumn = cur.getColumnIndex(
                MediaStore.Video.VideoColumns.DATA);
        mVideoDateColumn = cur.getColumnIndex(
                MediaStore.Video.VideoColumns.DATE_TAKEN);
        mVideoDuration = cur.getColumnIndex(
                MediaStore.Video.VideoColumns.DURATION);
        mVideoSize = cur.getColumnIndex(
                MediaStore.Video.VideoColumns.SIZE);

        int currentVideoId = 0;
        String videoMimeType = "", videoPath = "", description = null, tags = null;
        Double latitude = 0.0, longitude = 0.0;
        long date = 0, size = 0;
        int duration = 0;

        if (mVideoId != -1) {
            currentVideoId = cur.getInt(mPictureId);
            //Log.d(TAG, "xxx currentVideoId - " + currentVideoId);
        }

        if (mVideoMimeType != -1) {
            videoMimeType = cur.getString(mVideoMimeType);
            //Log.d(TAG, "xxx videoMimeType - " + videoMimeType);
        }

        if (mVideoLatitude != -1) {
            latitude = cur.getDouble(mVideoLatitude);
            //Log.d(TAG, "xxx latitude - " + latitude);
        }

        if (mVideoLongitude != -1) {
            longitude = cur.getDouble(mVideoLongitude);
            //Log.d(TAG, "xxx longitude - " + longitude);
        }

        if (mVideoDataColumn != -1) {
            videoPath = cur.getString(mVideoDataColumn);
            //Log.d(TAG, "xxx videoPath - " + videoPath);
        }

        if (mVideoDateColumn != -1) {
            date = cur.getLong(mVideoDateColumn);
            //Log.d(TAG,  "xxx date - " + date);
        }

        if (mVideoDuration != -1) {
            duration = cur.getInt(mVideoDuration);
            //Log.d(TAG, "xxx duration - " + duration);
        }

        if (mVideoSize != -1) {
            size = cur.getLong(mVideoSize);
            //Log.d(TAG, "xxx size - " + size);
        }

        if (mVideoDescription != -1) {
            description = cur.getString(mVideoDescription);
            //Log.d(TAG, "xxx Descp - " + description);

        }

        if (mVideoTag != -1) {
            tags = cur.getString(mVideoTag);
            //Log.d(TAG, "xxx Tags - " + tags);
        }

    }

    private String getTitleFromPair(Pair<Long, Long> pair) {
        long ms_in_day = 86400000;
        Calendar current = Calendar.getInstance((Locale.getDefault()));
        long secondPair = 0;
        String title = "";
        if (pair == null)
            return title;

        if (pair.second - pair.first <= ms_in_day) {
            mIsTitleDate = true;
        }
        mTitleCalendar.clear();
        mTitleCalendar.setTimeInMillis(pair.first);
        title += mTitleCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
        title += " ";

        title += mTitleCalendar.get(Calendar.DAY_OF_MONTH);
        title += ", '";

        title += mTitleCalendar.get(Calendar.YEAR) % 100;

        if (pair.second - pair.first <= ms_in_day) {
            return title;
        }
        title += " - ";

        if (pair.second >= current.getTimeInMillis()) {
            secondPair = current.getTimeInMillis();
            title += "(Today) ";
        }
        else {
            secondPair = pair.second;
        }
        mTitleCalendar.setTimeInMillis(secondPair);
        title += mTitleCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH);
        title += " ";

        title += mTitleCalendar.get(Calendar.DAY_OF_MONTH);
        title += ", '";

        title += mTitleCalendar.get(Calendar.YEAR) % 100;

        mIsTitleDate = true;
        return title;
    }
}
