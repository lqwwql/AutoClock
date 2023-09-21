package com.meteorshower.autoclock.view;

import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.event.CollectMenuEvent;
import com.meteorshower.autoclock.event.FloatingViewClickEvent;
import com.meteorshower.autoclock.event.ScrollEndActionEvent;
import com.meteorshower.autoclock.event.ScrollMenuEvent;
import com.meteorshower.autoclock.event.ScrollTypeChangeEvent;
import com.meteorshower.autoclock.util.AutoClickUtil;
import com.meteorshower.autoclock.util.DeviceUtils;
import com.meteorshower.autoclock.util.LogUtils;
import com.meteorshower.autoclock.util.SharedPreferencesUtil;
import com.meteorshower.autoclock.util.StringUtils;
import com.meteorshower.autoclock.util.UIUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class FloatingViewManager {

    private FloatingView floatBall;//悬浮球
    private View floatingMenu;//悬浮菜单
    private View floatingPanel;//坐标采集透明窗
    private WindowManager windowManager;
    public static FloatingViewManager manager;
    private WindowManager.LayoutParams floatBallParams;
    private WindowManager.LayoutParams floatMenuParams;
    private WindowManager.LayoutParams floatPanelParams;
    private int collectMode = 0;//0-坐标采集 1-轨迹采集
    private List<Point> pointList;//轨迹点

    private FloatingViewManager(Context context) {
        this.windowManager = getWindowManager();
    }

    public static FloatingViewManager getInstance(Context context) {
        if (manager == null) {
            manager = new FloatingViewManager(MyApplication.getContext());
        }
        return manager;
    }

    public void showFloatingBall(int floatingViewSize) {
        try {
            floatBall = new FloatingView(MyApplication.getContext(), floatingViewSize, floatingViewSize);
            if (floatBallParams == null) {
                floatBallParams = new WindowManager.LayoutParams();
                floatBallParams.width = floatBall.width;
                floatBallParams.height = floatBall.height;
                floatBallParams.gravity = Gravity.TOP | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    floatBallParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
//                    floatBallParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                    floatBallParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                floatBallParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                floatBallParams.format = PixelFormat.RGBA_8888;
            }

            windowManager.addView(floatBall, floatBallParams);

            floatBall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new FloatingViewClickEvent(1));
                }
            });

            floatBall.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    EventBus.getDefault().post(new FloatingViewClickEvent(2));
                    return false;
                }
            });

            floatBall.setOnTouchListener(new View.OnTouchListener() {
                float startX;
                float startY;
                float tempX;
                float tempY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = event.getRawX();
                            startY = event.getRawY();

                            tempX = event.getRawX();
                            tempY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float dx = event.getRawX() - startX;
                            float dy = event.getRawY() - startY;
                            //计算偏移量，刷新视图
                            floatBallParams.x += dx;
                            floatBallParams.y += dy;
                            windowManager.updateViewLayout(floatBall, floatBallParams);
                            startX = event.getRawX();
                            startY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            //判断松手时View的横坐标是靠近屏幕哪一侧，将View移动到依靠屏幕
                            float endX = event.getRawX();
                            float endY = event.getRawY();
                            if (endX < getScreenWidth() / 2) {
                                endX = 0;
                            } else {
                                endX = getScreenWidth() - floatBall.width;
                            }
                            floatBallParams.x = (int) endX;
                            windowManager.updateViewLayout(floatBall, floatBallParams);
                            //如果初始落点与松手落点的坐标差值超过6个像素，则拦截该点击事件
                            //否则继续传递，将事件交给OnClickListener函数处理
                            if (Math.abs(endX - tempX) > 6 && Math.abs(endY - tempY) > 6) {
                                return true;
                            }
                            break;
                    }
                    return false;
                }

            });
            Toaster.show("开启悬浮窗");
        } catch (Exception e) {
            Toaster.show("开启悬浮窗失败");
            Log.d(AppConstant.TAG, "ViewManager showFloatingBall error : " + Log.getStackTraceString(e));
//            LogUtils.getInstance().e("ViewManager showFloatingBall error : "+ Log.getStackTraceString(e));
        }
    }

    public void hideFloatingBall() {
        try {
            LogUtils.getInstance().e("hideFloatingBall windowManager="+windowManager +" floatBall="+floatBall);
            if(windowManager == null){
                Toaster.show("重新获取窗口管理器");
                LogUtils.getInstance().e("windowManager is null , reload");
                windowManager = getWindowManager();
            }
            if (windowManager != null && floatBall != null) {
                windowManager.removeView(floatBall);
                floatBall = null;
                floatBallParams = null;
                Toaster.show("关闭悬浮窗");
            }
        } catch (Exception e) {
            Toaster.show("关闭悬浮窗失败");
            Log.d(AppConstant.TAG, "ViewManager hideFloatingBall error : " + Log.getStackTraceString(e));
//            LogUtils.getInstance().e("ViewManager hideFloatingBall error : "+ Log.getStackTraceString(e));
        }
    }

    public void showFloatingMenu() {
        try {
            if(windowManager == null){
                Toaster.show("重新获取窗口管理器");
                LogUtils.getInstance().e("windowManager is null , reload");
                windowManager = getWindowManager();
            }
            if (windowManager == null) {
                return;
            }
            floatingMenu = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.floating_menu_view, null);
            TextView tvScroll = floatingMenu.findViewById(R.id.tv_scroll_text);
            RadioGroup scrollType = floatingMenu.findViewById(R.id.rg_scroll_time_select);
            ImageView ivClose = floatingMenu.findViewById(R.id.iv_close);
            ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toaster.show("关闭弹窗");
                    LogUtils.getInstance().e("floatingMenu ivClose click");
                    ((WindowManager) MyApplication.getContext().getSystemService(Service.WINDOW_SERVICE)).removeView(floatingMenu);
                    floatingMenu = null;
                }
            });
            int scrollTimeSelect = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_SELECT, 0, SharedPreferencesUtil.SCROLL_CONFIG);
            int customTimes = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_CUSTOM_TIMES, 0, SharedPreferencesUtil.SCROLL_CONFIG);
            switch (scrollTimeSelect) {
                case 0:
                    scrollType.check(R.id.rb_time_15);
                    break;
                case 1:
                    scrollType.check(R.id.rb_time_30);
                    break;
                case 2:
                    ((RadioButton) floatingMenu.findViewById(R.id.rb_time_custom)).setText(customTimes + "s");
                    scrollType.check(R.id.rb_time_custom);
                    break;
            }
            scrollType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int type = 0;
                    int value = 0;
                    switch (checkedId) {
                        case R.id.rb_time_15:
                            type = 0;
                            break;
                        case R.id.rb_time_30:
                            type = 1;
                            break;
                        case R.id.rb_time_custom:
                            type = 2;
                            break;
                    }
                    EventBus.getDefault().post(new ScrollTypeChangeEvent(type, value));
                }
            });

            RadioGroup scrollEndAction = floatingMenu.findViewById(R.id.rg_scroll_end_select);
            scrollEndAction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    int type = 0;
                    switch (checkedId) {
                        case R.id.rb_end_no:
                            type = 0;
                            break;
                        case R.id.rb_end_back:
                            type = 1;
                            break;
                        case R.id.rb_end_back_click:
                            type = 2;
                            break;
                    }
                    SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_END_ACTION, type, SharedPreferencesUtil.SCROLL_CONFIG);
                    EventBus.getDefault().post(new ScrollEndActionEvent(type));
                }
            });
            int scrollEnd = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_END_ACTION, 0, SharedPreferencesUtil.SCROLL_CONFIG);
            int finishOp = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FINISH_OP_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);
            if (finishOp == 1) {
                scrollEnd = 0;
            } else if (finishOp == 2 && scrollEnd == 0) {
                scrollEnd = 1;
            }
            switch (scrollEnd) {
                case 0:
                    scrollEndAction.check(R.id.rb_end_no);
                    break;
                case 1:
                    scrollEndAction.check(R.id.rb_end_back);
                    break;
                case 2:
                    scrollEndAction.check(R.id.rb_end_back_click);
                    break;
            }
            if (AutoClickUtil.getInstance().isRunning()) {
                tvScroll.setText("关闭滑动");
            } else {
                tvScroll.setText("开启滑动");
            }
            if (floatMenuParams == null) {
                floatMenuParams = new WindowManager.LayoutParams();
                floatMenuParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                floatMenuParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                floatMenuParams.gravity = Gravity.CENTER;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    floatMenuParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    floatMenuParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                floatMenuParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                floatMenuParams.format = PixelFormat.RGBA_8888;
            }
            windowManager.addView(floatingMenu, floatMenuParams);
            floatingMenu.findViewById(R.id.rl_scroll).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toaster.show("开始执行滑动");
                    hideFloatingMenu();
                    EventBus.getDefault().post(new ScrollMenuEvent());
                }
            });
            floatingMenu.findViewById(R.id.rl_collect_point).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toaster.show("请点击屏幕采集坐标");
                    collectMode = 0;
                    hideFloatingMenu();
                    hideFloatingPanel();
                    showFloatingPanel();
                }
            });
            floatingMenu.findViewById(R.id.rl_collect_track).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toaster.show("请滑动屏幕采集轨迹");
                    if (pointList == null) {
                        pointList = new ArrayList<>();
                    } else {
                        pointList.clear();
                    }
                    collectMode = 1;
                    hideFloatingMenu();
                    hideFloatingPanel();
                    showFloatingPanel();
                }
            });
            floatingMenu.findViewById(R.id.rl_content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideFloatingMenu();
                }
            });
            floatingMenu.findViewById(R.id.rl_remove_all).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeAll();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideFloatingMenu() {
        LogUtils.getInstance().e("hideFloatingMenu windowManager="+windowManager +" floatingMenu="+floatingMenu);
        if(windowManager == null){
            Toaster.show("重新获取窗口管理器");
            LogUtils.getInstance().e("windowManager is null , reload");
            windowManager = getWindowManager();
        }
        if (windowManager != null && floatingMenu != null) {
            windowManager.removeView(floatingMenu);
            floatingMenu = null;
            floatMenuParams = null;
        }
    }

    public void showFloatingPanel() {
        if(windowManager == null){
            Toaster.show("重新获取窗口管理器");
            LogUtils.getInstance().e("windowManager is null , reload");
            windowManager = getWindowManager();
        }
        if (windowManager == null) {
            return;
        }
//        floatingPanel = new View(MyApplication.getContext());
        floatingPanel =  LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.floating_panel_view, null);
        TrackView trackView = floatingPanel.findViewById(R.id.custom_track_view);
        if (floatPanelParams == null) {
            floatPanelParams = new WindowManager.LayoutParams();
            floatPanelParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            floatPanelParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            floatPanelParams.gravity = Gravity.CENTER;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                floatPanelParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                floatPanelParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            floatPanelParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            floatPanelParams.format = PixelFormat.RGBA_8888;
        }
        floatingPanel.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Toaster.show("开始执行滑动");
                    EventBus.getDefault().post(new ScrollMenuEvent());
