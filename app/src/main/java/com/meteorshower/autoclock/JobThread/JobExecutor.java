package com.meteorshower.autoclock.JobThread;

import android.util.Log;

import com.meteorshower.autoclock.Job.Job;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.presenter.JobPresenter;

import java.util.concurrent.ArrayBlockingQueue;
/**
 * 每10秒执行一次任务
 * */
public class JobExecutor extends Thread {

    private boolean isDoingJob = false;
    public static ArrayBlockingQueue<Job> jobQueue = new ArrayBlockingQueue<>(4);
    private JobPresenter jobPresenter;
    private boolean isRunning = true;

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
        Log.d("JobExecutor", "JobExecutor start -------------------------------- ");
        Job job = null;
        while (isRunning) {
            try {
                Thread.sleep(AppConstant.EXC_JOP_SLEEP_TIME);
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
        Log.d("JobExecutor", "JobExecutor stop -------------------------------- ");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void addJob(Job job) {
        try {
            if (!isDoingJob) {
                Log.d("JobExecutor", "add a job to queue");
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
