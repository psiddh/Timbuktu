package app.com.timbuktu;

import java.util.ArrayList;
import java.util.Arrays;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class VoiceInputView extends View implements RecognitionListener {

    private static final String TAG = VoiceInputView.class.getName();

    private static final int STATE_NONE = -1;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_PRESSED = 1;
    private static final int STATE_POST_ANIM = 2;


    private Bitmap mNormalBitmap;
    private Bitmap mPressedBitmap;
    private Paint mPaint;
    private Paint mDrawablePaint;

    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private OnVoiceInputListener mOnVoiceInputListener;

    private int mState = STATE_NORMAL;
    private boolean mIsRecording = false;
    private float mMinRadius;
    private float mMaxRadius;
    private float mCurrentRadius;

    private boolean bCancelAnimation = false;
    private SpeechRecognizer mSpeech = null;
    private Intent mRecognizerIntent;

    private final int ANIMATE_RADIUS = 1001;
    private final int ANIMATE_POST = 1002;

    private final int mTimeOutTextVals = 1000;

    private float mStartX, mStartY, mStopX, mStopY = 0;

    private Handler mHandler = new android.os.Handler();

    private boolean TESTING = true;

    private Runnable mAnimateRunnable = new Runnable() {
        @Override
        public void run() {
            animateRadius(300);
            //if (mState == STATE_PRESSED)
                repeatAnimation();
        }
    };

    public VoiceInputView(Context context) {
        super(context);
        init();
    }

    public VoiceInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mNormalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.voice);
        mPressedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.voice_pressed);

        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);


        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                40, getContext().getResources().getDisplayMetrics());
        mMinRadius = px / 2;
        mCurrentRadius = 0;//mMinRadius;
        setupSpeechRecognition();
    }

    private void repeatAnimation() {
        mHandler.postDelayed(mAnimateRunnable, 300);
    }

    private void stopAnimation() {
    }

    private void setupSpeechRecognition() {
        mSpeech = SpeechRecognizer.createSpeechRecognizer(getContext());
        mSpeech.setRecognitionListener(this);
        mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getContext().getPackageName());
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMaxRadius = Math.min(w, h) / 2;
        Log.d(TAG, "MaxRadius: " + mMaxRadius);
    }

    @Override
    protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus,direction, previouslyFocusedRect);
        bCancelAnimation = !gainFocus;
        if (bCancelAnimation) {
            if(mAnimatorSet.isRunning()){
                mAnimatorSet.cancel();
            }
            mState = STATE_NONE;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setClickable(true);
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (mState == STATE_POST_ANIM) {
            //canvas.drawBitmap(mContentCreateBitmap, width / 2 - mMinRadius - 10, height / 2 - mMinRadius, mPaint);
            //repeatPostAnim();

            /*Message mesg = new Message();
            mesg.what = ANIMATE_POST;
            mAnimateHandler.sendMessageDelayed(mesg, mPostTimeOutTextVals);*/
            //mState = STATE_NORMAL;
        }

        if (mCurrentRadius  == 0 ) {
            mCurrentRadius = (float) (getWidth() / 2.6);
            mCurrentRadius /= 4;
        }
        if (mCurrentRadius > ((float) (getWidth() / 2.6)) * 3 ) {
            mCurrentRadius = (float) (getWidth() / 2.6);
            mCurrentRadius /= 4;
        }


        /*if(mCurrentRadius > mMinRadius){
            canvas.drawCircle(width / 2, height / 2, mCurrentRadius, mPaint);
        }*/
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCurrentRadius, mPaint);


        switch (mState){
            case STATE_NORMAL:
                //canvas.drawCircle(width / 2, height / 2, mCurrentRadius, mPaint);
                //canvas.drawBitmap(mNormalBitmap, width / 2 - mMinRadius -10,  height / 2 - mMinRadius, mPaint);
                canvas.drawBitmap(mNormalBitmap, (getWidth() - mNormalBitmap.getWidth()) / 2,
                        (getHeight() - mNormalBitmap.getHeight()) / 2, mDrawablePaint);
                break;
            case STATE_PRESSED:
                canvas.drawBitmap(mPressedBitmap, (getWidth() - mPressedBitmap.getWidth()) / 2,
                        (getHeight() - mPressedBitmap.getHeight()) / 2, mDrawablePaint);
                break;
        }
    }

    public void animateRadius(float radius){
        bCancelAnimation = true;
        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
        }
       float  r  = getCurrentRadius();
        mAnimatorSet.playSequentially(
                ObjectAnimator.ofFloat(this, "CurrentRadius", getCurrentRadius(), radius).setDuration(300),
               // ObjectAnimator.ofFloat(this, "CurrentRadius", 2 * getCurrentRadius(), 3 * getCurrentRadius()).setDuration(300),
               // ObjectAnimator.ofFloat(this, "CurrentRadius", 3 * getCurrentRadius(), 2 * getCurrentRadius()).setDuration(300),
                ObjectAnimator.ofFloat(this, "CurrentRadius", radius, getCurrentRadius()).setDuration(300)

        );
        mAnimatorSet.start();
    }

    public float getCurrentRadius() {
        return mCurrentRadius;
    }

    public void setCurrentRadius(float currentRadius) {
        mCurrentRadius = currentRadius;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                mState = STATE_PRESSED;
                mSpeech.startListening(mRecognizerIntent);
                mOnVoiceInputListener.onVoiceInputStart();
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                /*if(mIsRecording){
                    mState = STATE_NORMAL;
                    mSpeech.stopListening();
                }
                if(mAnimatorSet.isRunning()){
                   mAnimatorSet.cancel();
                }
                mIsRecording = !mIsRecording;*/
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void setOnVoiceInputListener(OnVoiceInputListener OnVoiceInputListener) {
        mOnVoiceInputListener = OnVoiceInputListener;
    }

    public void onUpdateUIPostSpeech() {
        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
        }
        mState = STATE_POST_ANIM;
        //mAnimateHandler.removeMessages(ANIMATE_RADIUS);
        invalidate();
    }

    public static interface OnVoiceInputListener{
        public void onVoiceInputStart();
        public void onVoiceInputDone(String text);
        public void onVoiceMatchResults( ArrayList<String> matchResults);
        public void onVoiceStatus(int code, String text);

    }

    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, "onResults");
        bCancelAnimation = true;
        ArrayList<String> matchResults = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (!matchResults.isEmpty()) {
            if(mOnVoiceInputListener != null){
                mOnVoiceInputListener.onVoiceMatchResults(matchResults);
            }
        }
        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
            mCurrentRadius /= 4;
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        mOnVoiceInputListener.onVoiceStatus(1, "");
        // TODO Auto-generated method stub
        repeatAnimation();

        /*Message mesg = new Message();
        mesg.what = ANIMATE_RADIUS;
        mAnimateHandler.sendMessageDelayed(mesg, mTimeOutTextVals);*/
    }

    @Override
    public void onBeginningOfSpeech() {
        // TODO Auto-generated method stub
        mOnVoiceInputListener.onVoiceStatus(1, "");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEndOfSpeech() {
        // TODO Auto-generated method stub
        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
            mAnimatorSet.end();
        }
        done();
    }

    @Override
    public void onError(int error) {
        // TODO Auto-generated method stub

        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
            mAnimatorSet.end();
        }
        if(mOnVoiceInputListener != null){
            mState = STATE_NORMAL;

            if (TESTING) {
                ArrayList<String> matchResults = new ArrayList<>();
                matchResults.add("San Francisco");
                mOnVoiceInputListener.onVoiceMatchResults(matchResults);
            } else {
                mOnVoiceInputListener.onVoiceStatus(0, "Sorry. I did not get you. Please try again!");
            }


        }
        done();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // TODO Auto-generated method stub

    }

    private void done() {
        if(mIsRecording || true){
            mState = STATE_NORMAL;
            mSpeech.stopListening();
        }
        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
        }
        mIsRecording = !mIsRecording;
    }
}