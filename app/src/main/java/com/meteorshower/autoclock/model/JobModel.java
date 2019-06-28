package com.meteorshower.autoclock.model;

import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.bean.PostData;
import com.meteorshower.autoclock.view.JobListener;

public interface JobModel {
    void getJob(int job_type,int status,JobListener.GetJobListener getJobListener);
    void addJob(PostData jobData, JobListener.AddJobListener addJobListener);
    void updateJob(PostData jobData,JobListener.UpdateJobListener updateJobListener);
    void callAll();
}
