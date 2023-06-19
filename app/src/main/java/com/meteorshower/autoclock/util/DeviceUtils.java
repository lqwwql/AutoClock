package com.meteorshower.autoclock.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.RequiresApi;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.constant.AppConstant;

import java.lang.reflect.Method;
import java.util.List;

public class DeviceUtils {

    private static boolean hasExecuteNotch = false;
    private static boolean isNotch = false;
    private static int notchHeight;
    private static int statusBarHeight;

    /**
     * 小米刘海屏判断
     *
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static boolean hasNotchAtXiaoMi() {
        return getInt("ro.miui.notch") == 1;
    }

    public static int getInt(String key) {
        int result = 0;
        if (android.os.Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            try {
                ClassLoader classLoader = MyApplication.getContext().getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 华为刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtHuawei() {
        boolean ret = false;
        try {
            ClassLoader classLoader = MyApplication.getContext().getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }


    /**
     * OPPO刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtOPPO() {
        return MyApplication.getContext().getPackageManager()
                .hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * VIVO刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtVivo() {
        boolean ret = false;
        try {
            ClassLoader classLoader = MyApplication.getContext().getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
//            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 判断三星手机是否有刘海屏
     *
     * @return
     */
    private static boolean hasNotchSamsung() {
        if (android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            try {
                final Resources res = MyApplication.getContext().getResources();
                final int resId = res.getIdentifier("config_mainBuiltInDisplayCutout", "string", "android");
                final String spec = resId > 0 ? res.getString(resId) : null;
                return spec != null && !StringUtils.isEmptyOrNull(spec);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param activity
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    private static boolean isOtherBrandHasNotch(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            View decorView = activity.getWindow().getDecorView();
            if ((Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)) {
                WindowInsets windowInsets = decorView.getRootWindowInsets();
                if (windowInsets != null) {
                    if ((Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P)) {
                        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                        if (displayCutout != null) {
                            List<Rect> rects = displayCutout.getBoundingRects();
                            if (rects != null && rects.size() > 0) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 是否是刘海或是钻孔屏幕 全局只取一次
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean hasNotch(Activity activity) {
        if (!hasExecuteNotch) {
            if (hasNotchAtXiaoMi() || hasNotchAtHuawei() || hasNotchAtOPPO()
                    || hasNotchAtVivo() || hasNotchSamsung() || isOtherBrandHasNotch(activity)) {
                isNotch = true;
            } else {
                isNotch = false;
            }
            hasExecuteNotch = true;
        }
        return isNotch;
    }

    /**
     * 获取刘海高度
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static int getNotchHeight(Activity activity) {
        String manufacturer = android.os.Build.MANUFACTURER.toLowerCase();
        if (hasNotch(activity)) {
            //有刘海才获取高度 否则默认刘海高度是0
            if (manufacturer.equalsIgnoreCase("xiaomi")) {
                notchHeight = getSysStatusBarHeight();//小米刘海会比状态栏小 直接获取状态栏高度
            } else if (manufacturer.equalsIgnoreCase("huawei") || manufacturer.equalsIgnoreCase("honour")) {
                notchHeight = getNotchSizeAtHuaWei();
            } else if (manufacturer.equalsIgnoreCase("vivo")) {
                //VIVO是32dp
                notchHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        32, MyApplication.getContext().getResources().getDisplayMetrics());
            } else if (manufacturer.equalsIgnoreCase("oppo")) {
                notchHeight = 80;//oppo当时是固定数值
            } else if (android.os.Build.MANUFACTURER.equalsIgnoreCase("smartisan")) {
                notchHeight = 82;//当时锤子PDF文档上是固定数值
            } else {
                //其他品牌手机
                if (activity != null && activity.getWindow() != null) {
                    View decorView = activity.getWindow().getDecorView();
                    if ((Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)) {
                        WindowInsets windowInsets = decorView.getRootWindowInsets();
                        if (windowInsets != null) {
                            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
                                DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                                if (displayCutout != null) {
                                    List<Rect> rects = displayCutout.getBoundingRects();
                                    if (rects != null && rects.size() > 1) {
                                        if (rects.get(0) != null) {
                                            notchHeight = rects.get(0).bottom;
                                            return notchHeight;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return notchHeight;
        }
        return 0;
    }

    /**
     * 华为获取刘海高度
     * 获取刘海尺寸：width、height
     * int[0]值为刘海宽度 int[1]值为刘海高度
     */
    public static int getNotchSizeAtHuaWei() {
        int height = 0;
        try {
            ClassLoader cl = MyApplication.getContext().getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            int[] ret = (int[]) get.invoke(HwNotchSizeUtil);
            height = ret[1];
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public static int getStatusBarHeight(Activity activity) {
        if (statusBarHeight == 0) {
            int height = getSysStatusBarHeight();
            int notchHeight = getNotchHeight(activity);
            Log.d(AppConstant.TAG, "getStatusBarHeight height=" + height +" notchHeight="+notchHeight);
            statusBarHeight = Math.max(height, notchHeight);
        }
        return statusBarHeight;
    }

    /**
     * 获得手机状态栏高度
     *
     * @return
     */
    public static int getSysStatusBarHeight() {
        int result = 0;
        Resources resources = MyApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        if (result == 0) {
//            result = resources.getDimensionPixelSize(R.dimen.default_48PX);
        }
        return result;
    }

    public static int getStatusBarHeight() {
        return statusBarHeight;
    }
}
