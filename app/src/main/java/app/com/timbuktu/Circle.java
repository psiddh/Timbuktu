package app.com.timbuktu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class Circle extends View implements RecognitionListener {

    private static final String TAG = "Circle:";
    private static final int START_ANGLE_POINT = 120;

    private final RectF rect;
    private float angle;

    private final Paint paint1;
    private final Paint paint2;
    private final Paint paint3;
    private final Paint paint4;
    private final Paint paint5;
    private final Paint mButtonPaint;

    private static int rect_left;
    private static int rect_top;
    private static int rect_right;
    private static int rect_bottom;

    private static int mWidth;
    private static int mHeight;
    private static int mSize = 300;

    private static int mBmpPosX;
    private static int mBmpPosY;

    private final Bitmap mNormalBitmap;
    private Handler mHandler = new android.os.Handler();

    private final int STATE_NONE = 0;
    private final int STATE_ANIMATE = 1;
    private final int STATE_INANIMATE = 2;

    private int mState = STATE_NONE;

    private SpeechRecognizer mSpeech = null;
    private Intent mRecognizerIntent;
    private boolean mIsRecording = false;

    private OnVoiceInputListener mOnVoiceInputListener;

    private Runnable mAnimate = new Runnable() {
        @Override
        public void run() {
            angle +=10;
            if (angle >= 360)
                angle = 0;
            invalidate();
        }
    };

    private void repeatAnimation() {
        if (mState == STATE_ANIMATE) {
            mHandler.removeCallbacksAndMessages(mAnimate);
            mHandler.postDelayed(mAnimate, 100);
        }
    }

    private void stopAnimation() {
        mHandler.removeCallbacksAndMessages(mAnimate);
    }

    private void stopListening() {
        mState = STATE_NONE;
        if (mIsRecording)
            mSpeech.stopListening();
        stopAnimation();
        mIsRecording = false;
    }

    private void startListening() {
        mState = STATE_ANIMATE;
        mSpeech.startListening(mRecognizerIntent);
        repeatAnimation();
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


    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Initial Angle (optional, it can be zero)
        angle = 0;
        final int mainCircleStrokeWidth = 30;
        final int secondCircleWidth = 30;
        rect = new RectF(rect_left, rect_top, rect_right, rect_bottom);


        paint1 = new Paint();
        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(mainCircleStrokeWidth);
        //Circle color
        paint1.setColor(Color.rgb(255, 228, 225)); // paleRoseColor

        paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(mainCircleStrokeWidth);
        //Circle color
        paint2.setColor(Color.rgb(248, 197, 143)); // pastelOrangeColor

        paint3 = new Paint();
        paint3.setAntiAlias(true);
        paint3.setStyle(Paint.Style.STROKE);
        paint3.setStrokeWidth(mainCircleStrokeWidth);
        //Circle color
        paint3.setColor(Color.rgb(190, 220, 230)); // babyBlueColor

        paint4 = new Paint();
        paint4.setAntiAlias(true);
        paint4.setStyle(Paint.Style.STROKE);
        paint4.setStrokeWidth(mainCircleStrokeWidth);
        //Circle color
        paint4.setColor(Color.rgb(216, 255, 231)); // honeydewColor

        paint5 = new Paint();
        paint5.setAntiAlias(true);
        paint5.setStyle(Paint.Style.STROKE);
        paint5.setStrokeWidth(secondCircleWidth);
        //Circle color
        paint5.setColor(Color.rgb(255, 99, 71)); // tomatoColor

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(Color.WHITE);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));

        mNormalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.voice);
        setupSpeechRecognition();
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        mWidth = w/2 - mSize/2;
        mHeight = h/2 - mSize/2;

        rect.left = rect_left = mWidth;
        rect.right = rect_right = rect_left + mSize;
        rect.top = rect_top = mHeight;
        rect.bottom = rect_bottom = rect_top + mSize;

        mBmpPosX = rect_left + ((rect_right - rect_left) / 2 )- mNormalBitmap.getWidth() / 2;
        mBmpPosY = rect_top + ((rect_bottom - rect_top) / 2 ) - mNormalBitmap.getHeight() / 2;
    }

    @Override
    protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        if (!gainFocus) {
            stopAnimation();
        } else if (mState == STATE_ANIMATE) {
            repeatAnimation();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw circle with different paints
        canvas.drawArc(rect, START_ANGLE_POINT, 40, false, paint1);
        canvas.drawArc(rect, 160, 50, false, paint2);
        canvas.drawArc(rect, 210, 90, false, paint3);
        canvas.drawArc(rect, 300, 120, false, paint4);

        // draw Microphone button
        canvas.drawOval(rect.left, rect.top, rect.right, rect.bottom, mButtonPaint);
        canvas.drawBitmap(mNormalBitmap, mBmpPosX, mBmpPosY, mButtonPaint);

        // draw animated arc
        if (mState == STATE_ANIMATE) {
            canvas.drawArc(rect, angle, START_ANGLE_POINT, false, paint5);
            repeatAnimation();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                startListening();
                mIsRecording = true;
                setAlpha(0.6f);
                return true;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                setAlpha(1.0f);
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        mOnVoiceInputListener.onVoiceStatus(1, "");
    }

    @Override
    public void onBeginningOfSpeech() {
        mOnVoiceInputListener.onVoiceStatus(1, "");
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        stopListening();
    }

    @Override
    public void onError(int i) {
        stopListening();
        mOnVoiceInputListener.onVoiceStatus(0, "Oops! Please try again");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(TAG, "onResults");
        ArrayList<String> matchResults = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (!matchResults.isEmpty()) {
            if(mOnVoiceInputListener != null){
                mOnVoiceInputListener.onVoiceMatchResults(matchResults);
            }
        }
    }

    @Override
    public void onPartialResults(Bundle results) {

    }

    @Override
    public void onEvent(int i, Bundle result) {

    }


    public void setOnVoiceInputListener(OnVoiceInputListener OnVoiceInputListener) {
        mOnVoiceInputListener = OnVoiceInputListener;
    }

    public static interface OnVoiceInputListener{
        public void onVoiceInputStart();
        public void onVoiceInputDone(String text);
        public void onVoiceMatchResults( ArrayList<String> matchResults);
        public void onVoiceStatus(int code, String text);

    }

}