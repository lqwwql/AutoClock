package com.meteorshower.autoclock.view;

import com.meteorshower.autoclock.bean.JobData;

import java.util.List;

public class JobView {

    public interface AddJobView {
        void addSuccess();
        void addFailure(String message);
    }

    public interface GetJobView {
        void getSuccess(List<JobData> jobList);
        void getFailure(String message);
    }

    public interface UpdateJobView {
        void updateSuccess();
        void updateFailure(String message);
    }


}
