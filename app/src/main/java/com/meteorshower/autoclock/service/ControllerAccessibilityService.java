package com.meteorshower.autoclock.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.google.gson.JsonObject;
import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.constant.Constant;
import com.meteorshower.autoclock.http.ApiService;
import com.meteorshower.autoclock.http.RetrofitManager;
import com.meteorshower.autoclock.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 辅助功能类
 */
public class ControllerAccessibilityService extends AccessibilityService {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private ScheduledExecutorService mScheduledExecutorService;//定时任务的线程池
    private ScheduledFuture mUploadScheduledFuture;

    /**
     * 启动辅助功能连接成功时
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(Constant.TAG, "onServiceConnected");
        //辅助功能链接时启动activity
        /*Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);//启动Activity*/

        JobFactory.getInstance().start();
        JobExecutor.getInstance().start();
        postHeartBeat(5 * 60);//每5分钟上传一次心跳包
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
    public void onInterrupt() {

    }

    private void postHeartBeat(int delayTime) {
        mScheduledExecutorService = Executors.newScheduledThreadPool(5);//线程池
        mUploadScheduledFuture = mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    JsonObject object = new JsonObject();
                    object.addProperty("heart_time", StringUtils.getNow());
                    object.addProperty("is_doing_job", "" + JobExecutor.getInstance().isDoingJob());
                    object.addProperty("is_getting_job", "" + JobFactory.getInstance().isGetJob());
                    RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), object.toString());
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
                } catch (Exception e) {
                    //打印日志
                }
            }
        }, 10, delayTime, TimeUnit.SECONDS);
        mUploadScheduledFuture.cancel(true);
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

}