//                    ((WindowManager) MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE)).removeView(floatingPanel);
                }catch (Exception e){
                }
            }
        });
        windowManager.addView(floatingPanel, floatPanelParams);
        floatingPanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int clickX = (int) event.getX();
                int clickY = (int) event.getY();
                clickY += DeviceUtils.getStatusBarHeight();//补充头部刘海屏高度
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (collectMode == 1) {
                            pointList.add(new Point(clickX, clickY));
//                            drawLineView(pointList, floatingPanel);
                            trackView.setTrackPoints(pointList);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (collectMode == 1) {
                            pointList.add(new Point(clickX, clickY));
//                            drawLineView(pointList, floatingPanel);
                            trackView.setTrackPoints(pointList);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (collectMode == 0) {
                            EventBus.getDefault().post(new CollectMenuEvent(collectMode, clickX, clickY));
                            hideFloatingPanel();
                            Toaster.show("已采集屏幕坐标[" + clickX + "," + clickY + "]");
                        } else {
                            trackView.setTrackPoints(pointList);
//                            drawLineView(pointList, floatingPanel);
                            EventBus.getDefault().post(new CollectMenuEvent(collectMode, pointList));
                            Log.d(AppConstant.TAG, "onTouch ACTION_UP pointList=" + pointList.size());
                            hideFloatingPanel();
                            Toaster.show("已采集屏幕"+pointList.size()+"个轨迹点");
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void drawLineView(List<Point> lines, View view) {
        //画线
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(UIUtils.px2dp(2));

        Canvas canvas = new Canvas();
        float[] linePoint = new float[lines.size() * 2];
        for (int i = 0; i < lines.size(); i++) {
            linePoint[i * 2] = lines.get(i).x;
            linePoint[i * 2 + 1] = lines.get(i).y;
        }
        Log.d(AppConstant.TAG, "linePoint = " + new Gson().toJson(linePoint));
        canvas.drawLines(linePoint, paint);
        view.draw(canvas);
        view.invalidate();
    }

    public void hideFloatingPanel() {
        LogUtils.getInstance().e("hideFloatingPanel windowManager="+windowManager +" floatingPanel="+floatingPanel);
        if(windowManager == null){
            Toaster.show("重新获取窗口管理器");
            LogUtils.getInstance().e("windowManager is null , reload");
            windowManager = getWindowManager();
        }
        if (windowManager != null && floatingPanel != null) {
            windowManager.removeView(floatingPanel);
            floatingPanel = null;
            floatPanelParams = null;
        }
    }


    public void changeFloatingViewState(boolean isRunning) {
        if (floatBall != null) {
            floatBall.changeState(isRunning);
        }
    }

    public void changeFloatingViewSize(int size) {
        if (floatBall != null) {
            floatBall.changeSize(size);
        }
    }

    public int getScreenWidth() {
        return windowManager.getDefaultDisplay().getWidth();
    }

    public WindowManager getWindowManager(){
        return (WindowManager) MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public void removeAll(){
        if(floatBall!=null){
            getWindowManager().removeView(floatBall);
            floatBall = null;
        }
        if(floatingMenu!=null){
            getWindowManager().removeView(floatingMenu);
            floatingMenu = null;
        }
        if(floatingPanel!=null){
            getWindowManager().removeView(floatingPanel);
            floatingPanel = null;
        }
    }

}
