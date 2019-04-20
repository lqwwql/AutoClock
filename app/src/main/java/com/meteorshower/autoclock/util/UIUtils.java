package com.meteorshower.autoclock.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.view.View;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.meteorshower.autoclock.application.MyApplication;


/**
 * 工具类
 * Created by xiaoyehai on 2017/1/8.
 */
public class UIUtils {

    /**
     * 获取Context
     *
     * @return
     */
    public static Context getContext() {
        return MyApplication.getContext();
    }

    /**
     * 获取Handler
     *
     * @return
     */
    public static Handler getHandler() {
        return MyApplication.getHandler();
    }

    /**
     * 获取主线程id:MainThreadId
     *
     * @return
     */
    public static int getMainThreadId() {
        return MyApplication.getMainThreadId();
    }

    ////////////////////加载资源文件//////////////////////////

    /**
     * 获取资源文件的字符串
     *
     * @param id
     * @return
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /**
     * 获取资源文件的字符串数组
     *
     * @param id
     * @return
     */
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    /**
     * 获取资源的图片
     *
     * @param id
     * @return
     */
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    /**
     * 获取资源文件color.xml中的颜色
     *
     * @param id
     * @return
     */
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    /**
     * 根据id获取颜色的状态选择器
     *
     * @param id
     * @return
     */
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    /**
     * 获取资源文件dimens.xml文件中的尺寸
     *
     * @param id
     * @return
     */
    public static int getDimen(int id) {
        //返回具体像素值
        return getContext().getResources().getDimensionPixelSize(id);
    }

    ///////////////////////////////////////////////

    /**
     * dp转为px
     *
     * @param value
     * @return
     */
    public static int dp2px(float value) {
        //设备密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }

    /**
     * px转为dp
     *
     * @param px
     * @return
     */
    public static int px2dp(float px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }

    /**
     * 获取屏幕的宽
     *
     * @return
     */
    public static int getScreenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕的高
     *
     * @return
     */
    public static int getScreenHeight() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 加载布局文件
     *
     * @param id
     * @return
     */
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

    /**
     * 判断是否运行在主线程
     *
     * @return
     */
    public static boolean isRunOnUIThread() {
        //获取当前线程id
        int myTid = Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        }
        return false;
    }

    /**
     * 运行在主线程的方法
     *
     * @param r
     */
    public static void runOnUIThread(Runnable r) {
        if (isRunOnUIThread()) {
            //在主线程，直接运行
            r.run();
        } else {
            //在子线程，借助Handler让其运行在主线程
            getHandler().post(r);
        }
    }

    /* * 根据值, 设置spinner默认选中:
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.equals(apsAdapter.getItem(i).toString())) {
//                spinner.setSelection(i,true);// 默认选中项
                spinner.setSelection(i);// 默认选中项

                break;
            }
        }
    }
}
