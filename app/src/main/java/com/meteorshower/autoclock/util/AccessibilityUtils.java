package com.meteorshower.autoclock.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.PowerManager;
import android.util.Log;

import com.meteorshower.autoclock.service.ControllerAccessibilityService;

import java.util.Random;

public class AccessibilityUtils {

    static int KEYCODE_ENTER = 66;
    static int KEYCODE_GOBACK = 4;
    static int KEYCODE_TAB = 61;
    static int KEYCODE_DEL = 67;

    /**
     * 打开指定Activity
     */
    public static boolean openModifyNameUI() throws InterruptedException {
        ShellUtils.performSuCommand("am start --activity-clear-top com.alibaba.android.rimet.biz.SplashActivity");
        return ControllerAccessibilityService.Wait(25 * 100);
    }

    /**
     * 点击指定区域
     */
    public static void oneClick(Rect outBounds) throws InterruptedException {
        Log.d("lqwtest", "click oneClick");
        Random random = new Random(System.currentTimeMillis());
        int x = (outBounds.left + 1)
                + random.nextInt(outBounds.right - outBounds.left - 2);
        int y = (outBounds.top + 1)
                + random.nextInt(outBounds.bottom - outBounds.top - 2);
        ShellUtils.performSuCommand("input tap " + x + " " + y);
    }


    public static void clickRect(Rect outBounds) {
        boolean isRoot = ShellUtils.checkRootPermission();
        Log.d("lqwtest", "isroot = " + isRoot);
        if (isRoot) {
            Random random = new Random(System.currentTimeMillis());
            int x = (outBounds.left + 1)
                    + random.nextInt(outBounds.right - outBounds.left - 2);
            int y = (outBounds.top + 1)
                    + random.nextInt(outBounds.bottom - outBounds.top - 2);
            String commonds = "input tap " + x + " " + y;
            ShellUtils.CommandResult result = ShellUtils.execCommand(commonds, isRoot);
            Log.d("lqwtest", "result = " + result.toString());
        }
    }

    public static void goBack() throws InterruptedException {
        ShellUtils.performSuCommand("input keyevent " + KEYCODE_GOBACK);
    }

    /**
     * 回到桌面
     * */
    public static void goToHome(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }

    /**
     * 唤醒屏幕
     * */
    public static void wakeUpAndUnlock(Context context){
        //屏锁管理器
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

}
