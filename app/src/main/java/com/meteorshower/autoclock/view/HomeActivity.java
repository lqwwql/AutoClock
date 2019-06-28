package com.meteorshower.autoclock.view;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.bean.PostData;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;
import com.meteorshower.autoclock.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BasicActivity implements JobView.AddJobView {

    @BindView(R.id.et_jobName)
    EditText jobName;
    @BindView(R.id.et_jobRemark)
    EditText jobRemark;
    @BindView(R.id.et_jobType)
    EditText jobType;

    private JobPresenter jobPresenter;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        jobPresenter = new JobPresenterImpl(this);
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_start, R.id.btn_end, R.id.btn_add, R.id.btn_check,R.id.btn_look})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if(!JobExecutor.getInstance().isRuning()){
                    JobExecutor.getInstance().setRuning(true);
                    JobExecutor.getInstance().start();
                }
                if(!JobFactory.getInstance().isRuning()){
                    JobFactory.getInstance().setRuning(true);
                    JobFactory.getInstance().start();
                }
                JobFactory.getInstance().setGetJob(true);
                break;
            case R.id.btn_end:
                JobFactory.getInstance().setGetJob(false);
                break;
            case R.id.btn_check:
                JobFactory.getInstance().setRuning(false);
                JobExecutor.getInstance().setRuning(false);
                break;
            case R.id.btn_look:
                startActivity(new Intent(HomeActivity.this,CheckJobActivity.class));
                break;
            case R.id.btn_add:
                try {
                    String sJobName = jobName.getText().toString();
                    String sJobRemark = jobRemark.getText().toString();
                    String sJobType = jobType.getText().toString();
                    int type = Integer.parseInt(sJobType);

                    PostData jobData = new PostData();
                    jobData.setJob_name(sJobName);
                    jobData.setExtra_info(sJobRemark);
                    jobData.setType(type);
                    jobPresenter.addNewJob(jobData);
                } catch (Exception e) {
                    Log.d("lqwtest", "add error = " + Log.getStackTraceString(e));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void addSuccess() {
        Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void addFailure(String message) {
        Log.d("lqwtest", "addFailure message=" + message);
        Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
    }
}
