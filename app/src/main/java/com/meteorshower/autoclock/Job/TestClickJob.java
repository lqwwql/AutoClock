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

public class TestClickJob extends Job implements JobView.UpdateJobView {

    private String JobName = "TestClickJob";
    private Context context;
    private JobData jobData;
    private JobPresenter jobPresenter;

    public TestClickJob(JobData jobData) {
        this.context = MyApplication.getContext();
        this.jobData = jobData;
        jobPresenter = new JobPresenterImpl(this);
    }

    @Override
    public void doJob() {
        try {
            Log.d("lqwtest", "TestClickJob doJob");
            Thread.sleep(5 * 1000);
            AccessibilityNodeInfo node1 = AccessibilityUtils.findNode(AccessibilityUtils.getRootInActiveWindow(), "工作台", false);
            if (node1 != null) {
                node1.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.d("lqwtest", "node1 performAction ACTION_CLICK: ");
                Thread.sleep(10 * 1000);
                AccessibilityNodeInfo node2 = AccessibilityUtils.findNode(AccessibilityUtils.getRootInActiveWindow(), "考勤打卡", false);
                if(node2!=null){
                    node2.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.d("lqwtest", "node2 performAction ACTION_CLICK: ");

                    Thread.sleep(10 * 1000);
                    AccessibilityNodeInfo node3 = AccessibilityUtils.findNode(AccessibilityUtils.getRootInActiveWindow(), "下班打卡", false);
                    if(node3!=null){
                        node3.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.d("lqwtest", "node3 performAction ACTION_CLICK: ");
                    }else {
                        Log.d("lqwtest", "node3 is null");
                    }
                }else {
                    Log.d("lqwtest", "node2 is null");
                }
            } else {
                Log.d("lqwtest", "node1 is null");
            }
        }catch (Exception e){
            Log.d("lqwtest", "goback InterruptedException " + Log.getStackTraceString(e));
        }
    }

    private void clickAutoClock(){
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
}
