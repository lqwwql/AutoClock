package com.meteorshower.autoclock.presenter;

import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.bean.PostData;
import com.meteorshower.autoclock.model.JobModel;
import com.meteorshower.autoclock.model.JobModelImpl;
import com.meteorshower.autoclock.view.JobListener;
import com.meteorshower.autoclock.view.JobView;

import java.util.List;

public class JobPresenterImpl implements JobPresenter {

    private JobView.AddJobView addJobView;
    private JobView.GetJobView getJobView;
    private JobView.UpdateJobView updateJobView;
    private JobModel jobModel;

    public JobPresenterImpl(JobView.AddJobView addJobView) {
        this.addJobView = addJobView;
        this.jobModel = new JobModelImpl();
    }

    public JobPresenterImpl(JobView.GetJobView getJobView) {
        this.getJobView = getJobView;
        this.jobModel = new JobModelImpl();
    }

    public JobPresenterImpl(JobView.UpdateJobView updateJobView) {
        this.updateJobView = updateJobView;
        this.jobModel = new JobModelImpl();
    }


    @Override
    public void getCurrentJob(int job_type,int status) {
        jobModel.getJob(job_type,status,new JobListener.GetJobListener() {
            @Override
            public void onSuccess(List<JobData> jobDataList) {
                getJobView.getSuccess(jobDataList);
            }

            @Override
            public void onFailure(String message) {
                getJobView.getFailure(message);
            }
        });
    }

    @Override
    public void addNewJob(PostData jobData) {
        jobModel.addJob(jobData, new JobListener.AddJobListener() {
            @Override
            public void onSuccess() {
                addJobView.addSuccess();
            }

            @Override
            public void onFailure(String message) {
                addJobView.addFailure(message);
            }
        });
    }

    @Override
    public void updateCurrentJob(PostData jobData) {
        jobModel.updateJob(jobData, new JobListener.UpdateJobListener() {
            @Override
            public void onSuccess() {
                updateJobView.updateSuccess();
            }

            @Override
            public void onFailure(String message) {
                updateJobView.updateFailure(message);
            }
        });
    }

    @Override
    public void callCall() {

    }
}
