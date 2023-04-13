package com.meteorshower.autoclock.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.receiver.AlarmReceiver;
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
    public static final int SCROLL_WHAT = 1001;
    private int scrollTimes = 0;
    private int scrollDuration = 0;
    private int slideDuration = 0;
    private boolean isRunning = false;
    private boolean isStop = false;
    private boolean isRandomSeconds = false;
    private int direction = 1;
    private int finishOp = 1;
    private int range = 1;//1-大幅度,2-小幅度
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private AlarmReceiver alarmReceiver;
    private int timerType = 1;//1-Handler,2-闹钟

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
        Log.d(AppConstant.TAG, "onServiceConnected");
        JobFactory.getInstance().start();
        JobExecutor.getInstance().start();
        alarmReceiver = new AlarmReceiver(scrollHandler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstant.ALARM_RECEIVER_ACTION);
        registerReceiver(alarmReceiver, intentFilter);
        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String className = event.getClassName().toString();
//        Log.d(AppConstant.TAG, "className = " + className);

        int eventType = event.getEventType();
//        Log.d(AppConstant.TAG, "eventType = " + eventType);

        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//                Log.d(AppConstant.TAG, "TYPE_WINDOW_STATE_CHANGED");
//                atomicInteger.getAndIncrement();
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        Toaster.show("无障碍服务已关闭");
        unregisterReceiver(alarmReceiver);
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {
        Toaster.show("Service onInterrupt");
    }

    private Handler scrollHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isRunning) {
                return;
            }
            switch (msg.what) {
                case SCROLL_WHAT:
                    executeScrollView();
                    break;
            }
        }
    };

    public void setScrollParam(int scrollTimes, int scrollDuration, int slideDuration, int direction, int finishOp, int timerType, int range, boolean isRunning, boolean isRandomSeconds) {
        this.scrollTimes = scrollTimes;
        this.scrollDuration = scrollDuration;
        this.slideDuration = slideDuration;
        this.isRunning = isRunning;
        this.isRandomSeconds = isRandomSeconds;
        this.direction = direction;
        this.finishOp = finishOp;
        this.timerType = timerType;
        this.range = range;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void executeScrollView() {
        if (!isRunning) {
            return;
        }
        Log.d(AppConstant.TAG, "range=" + range);
        if (scrollTimes == 0) {
            Toaster.show("滑动程序次数已执行完毕");
            stopRunning();
            if (finishOp == 2) {
                performGlobalAction(GLOBAL_ACTION_BACK);
            } else if (finishOp == 3) {
                startActivity(new Intent(this, ScrollSettingActivity.class));
            } else if (finishOp == 4) {
                performGlobalAction(GLOBAL_ACTION_HOME);
            }
            return;
        }
        scrollTimes--;

        int startX = 260, startY = 820, endX = 260, endY = 260;
        String directionStr = "";
        switch (direction) {
            case 1:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = AppConstant.ScreenWidth * 6 / 8;
                    startY = (range == 1 ? AppConstant.ScreenHeight * 6 / 7 : AppConstant.ScreenHeight * 4 / 7);
                    endX = AppConstant.ScreenWidth * 6 / 8;
                    endY = (range == 1 ? AppConstant.ScreenHeight / 7 : AppConstant.ScreenHeight * 3 / 7);
                }
                directionStr = "向上";
                break;
            case 2:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = AppConstant.ScreenWidth * 6 / 8;
                    startY = (range == 1 ? AppConstant.ScreenHeight / 7 : AppConstant.ScreenHeight * 3 / 7);
                    endX = AppConstant.ScreenWidth * 6 / 8;
                    endY = (range == 1 ? AppConstant.ScreenHeight * 6 / 7 : AppConstant.ScreenHeight * 4 / 7);
                }
                directionStr = "向下";
                break;
            case 3:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = (range == 1 ? AppConstant.ScreenWidth * 6 / 7 : AppConstant.ScreenWidth * 4 / 7);
                    startY = AppConstant.ScreenHeight / 2;
                    endX = (range == 1 ? AppConstant.ScreenWidth / 7 : AppConstant.ScreenWidth * 3 / 7);
                    endY = AppConstant.ScreenHeight / 2;
                }
                directionStr = "向左";
                break;
            case 4:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = (range == 1 ? AppConstant.ScreenWidth / 7 : AppConstant.ScreenWidth * 3 / 7);
                    startY = AppConstant.ScreenHeight / 2;
                    endX = (range == 1 ? AppConstant.ScreenWidth * 6 / 7 : AppConstant.ScreenWidth * 4 / 7);
                    endY = AppConstant.ScreenHeight / 2;
                }
                directionStr = "向右";
                break;
        }
        Toaster.show("执行" + directionStr + "滑动,间隔:" + scrollDuration + ",剩余次数:" + scrollTimes);
        int randomX = (new Random().nextInt(2) == 0 ? -getRandomXY() : +getRandomXY());
        mockSwipe(startX + randomX, startY + getRandomXY(), endX + randomX, endY + getRandomXY(), 0, slideDuration);
        if (timerType == 1) {
            sendDelayMessage();
        } else {
            sendDelayIntent();
        }
    }

    public int getRandomXY() {
        return (new Random().nextInt(5) + 1) * 10;
    }

    public int getRandomSeconds() {
        return (new Random().nextInt(5) + 1);
    }

    public void sendDelayMessage() {
        if (!isRunning) {
            return;
        }
        scrollHandler.sendEmptyMessageDelayed(SCROLL_WHAT, (scrollDuration + (isRandomSeconds ? getRandomSeconds() : 0)) * 1000);
    }

    public void sendDelayIntent() {
        if (!isRunning || alarmManager == null) {
            return;
        }
        Intent intent2 = new Intent(AppConstant.ALARM_RECEIVER_ACTION);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent2, 0);
        long triggerTime = (scrollDuration + (isRandomSeconds ? getRandomSeconds() : 0)) * 1000;
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + triggerTime, pendingIntent);
    }

    public void startRunning() {
        if (timerType == 1) {
            scrollHandler.sendEmptyMessage(SCROLL_WHAT);
        } else {
            sendDelayIntent();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stopRunning() {
        if (scrollHandler != null) {
            scrollHandler.removeMessages(SCROLL_WHAT);
        }
        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
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

    //滑动
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void mockSwipe(int fromX, int fromY, int toX, int toY, long startTime, long duration) {
        Log.d(AppConstant.TAG, "mockSwipe fromX=" + fromX + " fromY=" + fromY + " toX=" + toX + " toY=" + toY + " startTime=" + startTime + " duration=" + duration);
        final Path path = new Path();
        //滑动的起始位置，例如屏幕的中心点X、Y
        path.moveTo(fromX, fromY);
        //需要滑动的位置，如从中心点滑到屏幕的顶部
        path.lineTo(toX, toY);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, duration));

        GestureDescription gestureDescription = builder.build();
        //移动到中心点，100ms后开始滑动，滑动的时间持续400ms，可以调整
        //如果滑动成功，会回调如下函数，可以在下面记录是否滑动成功，滑动成功或失败都要关闭该路径笔
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                path.close();
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                path.close();
            }
        }, null); //handler为空即可
    }

    //双击
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void mockDoubleClick(int X, int Y, long startTime, long duration) {
        final Path path = new Path();
        path.moveTo(X, Y);
        //X和Y是需要双击的按钮坐标
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, duration)).build();
        dispatchGesture(gestureDescription, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Path path2 = new Path();
                path2.moveTo(X, Y);
                // 以下代码是完成第二个手势操作
                GestureDescription.Builder builder2 = new GestureDescription.Builder();
                GestureDescription gestureDescription2 = builder2.addStroke(
                        new GestureDescription.StrokeDescription(path2, startTime, duration)).build();
                ControllerAccessibilityService.this.dispatchGesture(gestureDescription2, null, null);
                path.close();
                path2.close();
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                path.close();
            }
        }, null);
    }
}
