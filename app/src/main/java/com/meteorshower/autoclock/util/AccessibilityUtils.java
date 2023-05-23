package com.meteorshower.autoclock.util;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.crash.NodeNoFindException;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;

import java.util.List;
import java.util.Random;

public class AccessibilityUtils {

    static int KEYCODE_ENTER = 66;
    static int KEYCODE_GOBACK = 4;
    static int KEYCODE_TAB = 61;
    static int KEYCODE_DEL = 67;

    /**
     * 打开指定Activity
     */
    public static boolean openAppByName(String activityName) throws InterruptedException {
        ShellUtils.performSuCommand("am start --activity-clear-top " + activityName);
        return ControllerAccessibilityService.Wait(25 * 100);
    }

    public static boolean openAppByPackageAndName(String packageName, String activityName) throws InterruptedException {
        String command = "adb shell am start -n \"" + packageName + "/" + activityName + "\" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER";
        ShellUtils.performSuCommand(command);
        return ControllerAccessibilityService.Wait(25 * 100);
    }

    public static ShellUtils.CommandResult openAppByExeCommand(String packageName, String activityName) throws InterruptedException {
        String command = "am start -n \"" + packageName + "/" + activityName + "\" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER";
        return ShellUtils.execCommand(command, false);
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

    public static String getClickCommand(Rect outBounds) {
        Random random = new Random(System.currentTimeMillis());
        int x = (outBounds.left + 1)
                + random.nextInt(outBounds.right - outBounds.left - 2);
        int y = (outBounds.top + 1)
                + random.nextInt(outBounds.bottom - outBounds.top - 2);
        return "input tap " + x + " " + y;
    }

    public static Rect getClickRect(int x, int y, int offSet) {
        Rect rect = new Rect();
        rect.set(x - offSet, y - offSet, x + offSet, y + offSet);
        return rect;
    }

    public static void goBack() throws InterruptedException {
        ShellUtils.performSuCommand("input keyevent " + KEYCODE_GOBACK);
    }

    public static String getGoBackCommand() {
        return "input keyevent " + KEYCODE_GOBACK;
    }

    /**
     * 回到桌面
     */
    public static void goToHome(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }

    public static void goToAccessibilitySetting(Context context){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        context.startActivity(intent);
    }

    /**
     * 唤醒屏幕
     */
    public static void wakeUpAndUnlock(Context context) {
        //屏锁管理器
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

    public static AccessibilityNodeInfo findNodeEqualText(String text) {
        AccessibilityNodeInfo root = null;
        try {
            root = getRootInActiveWindow();
        } catch (NodeNoFindException e) {
            e.printStackTrace();
        }

        if (root == null)
            return null;

        List<AccessibilityNodeInfo> l = root
                .findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < l.size(); i++) {
            AccessibilityNodeInfo tmp = l.get(i);
            if (tmp.getText() != null && tmp.getText().toString().equals(text))
                return tmp;
        }

        return null;
    }

    public static AccessibilityNodeInfo findNodeEqualContentDescription(String text) {
        AccessibilityNodeInfo root = null;
        try {
            root = getRootInActiveWindow();
        } catch (NodeNoFindException e) {
            e.printStackTrace();
        }

        if (root == null)
            return null;

        List<AccessibilityNodeInfo> l = root
                .findAccessibilityNodeInfosByText(text);
        for (int i = 0; i < l.size(); i++) {
            AccessibilityNodeInfo tmp = l.get(i);
            if (tmp.getContentDescription() != null && tmp.getContentDescription().toString().equals(text))
                return tmp;
        }

        return null;
    }

    public static AccessibilityNodeInfo findNode(AccessibilityNodeInfo node, String textOrDescribe, boolean equalOrContains) {
        if (node == null)
            return null;

        if (equalOrContains == true) {
            if ((node.getText() != null && node.getText().toString().equals(textOrDescribe))
                    || (node.getContentDescription() != null && node.getContentDescription().toString().equals(textOrDescribe))) {
                return node;
            }
        } else {
            if ((node.getText() != null && node.getText().toString().contains(textOrDescribe))
                    || (node.getContentDescription() != null && node.getContentDescription().toString().contains(textOrDescribe))) {
                return node;
            }
        }

        AccessibilityNodeInfo target = null;
        for (int i = 0; i < node.getChildCount(); i++) {
            target = findNode(node.getChild(i), textOrDescribe, equalOrContains);
            if (target != null)
                break;
        }

        return target;
    }


    public static AccessibilityNodeInfo getRootInActiveWindow()
            throws NodeNoFindException {
        return getRootInActiveWindow(null);
    }

    @SuppressLint("NewApi")
    public static AccessibilityNodeInfo getRootInActiveWindow(String TAG)
            throws NodeNoFindException {
        if (TAG == null) {
            TAG = "getRoot";
        }
        AccessibilityNodeInfo root = null;
        for (int i = 0; i < 3; i++) {
            ControllerAccessibilityService mAccessibilityServiceTool = ControllerAccessibilityService.getInstance();
            Log.i(TAG, "" + mAccessibilityServiceTool);
            if (mAccessibilityServiceTool != null)
                root = mAccessibilityServiceTool.getRootInActiveWindow();
            if (root != null && android.os.Build.VERSION.SDK_INT >= 18)
                root.refresh();
            if (root != null) {
                Log.i(TAG, "root != null, return.");
                return root;
            }
            try {
                Log.i(TAG, "root == null, sleep 2s.");
                Thread.sleep(2000);
            } catch (Exception e) {

            }
        }
        throw new NodeNoFindException(
                "AccessibilityUtil.getRootInActiveWindowroot return null");
    }

    public static String getCommand(String input) {
        Log.d(AppConstant.TAG, "input = " + input);
        String command = "";
        switch (input) {
            case "1":
                command = AppConstant.THREAD_IS_START_COMMAND;
                break;
            case "2":
                command = AppConstant.MAIN_THREAD_RUNNING_CHANGE;
                break;
            case "3":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(AppConstant.DESKTOP_DD_LT[0], AppConstant.DESKTOP_DD_LT[1], 10));
                break;
            case "4":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(AppConstant.DD_WORK_LT[0], AppConstant.DD_WORK_LT[1], 10));
                break;
            case "5":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(AppConstant.DD_KAO_LT[0], AppConstant.DD_KAO_LT[1], 10));
                break;
            case "6":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(AppConstant.DD_UP_LT[0], AppConstant.DD_UP_LT[1], 10));
                break;
            case "7":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getGoBackCommand();
                break;
            case "8":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(AppConstant.DD_NEWS_LT[0], AppConstant.DD_NEWS_LT[1], 10));
                break;
            case "9":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(AppConstant.DD_DOWN_LT[0], AppConstant.DD_DOWN_LT[1], 10));
                break;
            case "10":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(AppConstant.DESKTOP_AU_LT[0], AppConstant.DESKTOP_AU_LT[1], 10));
                break;
            case "11":
                try {
                    Thread.sleep(8 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(AppConstant.ZFB_QJ[0], AppConstant.ZFB_QJ[1], 10));
                break;
            case "12":
                command = "nohup app_process -Djava.class.path=/data/local/tmp/classes.dex /system/bin com.sftech.shellprocess.ShellMain & ";
                break;
        }
        Log.d(AppConstant.TAG, "command = " + command);
        return command;
    }
}
