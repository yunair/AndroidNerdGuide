package com.air.runtracker.model;

import java.util.Date;

/**
 * Created by Air on 15/8/17.
 */
public class Run {
    private Date mStartDate;

    public Run() {
        mStartDate = new Date();
    }


    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public int getDurationSeconds(long endMillis) {
        return (int) ((endMillis - mStartDate.getTime()) / 1000);
    }

    public static String formatDuration(int durationSeconds){
        int tmpSeconds = durationSeconds;
        int seconds = tmpSeconds % 60;
        tmpSeconds /= 60;
        int minutes = tmpSeconds % 60;
        tmpSeconds /= 60;
        int hours = tmpSeconds % 60;
        /**
         * 另外一种计算逻辑
         * int seconds = durationSeconds % 60;
         * int minutes = ((durationSeconds - seconds) / 60) % 60;
         * int hours = (durationSeconds - (minutes * 60) - seconds) / 3600;
         */

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
