package app.com.timbuktu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import app.com.timbuktu.service.SyncMediaDetails;
import app.com.timbuktu.service.TimbuktuService;
import app.com.timbuktu.util.SystemUiHider;

public class splash_fragmentview extends Activity implements Animation.AnimationListener, Loader.OnLoadCompleteListener<Cursor>, VoiceInputView.OnVoiceInputListener {
    private static final String TAG = "splash_fragmentview";

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

    private VoiceInputView mVoiceInputView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_activity);
        createCursorLoader();

        TextView view = (TextView) findViewById(R.id.textview);
        mSystemUiHider = SystemUiHider.getInstance(this, view, HIDER_FLAGS);
        mSystemUiHider.setup();
        mVoiceInputView = (VoiceInputView) findViewById(R.id.voiceview);
        mVoiceInputView.setOnVoiceInputListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(5000);
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
}
