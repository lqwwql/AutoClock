package com.meteorshower.autoclock.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import com.meteorshower.autoclock.constant.AppConstant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class NotchScreenUtils {
    private static final String SP_NAME = "MY_NOTCH_SP";//保存异形屏Name
    private static final String KEY_IS_NOTCH_SCREEN = "KEY_IS_NOTCH_SCREEN";//是否是异形屏KEY

    /**
     * 保存当前手机是否是异形屏
     *
     * @param isNotchScreen 是否是异形屏(刘海屏,水滴屏,挖孔屏等)
     */
    public static void saveScreen(Context context, boolean isNotchScreen) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        if (!sp.contains(KEY_IS_NOTCH_SCREEN)) {//不存在的时候保存
            edit.putBoolean(KEY_IS_NOTCH_SCREEN, isNotchScreen);
        } else if (sp.contains(KEY_IS_NOTCH_SCREEN) && !sp.getBoolean(KEY_IS_NOTCH_SCREEN, false)) {
            //当这个值存在并且是false的时候也保存
            edit.putBoolean(KEY_IS_NOTCH_SCREEN, isNotchScreen);
        }
        //当这个值存在并且为true的时候不去覆盖数据.
        edit.commit();
    }

    /**
     * 获取保存在本地的屏幕类型,是否是异形屏(刘海屏,水滴屏,挖孔屏等)
     */
    public static boolean getScreenType(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getBoolean(KEY_IS_NOTCH_SCREEN, false);
    }


    /**
     * 判断是否是8.0系统之后的异形屏(刘海屏,水滴屏,挖孔屏等)
     * 必须在Activity的onAttachedToWindow()方法中才会生效,其他地方windowInsets为 null
     * onCreate->onStart->onResume->onAttachedToWindow
     */
    public static boolean IsNotchScreen(Context context, WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= 26) {
            if (isAndroidPNotchScreen(windowInsets) || hasNotchInScreenAtVivo(context) || hasNotchInScreenAtOppo(context)
                    || hasNotchInScreenAtHuawei(context) || hasNotchInScreenAtMI()) { //TODO 各种品牌
                return true;
            }
        }
        return false;
    }


    /**
     * Android P 异形屏判断
     *
     * @param windowInsets 必须使用控件的setOnApplyWindowInsetsListener()接口回调方法中的才或者在Activity的
     *                     onAttachedToWindow()方法中调用getWindow().getDecorView().getRootWindowInsets();
     *                     其他地方获取会直接为空
     */
    @SuppressLint("NewApi")
    public static boolean isAndroidPNotchScreen(WindowInsets windowInsets) {
        boolean isNotchScreen = false;
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) && null != windowInsets) {
            DisplayCutout cutout = windowInsets.getDisplayCutout();
            if (cutout != null) {
                List<Rect> rects = cutout.getBoundingRects();
                if (rects != null && rects.size() > 0) {
                    isNotchScreen = true;
                }
            }
        }
        return isNotchScreen;
    }

    /**
     * 判断是否华为异形屏
     */
    public static boolean hasNotchInScreenAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(AppConstant.TAG,"hasNotchInScreenAtHuawei()-> ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(AppConstant.TAG,"hasNotchInScreenAtHuawei()-> NoSuchMethodException");
        } catch (Exception e) {
            Log.e(AppConstant.TAG,"hasNotchInScreenAtHuawei()-> Exception");
        } finally {
            Log.e(AppConstant.TAG,"hasNotchInScreenAtHuawei()-> ClassNotFoundException");
            return ret;
        }
    }

    /**
     * 获取华为O版本异形屏宽高
     */
    public static int[] getNotchSizeForHuawei(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(AppConstant.TAG,"HUAWEI-getNotchSize()-> ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(AppConstant.TAG,"HUAWEI-getNotchSize()-> NoSuchMethodException");
        } catch (Exception e) {
            Log.e(AppConstant.TAG,"HUAWEI-getNotchSize()-> Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 判断oppo 机型是否异形屏
     */
    public static boolean hasNotchInScreenAtOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    public static String getNotchOppoProperty() {
        String value = "";
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method hideMethod = cls.getMethod("get", String.class);
            Object object = cls.newInstance();
            value = (String) hideMethod.invoke(object, "ro.oppo.screen.heteromorphism");
        } catch (ClassNotFoundException e) {
            Log.e(AppConstant.TAG,"getNotchOppoProperty()->get error() "+e.getMessage());
        } catch (NoSuchMethodException e) {
            Log.e(AppConstant.TAG,"getNotchOppoProperty()->get error() "+e.getMessage());
        } catch (InstantiationException e) {
            Log.e(AppConstant.TAG,"getNotchOppoProperty()->get error() "+e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e(AppConstant.TAG,"getNotchOppoProperty()->get error() "+e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.e(AppConstant.TAG,"getNotchOppoProperty()->get error() "+e.getMessage());
        } catch (InvocationTargetException e) {
            Log.e(AppConstant.TAG,"getNotchOppoProperty()->get error() "+e.getMessage());
        }
        return value;
    }

    /**
     * 判断vivo机型是否异形屏
     */
    public static final int NOTCH_IN_SCREEN_VOIO = 0x00000020;//是否有凹槽
    public static final int ROUNDED_IN_SCREEN_VOIO = 0x00000008;//是否有圆角

    public static boolean hasNotchInScreenAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class FtFeature = cl.loadClass("android.util.FtFeature");
            Method get = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) get.invoke(FtFeature, NOTCH_IN_SCREEN_VOIO);

        } catch (ClassNotFoundException e) {
            Log.e(AppConstant.TAG,"hasNotchInScreenAtVivo()-> ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(AppConstant.TAG,"hasNotchInScreenAtVivo()-> NoSuchMethodException");
        } catch (Exception e) {
            Log.e(AppConstant.TAG,"hasNotchInScreenAtVivo()-> Exception");
        } finally {
            return ret;
        }
    }

    /**
     * 判断小米机型是否异形屏
     */
    private static Method getBooleanMethod = null;

    public static boolean hasNotchInScreenAtMI() {
        try {
            if (getBooleanMethod == null) {
                getBooleanMethod = Class.forName("android.os.SystemProperties").getMethod("getBoolean", String.class, boolean.class);
            }
            //Log.i(TAG,"getBoolean:"+getBooleanMethod.invoke(null, key, def));
            return (Boolean) getBooleanMethod.invoke(null, "ro.miui.notch", false);
        } catch (Exception e) {
            return false;
        }
    }

    public static void setNotchScreenParentLayout(final Context context, final View parentLayout) {
        //异形屏 刘海屏从安卓8.0系统开始推出
        if (null != context && null != parentLayout && Build.VERSION.SDK_INT >= 26) {
            //设置异形屏的安全区域,避免遮挡内容
            parentLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    boolean isNotchScreen = IsNotchScreen(context, windowInsets);

                    if (isNotchScreen && null != windowInsets) {
                        parentLayout.setPadding(0, 0, 0, 0);
                        if (Build.VERSION.SDK_INT >= 28 && null != windowInsets.getDisplayCutout()) {
                            DisplayCutout cutout = windowInsets.getDisplayCutout();
                            if (cutout != null) {
                                List<Rect> rects = cutout.getBoundingRects();
                                if (rects != null && rects.size() > 0) {
                                    Log.e(AppConstant.TAG,"异形屏->安卓9.0之后系统,间距左" + cutout.getSafeInsetLeft() +
                                            ",上:" + cutout.getSafeInsetTop() + ",右:" + cutout.getSafeInsetRight() + ",下:" + cutout.getSafeInsetBottom());
                                    parentLayout.setPadding(cutout.getSafeInsetLeft(), cutout.getSafeInsetTop(),
                                            cutout.getSafeInsetRight(), cutout.getSafeInsetBottom());
                                }
                            }
                        }
                    }
                    return windowInsets;
                }
            });
        }
    }
    
}
