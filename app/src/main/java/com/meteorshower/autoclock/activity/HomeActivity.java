package com.meteorshower.autoclock.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.hjq.toast.Toaster;
import com.hw.datepickerlibrary.util.CalendarUtil;
import com.hw.datepickerlibrary.view.DatePopupWindow;
import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.entity.ActionMode;
import com.meteorshower.autoclock.greendao.ActionModeDbHelper;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;
import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.util.AnimUtils;
import com.meteorshower.autoclock.util.AutoClickUtil;
import com.meteorshower.autoclock.util.LogUtils;
import com.meteorshower.autoclock.util.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.OnClick;
/**
 * 应用主界面
 * */
public class HomeActivity extends BaseActivity {

    private final static int MOVE_VIEW = 1001;
    private final static int UPDATE_VIEW = 1002;
    private LinearLayout llContent;
    private RelativeLayout llFilter;
    private LinearLayout llListView;
    private ImageView ivArrow;
    private ListView lvContent;
    private int startGroup = -1;//全局量
    private int endGroup = -1;
    private int startChild = -1;
    private int endChild = -1;
    private boolean isRotate = false;
    private boolean isScale = false;
    private float startX = 0, startY = 0;
    private int maxY = 0;
    private int minY = 0;
    private boolean isMove = false;
    private ArrayAdapter arrayAdapter;
    private Button btnSure;
    private VelocityTracker mVelocityTracker = null;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        if (ControllerAccessibilityService.getInstance() == null) {
            Toaster.show("无障碍服务未启动");
            LogUtils.getInstance().e("HomeActivity 无障碍服务未启动 version="+StringUtils.getAppVersion());
        } else {
            LogUtils.getInstance().e("HomeActivity 无障碍服务已启动"+StringUtils.getAppVersion());
            Toaster.show("无障碍服务已启动");
        }
        llContent = findViewById(R.id.ll_content);
        ivArrow = findViewById(R.id.iv_arrow);
        llFilter = findViewById(R.id.rl_filter);
        lvContent = findViewById(R.id.lv_content);
        llListView = findViewById(R.id.ll_view_list);
        btnSure = findViewById(R.id.btn_sure);
        initListViewMove();
    }

    @Override
    protected void initData() {
        try {
            mVelocityTracker = VelocityTracker.obtain();
            ViewConfiguration mViewConfiguration = ViewConfiguration.get(this);
            int mMaxFlingVelocity = mViewConfiguration.getScaledMaximumFlingVelocity();
            int mMinFlingVelocity = mViewConfiguration.getScaledMinimumFlingVelocity();
            Log.d(AppConstant.TAG, "initData mMaxFlingVelocity="+mMaxFlingVelocity+" mMinFlingVelocity="+mMinFlingVelocity);

            List<ActionMode> actionModes = ActionModeDbHelper.getInstance().getAllAction();
            if (actionModes.size() < AppConstant.Action.values().length) {
                ActionModeDbHelper.getInstance().deleteAll();
                actionModes.clear();
                for (AppConstant.Action action : AppConstant.Action.values()) {
                    actionModes.add(new ActionMode(StringUtils.newGUID(), action.actionCode, action.actionName));
                }
                ActionModeDbHelper.getInstance().insertList(actionModes);
            }

            List<String> contentList = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                contentList.add("项目" + i);
            }
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contentList);
            lvContent.setAdapter(arrayAdapter);
        } catch (Exception e) {

        }
    }

    private Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MOVE_VIEW:
                    float distance = llListView.getTranslationY() - (float) msg.obj;
                    Log.d(AppConstant.TAG, "llListView before TranslationY=" + llListView.getTranslationY());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            llListView.setTranslationY(distance);
                            Log.d(AppConstant.TAG, "llListView after TranslationY=" + llListView.getTranslationY());
                        }
                    });

                    Message message = new Message();
                    message.what = MOVE_VIEW;
                    message.obj = 10f;
                    myHandler.sendMessageDelayed(message, 1000);
                    break;
                case UPDATE_VIEW:
                    if (arrayAdapter != null) {
                        arrayAdapter.notifyDataSetChanged();
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.btn_start, R.id.btn_end, R.id.btn_add, R.id.btn_stop_running, R.id.btn_look,
            R.id.btn_test, R.id.btn_reset, R.id.btn_exc_cmd, R.id.btn_setting, R.id.btn_scroll_setting,
            R.id.btn_test_view, R.id.btn_test_view1, R.id.iv_arrow, R.id.btn_content
    })
    public void doClick(View view) {
        Log.d("lqwtest", "doClick id=" + view.getId());
        switch (view.getId()) {
            case R.id.btn_start:
                if (!JobExecutor.getInstance().isRunning()) {
                    Toaster.show("启动任务执行器");
                    JobExecutor.getInstance().setRunning(true);
                    JobExecutor.getInstance().start();
                }
                if (!JobFactory.getInstance().isRunning()) {
                    Toaster.show("启动任务工厂");
                    JobFactory.getInstance().setRunning(true);
                    JobFactory.getInstance().start();
                }
                Toaster.show("设置任务可获取");
                JobFactory.getInstance().setGetJob(true);
                break;
            case R.id.btn_end:
                Toaster.show("设置任务不可获取");
                JobFactory.getInstance().setGetJob(false);
                break;
            case R.id.btn_stop_running:
                JobFactory.getInstance().setRunning(false);
                JobExecutor.getInstance().setRunning(false);
                Toaster.show("停止任务线程");
                break;
            case R.id.btn_look:
                startActivity(new Intent(HomeActivity.this, CheckJobActivity.class));
                break;
            case R.id.btn_add:
                startActivity(new Intent(HomeActivity.this, AddJobActivity.class));
                break;
            case R.id.btn_test:
                startActivity(new Intent(HomeActivity.this, CheckHeartActivity.class));
                break;
            case R.id.btn_reset:
                Toaster.show("任务执行:" + (JobExecutor.getInstance().isRunning()) + " 任务获取:" + (JobFactory.getInstance().isRunning()));
                AutoClickUtil.getInstance().resetParam();
                break;
            case R.id.btn_exc_cmd:
                startActivity(new Intent(HomeActivity.this, CommandExecuteActivity.class));
                break;
            case R.id.btn_setting:
                AccessibilityUtils.goToAccessibilitySetting(HomeActivity.this);
                break;
            case R.id.btn_scroll_setting:
                startActivity(new Intent(HomeActivity.this, ScrollSettingActivity.class));
                break;
            case R.id.btn_test_view:
                llListView.setTop(800);
                showListViewInfo();
                break;
            case R.id.btn_test_view1:
                llListView.setTop(400);
                showListViewInfo();
                break;
            case R.id.iv_arrow:
                if (isRotate) {
                    AnimUtils.rotateAnticlockwise(ivArrow, 1000, null);
                } else {
                    llContent.setVisibility(View.VISIBLE);
                    AnimUtils.rotateClockWise(ivArrow, 1000, null);
                }
                isRotate = !isRotate;
                break;
            case R.id.btn_content:
                Toaster.show("内容点击测试");
                break;
            default:
                break;
        }
    }

    private void showFilter(boolean isShow) {
        if (isShow) {
            final int filterHeight1 = llFilter.getHeight();
            Log.d(AppConstant.TAG, "after filterHeight1=" + filterHeight1);
            PropertyValuesHolder translationY2 = PropertyValuesHolder.ofFloat("translationY", -llContent.getHeight(), 0f);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(llContent, translationY2);
            objectAnimator2.setDuration(500);
            objectAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int distance = Float.valueOf((Float) animation.getAnimatedValue()).intValue();
                    Log.d(AppConstant.TAG, "llFilter translationY distance=" + distance);
                    ViewGroup.LayoutParams params = llFilter.getLayoutParams();
                    params.height = filterHeight1 - Math.abs(distance + llContent.getHeight());
                    llFilter.setLayoutParams(params);
                }
            });
            objectAnimator2.start();

            PropertyValuesHolder translationY3 = PropertyValuesHolder.ofFloat("translationY", -llContent.getHeight(), 0f);
            ObjectAnimator objectAnimator3 = ObjectAnimator.ofPropertyValuesHolder(llFilter, translationY3);
            objectAnimator3.setDuration(500);
            objectAnimator3.start();
        } else {
            final int filterHeight = llFilter.getHeight();
            Log.d(AppConstant.TAG, "before filterHeight=" + filterHeight);
            PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", 0f, -llContent.getHeight());
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(llContent, translationY);
            objectAnimator.setDuration(500);
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int distance = Float.valueOf((Float) animation.getAnimatedValue()).intValue();
                    Log.d(AppConstant.TAG, "llContent translationY distance=" + distance);
                    ViewGroup.LayoutParams params = llFilter.getLayoutParams();
                    params.height = filterHeight + Math.abs(distance);
                    llFilter.setLayoutParams(params);
                }
            });
            objectAnimator.start();

            PropertyValuesHolder translationY1 = PropertyValuesHolder.ofFloat("translationY", 0f, -llContent.getHeight());
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(llFilter, translationY1);
            objectAnimator1.setDuration(500);
            objectAnimator1.start();
        }
    }

    private void showCalendar() {
        new DatePopupWindow
                .Builder(HomeActivity.this, Calendar.getInstance().getTime(), llListView)//初始化
                .setInitSelect(startGroup, startChild, endGroup, endChild)//设置上一次选中的区间状态
                .setInitDay(true)//默认为true，UI内容为共几天、开始、结束；当为false时,UI内容为共几晚、入住、离开
                .setDateOnClickListener(new DatePopupWindow.DateOnClickListener() {//设置监听
                    //点击完成按钮后回调返回方法
                    @Override
                    public void getDate(String startDate, String endDate, int startGroupPosition, int
                            startChildPosition, int endGroupPosition, int endChildPosition) {
                        startGroup = startGroupPosition;//开始月份位置
                        startChild = startChildPosition;//开始对应月份中日的位置
                        endGroup = endGroupPosition;//结束月份位置
                        endChild = endChildPosition;//结束对应月份中日的位置
                        String mStartTime = CalendarUtil.FormatDateYMD(startDate);
                        String mEndTime = CalendarUtil.FormatDateYMD(endDate);
                        Log.d(AppConstant.TAG, "您选择了：" + mStartTime + "到" + mEndTime);
                    }
                }).builder();
    }

    private void initListViewMove() {
        minY = (int) (AppConstant.ScreenHeight * 0.2);
        Log.d(AppConstant.TAG, "minY=" + minY);
        llListView.post(new Runnable() {
            @Override
            public void run() {
                maxY = llListView.getTop();
                Log.d(AppConstant.TAG, "maxY=" + maxY);
                showListViewInfo();
            }
        });
        llListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //2、重置
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isMove = true;
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int yDistance = (int) (event.getY() - startY);
                        int currentY = llListView.getTop();
                        Log.d(AppConstant.TAG, "currentY=" + currentY + " yDistance=" + yDistance + " llListView.getHeight()=" + llListView.getHeight());
                        if (maxY < (yDistance + currentY)) {
                            yDistance = maxY - currentY;
                        } else if (minY > (yDistance + currentY)) {
                            yDistance = minY - currentY;
                        }
                        llListView.setTop(yDistance + currentY);
                        Log.d(AppConstant.TAG, "after llListView.getHeight()=" + llListView.getHeight());

                        ViewGroup.LayoutParams params = llListView.getLayoutParams();
                        params.height = llListView.getHeight();
                        llListView.setLayoutParams(params);
                        break;
                    case MotionEvent.ACTION_UP:
                        mVelocityTracker.computeCurrentVelocity(1000);
                        Log.d(AppConstant.TAG, "ACTION_UP speedY=" + mVelocityTracker.getYVelocity(0));
                        isMove = false;
                        break;
                }
                return isMove;
            }
        });
    }

    private void showListViewInfo() {
        Log.d(AppConstant.TAG, "ScreenHeight=" + AppConstant.ScreenHeight + " ScreenWidth=" + AppConstant.ScreenWidth);
        Log.d(AppConstant.TAG, " width=" + llListView.getWidth() + " height=" + llListView.getHeight() + " llListView left=" + llListView.getLeft() + " right=" + llListView.getRight() + " top=" + llListView.getTop() + " bottom=" + llListView.getBottom());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //用完记得回收
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }
}
