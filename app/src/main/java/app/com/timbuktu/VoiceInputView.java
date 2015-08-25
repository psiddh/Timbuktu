package app.com.timbuktu;

import java.util.ArrayList;

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
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class VoiceInputView extends View implements RecognitionListener {

    private static final String TAG = VoiceInputView.class.getName();

    private static final int STATE_NORMAL = 0;
    private static final int STATE_PRESSED = 1;

    private Bitmap mNormalBitmap;
    private Bitmap mPressedBitmap;
    private Paint mPaint;
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

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255, 190, 220, 230));

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                40, getContext().getResources().getDisplayMetrics());
        mMinRadius = px / 2;
        mCurrentRadius = mMinRadius;
        mAnimatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //mCurrentRadius = mMinRadius;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                //mCurrentRadius = mMinRadius;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        setupSpeechRecognition();
    }

    private void repeatAnimation() {
        //if (bCancelAnimation) return;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        animateRadius(100);
                        repeatAnimation();
                    }
                },
                1200);
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
        bCancelAnimation = !gainFocus;
        if (bCancelAnimation) {
            if(mAnimatorSet.isRunning()){
                mAnimatorSet.cancel();
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //if(mCurrentRadius > mMinRadius){
            canvas.drawCircle(width / 2, height / 2, mCurrentRadius, mPaint);
        //}

        switch (mState){
            case STATE_NORMAL:
                canvas.drawBitmap(mNormalBitmap, width / 2 - mMinRadius -10,  height / 2 - mMinRadius, mPaint);
                break;
            case STATE_PRESSED:
                canvas.drawBitmap(mPressedBitmap, width / 2 - mMinRadius - 10,  height / 2 - mMinRadius, mPaint);
                break;
        }
    }

    public void animateRadius(float radius){
        bCancelAnimation = true;
        if(radius <= mCurrentRadius){
            return;
        }
        if(radius > mMaxRadius){
            radius = mMaxRadius;
        }else if(radius < mMinRadius){
            radius = mMinRadius;
        }
        if(radius == mCurrentRadius){
            return;
        }
        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
        }
        mAnimatorSet.playSequentially(
                ObjectAnimator.ofFloat(this, "CurrentRadius", getCurrentRadius(), 2 * getCurrentRadius()).setDuration(300),
                ObjectAnimator.ofFloat(this, "CurrentRadius", 2*getCurrentRadius(), 3 * getCurrentRadius()).setDuration(300),
                ObjectAnimator.ofFloat(this, "CurrentRadius", 3 * getCurrentRadius() , getCurrentRadius()).setDuration(600)
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
                invalidate();
                mState = STATE_PRESSED;
                mSpeech.startListening(mRecognizerIntent);
                mOnVoiceInputListener.onVoiceInputStart();
                return true;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                if(mIsRecording){
                    mState = STATE_NORMAL;
                    mSpeech.stopListening();
                }
                if(mAnimatorSet.isRunning()){
                    mAnimatorSet.cancel();
                }
                mIsRecording = !mIsRecording;
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public void setOnVoiceInputListener(OnVoiceInputListener OnVoiceInputListener) {
        mOnVoiceInputListener = OnVoiceInputListener;
    }

    public static interface OnVoiceInputListener{
        public void onVoiceInputStart();
        public void onVoiceInputDone(String text);
        public void onVoiceMatchResults( ArrayList<String> matchResults);
        public void onVoiceStatus(String text);

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
            mCurrentRadius = mMinRadius;

        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBeginningOfSpeech() {
        // TODO Auto-generated method stub
        repeatAnimation();
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
    }

    @Override
    public void onError(int error) {
        // TODO Auto-generated method stub
        if(mAnimatorSet.isRunning()){
            mAnimatorSet.cancel();
        }

    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // TODO Auto-generated method stub

    }
}