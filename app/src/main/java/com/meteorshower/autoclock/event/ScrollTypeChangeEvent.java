package com.meteorshower.autoclock.event;

public class ScrollTypeChangeEvent {

    public int type;

    public ScrollTypeChangeEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
