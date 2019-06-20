package com.meteorshower.autoclock.Job;

public abstract class Job {

    public int task_id;
    public String jobName;
    public String doTime;
    public int status;
    public String extraInfo;
    public int result;
    public String extraMsg;

    public abstract void doJob();
    public abstract String getJobName();

    public int getResult() {
        return result;
    }


}
