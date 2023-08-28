package com.meteorshower.autoclock.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.meteorshower.autoclock.util.AutoClickUtil;
import com.meteorshower.autoclock.util.StringUtils;

import java.util.Calendar;
import java.util.List;

import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    private LinearLayout llCalendar;
    private int startGroup = -1;//全局量
    private int endGroup = -1;
    private int startChild = -1;
    private int endChild = -1;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        if (ControllerAccessibilityService.getInstance() == null) {
            Toaster.show("无障碍服务未启动");
        } else {
            Toaster.show("无障碍服务已启动");
        }
        llCalendar = findViewById(R.id.ll_calendar);
    }

    @Override
    protected void initData() {
        try {
            List<ActionMode> actionModes = ActionModeDbHelper.getInstance().getAllAction();
            if (actionModes.size() < AppConstant.Action.values().length) {
                Log.d(AppConstant.TAG, "HomeActivity initData actionModes is less actionValues ");
                ActionModeDbHelper.getInstance().deleteAll();
                actionModes.clear();
                for (AppConstant.Action action : AppConstant.Action.values()) {
                    actionModes.add(new ActionMode(StringUtils.newGUID(), action.actionCode, action.actionName));
                }
                ActionModeDbHelper.getInstance().insertList(actionModes);
            }
        } catch (Exception e) {

        }
    }

    @OnClick({R.id.btn_start, R.id.btn_end, R.id.btn_add, R.id.btn_stop_running, R.id.btn_look,
            R.id.btn_test, R.id.btn_reset, R.id.btn_exc_cmd, R.id.btn_setting, R.id.btn_scroll_setting,
            R.id.btn_test_view
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
                showCalendar();
                break;
            default:
                break;
        }
    }

    private void showCalendar(){
        new DatePopupWindow
                .Builder(HomeActivity.this, Calendar.getInstance().getTime(), llCalendar)//初始化
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
                        Log.d(AppConstant.TAG,"您选择了：" + mStartTime + "到" + mEndTime);
                    }
                }).builder();
    }

}
