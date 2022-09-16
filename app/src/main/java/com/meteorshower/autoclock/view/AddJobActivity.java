package com.meteorshower.autoclock.view;

import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.bean.PostData;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;
import com.meteorshower.autoclock.util.StringUtils;
import com.meteorshower.autoclock.util.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class AddJobActivity extends BaseActivity implements JobView.AddJobView {

    @BindView(R.id.et_jobName)
    EditText jobName;
    @BindView(R.id.et_jobRemark)
    EditText jobRemark;
    @BindView(R.id.et_jobType)
    EditText jobType;
    private JobPresenter jobPresenter;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_add_job;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        jobPresenter = new JobPresenterImpl(this);
    }

    @OnClick({R.id.btn_add,R.id.tv_back})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                addJob();
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
        }
    }

    private void addJob() {
        try {
            String sJobName = jobName.getText().toString();
            String sJobRemark = jobRemark.getText().toString();
            String sJobType = jobType.getText().toString();
            if (StringUtils.isEmptyOrNull(sJobName)) {
                ToastUtils.show("请输入任务名称");
                return;
            }
            if (StringUtils.isEmptyOrNull(sJobRemark)) {
                ToastUtils.show("请输入任务备注");
                return;
            }
            if (StringUtils.isEmptyOrNull(sJobType)) {
                ToastUtils.show("请输入任务类型");
                return;
            }
            int type = Integer.parseInt(sJobType);
            PostData jobData = new PostData();
            jobData.setJob_name(sJobName);
            jobData.setExtra_info(sJobRemark);
            jobData.setType(type);
            jobPresenter.addNewJob(jobData);
        } catch (Exception e) {
            Log.d("lqwtest", "add error = " + Log.getStackTraceString(e));
        }
    }

    @Override
    public void addSuccess() {
        jobName.setText("");
        jobType.setText("");
        jobRemark.setText("");
        ToastUtils.show("添加任务成功");
    }

    @Override
    public void addFailure(String message) {
        ToastUtils.show(message);
    }
}