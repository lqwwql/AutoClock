package com.meteorshower.autoclock.view;

import android.content.Intent;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.Job.TestClickJob;
import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;
import com.meteorshower.autoclock.util.AccessibilityUtils;

import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        if (ControllerAccessibilityService.getInstance() == null) {
            Toaster.show("无障碍服务未启动");
            return;
        }
    }

    @Override
    protected void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.heightPixels > dm.widthPixels) {
            AppConstant.ScreenHeight = dm.heightPixels;
            AppConstant.ScreenWidth = dm.widthPixels;
        } else {
            AppConstant.ScreenHeight = dm.widthPixels;
            AppConstant.ScreenWidth = dm.heightPixels;
        }
    }

    @OnClick({R.id.btn_start, R.id.btn_end, R.id.btn_add, R.id.btn_check, R.id.btn_look,
            R.id.btn_test, R.id.btn_reset, R.id.btn_exc_cmd, R.id.btn_setting, R.id.btn_scroll_setting})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (!JobExecutor.getInstance().isRunning()) {
                    JobExecutor.getInstance().setRunning(true);
                    JobExecutor.getInstance().start();
                }
                if (!JobFactory.getInstance().isRunning()) {
                    JobFactory.getInstance().setRunning(true);
                    JobFactory.getInstance().start();
                }
                JobFactory.getInstance().setGetJob(true);
                break;
            case R.id.btn_end:
                JobFactory.getInstance().setGetJob(false);
                break;
            case R.id.btn_check:
                JobFactory.getInstance().setRunning(false);
                JobExecutor.getInstance().setRunning(false);
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new TestClickJob(null).doJob();
                    }
                }).start();
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
            default:
                break;
        }
    }
}
