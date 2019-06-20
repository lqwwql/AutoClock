package com.meteorshower.autoclock.view;

import com.meteorshower.autoclock.bean.JobData;

import java.util.List;

public interface JobListener {

    interface AddJobListener{
        void onSuccess();
        void onFailure(String message);
    }

    interface GetJobListener{
        void onSuccess(List<JobData> jobDataList);
        void onFailure(String message);
    }

    interface UpdateJobListener{
        void onSuccess();
        void onFailure(String message);
    }

}
