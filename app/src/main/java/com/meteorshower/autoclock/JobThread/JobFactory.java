package com.meteorshower.autoclock.JobThread;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.meteorshower.autoclock.Job.ShellClickJob;
import com.meteorshower.autoclock.bean.HeatBeat;
import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.http.ApiService;
import com.meteorshower.autoclock.http.RetrofitManager;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;
import com.meteorshower.autoclock.util.StringUtils;
import com.meteorshower.autoclock.view.JobView;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobFactory extends Thread implements JobView.GetJobView {

    private JobPresenter jobPresenter;
    private int sleepTime = AppConstant.GET_JOP_SLEEP_TIME;
    private boolean isGetJob = false;
    private boolean isRunning = true;

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
        while (isRunning) {
            try {
                //每1分钟发送一次心跳
                postToNet();
                Thread.sleep(sleepTime);
                Log.d("JobFactory", "isGetJob=" + isGetJob);
                if (!JobExecutor.getInstance().isDoingJob() && isGetJob) {
                    Log.d("JobFactory", "start get a job ");
                    jobPresenter.getCurrentJob(0, 1);
                }
            } catch (Exception e) {
                Log.d("JobFactory", "JobFactory run error: " + Log.getStackTraceString(e));
            }

        }
        Log.d("JobFactory", "JobFactory stop -------------------------------- ");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        if(!isRunning && running){
            run();
        }
        isRunning = running;
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
        sleepTime = AppConstant.GETED_JOP_SLEEP_TIME;
        try {
            if (jobList == null || jobList.size() <= 0) {
                return;
            }
            JobData jobData = jobList.get(0);
//            AutoClickJob autoClickJob = new AutoClickJob(jobData);
            ShellClickJob shellClickJob = new ShellClickJob(jobData);
            JobExecutor.getInstance().addJob(shellClickJob);
        } catch (Exception e) {
            Log.d("JobFactory", "JobFactory getSuccess error = " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void getFailure(String message) {
        Log.d("JobFactory", "get job getFailure message=" + message);
    }

    private void postToNet() {
        HeatBeat heatBeat = new HeatBeat();
        heatBeat.setHeart_time(StringUtils.getNow());
        heatBeat.setIs_doing_job(JobExecutor.getInstance().isDoingJob()+"");
        heatBeat.setIs_getting_job(JobFactory.getInstance().isGetJob()+"");

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(heatBeat));
        Call call = RetrofitManager.getInstance().getService(ApiService.class).postHeartBeat(body);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                //打印日志
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                //打印日志
            }
        });
    }
}
