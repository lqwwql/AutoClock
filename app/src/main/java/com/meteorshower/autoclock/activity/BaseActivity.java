package com.meteorshower.autoclock.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.meteorshower.autoclock.util.StatusBarUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends Activity {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        //允许window 的内容可以上移到刘海屏状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
        setContentView(getLayoutID());
        unbinder = ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    protected abstract int getLayoutID();

    protected abstract void initView();

    protected abstract void initData();

}
