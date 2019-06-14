package com.meteorshower.autoclock.Job;

public abstract class Job {

    public int task_id;
    public String doTime;
    public int result;
    public String extraMsg;

    public abstract void doJob();
    public abstract String getJobName();

    public int getResult() {
        return result;
    }


}
