package com.meteorshower.autoclock.constant;

public class AppConstant {

    public static int ScreenWidth = 0;
    public static int ScreenHeight = 0;

    public static int FloatingViewSize = 100;

    //自身
    public static String LOCAL_APPNAME = "com.meteorshower.autoclock";
    public static String LOCAL_ACTIVITY_NAME = "com.meteorshower.autoclock.activity.HomeActivity";


    //APP文件目录
    public static String APP_PATH = "辅助程序";
    public static final String DIR_LOG = "Log";
    //异常捕获保存路径
    public static String ERROR = APP_PATH + "ERROR/";
    //数据库路径
    public static final String DB_DIR = "DB";
    public static final String DB_NAME = "ac.db";

    /**
     * 系统用到的文件目录
     */
    public static final String[] DIRECTORYS = new String[]{DIR_LOG,DB_DIR};

    //TAG
    public static String TAG = "lqwtest";
    //Token校验
    public static String TOKEN = "";
    //IP
    public static String BASE_URL = "http://1.12.59.45:8870";

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

    public static final int GET_JOP_SLEEP_TIME = 60 * 1000;
    public static final int GETED_JOP_SLEEP_TIME = 5 * 60 * 1000;
    public static final int EXC_JOP_SLEEP_TIME = 10 * 1000;


    public static final int[] DESKTOP_DD_8 = {1, 3};
    public static final int[] DESKTOP_AU_8 = {1, 3};
    public static final int[] DD_NEWS_8 = {1, 3};
    public static final int[] DD_WORK_8 = {1, 3};
    public static final int[] DD_KAO_8 = {1, 3};
    public static final int[] DD_UP_8 = {1, 3};
    public static final int[] DD_DOWN_8 = {1, 3};

    public static final int[] DESKTOP_DD_LT = {78, 952};
    public static final int[] DESKTOP_AU_LT = {636, 966};
    public static final int[] DD_NEWS_LT = {70, 1234};
    public static final int[] DD_WORK_LT = {355, 1234};
    //    public static final int[] DD_KAO_LT = {88,840};
    public static final int[] DD_KAO_LT = {92, 722};
    //    public static final int[] DD_UP_LT = {357,375};
    public static final int[] DD_UP_LT = {370, 725};
    //    public static final int[] DD_DOWN_LT = {368,656};
    public static final int[] DD_DOWN_LT = {370, 725};

    public static final int[] ZFB_QJ = {550, 745};

    public static final String ALARM_RECEIVER_ACTION = "com.meteorshower.autoclock.receiver.AlarmReceiver.ALARM_RECEIVER_ACTION";

    public enum Action {
        ACTION_SINGLE_CLICK(1011,"单击"),
        ACTION_DOUBLE_CLICK(1012,"双击"),
        ACTION_LONG_CLICK(1013,"长按"),
        ACTION_INPUT(1014,"输入"),
        ACTION_SLIDE_UP(1015,"上滑"),
        ACTION_SLIDE_DOWN(1016,"下滑"),
        ACTION_SLIDE_LEFT(1017,"左滑"),
        ACTION_SLIDE_RIGHT(1018,"右滑"),
        ACTION_SLIDE_UP_DOWN(1019,"上下");

        public int actionCode;
        public String actionName;

        Action(int actionCode,String actionName){
            this.actionCode = actionCode;
            this.actionName = actionName;
        }
    }

}
