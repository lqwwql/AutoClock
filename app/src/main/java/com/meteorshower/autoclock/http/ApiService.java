package com.meteorshower.autoclock.http;

import com.meteorshower.autoclock.bean.BaseCallBack;
import com.meteorshower.autoclock.bean.JobData;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    /**
     * 获取任务
     */
    @POST("Job/getJobInfo")
    Call<BaseCallBack<JobData>> getJobInfo();

    /**
     * 上传任务
     */
    @POST("Job/postJobInfo")
    Call<BaseCallBack<JobData>> postJobInfo(@Body RequestBody requestBody);

}





