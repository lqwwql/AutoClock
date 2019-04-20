package com.meteorshower.autoclock.view;

import android.view.View;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.util.ToastUtil;

import butterknife.OnClick;

public class HomeActivity extends BasicActivity {

    @OnClick({R.id.btn_start, R.id.btn_end})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                ToastUtil.show(this, "点击开始");
                break;
            case R.id.btn_end:
                ToastUtil.show(this, "点击结束");
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }
}
