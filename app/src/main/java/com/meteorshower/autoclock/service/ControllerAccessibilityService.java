package com.meteorshower.autoclock.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.view.HomeActivity;
import com.meteorshower.autoclock.view.ScrollSettingActivity;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 辅助功能类
 * 手机设置里无障碍打开开关后
 * 重新打开软件即可连上服务
 * 连上服务才会启动心跳包
 */
public class ControllerAccessibilityService extends AccessibilityService {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static final int SCROLL_WHAT = 1001;
    private int scrollTimes = 0;
    private int scrollDuration = 0;
    private int slideDuration = 0;
    private boolean isRunning = false;
    private boolean isStop = false;
    private boolean isRandomSeconds = false;
    private int direction = 1;
    private int finishOp = 1;

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
        Toaster.show("Service onServiceConnected");
        controllerAccessibilityService = this;
        Log.d(AppConstant.TAG, "onServiceConnected");
        JobFactory.getInstance().start();
        JobExecutor.getInstance().start();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        Log.d(AppConstant.TAG, "className = " + className);

        int eventType = event.getEventType();
        Log.d(AppConstant.TAG, "eventType = " + eventType);

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Log.d(AppConstant.TAG, "TYPE_WINDOW_STATE_CHANGED");
//                atomicInteger.getAndIncrement();
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        Toaster.show("Service onDestroy");
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
        Toaster.show("Service onInterrupt");
    }

    private Handler scrollHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCROLL_WHAT:
                    executeScrollView();
                    break;
            }
        }
    };

    public void setScrollParam(int scrollTimes, int scrollDuration, int slideDuration, int direction, int finishOp, boolean isRunning, boolean isRandomSeconds) {
        this.scrollTimes = scrollTimes;
        this.scrollDuration = scrollDuration;
        this.slideDuration = slideDuration;
        this.isRunning = isRunning;
        this.isRandomSeconds = isRandomSeconds;
        this.direction = direction;
        this.finishOp = finishOp;
    }


    public void executeScrollView() {
        if (!isRunning) {
            return;
        }
        if (scrollTimes == 0) {
            Toaster.show("滑动程序次数已执行完毕");
            scrollHandler.removeMessages(SCROLL_WHAT);
            isRunning = !isRunning;
            if (finishOp == 1) {
                startActivity(new Intent(this, ScrollSettingActivity.class));
            } else if (finishOp == 2) {
                AccessibilityUtils.goToHome(this);
            }
            return;
        }
        scrollTimes--;

        int startX = 260, startY = 820, endX = 260, endY = 260;
        String directionStr = "";
        switch (direction) {
            case 1:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = AppConstant.ScreenWidth / 2;
                    startY = AppConstant.ScreenHeight * 6 / 7;
                    endX = AppConstant.ScreenWidth / 2;
                    endY = AppConstant.ScreenHeight / 7;
                }
                directionStr = "向上";
                break;
            case 2:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    endX = AppConstant.ScreenWidth / 2;
                    endY = AppConstant.ScreenHeight * 6 / 7;
                    startX = AppConstant.ScreenWidth / 2;
                    startY = AppConstant.ScreenHeight / 7;
                }
                directionStr = "向下";
                break;
            case 3:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startY = AppConstant.ScreenWidth / 2;
                    startX = AppConstant.ScreenHeight * 6 / 7;
                    endY = AppConstant.ScreenWidth / 2;
                    endX = AppConstant.ScreenHeight / 7;
                }
                directionStr = "向左";
                break;
            case 4:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    endY = AppConstant.ScreenWidth / 2;
                    endX = AppConstant.ScreenHeight * 6 / 7;
                    startY = AppConstant.ScreenWidth / 2;
                    startX = AppConstant.ScreenHeight / 7;
                }
                directionStr = "向右";
                break;
        }
        Toaster.show("执行" + directionStr + "滑动,间隔:" + scrollDuration + ",剩余次数:" + scrollTimes);
        execScrollGesture(startX + getRandomXY(), startY + getRandomXY(), endX + getRandomXY(), endY + getRandomXY(), 100, slideDuration);
        sendDelayMessage();
    }

    public int getRandomXY() {
        return (new Random().nextInt(5) + 1) * 10;
    }

    public int getRandomSeconds() {
        return (new Random().nextInt(5) + 1);
    }

    public void sendDelayMessage() {
        scrollHandler.sendEmptyMessageDelayed(SCROLL_WHAT, (scrollDuration + (isRandomSeconds ? getRandomSeconds() : 0)) * 1000);
    }

    public void startRunning() {
        scrollHandler.sendEmptyMessage(SCROLL_WHAT);
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stopRunning() {
        scrollHandler.removeMessages(SCROLL_WHAT);
        isRunning = !isRunning;
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
            Log.d(AppConstant.TAG, "onCompleted = " + (gestureDescription == null ? "" : gestureDescription));
        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            super.onCancelled(gestureDescription);
            Log.d(AppConstant.TAG, "onCancelled = " + (gestureDescription == null ? "" : gestureDescription));
        }
    }

}
