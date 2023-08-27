package com.meteorshower.autoclock.util;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.activity.ScrollSettingActivity;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.event.ScrollFinishEvent;
import com.meteorshower.autoclock.receiver.AlarmReceiver;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Random;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;
import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME;

/**
 * 自动点击/滑动工具类
 */
public class AutoClickUtil {

    private static AutoClickUtil instance;
    private ControllerAccessibilityService service;
    public static final int SCROLL_WHAT = 1001;
    public static final int CONTINUE_SCROLL_WHAT = 1002;
    private int scrollTimes = 0;
    private int scrollDuration = 0;
    private int slideDuration = 0;
    private boolean isRunning = false;
    private boolean isStop = false;
    private boolean isRandomSeconds = false;
    private boolean isCheckJump = false;
    private boolean isContinue = false;
    private int direction = 1;
    private int finishOp = 1;
    private int range = 1;//1-大幅度,2-小幅度
    private int clickX = 0;
    private int clickY = 0;
    private int clickTimes = 0;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private AlarmReceiver alarmReceiver;
    private int timerType = 1;//1-Handler,2-闹钟
    private int clickCount = 0;
    private int scrollCount = 0;
    private List<Point> trackList;

    private AutoClickUtil() {
    }

    public static AutoClickUtil getInstance() {
        if (instance == null) {
            instance = new AutoClickUtil();
        }
        return instance;
    }

    public void setService(ControllerAccessibilityService service) {
        this.service = service;
    }

