package com.meteorshower.autoclock.event;

public class ScrollTypeChangeEvent {

    private int type;
    private int value;

    public ScrollTypeChangeEvent(int type, int value) {
        this.type = type;
        this.value = value;
    }

    public ScrollTypeChangeEvent(int type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
