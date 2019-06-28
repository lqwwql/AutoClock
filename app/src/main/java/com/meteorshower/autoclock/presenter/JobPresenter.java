package com.meteorshower.autoclock.presenter;

import com.meteorshower.autoclock.bean.PostData;

public interface JobPresenter {

    void getCurrentJob(int job_type,int status);
    void addNewJob(PostData jobData);
    void updateCurrentJob(PostData jobData);
    void callCall();

}
