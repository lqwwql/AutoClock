package com.meteorshower.autoclock.JobThread;

import android.util.Log;

import com.meteorshower.autoclock.Job.Job;
import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;
import com.meteorshower.autoclock.view.JobView;

import java.util.concurrent.ArrayBlockingQueue;

public class JobExecutor extends Thread {

    private boolean isDoingJob = false;
    public static ArrayBlockingQueue<Job> jobQueue = new ArrayBlockingQueue<>(4);
    private JobPresenter jobPresenter;
    private boolean isRuning = true;

    private JobExecutor() {
    }

    public static JobExecutor getInstance() {
        return SingleTonHolder.sInstance;
    }

    private static class SingleTonHolder {
        private static JobExecutor sInstance = new JobExecutor();
    }

    @Override
    public void run() {
        Log.d("JobExecutor","JobExecutor start -------------------------------- ");
        Job job = null;
        while (isRuning) {
            try {
                Thread.sleep(10 * 1000);
                job = jobQueue.take();
                if (job == null) {
                    Log.d("JobExecutor", "start get a job ");
                    continue;
                }
                Log.d("JobExecutor", "start doing a job ");
                isDoingJob = true;
                job.doJob();
                isDoingJob = false;
            } catch (Exception e) {
                isDoingJob = false;
                Log.d("JobExecutor", "JobExecutor addJob error: " + Log.getStackTraceString(e));
            }
        }
        Log.d("JobExecutor","JobExecutor stop -------------------------------- ");
    }

    public boolean isRuning() {
        return isRuning;
    }

    public void setRuning(boolean runing) {
        isRuning = runing;
    }

    public void addJob(Job job) {
        try {
            if (!isDoingJob) {
                Log.d("JobExecutor","add a job to queue");
                jobQueue.add(job);
            }
        } catch (Exception e) {
            Log.d("JobExecutor", "JobExecutor addJob error: " + Log.getStackTraceString(e));
        }
    }

    public boolean isDoingJob() {
        return isDoingJob;
    }

    public void setDoingJob(boolean doingJob) {
        isDoingJob = doingJob;
    }


}
