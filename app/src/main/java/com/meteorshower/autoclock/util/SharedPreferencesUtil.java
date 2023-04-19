package com.meteorshower.autoclock.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharedPreferencesUtil {

    public static final String APP_CONFIG = "APP_CONFIG";
    public static final String SCROLL_CONFIG = "SCROLL_CONFIG";

    public static final String SCROLL_TIME_KEY = "SCROLL_TIME_KEY";
    public static final String SCROLL_DURATION_KEY = "SCROLL_DURATION_KEY";
    public static final String SLIDE_DURATION_KEY = "SLIDE_DURATION_KEY";
    public static final String IS_RANDOM_TIME_KEY = "IS_RANDOM_TIME_KEY";
    public static final String SCROLL_DIRECTION_KEY = "SCROLL_DIRECTION_KEY";
    public static final String FINISH_OP_KEY = "FINISH_OP_KEY";
    public static final String TIMER_TYPE_KEY = "TIMER_TYPE_KEY";
    public static final String SCROLL_RANGE_KEY = "SCROLL_RANGE_KEY";
    public static final String FLOATING_VIEW_FUNCTION_KEY = "FLOATING_VIEW_FUNCTION_KEY";
    public static final String FLOATING_VIEW_SIZE_KEY = "FLOATING_VIEW_SIZE_KEY";

    //String的保存及获取
    public static void saveDataToSharedPreferences(Context context,
                                                   String key, String value, String configName) {
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getDataToSharedPreferences(Context context,
                                                    String key, String defaluevalue, String configName) {
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        return sp.getString(key, defaluevalue);
    }

    //Boolean的保存及获取
    public static void saveDataToSharedPreferences(Context context,
                                                   String key, boolean value, String configName) {
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getDataToSharedPreferences(Context context,
                                                     String key, boolean defaluevalue, String configName) {
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        return sp.getBoolean(key, defaluevalue);
    }

    //FLOAT的保存及获取
    public static void saveDataToSharedPreferences(Context context,
                                                   String key, float value, String configName) {
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        sp.edit().putFloat(key, value).commit();
    }

    public static float getDataToSharedPreferences(Context context,
                                                 String key, float defaluevalue, String configName) {
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        return sp.getFloat(key, defaluevalue);
    }



    //int的保存及获取
    public static void saveDataToSharedPreferences(Context context,
                                                   String key, int value, String configName) {
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getDataToSharedPreferences(Context context,
                                                 String key, int defaluevalue, String configName) {
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        return sp.getInt(key, defaluevalue);
    }

    public static Map<String,?> getAll(Context context,String configName){
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        return sp.getAll();
    }

    public static void remove(Context context,String configName,String account){
        SharedPreferences sp = context.getSharedPreferences(configName, context.MODE_PRIVATE);
        sp.edit().remove(account).commit();
    }

}
