package com.meteorshower.autoclock.Job;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.bean.PostData;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;
import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.util.LogUtils;
import com.meteorshower.autoclock.view.JobView;

public class AutoClickJob extends Job implements JobView.UpdateJobView {

    private String JobName = "AutoClickJob";
    private Context context;
    private JobData jobData;
    private JobPresenter jobPresenter;

    public AutoClickJob(JobData jobData) {
        this.context = MyApplication.getContext();
        this.jobData = jobData;
        jobPresenter = new JobPresenterImpl(this);
    }

    @Override
    public void doJob() {
        try {
            //唤醒屏幕
            try {
                AccessibilityUtils.wakeUpAndUnlock(context);
            } catch (Exception e) {
                Log.d("lqwtest", "wakeUpAndUnlock error = " + Log.getStackTraceString(e));
            }

            //返回桌面
            AccessibilityUtils.goToHome(context);
            //等待5秒后点击钉钉
            Thread.sleep(5 * 1000);
            Rect rect = new Rect();
            rect.set(579, 563, 668, 652);//桌面钉钉图标位置
            AccessibilityUtils.clickRect(rect);

            //点击工作，等待10s刷新
            Thread.sleep(10 * 1000);
            rect.set(357, 1206, 377, 1226);//工作
            AccessibilityUtils.clickRect(rect);

            //点击考勤打卡，等待10s刷新
            Thread.sleep(10 * 1000);
            rect.set(100, 700, 120, 720);//考勤打卡
            AccessibilityUtils.clickRect(rect);

            if (jobData.getType() == 1) {
                //点击上班打卡，等到10s检测是否成功
                Thread.sleep(20 * 1000);
                rect.set(290, 426, 430, 470);//上班打卡
                AccessibilityUtils.clickRect(rect);
            } else if (jobData.getType() == 2) {
                //点击下班打卡，等到10s检测是否成功
                Thread.sleep(20 * 1000);
                rect.set(290, 778, 430, 822);//下班打卡
                AccessibilityUtils.clickRect(rect);
            }
            //上传结果到服务器
            Thread.sleep(10 * 1000);
            jobData.setStatus(2);

            PostData postData = new PostData();
            postData.setId(jobData.getId());
            postData.setJob_name(jobData.getJobName());
            postData.setJob_time(jobData.getJobTime());
            postData.setExtra_info(jobData.getExtraInfo());
            postData.setType(jobData.getType());
            postData.setStatus(3);
            jobPresenter.updateCurrentJob(postData);

        } catch (InterruptedException e) {
            LogUtils.getInstance().e("doJob error = " + Log.getStackTraceString(e));
        } finally {
            goBack();
            //返回桌面
            AccessibilityUtils.goToHome(context);
            clickAutoClock();
        }
    }

    private void clickAutoClock() {
        try {
            Thread.sleep(5 * 1000);
            Rect rect = new Rect();
            rect.set(442, 375, 462, 395);
            AccessibilityUtils.clickRect(rect);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void goBack() {
        try {
            AccessibilityUtils.goBack();
            Thread.sleep(3 * 1000);
            AccessibilityUtils.goBack();
        } catch (InterruptedException e) {
            Log.d("lqwtest", "goback InterruptedException " + Log.getStackTraceString(e));
        }
    }

    @Override
    public String getJobName() {
        return JobName;
    }


    @Override
    public void updateSuccess() {
        Log.d("lqwtest", "updateSuccess");
    }

    @Override
    public void updateFailure(String message) {
        Log.d("lqwtest", "updateFailure message: " + message);
    }

    private void doAnotherJob() {
        try {
            AccessibilityNodeInfo node = AccessibilityUtils.findNode(AccessibilityUtils.getRootInActiveWindow(), "工作台", true);
            if (node != null) {
                Thread.sleep(5 * 1000);
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d("lqwtest", "performAction ACTION_CLICK: ");
            } else {
                Log.d("lqwtest", "node is null");
            }
        } catch (Exception e) {
            Log.d("lqwtest", "doAnotherJob error: " + Log.getStackTraceString(e));
        }
    }
}
