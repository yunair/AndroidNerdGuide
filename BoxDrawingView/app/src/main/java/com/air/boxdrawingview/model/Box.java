package com.air.boxdrawingview.model;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Air on 15/8/5.
 */
public class Box implements Parcelable{

    private PointF mOrigin;
    private PointF mCurrent;

    public Box(PointF origin) {
        mOrigin = mCurrent = origin;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public void setOrigin(PointF origin) {
        mOrigin = origin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mOrigin, 0);
        dest.writeParcelable(this.mCurrent, 0);
    }

    protected Box(Parcel in) {
        this.mOrigin = in.readParcelable(PointF.class.getClassLoader());
        this.mCurrent = in.readParcelable(PointF.class.getClassLoader());
    }

    public static final Creator<Box> CREATOR = new Creator<Box>() {
        public Box createFromParcel(Parcel source) {
            return new Box(source);
        }

        public Box[] newArray(int size) {
            return new Box[size];
        }
    };
}
