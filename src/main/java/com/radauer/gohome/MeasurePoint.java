package com.radauer.gohome;

import java.util.Date;

/**
 * Created by Andreas on 12.12.2017.
 */
public class MeasurePoint {

    private Date time;
    private boolean up;
    private int duration;

    public MeasurePoint(Date time, boolean up, int duration) {
        this.time = time;
        this.up = up;
        this.duration = duration;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
