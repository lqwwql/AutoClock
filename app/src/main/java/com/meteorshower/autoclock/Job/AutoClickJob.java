package com.meteorshower.autoclock.Job;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.util.LogUtils;
import com.meteorshower.autoclock.util.ToastUtils;

public class AutoClickJob extends Job {

    private String JobName = "AutoClickJob";
    private Context context;
    private int state;//1:上班 2:下班

    public AutoClickJob(Context context, int state, int task_id) {
        this.context = context;
        this.state = state;
        this.task_id = task_id;
    }

    @Override
    public void doJob() {
        try {
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
            rect.set(90, 890, 110, 910);//考勤打卡
            AccessibilityUtils.clickRect(rect);

            if (state == 1) {
                //点击上班打卡，等到10s检测是否成功
            /*Thread.sleep(10 * 1000);
            rect.set(90, 110, 890, 910);//上班打卡
            AccessibilityUtils.clickRect(rect);*/
            } else if (state == 2) {
                //点击下班打卡，等到10s检测是否成功
                Thread.sleep(10 * 1000);
                rect.set(350, 790, 370, 810);//下班打卡
                AccessibilityUtils.clickRect(rect);
            }
            //上传结果到服务器
            Thread.sleep(10 * 1000);
            ToastUtils.show("完成任务");

            //返回桌面
            AccessibilityUtils.goToHome(context);


            //返回桌面
            AccessibilityUtils.goToHome(context);
        } catch (InterruptedException e) {
            ToastUtils.show("发生错误");
            LogUtils.getInstance().e("doJob error = " + Log.getStackTraceString(e), 1);
        }
    }

    @Override
    public String getJobName() {
        return JobName;
    }

}
