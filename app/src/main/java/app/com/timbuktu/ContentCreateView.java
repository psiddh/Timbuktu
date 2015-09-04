package app.com.timbuktu;

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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class ContentCreateView extends View {

    private static final String TAG = ContentCreateView.class.getName();

    private static final int STATE_NORMAL = 0;
    private static final int STATE_PRESSED = 1;

    private Bitmap mNormalBitmap;
    private Paint mPaint;
    private AnimatorSet mAnimatorSet = new AnimatorSet();

    private int mState = STATE_NORMAL;
    private boolean bCancelAnimation = false;

    private int mWidth = 150;
    private int mHeight = 700;

    private int mXIndex = 10;
    private int mYIndex = 10;

    public ContentCreateView(Context context) {
        super(context);
        init();
    }

    public ContentCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mNormalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.voice);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.argb(255, 190, 220, 230));

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                40, getContext().getResources().getDisplayMetrics());

    }

    private void repeatAnimation() {
        //if (bCancelAnimation) return;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        animateRadius(300);
                        repeatAnimation();
                    }
                },
                1200);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) - (int)mWidth + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w) - (int)mWidth, heightMeasureSpec, 0);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        bCancelAnimation = !gainFocus;
        if (bCancelAnimation) {
            if(mAnimatorSet.isRunning()){
                mAnimatorSet.cancel();
            }
        } else {
            repeatAnimation();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.drawBitmap(mNormalBitmap, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        canvas.drawLine(mWidth, mHeight, mWidth + mXIndex, mHeight + mYIndex, mPaint);
        mXIndex += 10;
        mYIndex += 10;
        /*if(mCurrentRadius > mMinRadius){
            canvas.drawCircle(width / 2, height / 2, mCurrentRadius, mPaint);
        }

        switch (mState){
            case STATE_NORMAL:
                canvas.drawCircle(width / 2, height / 2, mCurrentRadius, mPaint);
                canvas.drawBitmap(mNormalBitmap, width / 2 - mMinRadius -10,  height / 2 - mMinRadius, mPaint);
                break;
            case STATE_PRESSED:
                canvas.drawBitmap(mPressedBitmap, width / 2 - mMinRadius - 10,  height / 2 - mMinRadius, mPaint);
                break;
        }*/
    }

    public void animateRadius(int width){
        bCancelAnimation = true;

        mAnimatorSet.playSequentially(
                ObjectAnimator.ofFloat(this, "CurrentPoint", mWidth, mHeight + 10).setDuration(250),
                ObjectAnimator.ofFloat(this, "CurrentPoint", mWidth + 10, mHeight + 20).setDuration(250),
                ObjectAnimator.ofFloat(this, "CurrentPoint", mWidth + 20 , mHeight + 30).setDuration(250),
                ObjectAnimator.ofFloat(this, "CurrentPoint", mWidth + 30 , mHeight ).setDuration(250)
        );
        mAnimatorSet.start();
    }

    public float getCurrentRadius() {
        return 0;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");

            default:
                return super.onTouchEvent(event);
        }
    }
}