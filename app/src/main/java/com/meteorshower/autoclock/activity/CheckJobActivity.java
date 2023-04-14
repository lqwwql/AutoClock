package com.meteorshower.autoclock.activity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.adapter.JobListViewAdapter;
import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.bean.SpinnerOption;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.presenter.JobPresenter;
import com.meteorshower.autoclock.presenter.JobPresenterImpl;
import com.meteorshower.autoclock.view.JobView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CheckJobActivity extends BaseActivity implements JobView.GetJobView {

    @BindView(R.id.sp_job_status)
    Spinner spJobStatus;
    @BindView(R.id.sp_job_type)
    Spinner spJobType;
    @BindView(R.id.lv_job_list)
    ListView lvJobList;

    private JobListViewAdapter jobListViewAdapter;
    private List<JobData> dataList;
    private JobPresenter jobPresenter;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_check_job;
    }

    @Override
    protected void initView() {
        try {
            dataList = new ArrayList<>();
            jobPresenter = new JobPresenterImpl(this);
            jobListViewAdapter = new JobListViewAdapter(this, dataList);
            lvJobList.setAdapter(jobListViewAdapter);
            initSpinner();
            jobPresenter.getCurrentJob(0, 0);
        } catch (Exception e) {
            Log.d("lqwtest", "initview error = " + Log.getStackTraceString(e));
        }
    }

    @Override
    protected void initData() {

    }

    private void initSpinner() {
        List<SpinnerOption> typeList = new ArrayList<>();
        typeList.add(new SpinnerOption(AppConstant.JOP_TYPE_0, "所有"));
        typeList.add(new SpinnerOption(AppConstant.JOP_TYPE_1, "早上"));
        typeList.add(new SpinnerOption(AppConstant.JOP_TYPE_2, "晚上"));

        List<SpinnerOption> statusList = new ArrayList<>();
        statusList.add(new SpinnerOption(AppConstant.JOP_STATUS_0, "所有"));
        statusList.add(new SpinnerOption(AppConstant.JOP_STATUS_1, "未开始"));
        statusList.add(new SpinnerOption(AppConstant.JOP_STATUS_2, "已下发"));
        statusList.add(new SpinnerOption(AppConstant.JOP_STATUS_3, "已成功"));

        ArrayAdapter typeAdapter = new ArrayAdapter<SpinnerOption>(this, android.R.layout.simple_spinner_dropdown_item, typeList);
        ArrayAdapter statusAdapter = new ArrayAdapter<SpinnerOption>(this, android.R.layout.simple_spinner_dropdown_item, statusList);

        spJobType.setAdapter(typeAdapter);
        spJobStatus.setAdapter(statusAdapter);
    }

    @OnClick({R.id.btn_check_job,R.id.tv_back})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_check_job:
                String type = ((SpinnerOption) spJobType.getSelectedItem()).getValue();
                String status = ((SpinnerOption) spJobStatus.getSelectedItem()).getValue();
                Log.d("lqwtest", "type=" + type + " status=" + status);
                jobPresenter.getCurrentJob(Integer.parseInt(type), Integer.parseInt(status));
                break;
            case R.id.tv_back:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void getSuccess(List<JobData> jobList) {
        if (jobList == null || jobList.size() <= 0) {
            Toaster.show("无数据");
            jobListViewAdapter.onDataChange(new ArrayList<JobData>());
            return;
        }
        Collections.reverse(jobList);
        jobListViewAdapter.onDataChange(jobList);
    }

    @Override
    public void getFailure(String message) {
        Toaster.show(message);
    }
}