    private Handler scrollHandler = new Handler(Looper.myLooper()) {
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
                case CONTINUE_SCROLL_WHAT:
                    Toaster.show("执行单击位置[" + clickX + "," + clickY + "]");
                    Log.d(AppConstant.TAG, "执行单击位置[" + clickX + "," + clickY + "]");
                    singleClick(clickX, clickY, 0, 50);
                    sendEmptyMessageDelayed(SCROLL_WHAT, 5 * 1000);
                    break;
            }
        }
    };

    public void setScrollParam(int scrollTimes, int scrollDuration, int slideDuration, int direction,
                               int finishOp, int timerType, int range,
                               boolean isRunning, boolean isRandomSeconds, boolean isCheckJump, boolean isContinue,
                               int clickTimes, int clickX, int clickY, List<Point> trackList) {
        this.scrollTimes = scrollTimes;
        this.scrollDuration = scrollDuration;
        this.slideDuration = slideDuration;
        this.isRandomSeconds = isRandomSeconds;
        this.direction = direction;
        this.finishOp = finishOp;
        this.timerType = timerType;
        this.range = range;
        this.isCheckJump = isCheckJump;
        this.isContinue = isContinue;
        this.clickTimes = clickTimes;
        this.clickX = clickX;
        this.clickY = clickY;
        clickCount = 0;
        scrollCount = 0;
        this.trackList = trackList;
    }

    public void executeScrollView() {
        if (!isRunning) {
            return;
        }
        if (service == null) {
            Toaster.show("辅助服务未启动");
            return;
        }
        Log.d(AppConstant.TAG, "executeScrollView scrollTimes=" + scrollTimes + " scrollCount=" + scrollCount);
        if (scrollCount >= scrollTimes) {
            scrollCount = 0;
            if (direction == 6) {
                direction = 5;
            }
            if (finishOp == 2) {
                service.performGlobalAction(GLOBAL_ACTION_BACK);
                clickCount++;
                if (isContinue && clickCount < clickTimes) {
                    Toaster.show("执行第" + clickCount + "次结束");
                    scrollHandler.sendEmptyMessageDelayed(CONTINUE_SCROLL_WHAT, 3 * 1000);
                    return;
                }
                clickCount = 0;
                Toaster.show("滑动执行完毕,返回上一页");
            } else if (finishOp == 3) {
                Toaster.show("滑动执行完毕,返回程序");
                service.startActivity(new Intent(service, ScrollSettingActivity.class));
            } else if (finishOp == 4) {
                Toaster.show("滑动执行完毕,返回桌面");
                service.performGlobalAction(GLOBAL_ACTION_HOME);
            } else {
                Toaster.show("滑动执行完毕,无操作");
            }
            stopRunning();
            EventBus.getDefault().post(new ScrollFinishEvent());
            return;
        }
        scrollCount++;

        int startX = 260, startY = 820, endX = 260, endY = 260;
        String directionStr = "";
        switch (direction) {
            case 1:
            case 5:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = (AppConstant.ScreenWidth * 6 / 8) - (AppConstant.ScreenWidth / 10);
                    startY = (range == 1 ? AppConstant.ScreenHeight * 6 / 7 : AppConstant.ScreenHeight * 4 / 7);
                    endX = AppConstant.ScreenWidth * 6 / 8;
                    endY = (range == 1 ? AppConstant.ScreenHeight / 7 : AppConstant.ScreenHeight * 3 / 7);
                }
                directionStr = "向上";
                if (direction == 5) {
                    direction = 6;
                }
                break;
            case 2:
            case 6:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = (AppConstant.ScreenWidth * 6 / 8) - (AppConstant.ScreenWidth / 10);
                    startY = (range == 1 ? AppConstant.ScreenHeight / 7 : AppConstant.ScreenHeight * 3 / 7);
                    endX = AppConstant.ScreenWidth * 6 / 8;
                    endY = (range == 1 ? AppConstant.ScreenHeight * 6 / 7 : AppConstant.ScreenHeight * 4 / 7);
                }
                directionStr = "向下";
                if (direction == 6) {
                    direction = 5;
                }
                break;
            case 3:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = (range == 1 ? AppConstant.ScreenWidth * 6 / 7 : AppConstant.ScreenWidth * 4 / 7);
                    startY = (AppConstant.ScreenHeight / 2) - (AppConstant.ScreenHeight / 20) ;
                    endX = (range == 1 ? AppConstant.ScreenWidth / 7 : AppConstant.ScreenWidth * 3 / 7);
                    endY = AppConstant.ScreenHeight / 2;
                }
                directionStr = "向左";
                break;
            case 4:
                if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
                    startX = (range == 1 ? AppConstant.ScreenWidth / 7 : AppConstant.ScreenWidth * 3 / 7);
                    startY = (AppConstant.ScreenHeight / 2) - (AppConstant.ScreenHeight / 20) ;
                    endX = (range == 1 ? AppConstant.ScreenWidth * 6 / 7 : AppConstant.ScreenWidth * 4 / 7);
                    endY = AppConstant.ScreenHeight / 2;
                }
                directionStr = "向右";
                break;
        }
        Toaster.show("执行" + directionStr + "滑动,间隔:" + scrollDuration + ",剩余次数:" + (scrollTimes - scrollCount));
        int randomX = (new Random().nextInt(2) == 0 ? -getRandomXY() : +getRandomXY());
        if (trackList != null && !trackList.isEmpty()) {
            mockSwipeLine(trackList, 0, scrollDuration);
        } else {
            mockSwipe(startX + randomX, startY + getRandomXY(), endX + randomX, endY + getRandomXY(), 0, slideDuration);
        }
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
        pendingIntent = PendingIntent.getBroadcast(service, 0, intent2, 0);
        long triggerTime = (scrollDuration + (isRandomSeconds ? getRandomSeconds() : 0)) * 1000;
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + triggerTime, pendingIntent);
    }

    public void startRunning() {
        isRunning = true;
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
        resetParam();
    }


    //滑动-起止点
    public void mockSwipe(int fromX, int fromY, int toX, int toY, long startTime, long duration) {
        if (service == null) {
            Toaster.show("辅助服务未启动");
            return;
        }
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
        service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
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

    //滑动-轨迹集合
    public void mockSwipeLine(List<Point> dataList, long startTime, long duration) {
        if (service == null) {
            Toaster.show("辅助服务未启动");
            return;
        }
        if (dataList == null || dataList.isEmpty()) {
            Toaster.show("轨迹点为空");
            return;
        }
        final Path path = new Path();
        //滑动的起始位置，例如屏幕的中心点X、Y
        path.moveTo(dataList.get(0).x, dataList.get(0).y);
        for (int i = 1; i < dataList.size(); i++) {
            //需要滑动的位置，如从中心点滑到屏幕 的顶部
            Log.d(AppConstant.TAG, "mockSwipeLine x=" + dataList.get(i).x + " y=" + dataList.get(i).y);
            path.lineTo(dataList.get(i).x, dataList.get(i).y);
            path.moveTo(dataList.get(i).x, dataList.get(i).y);
        }


        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, duration));

        GestureDescription gestureDescription = builder.build();
        //移动到中心点，100ms后开始滑动，滑动的时间持续400ms，可以调整
        //如果滑动成功，会回调如下函数，可以在下面记录是否滑动成功，滑动成功或失败都要关闭该路径笔
        service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
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


    //滑动-起止点加速
    public void mockSwipeSpeedUp(int fromX, int fromY, int toX, int toY, long startTime, long duration) {
        if (service == null) {
            Toaster.show("辅助服务未启动");
            return;
        }
        Log.d(AppConstant.TAG, "mockSwipeSpeedUp fromX=" + fromX + " fromY=" + fromY + " toX=" + toX + " toY=" + toY + " startTime=" + startTime + " duration=" + duration);
        final Path path = new Path();
        //滑动的起始位置，例如屏幕的中心点X、Y
        path.moveTo(fromX, fromY);
        //需要滑动的位置，如从中心点滑到屏幕的顶部
        path.lineTo(fromX, fromY - 100);

        final Path path1 = new Path();
        //滑动的起始位置，例如屏幕的中心点X、Y
        path1.moveTo(fromX, fromY - 100);
        //需要滑动的位置，如从中心点滑到屏幕的顶部
        path1.lineTo(fromX, fromY - 400);


        final Path path2 = new Path();
        //滑动的起始位置，例如屏幕的中心点X、Y
        path2.moveTo(fromX, fromY - 400);
        //需要滑动的位置，如从中心点滑到屏幕的顶部
        path2.lineTo(fromX, 0);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, duration));
        builder.addStroke(new GestureDescription.StrokeDescription(path1, startTime, duration));
        builder.addStroke(new GestureDescription.StrokeDescription(path2, startTime, duration));

        GestureDescription gestureDescription = builder.build();
        //移动到中心点，100ms后开始滑动，滑动的时间持续400ms，可以调整
        //如果滑动成功，会回调如下函数，可以在下面记录是否滑动成功，滑动成功或失败都要关闭该路径笔
        service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
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

    //单击
    public void singleClick(int X, int Y, long startTime, long duration) {
        if (service == null) {
            Toaster.show("辅助服务未启动");
            return;
        }
        final Path path = new Path();
        path.moveTo(X, Y);
        //X和Y是需要双击的按钮坐标
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, duration)).build();
        service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
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
        }, null);
    }

    //双击
    public void mockDoubleClick(int X, int Y, long startTime, long duration) {
        if (service == null) {
            Toaster.show("辅助服务未启动");
            return;
        }
        final Path path = new Path();
        path.moveTo(X, Y);
        //X和Y是需要双击的按钮坐标
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, startTime, duration)).build();
        service.dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Path path2 = new Path();
                path2.moveTo(X, Y);
                // 以下代码是完成第二个手势操作
                GestureDescription.Builder builder2 = new GestureDescription.Builder();
                GestureDescription gestureDescription2 = builder2.addStroke(
                        new GestureDescription.StrokeDescription(path2, startTime, duration)).build();
                service.dispatchGesture(gestureDescription2, null, null);
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

    public void showNodes() {
        try {
            if (service == null) {
                Toaster.show("辅助服务未启动");
                return;
            }

            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) {
                Log.d("nodes", "root == null");
                return;
            }
            showChildNodes(root);
        } catch (Exception e) {
            Log.d("nodes", "showNodes error = " + Log.getStackTraceString(e));
        }
    }

    private void showChildNodes(AccessibilityNodeInfo parent) {
        try {
            if (parent == null) {
                Log.d("nodes", "parent == null");
                return;
            }
            int childCount = parent.getChildCount();
            Log.d("nodes", "childCount = " + childCount);
            for (int i = 0; i < childCount; i++) {
                AccessibilityNodeInfo child = parent.getChild(i);
                if (child == null) {
                    continue;
                }
                if (child.getChildCount() > 1) {
                    showChildNodes(child);
                } else {
                    Log.e("nodes", " ClassName=" + child.getClassName()
                            + " Text=" + child.getText()
                            + " ContentDescription=" + child.getContentDescription()
                            + " WindowId=" + child.getWindowId()
                            + " child=" + child);
                    LogUtils.getInstance().i("ControllerAccessibilityService ClassName=" + child.getClassName()
                            + " Text=" + child.getText()
                            + " ContentDescription=" + child.getContentDescription()
                            + " WindowId=" + child.getWindowId()
                            + " child=" + child);
                }
            }
        } catch (Exception e) {
            Log.d("nodes", "showChildNodes error = " + Log.getStackTraceString(e));
        }
    }

    public void clickJump() {
        try {
            if (service == null) {
                Toaster.show("辅助服务未启动");
                return;
            }

            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) {
                Log.d("nodes", "root == null");
                return;
            }
            AccessibilityNodeInfo jumpNode = AccessibilityUtils.findNode(root, "跳过", false);
            if (jumpNode == null) {
                Log.d("nodes", "jumpNode == null");
                return;
            }
            Log.d("nodes", "find jumpNode =" + jumpNode);
            Rect outRect = new Rect();
            jumpNode.getBoundsInScreen(outRect);
            Log.d("nodes", "find jumpNode outRect=" + outRect);
            if (outRect.left <= 0 || outRect.top <= 0 || outRect.right <= 0 | outRect.bottom <= 0) {
                Log.d("nodes", "jumpNode outRect is empty ");
                return;
            }

            //非右侧
            if (outRect.left < (AppConstant.ScreenWidth * 2 / 3) || outRect.right < (AppConstant.ScreenWidth * 2 / 3)) {
                Log.d("nodes", "jumpNode outRect is not in available ");
                return;
            }
            //非右上角或右下角
            else if (outRect.top > (AppConstant.ScreenHeight / 5) || outRect.top < (AppConstant.ScreenHeight * 4 / 5)) {
                Log.d("nodes", "jumpNode outRect is not in available ");
                return;
            }
            jumpNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            LogUtils.getInstance().e("ControllerAccessibilityService clickJump 点击跳过 jumpNode=" + jumpNode);
            Toaster.show("点击跳过");
        } catch (Exception e) {
            LogUtils.getInstance().e("clickJump error = " + Log.getStackTraceString(e));
        }
    }

    public void performGlobalAction(int action) {
        if (service == null) {
            Toaster.show("辅助服务未启动");
            return;
        }
        service.performGlobalAction(action);
    }


    public void resetParam() {
        isRunning = false;
        clickCount = 0;
        scrollCount = 0;
        if (scrollHandler != null) {
            scrollHandler.removeMessages(SCROLL_WHAT);
            scrollHandler.removeMessages(CONTINUE_SCROLL_WHAT);
        }
    }
}
