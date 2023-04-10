package com.meteorshower.autoclock.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.constant.AppConstant;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 辅助功能类
 * 手机设置里无障碍打开开关后
 * 重新打开软件即可连上服务
 * 连上服务才会启动心跳包
 */
public class ControllerAccessibilityService extends AccessibilityService {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

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
        controllerAccessibilityService = this;
        Log.d(AppConstant.TAG, "onServiceConnected");
        //辅助功能链接时启动activity
        /*Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);//启动Activity*/

        JobFactory.getInstance().start();
        JobExecutor.getInstance().start();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
//        Log.d(Constant.TAG, "className = " + className);

        int eventType = event.getEventType();
//        Log.d(Constant.TAG, "eventType = " + eventType);

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                Log.d(Constant.TAG, "TYPE_WINDOW_STATE_CHANGED");
                atomicInteger.getAndIncrement();
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 指定mills内没有发生pemits次跳转
     */
    public static boolean Wait(int pemits, long mills) throws InterruptedException {
        int i = 0;
        while (atomicInteger.get() < pemits) {
            Thread.sleep(100);
            i++;
            if ((i * 100) >= mills) {
                return false;
            }
        }
        Thread.sleep(500);
        return true;
    }

    /**
     * 指定mills内没有发生1次跳转
     */
    public static boolean Wait(long mills) throws InterruptedException {
        int i = 0;
        while (atomicInteger.get() == 0) {
            Thread.sleep(100);
            i++;
            if ((i * 100) >= mills) {
                return false;
            }
        }
        Thread.sleep(500);
        return true;
    }

    /**
     * 执行滑动命令
     */
    public void execScrollGesture(int startX, int startY, int endX, int endY, long startTime, long durationTime) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Path path = new Path();
            path.moveTo(startX, startY);//滑动起点
            path.lineTo(endX, endY);//滑动终点
            GestureDescription.Builder builder = new GestureDescription.Builder();
            //100L 第一个是开始的时间，第二个是持续时间
            GestureDescription description = builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, durationTime)).build();
            dispatchGesture(description, new MyCallBack(), null);
        }
    }

    //模拟手势的监听
    @RequiresApi(api = Build.VERSION_CODES.N)
    private class MyCallBack extends GestureResultCallback {
        public MyCallBack() {
            super();
        }

        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            super.onCompleted(gestureDescription);
            Log.d(AppConstant.TAG, "onCompleted = " + (gestureDescription==null?"":gestureDescription));
        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            super.onCancelled(gestureDescription);
            Log.d(AppConstant.TAG, "onCancelled = " + (gestureDescription==null?"":gestureDescription));
        }
    }

}
