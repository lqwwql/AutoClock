package com.meteorshower.autoclock.bean;

public class HeatBeat {

    private int id;
    private String heart_time;
    private boolean is_doing_job;
    private boolean is_getting_job;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeart_time() {
        return heart_time;
    }

    public void setHeart_time(String heart_time) {
        this.heart_time = heart_time;
    }

    public boolean isIs_doing_job() {
        return is_doing_job;
    }

    public void setIs_doing_job(boolean is_doing_job) {
        this.is_doing_job = is_doing_job;
    }

    public boolean isIs_getting_job() {
        return is_getting_job;
    }

    public void setIs_getting_job(boolean is_getting_job) {
        this.is_getting_job = is_getting_job;
    }
}