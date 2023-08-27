package com.meteorshower.autoclock.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.event.ScrollFinishEvent;
import com.meteorshower.autoclock.receiver.AlarmReceiver;
import com.meteorshower.autoclock.activity.ScrollSettingActivity;
import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.util.AutoClickUtil;
import com.meteorshower.autoclock.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 辅助功能类
 * 手机设置里无障碍打开开关后
 * 重新打开软件即可连上服务
 * 连上服务才会启动心跳包
 */
public class ControllerAccessibilityService extends AccessibilityService {

    private static ControllerAccessibilityService controllerAccessibilityService;

    public static ControllerAccessibilityService getInstance() {
        if (controllerAccessibilityService == null)
            Log.i(AppConstant.TAG, "mAccessibilityServiceTool == null");
        return controllerAccessibilityService;
    }

    public ControllerAccessibilityService() {
        if (controllerAccessibilityService == null)
            controllerAccessibilityService = ControllerAccessibilityService.this;
    }

    /**
     * 启动辅助功能连接成功时
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toaster.show("无障碍服务已启动");
        controllerAccessibilityService = this;
//        JobFactory.getInstance().start();
//        JobExecutor.getInstance().start();

//        alarmReceiver = new AlarmReceiver(scrollHandler);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(AppConstant.ALARM_RECEIVER_ACTION);
//        registerReceiver(alarmReceiver, intentFilter);
//        if (alarmManager == null) {
//            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        }
        AutoClickUtil.getInstance().setService(this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;
            case AccessibilityEvent.WINDOWS_CHANGE_ADDED:
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        Toaster.show("无障碍服务已关闭");
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
        Toaster.show("Service onInterrupt");
    }


}
