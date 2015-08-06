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
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        PointF curr = new PointF(event.getX(), event.getY());

        Log.i(TAG, "Received event at x=" + curr.x +
                ", y=" + curr.y + ":");

        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "  ACTION_DOWN");
                mCurrentBox = new Box(curr);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                final int count = event.getPointerCount();
                Log.w(TAG, "count : " + count);
                final int index = event.getActionIndex();
                final int mask = event.getActionMasked();
                final int id = event.getPointerId(index);
                Log.w(TAG, "index : " + index + "\nid : " + id + "\n mask : " + mask);
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
}
