package app.com.timbuktu.collections;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import app.com.timbuktu.MediaItem;
import app.com.timbuktu.SyncCache;
import criteria.Criteria;

public class CollectionsWorkerTask {

    private static String TAG = CollectionsWorkerTask.class.getName();
    HandlerThread mHandlerThread = new HandlerThread(TAG);
    private Handler mHandler;
    private Criteria mCriterion;
    //ArrayList<ArrayList<Integer>> mCollectionsIds = new ArrayList<ArrayList<Integer>>();

    private Collections mCollections;
    private ICollectionResults mCallback = null;

    private SyncCache sIntance = SyncCache.getInstance();

    private static final int TASK_STOPPED = -1;
    private static final int TASK_PRESTART = 1;
    private static final int TASK_INPROGRESS = 1;
    private static final int TASK_COMPLETED= 2;

    private int mTaskState = TASK_PRESTART;

    public CollectionsWorkerTask(Context context) {
        mCollections = new Collections();
        mCallback = (ICollectionResults) context;
    }

    public void start() {
        if (!canStart()) {
            Log.d(TAG, "Cannot start as it is already attempted to start");
            return;
        }

        if (mCallback == null) {
            Log.d(TAG, "Callback not set! So return");
            return;
        }
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
        mCriterion = new Criteria();
    }

    public void runWithCriteria(Criteria criterion) {
        if (!canStart()) {
            Log.d(TAG, "Cannot start as it is already attempted to start");
            return;
        }

        if (mCallback == null) {
            Log.d(TAG, "Callback not set! So return");
            return;
        }

        if (criterion == null || !criterion.isCriteriaSet()) {
            Log.d(TAG, "Ignore this Run request as the criteria seems to be not set or invalid!");
            return;
        }
        if (mTaskState == TASK_INPROGRESS) {
            if (criterion.equals(mCriterion)) {
                Log.d(TAG, "Another request to run with same criteria . So ignore!");
                return;
            }

            // What do we do ? restart !!!
            stop();
            start();
        }

        try {
            assertHandler();
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            return;
        }

        mCriterion = criterion;
        mHandler.post(new CollectionsTask());
        mTaskState = TASK_INPROGRESS; // In-Progress
    }



    public void stop() {
        if (mTaskState != TASK_STOPPED) {
            Log.d(TAG, "The task is already stopped!");
            return;
        }
        try {
            assertHandler();
            mHandlerThread.quitSafely();

        } catch (IllegalStateException ex) {
            ex.printStackTrace();
        }

        finally {
            mHandler.removeCallbacks(null);
            mCriterion.clearAll();
            mTaskState = TASK_STOPPED;
        }
    }

    private boolean canStart() {
        return (mTaskState == TASK_PRESTART);
    }

    private void assertHandler() throws IllegalStateException {
        if (mHandlerThread == null || !mHandlerThread.isAlive()) {
            throw new IllegalStateException("Handler not initialized. First call start()");
        }
    }

    private class CollectionsTask implements Runnable {

        private int CITY_INDEX = 0;
        private int STATE_INDEX = 1;
        private int COUNTRY_INDEX = 2;

        private int MAX_PLACE_INDICES = 3;

        private String[] places = new String[MAX_PLACE_INDICES];
        private Long startDateRange;
        private Long endDateRange;

        private boolean bDateRangeSet = false;
        private boolean bPlaceSet = false;


        public CollectionsTask() {
            if (mCriterion.isCriteriaSet()) {
                int size = mCriterion.getPlaces().size();
                if (size != 0) {
                    if (size > 0)
                        places[CITY_INDEX] = mCriterion.getPlaces().get(CITY_INDEX);
                    if (size > 1)
                        places[STATE_INDEX] = mCriterion.getPlaces().get(STATE_INDEX);
                    if (size > 2)
                        places[COUNTRY_INDEX] = mCriterion.getPlaces().get(COUNTRY_INDEX);
                    bPlaceSet = (size > 0) ? true : false;
                }

                startDateRange = mCriterion.getDateRange().first;
                endDateRange = mCriterion.getDateRange().second;

                if (startDateRange != null && endDateRange != null) {
                    bDateRangeSet = true;
                }
            }
        }

        @Override
        public void run() {
            if (!bPlaceSet && !bDateRangeSet) {
                Log.d(TAG, "No Cirteria set. Abort");
                return;
            }
            LinkedHashMap cache = sIntance.getMap();
            ArrayList<String> placeFound = mCriterion.getPlaces();
            Pair<Long, Long> dateFound = mCriterion.getDateRange();
            boolean found = false;
            Collection coll = new Collection();

            ArrayList<Integer> ids = new ArrayList<>();
            for (Object i : cache.keySet()) {
                Integer id = (Integer) i;
                MediaItem item = sIntance.getMediaItem(id);
                found = false;

                if (bPlaceSet && bDateRangeSet) {
                    if (item.getPlaces().contains(placeFound)) {
                        if ((item.getTimeStamp() >= dateFound.first) && (item.getTimeStamp() <= dateFound.second)) {
                            found = true;
                        }
                    }
                } else if (bPlaceSet) {
                    String place = placeFound.get(0);
                    if (place == null) continue;
                    if (item.getPlaces().contains(place)) {
                        found = true;
                    }
                } else if (bDateRangeSet) {
                    if ((item.getTimeStamp() >= dateFound.first) && (item.getTimeStamp() <= dateFound.second)) {
                        found = true;
                    }
                }

                if (found && isDefaultCriteriaMet(id)) {
                    coll.add(id);
                } else if (coll.size() != 0) {
                    mCollections.add(coll);
                    coll.reset();
                } else if (found) {

                    mCollections.add(coll);
                    coll.reset();
                    coll.add(id);

                }
            }

            mTaskState = TASK_COMPLETED; // Task completed
            mCallback.onResults(mCollections);
        }

        private boolean isDefaultCriteriaMet(Integer id) {
            if (mCollections.size() == 0)
                return true;
            Collection collection = mCollections.at(mCollections.size() - 1);
            if (collection.size() == 0)
                return true;
            Integer lastId = collection.at(collection.size() - 1);

            MediaItem currItem = sIntance.getMediaItem(id);
            MediaItem lastItem = sIntance.getMediaItem(lastId);

            if (Math.abs(currItem.getTimeStamp() - lastItem.getTimeStamp()) <= Criteria.DEFAULT_TIMESTAMP_DIFF) {
                return true;
            }
            return false;
        }
    }
}
