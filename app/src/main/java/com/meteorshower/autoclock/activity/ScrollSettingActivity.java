package com.meteorshower.autoclock.activity;

import android.os.Build;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.event.FloatingViewClickEvent;
import com.meteorshower.autoclock.event.ScrollFinishEvent;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;
import com.meteorshower.autoclock.util.SharedPreferencesUtil;
import com.meteorshower.autoclock.util.StringUtils;
import com.meteorshower.autoclock.view.FloatingViewManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

public class ScrollSettingActivity extends BaseActivity {

    @BindView(R.id.et_scroll_time)
    EditText etTimes;
    @BindView(R.id.et_scroll_duration)
    EditText etDurations;
    @BindView(R.id.et_slide_duration)
    EditText etSlideDurations;
    @BindView(R.id.rg_random_time)
    RadioGroup rgRandomTime;
    @BindView(R.id.rg_direction)
    RadioGroup rgDirection;
    @BindView(R.id.rg_finish_op)
    RadioGroup rgFinishOp;
    @BindView(R.id.rg_timer_type)
    RadioGroup rgTimerType;
    @BindView(R.id.rg_scroll_distance)
    RadioGroup rgScrollDistance;
    @BindView(R.id.sw_floating_view)
    Switch swFloatingView;
    @BindView(R.id.btn_operation)
    Button btnOperation;

