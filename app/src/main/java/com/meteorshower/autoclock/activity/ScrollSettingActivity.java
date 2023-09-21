package com.meteorshower.autoclock.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.application.MyApplication;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.event.CollectMenuEvent;
import com.meteorshower.autoclock.event.FloatingViewClickEvent;
import com.meteorshower.autoclock.event.ScrollEndActionEvent;
import com.meteorshower.autoclock.event.ScrollFinishEvent;
import com.meteorshower.autoclock.event.ScrollMenuEvent;
import com.meteorshower.autoclock.event.ScrollTypeChangeEvent;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;
import com.meteorshower.autoclock.util.AutoClickUtil;
import com.meteorshower.autoclock.util.NotchScreenUtils;
import com.meteorshower.autoclock.util.SharedPreferencesUtil;
import com.meteorshower.autoclock.util.StringUtils;
import com.meteorshower.autoclock.view.FloatingViewManager;
import com.meteorshower.autoclock.view.FlowRadioGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;
import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME;

/**
 * 滑动相关设置类
 */
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
    FlowRadioGroup rgDirection;
    @BindView(R.id.rg_finish_op)
    RadioGroup rgFinishOp;
    @BindView(R.id.rg_timer_type)
    RadioGroup rgTimerType;
    @BindView(R.id.rg_scroll_distance)
    RadioGroup rgScrollDistance;
    @BindView(R.id.sw_floating_view)
    Switch swFloatingView;
    @BindView(R.id.ll_floating_fun)
    LinearLayout llFloatingFun;
    @BindView(R.id.rg_floating_fun)
    RadioGroup rgFloatingFun;
    @BindView(R.id.rg_jump)
    RadioGroup rgJump;
    @BindView(R.id.rg_return_continue)
    RadioGroup rgContinue;
    @BindView(R.id.ll_floating_size)
    LinearLayout llFloatingSize;
    @BindView(R.id.et_floating_size)
    EditText etFloatingSize;
    @BindView(R.id.et_continue_times)
    EditText etContinueTimes;
    @BindView(R.id.et_continue_x)
    EditText etContinueX;
    @BindView(R.id.et_continue_y)
    EditText etContinueY;
    @BindView(R.id.rg_scroll_type)
    RadioGroup rgScrollType;
    @BindView(R.id.rg_scroll_time_select)
    RadioGroup rgScrollTimeSelect;
    @BindView(R.id.ll_scroll_simple)
    LinearLayout llScrollSimple;
    @BindView(R.id.ll_scroll_complex)
    LinearLayout llScrollComplex;
    @BindView(R.id.ll_check_jump)
    LinearLayout llCheckJump;
    @BindView(R.id.ll_timer_type)
    LinearLayout llTimerType;
    @BindView(R.id.ll_scroll_distance)
    LinearLayout llScrollDistance;
    @BindView(R.id.ll_random_time)
    LinearLayout llRandomTime;
    @BindView(R.id.rl_custom)
    LinearLayout rlCustom;
    @BindView(R.id.et_scroll_times)
    EditText etCustomTimes;
    @BindView(R.id.btn_operation)
    Button btnOperation;

    private int scrollTimes = 0;
    private int scrollDuration = 0;
    private int slideDuration = 0;
    private static final int SCROLL_WHAT = 1001;
    private boolean isRunning = false;
    private boolean isStop = false;
    private boolean isRandomTime = false;
    private boolean isCheckJump = false;
    private boolean isContinue = false;
    private int direction = 1;//1-上滑，2-下滑，3-左滑，4-右滑
    private int finishOp = 1;
    private int timerType = 1;
    private int range = 1;
    private int floatingViewFun = 2;//1-系统导航 点击返回上一页，长按返回桌面 2-滑动开关 3-功能键 4-打开菜单
    private int floatingViewSize = AppConstant.FloatingViewSize;//悬浮窗按钮大小
    private int clickX = 0;
    private int clickY = 0;
    private int clickTimes = 0;
    private int scrollType = 0;//滑动时间设置 0-简单 1-详细
    private int scrollTimeSelect = 0;//简单时间选择 0-15秒 1-30秒 2-自定义时间
    private int customTimes = 0;//自定义滑动时间
    private List<Point> trackList = new ArrayList<>();

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
        scrollTimes = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_KEY, 300, SharedPreferencesUtil.SCROLL_CONFIG);
        scrollDuration = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DURATION_KEY, 10, SharedPreferencesUtil.SCROLL_CONFIG);
        slideDuration = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SLIDE_DURATION_KEY, 200, SharedPreferencesUtil.SCROLL_CONFIG);
        isRandomTime = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_RANDOM_TIME_KEY, false, SharedPreferencesUtil.SCROLL_CONFIG);
        direction = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DIRECTION_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);
        finishOp = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FINISH_OP_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);
        timerType = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.TIMER_TYPE_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);
        range = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_RANGE_KEY, 1, SharedPreferencesUtil.SCROLL_CONFIG);
        floatingViewFun = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FLOATING_VIEW_FUNCTION_KEY, 2, SharedPreferencesUtil.SCROLL_CONFIG);
        floatingViewSize = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FLOATING_VIEW_SIZE_KEY, AppConstant.FloatingViewSize, SharedPreferencesUtil.SCROLL_CONFIG);
        isCheckJump = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CHECK_JUMP_KEY, false, SharedPreferencesUtil.SCROLL_CONFIG);
        isContinue = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_CONTINUE_KEY, false, SharedPreferencesUtil.SCROLL_CONFIG);
        clickX = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CLICK_X_KEY, 0, SharedPreferencesUtil.SCROLL_CONFIG);
        clickY = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CLICK_Y_KEY, 0, SharedPreferencesUtil.SCROLL_CONFIG);
        clickTimes = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CONTINUE_TIMES_KEY, 0, SharedPreferencesUtil.SCROLL_CONFIG);
        scrollType = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TYPE, 0, SharedPreferencesUtil.SCROLL_CONFIG);
        scrollTimeSelect = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_SELECT, 0, SharedPreferencesUtil.SCROLL_CONFIG);
        customTimes = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_CUSTOM_TIMES, 0, SharedPreferencesUtil.SCROLL_CONFIG);
        String traceStr = SharedPreferencesUtil.getDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TRACE, "", SharedPreferencesUtil.SCROLL_CONFIG);
        if (!StringUtils.isEmptyOrNull(traceStr)) {
            List<Point> traceData = new Gson().fromJson(traceStr, new TypeToken<List<Point>>() {
            }.getType());
            if (traceData != null) {
                trackList.addAll(traceData);
            }
        }

        etTimes.setText(scrollTimes + "");
        etDurations.setText(scrollDuration + "");
        etSlideDurations.setText(slideDuration + "");
        etFloatingSize.setText(floatingViewSize + "");
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
            case 5:
                rgDirection.check(R.id.rb_up_down);
                break;
            case 7:
                rgDirection.check(R.id.rb_trace);
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
                    case R.id.rb_up_down:
                        direction = 5;
                        break;
                    case R.id.rb_trace:
                        direction = 7;
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

        switch (floatingViewFun) {
            case 1:
                rgFloatingFun.check(R.id.rb_navi);
                break;
            case 2:
                rgFloatingFun.check(R.id.rb_scroll);
                break;
            case 3:
                rgFloatingFun.check(R.id.rb_fun);
                break;
            case 4:
                rgFloatingFun.check(R.id.rb_menu);
                break;
        }
        rgFloatingFun.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_navi:
                        floatingViewFun = 1;
                        break;
                    case R.id.rb_scroll:
                        floatingViewFun = 2;
                        break;
                    case R.id.rb_fun:
                        floatingViewFun = 3;
                        break;
                    case R.id.rb_menu:
                        floatingViewFun = 4;
                        break;

                }
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FLOATING_VIEW_FUNCTION_KEY, floatingViewFun, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });

        swFloatingView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    swFloatingView.setText("开");
                    FloatingViewManager.getInstance(ScrollSettingActivity.this).showFloatingBall(floatingViewSize);
                    llFloatingFun.setVisibility(View.VISIBLE);
                    llFloatingSize.setVisibility(View.VISIBLE);
                } else {
                    FloatingViewManager.getInstance(ScrollSettingActivity.this).hideFloatingBall();
                    swFloatingView.setText("关");
                    llFloatingFun.setVisibility(View.GONE);
                    llFloatingSize.setVisibility(View.GONE);
                }
            }
        });

        rgJump.check(isCheckJump ? R.id.rb_jump_yes : R.id.rb_jump_no);
        rgJump.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_jump_yes:
                        isCheckJump = true;
                        break;
                    case R.id.rb_jump_no:
                        isCheckJump = false;
                        break;
                }
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CHECK_JUMP_KEY, isCheckJump, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });


        etContinueTimes.setText(clickTimes + "");
        etContinueX.setText(clickX + "");
        etContinueY.setText(clickY + "");
        rgContinue.check(isContinue ? R.id.rb_continue_yes : R.id.rb_continue_no);
        rgContinue.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_continue_yes:
                        isContinue = true;
                        break;
                    case R.id.rb_continue_no:
                        isContinue = false;
                        break;
                }
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_CONTINUE_KEY, isContinue, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });

        switch (scrollType) {
            case 0:
                rgScrollType.check(R.id.rb_simple);
                llScrollSimple.setVisibility(View.VISIBLE);
                llScrollComplex.setVisibility(View.GONE);
                llScrollDistance.setVisibility(View.GONE);
                llTimerType.setVisibility(View.GONE);
                llRandomTime.setVisibility(View.GONE);
                llCheckJump.setVisibility(View.GONE);
                setSelectTime(scrollTimeSelect, 0);
                break;
            case 1:
                rgScrollType.check(R.id.rb_complex);
                llScrollSimple.setVisibility(View.GONE);
                llScrollComplex.setVisibility(View.VISIBLE);
                llScrollDistance.setVisibility(View.VISIBLE);
                llTimerType.setVisibility(View.VISIBLE);
                llRandomTime.setVisibility(View.VISIBLE);
                llCheckJump.setVisibility(View.VISIBLE);
                break;
        }
        rgScrollType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_simple:
                        llScrollSimple.setVisibility(View.VISIBLE);
                        llScrollComplex.setVisibility(View.GONE);
                        llScrollDistance.setVisibility(View.GONE);
                        llTimerType.setVisibility(View.GONE);
                        llRandomTime.setVisibility(View.GONE);
                        llCheckJump.setVisibility(View.GONE);
                        scrollType = 0;
                        setSelectTime(scrollTimeSelect, 0);
                        break;
                    case R.id.rb_complex:
                        scrollType = 1;
                        llScrollSimple.setVisibility(View.GONE);
                        rlCustom.setVisibility(View.GONE);
                        llScrollComplex.setVisibility(View.VISIBLE);
                        llScrollDistance.setVisibility(View.VISIBLE);
                        llTimerType.setVisibility(View.VISIBLE);
                        llRandomTime.setVisibility(View.VISIBLE);
                        llCheckJump.setVisibility(View.VISIBLE);
                        break;
                }
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TYPE, scrollType, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });

        switch (scrollTimeSelect) {
            case 0:
                rgScrollTimeSelect.check(R.id.rb_time_15);
                rlCustom.setVisibility(View.GONE);
                break;
            case 1:
                rgScrollTimeSelect.check(R.id.rb_time_30);
                rlCustom.setVisibility(View.GONE);
                break;
            case 2:
                rgScrollTimeSelect.check(R.id.rb_time_custom);
                rlCustom.setVisibility(View.VISIBLE);
                break;
        }
        etCustomTimes.setText(customTimes + "");
        rgScrollTimeSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_time_15:
                        scrollTimeSelect = 0;
                        rlCustom.setVisibility(View.GONE);
                        break;
                    case R.id.rb_time_30:
                        scrollTimeSelect = 1;
                        rlCustom.setVisibility(View.GONE);
                        break;
                    case R.id.rb_time_custom:
                        scrollTimeSelect = 2;
                        Toaster.show("请填写时间并保存");
                        break;
                }
                setSelectTime(scrollTimeSelect, 0);
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_SELECT, scrollTimeSelect, SharedPreferencesUtil.SCROLL_CONFIG);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStop = false;
        if (ControllerAccessibilityService.getInstance() != null) {
            isRunning = AutoClickUtil.getInstance().isRunning();
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

    @OnClick({R.id.btn_operation, R.id.tv_back, R.id.btn_save, R.id.btn_reset})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_operation:
                runScroll();
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.btn_save:
                saveValue(true);
                break;
            case R.id.btn_reset:
                resetValue();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatingViewManager.getInstance(ScrollSettingActivity.this).removeAll();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void doFloatingViewClickEvent(FloatingViewClickEvent event) {
        if (event != null && event.getType() == 1) {
            if (floatingViewFun == 1) {
                AutoClickUtil.getInstance().performGlobalAction(GLOBAL_ACTION_BACK);
            } else if (floatingViewFun == 2) {
                runScroll();
            } else if (floatingViewFun == 3) {
                AutoClickUtil.getInstance().showNodes();
            } else if (floatingViewFun == 4) {
                FloatingViewManager.getInstance(ScrollSettingActivity.this).hideFloatingMenu();
                FloatingViewManager.getInstance(ScrollSettingActivity.this).showFloatingMenu();
            }
        } else if (event != null && event.getType() == 2) {
            if (floatingViewFun == 1) {
                AutoClickUtil.getInstance().performGlobalAction(GLOBAL_ACTION_HOME);
            } else {
                startActivity(new Intent(this, ScrollSettingActivity.class));
            }
        }
    }

    @Subscribe
    public void doScrollEvent(ScrollMenuEvent event) {
        if (event != null) {
            runScroll();
        }
    }

    @Subscribe
    public void doScrollFinishEvent(ScrollFinishEvent event) {
        if (event != null) {
            isRunning = !isRunning;
            FloatingViewManager.getInstance(ScrollSettingActivity.this).changeFloatingViewState(isRunning);
        }
    }

    @Subscribe
    public void doCollectXYEvent(CollectMenuEvent event) {
        if (event != null) {
            trackList.clear();
            if (event.getType() == 0) {
                etContinueX.setText(event.getClickX() + "");
                etContinueY.setText(event.getClickY() + "");
            } else {
                trackList.addAll(event.getTrackList());
                String traceStr = new Gson().toJson(trackList);
                SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TRACE, traceStr, SharedPreferencesUtil.SCROLL_CONFIG);
            }
            saveValue(false);
        }
    }

    @Subscribe
    public void doScrollTypeChangeEvent(ScrollTypeChangeEvent event) {
        if (event != null) {
            switch (event.getType()) {
                case 0:
                    rgScrollTimeSelect.check(R.id.rb_time_15);
                    break;
                case 1:
                    rgScrollTimeSelect.check(R.id.rb_time_30);
                    break;
                case 2:
                    rgScrollTimeSelect.check(R.id.rb_time_custom);
                    break;
            }
        }
    }

    @Subscribe
    public void doScrollEndActionEvent(ScrollEndActionEvent event) {
        if (event != null) {
            switch (event.getType()) {
                case 0:
                    rgFinishOp.check(R.id.rb_nothing);
                    rgContinue.check(R.id.rb_continue_no);
                    break;
                case 1:
                    rgFinishOp.check(R.id.rb_goback);
                    rgContinue.check(R.id.rb_continue_no);
                    break;
                case 2:
                    rgFinishOp.check(R.id.rb_goback);
                    rgContinue.check(R.id.rb_continue_yes);
                    break;
            }
            saveValue(false);
        }
    }


    private void runScroll() {
        saveValue(false);

        if (ControllerAccessibilityService.getInstance() == null) {
            Toaster.show("无障碍服务未启动");
            return;
        }

        if (isRunning) {
            btnOperation.setText("启动");
            Toaster.show("滑动程序已关闭");
            AutoClickUtil.getInstance().stopRunning();
        } else {
            btnOperation.setText("关闭");
            Toaster.show("滑动程序将在" + scrollDuration + "秒后启动");
            AutoClickUtil.getInstance().startRunning();
        }
        isRunning = !isRunning;
        FloatingViewManager.getInstance(ScrollSettingActivity.this).changeFloatingViewState(isRunning);
    }

    private void saveValue(boolean isShow) {
        if (!StringUtils.isEmptyOrNull(etTimes.getText().toString())) {
            scrollTimes = Integer.parseInt(etTimes.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etDurations.getText().toString())) {
            scrollDuration = Integer.parseInt(etDurations.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etSlideDurations.getText().toString())) {
            slideDuration = Integer.parseInt(etSlideDurations.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etFloatingSize.getText().toString())) {
            floatingViewSize = Integer.parseInt(etFloatingSize.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etContinueX.getText().toString())) {
            clickX = Integer.parseInt(etContinueX.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etContinueY.getText().toString())) {
            clickY = Integer.parseInt(etContinueY.getText().toString());
        }

        if (!StringUtils.isEmptyOrNull(etContinueTimes.getText().toString())) {
            clickTimes = Integer.parseInt(etContinueTimes.getText().toString());
        }

        if (scrollTimeSelect == 2 && !StringUtils.isEmptyOrNull(etCustomTimes.getText().toString())) {
            customTimes = Integer.parseInt(etCustomTimes.getText().toString());
            SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_CUSTOM_TIMES, customTimes, SharedPreferencesUtil.SCROLL_CONFIG);
            etTimes.setText(customTimes + "");
            etDurations.setText("1");
            etSlideDurations.setText("1200");
        }

        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_TIME_KEY, scrollTimes, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DURATION_KEY, scrollDuration, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SLIDE_DURATION_KEY, slideDuration, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_RANDOM_TIME_KEY, isRandomTime, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_DIRECTION_KEY, direction, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FINISH_OP_KEY, finishOp, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.TIMER_TYPE_KEY, timerType, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.SCROLL_RANGE_KEY, range, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FLOATING_VIEW_FUNCTION_KEY, floatingViewFun, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.FLOATING_VIEW_SIZE_KEY, floatingViewSize, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CHECK_JUMP_KEY, isCheckJump, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.IS_CONTINUE_KEY, isContinue, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CONTINUE_TIMES_KEY, clickTimes, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CLICK_X_KEY, clickX, SharedPreferencesUtil.SCROLL_CONFIG);
        SharedPreferencesUtil.saveDataToSharedPreferences(MyApplication.getContext(), SharedPreferencesUtil.CLICK_Y_KEY, clickY, SharedPreferencesUtil.SCROLL_CONFIG);

        AutoClickUtil.getInstance().setScrollParam(scrollTimes, scrollDuration, slideDuration,
                direction, finishOp, timerType, range, false, isRandomTime, isCheckJump,
                isContinue, clickTimes, clickX, clickY, trackList);

        if (swFloatingView.isChecked()) {
            FloatingViewManager.getInstance(ScrollSettingActivity.this).changeFloatingViewSize(floatingViewSize);
        }

        if (isShow) {
            Toaster.show("保存成功");
        }
    }

    private void setSelectTime(int selectType, int value) {
        switch (selectType) {
            case 0:
                etTimes.setText("3");
                etDurations.setText("6");
                etSlideDurations.setText("6200");
                rlCustom.setVisibility(View.GONE);
                break;
            case 1:
                etTimes.setText("4");
                etDurations.setText("8");
                etSlideDurations.setText("8200");
                rlCustom.setVisibility(View.GONE);
                break;
            case 2:
                rlCustom.setVisibility(View.VISIBLE);
                break;
        }
        saveValue(true);
    }

    private void resetValue() {
        rgScrollTimeSelect.check(R.id.rb_time_15);
        etTimes.setText("300");
        etDurations.setText("10");
        etSlideDurations.setText("200");

        saveValue(true);
    }

}