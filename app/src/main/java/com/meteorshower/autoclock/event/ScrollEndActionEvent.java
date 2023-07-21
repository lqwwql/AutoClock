package com.meteorshower.autoclock.event;

public class ScrollEndActionEvent {

    private int type;

    public ScrollEndActionEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
