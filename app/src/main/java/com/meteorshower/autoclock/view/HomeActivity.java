package com.meteorshower.autoclock.view;

import android.view.View;
import android.widget.Toast;

import com.meteorshower.autoclock.Job.AutoClickJob;
import com.meteorshower.autoclock.R;

import butterknife.OnClick;

public class HomeActivity extends BasicActivity {

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @OnClick({R.id.btn_start, R.id.btn_end})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new AutoClickJob(HomeActivity.this,2).doJob();
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

}
