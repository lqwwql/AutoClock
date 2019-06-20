package com.meteorshower.autoclock.JobThread;

import android.util.Log;

import com.google.gson.Gson;
import com.meteorshower.autoclock.Job.AutoClickJob;
import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;
import com.meteorshower.autoclock.view.JobView;

import java.util.List;

public class JobFactory extends Thread implements JobView.GetJobView {

    private JobPresenter jobPresenter;
    private int sleepTime = 60 * 1000;
    private boolean isGetJob = false;
    private boolean isRuning = true;

    private JobFactory() {
    }

    public static JobFactory getInstance() {
        return SingleTonHolder.sInstance;
    }

    private static class SingleTonHolder {
        private static JobFactory sInstance = new JobFactory();
    }


    @Override
    public void run() {
        Log.d("JobFactory", "JobFactory start -------------------------------- ");
        jobPresenter = new JobPresenterImpl(this);
        while (isRuning) {
            try {
                //每1分钟请求一次任务
                Thread.sleep(sleepTime);

                Log.d("JobFactory", "isGetJob=" + isGetJob);

                if (!JobExecutor.getInstance().isDoingJob() && isGetJob) {
                    Log.d("JobFactory", "start get a job ");
                    jobPresenter.getCurrentJob();
                }
            } catch (Exception e) {
                Log.d("JobFactory", "JobFactory run error: " + Log.getStackTraceString(e));
            }

        }
        Log.d("JobFactory", "JobFactory stop -------------------------------- ");
    }

    public boolean isRuning() {
        return isRuning;
    }

    public void setRuning(boolean runing) {
        isRuning = runing;
    }

    public boolean isGetJob() {
        return isGetJob;
    }

    public void setGetJob(boolean getJob) {
        isGetJob = getJob;
    }

    @Override
    public void getSuccess(List<JobData> jobList) {
        Log.d("JobFactory", "get job success " + new Gson().toJson(jobList));
        //获取到一次任务则睡眠5分钟
        sleepTime = 5 * 60 * 1000;
        try {
            if (jobList == null || jobList.size() <= 0) {
                return;
            }
            JobData jobData = jobList.get(0);
            AutoClickJob autoClickJob = new AutoClickJob(jobData);
            JobExecutor.getInstance().addJob(autoClickJob);
        } catch (Exception e) {
            Log.d("JobFactory", "JobFactory getSuccess error = " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void getFailure(String message) {
        Log.d("JobFactory", "get job getFailure message=" + message);
    }
}
