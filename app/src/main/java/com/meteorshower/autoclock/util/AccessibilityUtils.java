package com.meteorshower.autoclock.util;

import android.graphics.Rect;
import android.util.Log;

import com.meteorshower.autoclock.service.ControllerAccessibilityService;

import java.util.Random;

public class AccessibilityUtils {

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
}
