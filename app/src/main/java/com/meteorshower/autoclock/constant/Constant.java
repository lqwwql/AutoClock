package com.meteorshower.autoclock.constant;

public class Constant {

    //自身
    public static String LOCAL_APPNAME = "com.meteorshower.autoclock";
    public static String LOCAL_ACTIVITY_NAME = "com.meteorshower.autoclock.view.HomeActivity";

    //钉钉
    public static String TARGET_APPNAME = "com.alibaba.android.rimet";
    public static String TARGET_APP_ACTIVITY_NAME = "com.alibaba.android.rimet.biz.SplashActivity";

    //劳动力管理
    public static String TARGET_LAODONGLI_APPNAME = "com.gaiaworkforce.mobile";
    public static String TARGET_LAODONGLI_ACTIVITY_NAME = "com.gaiaworkforce.mobile.MainActivity";

    //APP文件目录
    public static String APP_PATH = "/storage/emulated/0/AutoClock/";
    //异常捕获保存路径
    public static String ERROR = APP_PATH + "ERROR/";
    //log文件保存路径
    public static String LOG = "LOG";
    //TAG
    public static String TAG = "lqwtest";
    //Token校验
    public static String TOKEN = "";
    //IP
    public static String BASE_URL = "http://120.78.179.218:14470";

    public static boolean MAIN_THREAD_RUNNING = true;
    public static final int SOCKET_PORT = 4344;
    public static final String THREAD_IS_START_COMMAND = "THREAD_IS_START_COMMAND";
    public static final String THREAD_IS_READY = "THREAD_IS_READY";
    public static final String MAIN_THREAD_RUNNING_CHANGE = "MAIN_THREAD_RUNNING_CHANGE";
    public static final String MAIN_THREAD_RUNNING_STOP = "MAIN_THREAD_RUNNING_STOP";
    public static final String COMMAND_EXC_SUCCESS = "COMMAND_EXC_SUCCESS";
    public static final String COMMAND_EXC_FAILURE = "COMMAND_EXC_FAILURE";
    public static final String COMMAND_EXC_ERROR = "COMMAND_EXC_ERROR";
    public static final String SOCKET_URL = "127.0.0.1";

    public static final String JOP_TYPE_0 = "0";//所有
    public static final String JOP_TYPE_1 = "1";//上班
    public static final String JOP_TYPE_2 = "2";//下班

    public static final String JOP_STATUS_0 = "0";//所有
    public static final String JOP_STATUS_1 = "1";//未开始
    public static final String JOP_STATUS_2 = "2";//已下发
    public static final String JOP_STATUS_3 = "3";//已成功

}
