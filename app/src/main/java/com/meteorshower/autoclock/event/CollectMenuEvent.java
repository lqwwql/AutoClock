package com.meteorshower.autoclock.event;

public class CollectMenuEvent {

    private int clickX;
    private int clickY;

    public CollectMenuEvent(int clickX, int clickY) {
        this.clickX = clickX;
        this.clickY = clickY;
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
