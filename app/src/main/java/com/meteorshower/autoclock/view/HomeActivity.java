package com.meteorshower.autoclock.view;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.util.ToastUtils;

import butterknife.OnClick;

public class HomeActivity extends BasicActivity {

    @OnClick({R.id.btn_start, R.id.btn_end})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                Log.d("lqwtest", "click btn_start");
                try {
                    Thread.sleep(5*1000);
                    Rect rect = new Rect();
                    rect.set(579, 563, 668, 652);
                    AccessibilityUtils.clickRect(rect);
                } catch (Exception e) {
                    Log.d("lqwtest", "openModifyNameUI err = ", e);
                }
                break;
            case R.id.btn_end:
                Toast.makeText(HomeActivity.this, "点击开始",Toast.LENGTH_SHORT).show();
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
