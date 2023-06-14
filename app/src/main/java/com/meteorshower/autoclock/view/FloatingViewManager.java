package com.meteorshower.autoclock.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.event.CollectMenuEvent;
import com.meteorshower.autoclock.event.FloatingViewClickEvent;
import com.meteorshower.autoclock.event.ScrollMenuEvent;
import com.meteorshower.autoclock.util.LogUtils;

import org.greenrobot.eventbus.EventBus;


public class FloatingViewManager {

    private FloatingView floatBall;
    private View floatingMenu;
    private View floatingPanel;
    private WindowManager windowManager;
    public static FloatingViewManager manager;
    private Context context;
    private WindowManager.LayoutParams floatBallParams;
    private WindowManager.LayoutParams floatMenuParams;
    private WindowManager.LayoutParams floatPanelParams;

    private FloatingViewManager(Context context) {
        this.context = context;
    }

    public static FloatingViewManager getInstance(Context context) {
        if (manager == null) {
            manager = new FloatingViewManager(context);
        }
        return manager;
    }

    public void showFloatingBall(int floatingViewSize) {
        try {
            floatBall = new FloatingView(context, floatingViewSize, floatingViewSize);
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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
            if (windowManager == null) {
                return;
            }
            floatingMenu = LayoutInflater.from(context).inflate(R.layout.floating_menu_view, null);
            if (floatMenuParams == null) {
                floatMenuParams = new WindowManager.LayoutParams();
                floatMenuParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                floatMenuParams.height = WindowManager.LayoutParams.MATCH_PARENT;
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
                    EventBus.getDefault().post(new ScrollMenuEvent());
                    hideFloatingMenu();
                }
            });
            floatingMenu.findViewById(R.id.rl_collect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toaster.show("请点击屏幕采集坐标");
                    hideFloatingMenu();
                    showFloatingPanel();
                }
            });
            floatingMenu.findViewById(R.id.rl_content).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideFloatingMenu();
                }
            });
        } catch (Exception e) {
        }
    }

    public void hideFloatingMenu() {
        if (windowManager != null && floatingMenu != null) {
            windowManager.removeView(floatingMenu);
            floatingMenu = null;
            floatMenuParams = null;
        }
    }

    public void showFloatingPanel() {
        if (windowManager == null) {
            return;
        }
        floatingPanel = new View(context);
        floatingPanel.setBackgroundColor(context.getResources().getColor(R.color.black));
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
        windowManager.addView(floatingPanel, floatPanelParams);
        floatingPanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        int clickX = (int) event.getX();
                        int clickY = (int) event.getY();
                        EventBus.getDefault().post(new CollectMenuEvent(clickX, clickY));
                        hideFloatingPanel();
                        Toaster.show("已采集屏幕坐标["+clickX+","+clickY+"]");
                        break;
                }
                return true;
            }
        });
    }

    public void hideFloatingPanel() {
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

}
