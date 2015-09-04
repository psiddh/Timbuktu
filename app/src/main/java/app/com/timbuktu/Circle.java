package app.com.timbuktu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class Circle extends View {

    private static final int START_ANGLE_POINT = 90;

    private final Paint paint;
    private final RectF rect;

    private float angle;

    private final Paint mBluePaint;
    private final RectF rect1;

    private final Paint mYellowPaint;
    private final RectF rect2;

    public Circle(Context context, AttributeSet attrs) {
        super(context, attrs);

        final int strokeWidth = 40;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        //Circle color
        paint.setColor(Color.RED);

        //size 200x200 example
        rect = new RectF(strokeWidth, strokeWidth, 140 + strokeWidth, 140 + strokeWidth);

        //Initial Angle (optional, it can be zero)
        angle = 0;


        final int strokeWidth2 = 20;
        mBluePaint = new Paint();
        mBluePaint.setAntiAlias(true);
        mBluePaint.setStyle(Paint.Style.STROKE);
        mBluePaint.setStrokeWidth(strokeWidth2);
        //Circle color
        mBluePaint.setColor(Color.BLUE);
        rect1 = new RectF(180, 180, 240, 240);

        final int strokeWidth3= 10;
        mYellowPaint = new Paint();
        mYellowPaint.setAntiAlias(true);
        mYellowPaint.setStyle(Paint.Style.STROKE);
        mYellowPaint.setStrokeWidth(strokeWidth3);
        //Circle color
        mYellowPaint.setColor(Color.rgb(190, 220, 230));
        rect2 = new RectF(240, 240, 300, 300);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (angle <= 180) {
            canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
        }
        else if (angle >180 && angle <= 270) {
            canvas.drawArc(rect, START_ANGLE_POINT, 180, false, paint);
            canvas.drawArc(rect, 270, (angle - 180), false, mBluePaint);
        }
        else {
            canvas.drawArc(rect, START_ANGLE_POINT, 180, false, paint);
            canvas.drawArc(rect, 270, 90, false, mBluePaint);
            canvas.drawArc(rect, 0, (angle - 270), false, mYellowPaint);
        }

    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}