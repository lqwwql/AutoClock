package com.meteorshower.autoclock.event;

public class FloatingViewClickEvent {

    private int type;

    public FloatingViewClickEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
