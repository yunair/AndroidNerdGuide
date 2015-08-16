package com.air.boxdrawingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.air.boxdrawingview.model.Box;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Air on 15/8/5.
 */
public class BoxDrawingView extends View {
    public static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private List<Box> mBoxes = new ArrayList<Box>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;

    private int stateToSave;
    private PointF[] trianglePoint;
    private double[] triangle;
    private boolean canRotate = false;

    // Used when creating the view in code
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);
        // Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
        triangle = new double[3];
        trianglePoint = new PointF[3];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Fill the background
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxes) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);

            if(canRotate){
                double value = calculateCosA(triangle);
                double rotate = Math.acos(value) * 180;
//                Log.d(TAG, "value : " + rotate);
                canvas.rotate((float) rotate);
            }
        }





        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        PointF curr = new PointF(event.getX(), event.getY());
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "  ACTION_DOWN");
                canRotate = false;
                mCurrentBox = new Box(curr);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w(TAG, "ACTION_POINTER_DOWN : curr : " + curr.x);
                trianglePoint[1] = new PointF(event.getX(1), event.getY(1));
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.w(TAG, "ACTION_POINTER_UP : curr : " + curr.x);
                PointF[] point = new PointF[2];
                for (int i = 0; i < 2; i++) {
                    point[i] = new PointF(event.getX(i), event.getY(i));
                }

                canRotate = true;
                triangle[0] = distanceTwoPoints(trianglePoint[1] , point[0]);
                triangle[1] = distanceTwoPoints(point[1], point[0]);
                triangle[2] = distanceTwoPoints(trianglePoint[1] , point[1]);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "  ACTION_MOVE");
                if(mCurrentBox != null){
                    mCurrentBox.setCurrent(curr);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "  ACTION_UP");
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "  ACTION_CANCEL");
                mCurrentBox = null;
                break;
        }

        return true;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("stateToSave", this.stateToSave);
        bundle.putParcelableArray(TAG, mBoxes.toArray(new Parcelable[mBoxes.size()]));
        super.onSaveInstanceState();
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            Parcelable[] parcels = bundle.getParcelableArray(TAG);
            if(parcels != null) {
                Box[] boxes = new Box[parcels.length];
                for (int i = 0; i < parcels.length; i++) {
                    boxes[i] = (Box) parcels[i];
                }
                //Arrays.asList return a fixed-size list
                mBoxes = new ArrayList<>(Arrays.asList(boxes));
                invalidate();
            }
            this.stateToSave = bundle.getInt("stateToSave");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    /**
     * 计算两点之间的距离
     * @param x
     * @param y
     * @return 两点之间的距离
     */
    private double distanceTwoPoints(PointF x, PointF y){
        float disX = Math.abs(x.x - y.x);
        float disY = Math.abs(x.y - y.y);
        return Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 余弦定理
     * @param triangle
     * @return cosA 的值
     */
    private double calculateCosA(double[] triangle){
        double numerator = triangle[0] * triangle[0] + triangle[1] * triangle[1] - triangle[2] * triangle[2];
        double denominator = 2 * triangle[0] * triangle[1];
        return numerator / denominator;
    }
}
