package com.meteorshower.autoclock.view;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.meteorshower.autoclock.Job.TestClickJob;
import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.bean.PostData;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity{

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_start, R.id.btn_end, R.id.btn_add, R.id.btn_check, R.id.btn_look, R.id.btn_test, R.id.btn_reset, R.id.btn_exc_cmd})
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
            default:
                break;
        }
    }
}
