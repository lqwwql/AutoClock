package com.meteorshower.autoclock.view;

import android.content.Intent;
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            goToHome();
                            Thread.sleep(3 * 1000);
                            Rect rect = new Rect();
                            rect.set(579, 563, 668, 652);
                            AccessibilityUtils.clickRect(rect);
                        } catch (Exception e) {
                            Log.d("lqwtest", "openModifyNameUI err = ", e);
                        }
                    }
                }).start();
                break;
            case R.id.btn_end:
                Toast.makeText(HomeActivity.this, "点击结束", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    private void goToHome() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //如果是服务里调用，必须加入new task标识
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
