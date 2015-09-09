package app.com.timbuktu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class Circle extends View {

    private static final int START_ANGLE_POINT = 120;

    private final RectF rect;

    private float angle;

    private final Paint paint1;
    private final Paint paint2;
    private final Paint paint3;
    private final Paint paint4;
    private final Paint paint5;
    private final Paint mButtonPaint;

    private boolean mShouldAnimate = true;
    private final Bitmap mNormalBitmap;
    private Handler mHandler = new android.os.Handler();

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
        if (mShouldAnimate) {
            mHandler.removeCallbacksAndMessages(mAnimate);
            mHandler.postDelayed(mAnimate, 3000);
        }
    }

    private void stopAnimation() {
        mShouldAnimate = false;
        mHandler.removeCallbacksAndMessages(mAnimate);
    }


    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Initial Angle (optional, it can be zero)
        angle = 0;
        final int mainCircleStrokeWidth = 15;
        final int secondCircleWidth = 40;
        //size 200x200 example
        rect = new RectF(200, 200, 500, 500);


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

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawArc(rect, START_ANGLE_POINT, 40, false, paint1);
        canvas.drawArc(rect, 160, 50, false, paint2);
        canvas.drawArc(rect, 210, 90, false, paint3);
        canvas.drawArc(rect, 300, 120, false, paint4);
        canvas.drawBitmap(mNormalBitmap, rect.centerX(), rect.top, mButtonPaint);

        canvas.drawArc(rect, angle , START_ANGLE_POINT, false, paint5);
        repeatAnimation();
        //canvas.drawBitmap(mNormalBitmap, (getWidth() - mNormalBitmap.getWidth()) / 2,
        //        (getHeight() - mNormalBitmap.getHeight()) / 2, paint5);

        /*if (angle >= 120 && angle <= 160) {
            canvas.drawArc(rect, START_ANGLE_POINT, (angle - START_ANGLE_POINT), false, paint1);
        } else if (angle > 160 && angle <= 210) {
            canvas.drawArc(rect, START_ANGLE_POINT, 40, false, paint1);
            canvas.drawArc(rect, 160, (angle - 160), false, paint2);
        } else if (angle > 210 && angle <= 300) {
            canvas.drawArc(rect, START_ANGLE_POINT, 40, false, paint1);
            canvas.drawArc(rect, 160, 50, false, paint2);
            canvas.drawArc(rect, 210, (angle - 210), false, paint3);
        } else if (angle > 310 && angle <= 360) {
            canvas.drawArc(rect, START_ANGLE_POINT, 40, false, paint1);
            canvas.drawArc(rect, 160, 50, false, paint2);
            canvas.drawArc(rect, 210, 300, false, paint3);
            canvas.drawArc(rect, 310, (angle - 360), false, paint3);

        }*/

    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}