    private int scrollTimes = 0;
    private int scrollDuration = 0;
    private int slideDuration = 0;
    private static final int SCROLL_WHAT = 1001;
    private boolean isRunning = false;
    private boolean isStop = false;
    private boolean isRandomTime = false;
    private int direction = 1;//1-上滑，2-下滑，3-左滑，4-右滑
    private int finishOp = 1;
    private int timerType = 1;
    private int range = 1;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_scroll_setting;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        scrollTimes = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_KEY, 100 * 1000, SharedPreferencesUtil.SCROLL_CONFIG);
        scrollDuration = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DURATION_KEY, 10, SharedPreferencesUtil.SCROLL_CONFIG);
        slideDuration = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SLIDE_DURATION_KEY, 500, SharedPreferencesUtil.SCROLL_CONFIG);
        isRandomTime = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_RANDOM_TIME_KEY, false, SharedPreferencesUtil.SCROLL_CONFIG);
        direction = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DIRECTION_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);
        finishOp = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FINISH_OP_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);
        timerType = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.TIMER_TYPE_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);
        range = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_RANGE_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);

        etTimes.setText(scrollTimes + "");
        etDurations.setText(scrollDuration + "");
        etSlideDurations.setText(slideDuration + "");
        rgRandomTime.check(isRandomTime ? R.id.rb_yes : R.id.rb_no);
        rgRandomTime.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isRandomTime = checkedId == R.id.rb_yes;
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_RANDOM_TIME_KEY, isRandomTime, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });
        switch (direction) {
            case 1:
                rgDirection.check(R.id.rb_up);
                break;
            case 2:
                rgDirection.check(R.id.rb_down);
                break;
            case 3:
                rgDirection.check(R.id.rb_left);
                break;
            case 4:
                rgDirection.check(R.id.rb_right);
                break;
        }
        rgDirection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_up:
                        direction = 1;
                        break;
                    case R.id.rb_down:
                        direction = 2;
                        break;
                    case R.id.rb_left:
                        direction = 3;
                        break;
                    case R.id.rb_right:
                        direction = 4;
                        break;
                }
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DIRECTION_KEY, direction, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });

        switch (finishOp) {
            case 1:
                rgFinishOp.check(R.id.rb_nothing);
                break;
            case 2:
                rgFinishOp.check(R.id.rb_goback);
                break;
            case 3:
                rgFinishOp.check(R.id.rb_our_app);
                break;
            case 4:
                rgFinishOp.check(R.id.rb_home_page);
                break;
        }
        rgFinishOp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_nothing:
                        finishOp = 1;
                        break;
                    case R.id.rb_goback:
                        finishOp = 2;
                        break;
                    case R.id.rb_our_app:
                        finishOp = 3;
                        break;
                    case R.id.rb_home_page:
                        finishOp = 4;
                        break;
                }
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FINISH_OP_KEY, finishOp, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });

        switch (timerType) {
            case 1:
                rgTimerType.check(R.id.rb_handler);
                break;
            case 2:
                rgTimerType.check(R.id.rb_alarm);
                break;
        }
        rgTimerType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_handler:
                        timerType = 1;
                        break;
                    case R.id.rb_alarm:
                        timerType = 2;
                        break;
                }
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.TIMER_TYPE_KEY, timerType, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });

        switch (range) {
            case 1:
                rgScrollDistance.check(R.id.rb_big);
                break;
            case 2:
                rgScrollDistance.check(R.id.rb_small);
                break;
        }
        rgScrollDistance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_big:
                        range = 1;
                        break;
                    case R.id.rb_small:
                        range = 2;
                        break;
                }
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_RANGE_KEY, range, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });

        swFloatingView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    swFloatingView.setText("开");
                    FloatingViewManager.getInstance(ScrollSettingActivity.this).showFloatingBall();
                } else {
                    FloatingViewManager.getInstance(ScrollSettingActivity.this).hideFloatingBall();
                    swFloatingView.setText("关");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStop = false;
        if (ControllerAccessibilityService.getInstance() != null) {
            isRunning = ControllerAccessibilityService.getInstance().isRunning();
        }
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

    @OnClick({R.id.btn_operation, R.id.tv_back,R.id.btn_save})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_operation:
                runScroll();
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
                case R.id.btn_save:
                saveValue();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatingViewManager.getInstance(ScrollSettingActivity.this).hideFloatingBall();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void doFloatingViewClickEvent(FloatingViewClickEvent event) {
        if (event != null && event.getType() == 1) {
            runScroll();
        } else if (event != null && event.getType() == 2) {

        }
    }

    @Subscribe
    public void doScrollFinishEvent(ScrollFinishEvent event) {
        if (event != null) {
            isRunning = !isRunning;
            FloatingViewManager.getInstance(ScrollSettingActivity.this).changeFloatingViewState(isRunning);
        }
    }


    private void runScroll() {
        if (!StringUtils.isEmptyOrNull(etTimes.getText().toString())) {
            scrollTimes = Integer.parseInt(etTimes.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etDurations.getText().toString())) {
            scrollDuration = Integer.parseInt(etDurations.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etSlideDurations.getText().toString())) {
            slideDuration = Integer.parseInt(etSlideDurations.getText().toString());
        }
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_KEY, scrollTimes, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DURATION_KEY, scrollDuration, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SLIDE_DURATION_KEY, slideDuration, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_RANDOM_TIME_KEY, isRandomTime, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DIRECTION_KEY, direction, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FINISH_OP_KEY, finishOp, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.TIMER_TYPE_KEY, timerType, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_RANGE_KEY, range, SharedPreferencesUtil.SCROLL_CONFIG);

        if (ControllerAccessibilityService.getInstance() == null) {
            Toaster.show("无障碍服务未启动");
            return;
        }

        if (isRunning) {
            btnOperation.setText("启动");
            Toaster.show("滑动程序已关闭");
            ControllerAccessibilityService.getInstance().stopRunning();
        } else {
            btnOperation.setText("关闭");
            Toaster.show("滑动程序将在" + scrollDuration + "秒后启动");
            ControllerAccessibilityService.getInstance().setScrollParam(scrollTimes, scrollDuration, slideDuration, direction, finishOp, timerType, range, true, isRandomTime);
//            ControllerAccessibilityService.getInstance().sendDelayMessage();
            ControllerAccessibilityService.getInstance().startRunning();
        }
        isRunning = !isRunning;
        FloatingViewManager.getInstance(ScrollSettingActivity.this).changeFloatingViewState(isRunning);
    }

    private void saveValue(){
        if (!StringUtils.isEmptyOrNull(etTimes.getText().toString())) {
            scrollTimes = Integer.parseInt(etTimes.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etDurations.getText().toString())) {
            scrollDuration = Integer.parseInt(etDurations.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etSlideDurations.getText().toString())) {
            slideDuration = Integer.parseInt(etSlideDurations.getText().toString());
        }
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_KEY, scrollTimes, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DURATION_KEY, scrollDuration, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SLIDE_DURATION_KEY, slideDuration, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_RANDOM_TIME_KEY, isRandomTime, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DIRECTION_KEY, direction, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FINISH_OP_KEY, finishOp, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.TIMER_TYPE_KEY, timerType, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_RANGE_KEY, range, SharedPreferencesUtil.SCROLL_CONFIG);

        Toaster.show("保存成功");
    }

}