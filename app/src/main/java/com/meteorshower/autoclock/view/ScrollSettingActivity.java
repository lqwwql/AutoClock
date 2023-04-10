package com.meteorshower.autoclock.view;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;
import com.meteorshower.autoclock.util.SharedPreferencesUtil;
import com.meteorshower.autoclock.util.StringUtils;

import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class ScrollSettingActivity extends BaseActivity {

    @BindView(R.id.et_scroll_time)
    EditText etTimes;
    @BindView(R.id.et_scroll_duration)
    EditText etDurations;
    @BindView(R.id.btn_operation)
    Button btnOperation;


    private int scrollTimes = 0;
    private int scrollDuration = 0;
    private static final int SCROLL_WHAT = 1001;
    private boolean isRunning = false;
    private boolean isStop = false;

    private Handler scrollHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCROLL_WHAT:
                    executeScrollView();
                    break;
            }
        }
    };

    @Override
    protected int getLayoutID() {
        return R.layout.activity_scroll_setting;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        scrollTimes = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_KEY, 100*1000, SharedPreferencesUtil.SCROLL_CONFIG);
        scrollDuration = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DURATION_KEY, 10, SharedPreferencesUtil.SCROLL_CONFIG);
        etTimes.setText(scrollTimes + "");
        etDurations.setText(scrollDuration + "");
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStop = false;
        if (isRunning) {
            btnOperation.setText("关闭");
        } else {
            btnOperation.setText("启动");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStop = true;
    }

    @OnClick({R.id.btn_operation, R.id.tv_back})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_operation:
                runScroll();
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
        }
    }


    private void runScroll() {
        if (!StringUtils.isEmptyOrNull(etTimes.getText().toString())) {
            scrollTimes = Integer.parseInt(etTimes.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etDurations.getText().toString())) {
            scrollDuration = Integer.parseInt(etDurations.getText().toString());
        }
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_KEY, scrollTimes, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DURATION_KEY, scrollDuration, SharedPreferencesUtil.SCROLL_CONFIG);

        if (ControllerAccessibilityService.getInstance() == null) {
            Toaster.show("无障碍服务未启动");
            return;
        }

        if (isRunning) {
            btnOperation.setText("启动");
            Toaster.show("滑动程序已关闭");
            scrollHandler.removeMessages(SCROLL_WHAT);
        } else {
            btnOperation.setText("关闭");
            Toaster.show("滑动程序将在" + scrollDuration + "秒后启动");
            sendDelayMessage();
        }
        isRunning = !isRunning;
    }

    private void sendDelayMessage() {
        scrollHandler.sendEmptyMessageDelayed(SCROLL_WHAT, scrollDuration * 1000);
    }

    private void executeScrollView() {
        if (!isRunning) {
            return;
        }
        if (scrollTimes == 0) {
            if (!isStop) {
                btnOperation.setText("启动");
            }
            Toaster.show("滑动程序次数已执行完毕");
            scrollHandler.removeMessages(SCROLL_WHAT);
            isRunning = !isRunning;
            return;
        }
        scrollTimes--;
        Toaster.show("执行滑动,间隔:" + scrollDuration + ",剩余次数:" + scrollTimes);
        int randleValueX = (new Random().nextInt(5) + 1) * 10;
        int randleValueY = (new Random().nextInt(5) + 1) * 10;
        int startX = 260, startY = 820, endX = 260, endY = 260;
        if (AppConstant.ScreenHeight > 0 && AppConstant.ScreenWidth > 0) {
            Log.d(AppConstant.TAG,"AppConstant.ScreenHeight="+AppConstant.ScreenHeight+" AppConstant.ScreenWidth="+AppConstant.ScreenWidth);
            startX = AppConstant.ScreenWidth / 2;
            startY = AppConstant.ScreenHeight * 4 / 5;
            endX = AppConstant.ScreenWidth / 2;
            endY = AppConstant.ScreenHeight / 5;
        }
        Log.d(AppConstant.TAG,"startX="+startX+" startY="+startY+" endX="+endX+" endY="+endY);
        ControllerAccessibilityService.getInstance().execScrollGesture(startX + randleValueX, startY + randleValueY, endX + randleValueX, endY + randleValueY, 100, 500);
        sendDelayMessage();
    }

}