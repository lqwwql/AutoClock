package com.meteorshower.autoclock.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;
import android.view.WindowManager;

import com.meteorshower.autoclock.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BasicActivity extends Activity {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
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
