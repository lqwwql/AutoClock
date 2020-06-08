package com.meteorshower.autoclock.Job;

import android.content.Context;
import android.util.Log;

import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.bean.PostData;
import com.meteorshower.autoclock.http.SocketClient;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;
import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.util.LogUtils;
import com.meteorshower.autoclock.view.JobView;

/**
 * 版权：咸鱼信息科技有限公司 版权所有
 *
 * @author lqw
 * 创建日期：2020/6/8 21:45
 * 描述：shell命令点击
 */
public class ShellClickJob extends Job implements JobView.UpdateJobView {

    private String JobName = "ShellClickJob";
    private Context context;
    private JobData jobData;
    private JobPresenter jobPresenter;
    private String result = "";

    public ShellClickJob(JobData jobData) {
        this.context = MyApplication.getContext();
        this.jobData = jobData;
        jobPresenter = new JobPresenterImpl(this);
    }


    @Override
    public void doJob() {

        try {
            //返回桌面
            AccessibilityUtils.goToHome(context);

            //等待10秒后点击钉钉
            Thread.sleep(10 * 1000);
            String clickTTCommand = AccessibilityUtils.getCommand("3");
            excCommand(clickTTCommand);

            //等待10秒后点击工作台
            Thread.sleep(10 * 1000);
            String wordClickCommand = AccessibilityUtils.getCommand("4");
            excCommand(wordClickCommand);

            //等待10秒后点击考勤打卡
            Thread.sleep(10 * 1000);
            String attenCommand = AccessibilityUtils.getCommand("5");
            excCommand(attenCommand);

            //等待10秒后点击打卡
            Thread.sleep(10 * 1000);
            if (jobData.getType() == 1) {
                String upCommand = AccessibilityUtils.getCommand("6");
                excCommand(upCommand);
            } else {
                String downCommand = AccessibilityUtils.getCommand("9");
                excCommand(downCommand);
            }

            //返回
            Thread.sleep(10 * 1000);
            String backCommand = AccessibilityUtils.getCommand("7");
            excCommand(backCommand);

            //回到消息页
            Thread.sleep(10 * 1000);
            String newsCommand = AccessibilityUtils.getCommand("8");
            excCommand(newsCommand);

            PostData postData = new PostData();
            postData.setId(jobData.getId());
            postData.setJob_name(jobData.getJobName());
            postData.setJob_time(jobData.getJobTime());
            postData.setExtra_info(jobData.getExtraInfo() + "/" + result);
            postData.setType(jobData.getType());
            postData.setStatus(3);
            jobPresenter.updateCurrentJob(postData);

        } catch (Exception e) {
            LogUtils.getInstance().e("doJob error = " + Log.getStackTraceString(e), 1);
        } finally {
            //返回桌面
            AccessibilityUtils.goToHome(context);

            try {
                Thread.sleep(10 * 1000);
            } catch (Exception e) { }

            //打开操作APP
            String startCommand = AccessibilityUtils.getCommand("10");
            excCommand(startCommand);
        }
    }

    @Override
    public String getJobName() {
        return null;
    }

    @Override
    public void updateSuccess() {

    }

    @Override
    public void updateFailure(String message) {

    }

    private void excCommand(final String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new SocketClient(command, new SocketClient.CmdExcSendListener() {
                    @Override
                    public void getExcResult(String result) {
                        setResult(result);
                    }
                });
            }
        }).start();
    }

    public void setResult(String result) {
        this.result += "/" + result;
    }
}
