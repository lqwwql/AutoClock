package com.meteorshower.autoclock.event;

import android.graphics.Point;

import java.util.List;

public class CollectMenuEvent {

    private int type;//0-点 1-轨迹
    private int clickX;
    private int clickY;
    private List<Point> trackList;

    public CollectMenuEvent(int type, List<Point> trackList) {
        this.type = type;
        this.trackList = trackList;
    }

    public CollectMenuEvent(int type, int clickX, int clickY) {
        this.type = type;
        this.clickX = clickX;
        this.clickY = clickY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Point> getTrackList() {
        return trackList;
    }

    public void setTrackList(List<Point> trackList) {
        this.trackList = trackList;
    }

    public int getClickX() {
        return clickX;
    }

    public void setClickX(int clickX) {
        this.clickX = clickX;
    }

    public int getClickY() {
        return clickY;
    }

    public void setClickY(int clickY) {
        this.clickY = clickY;
    }
}
