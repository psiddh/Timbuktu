package app.com.timbuktu;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import app.com.timbuktu.layout.CollectionLayout;
import app.com.timbuktu.service.CollectionsWorkerTask;
import app.com.timbuktu.service.SyncMediaDetails;
import app.com.timbuktu.util.SystemUiHider;
import criteria.Criteria;

public class main_activity extends Activity implements Animation.AnimationListener, Loader.OnLoadCompleteListener<Cursor>, Circle.OnVoiceInputListener {
    private static final String TAG = "main_activity";

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private CursorLoader mCursorLoader = null;

    private ImageView mImgDisplay;

    // Animation
    private Animation mAnimZoomIn;

    //private VoiceInputView mVoiceInputView;


    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

     private ArrayList<String> mMatchResults  = new ArrayList<>(0);
     private TextSwitcher mSwitcher;
     int mTimeOutTextVals = 200;
     private int mIndex = 0;

    private final int SHOW_ANIM_TEXT = 1001;
    private final int SHOW_POST_ANIM_UI = 1002;
    private final int SHOW_TEXT = 1003;
    private final int SHOW_NEXT_ACTIVITY = 1004;

    FloatingActionButton fabButton;
    private UserFilterAnalyzer mAnalyzer;
    private CollectionsWorkerTask mTask;

    private Handler mTextSwictherHandler = new Handler() {
        public void handleMessage (Message msg) {
            switch (msg.what) {
                case SHOW_ANIM_TEXT:
                    //success handling
                    updateTextSwitcherText();
                    break;
                case SHOW_TEXT:
                    updateFinalTextSwitcherText();
                    break;
                case SHOW_POST_ANIM_UI:
                    //mVoiceInputView.onUpdateUIPostSpeech();
                    break;
                case SHOW_NEXT_ACTIVITY:
                    Intent intent = new Intent(getApplicationContext(), collections_activity.class);
                    startActivity(intent);
                    break;
                default:
                    //failure handling
                    break;
            }
        }
    };

    // method to Update the TextSwitcher Text
    private void updateFinalTextSwitcherText() {
        String result = mMatchResults.get(0);
        mAnalyzer = new UserFilterAnalyzer(this, result);
        Pair<Long,Long> pairRange = mAnalyzer.getDateRange(result);
        int matchState = mAnalyzer.getMatchState();
        ArrayList<String> places = SyncCache.getInstance().getMatchingPlacesFromUserFilter(result);

        mSwitcher.setText(result + "...");

        if (pairRange != null)
            Log.d(TAG, "DBG: DATE RANGE - " + pairRange.first + " to " + pairRange.second);
        Log.d(TAG, "DBG: PLACES FOUND - " + places.size());
        for (String s : places) {
            Log.d(TAG, "DBG: " + s);
        }
        Log.d(TAG, "DBG: MATCH STATE - " + matchState);

        Criteria criteria = new Criteria();
        criteria.add(pairRange);
        criteria.add(places);

        mTask.runWithCriteria(criteria);

    }

        // method to Update the TextSwitcher Text
     private void updateTextSwitcherText() {

        if (mIndex + 1 < mMatchResults.size()) {
            mSwitcher.setText(mMatchResults.get(mIndex++));
            Message mesg = new Message();
            mesg.what = SHOW_ANIM_TEXT;
            mTextSwictherHandler.sendMessageDelayed(mesg, mTimeOutTextVals);
        } else if (mMatchResults.size() > 0) {
            mIndex = 0;
            mTextSwictherHandler.removeMessages(SHOW_ANIM_TEXT, mTimeOutTextVals);
            mSwitcher.setText(mMatchResults.get(0) + "...");
            bounce();
            Message mesg = new Message();
            mesg.what = SHOW_NEXT_ACTIVITY;
            mTextSwictherHandler.sendMessageDelayed(mesg, 2500);
            //mVoiceInputView.onUpdateUIPostSpeech();
        }
    }

    private void bounce() {
        mSwitcher.clearAnimation();
        TranslateAnimation translation;
        translation = new TranslateAnimation(0f, 0F, 0f, getDisplayHeight());
        translation.setStartOffset(500);
        translation.setDuration(2000);
        translation.setFillAfter(false);
        translation.setInterpolator(new BounceInterpolator());
        mSwitcher.startAnimation(translation);

    }

    private int getDisplayHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public void setupTextSwitcher() {
        mSwitcher = (TextSwitcher) findViewById(R.id.textSwitcher);
        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like color, size etc
                TextView myText = new TextView(getBaseContext());
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                myText.setTextSize(24);
                myText.setTextColor(Color.rgb(128, 128, 128));
                return myText;
            }
        });
        // Declare the in and out animations and initialize them
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this,android.R.anim.fade_out);

        // set the animation type of textSwitcher
        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);

        /*updateTextSwitcherText();

        Message msg = new Message();
        msg.what = SHOW_ANIM_TEXT;
            mTextSwictherHandler.removeMessages(SHOW_ANIM_TEXT, mTimeOutTextVals);
        mTextSwictherHandler.sendMessageDelayed(msg, mTimeOutTextVals);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        /*fabButton  = new FloatingActionButton.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.voice))
                .withButtonSize(100)
                .withButtonColor(Color.WHITE)
                .withGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL)
                .withMargins(0, 0, 16, 16)
                .create();*/

        Circle circle = (Circle) findViewById(R.id.circle);
        circle.setOnVoiceInputListener(this);
        CircleAngleAnimation animation = new CircleAngleAnimation(circle, 360);
        animation.setDuration(3000);
        circle.startAnimation(animation);

        createCursorLoader();
        setupTextSwitcher();

        mSystemUiHider = SystemUiHider.getInstance(this, mSwitcher, HIDER_FLAGS);
        mSystemUiHider.setup();

        mTask = new CollectionsWorkerTask();
        mTask.start();

        //mVoiceInputView = (VoiceInputView) findViewById(R.id.voiceview);
        //mVoiceInputView.setOnVoiceInputListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mTextSwictherHandler.removeMessages(SHOW_ANIM_TEXT, mTimeOutTextVals);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTextSwictherHandler.removeMessages(SHOW_ANIM_TEXT, mTimeOutTextVals);
        mTask.stop();
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onLoadComplete(Loader<Cursor> loader, Cursor data) {
        if (Looper.myLooper() == Looper.getMainLooper())
            Log.d(TAG, "DBG: In on onLoadComplete");
        new SyncMediaDetails(this, data).execute();
    }

    private void createCursorLoader() {
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

        mCursorLoader.registerListener(10001, this);
        mCursorLoader.startLoading();
    }

    @Override
    public void onVoiceInputStart() {

    }

    @Override
    public void onVoiceInputDone(String text) {

    }

    @Override
    public void onVoiceMatchResults(ArrayList<String> matchResults) {
        mMatchResults.clear();
        mMatchResults = new ArrayList<>(matchResults);
        Message msg = new Message();
        msg.what = SHOW_ANIM_TEXT;
        mTextSwictherHandler.removeMessages(SHOW_ANIM_TEXT, mTimeOutTextVals);
        mTextSwictherHandler.sendMessageDelayed(msg, mTimeOutTextVals);
    }

    @Override
    public void onVoiceStatus(int code, String text) {
        switch (code) {
            case 0: // Error
            case 1: // Ready for speech
                mMatchResults.clear();
                mMatchResults.add(text);
                Message msg = new Message();
                msg.what = SHOW_TEXT;
                mTextSwictherHandler.removeMessages(SHOW_TEXT, 1000);
                mTextSwictherHandler.sendMessageDelayed(msg, 0);
                break;
            default:
                break;
        }
    }

}
