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
    @POST("job/getJob")
    Call<BaseCallBack<JobData>> getJobInfo();

    /**
     * 添加任务
     */
    @POST("job/addJob")
    Call<BaseCallBack<JobData>> addJobInfo(@Body RequestBody requestBody);

    /**
     * 更新任务
     */
    @POST("job/updateJob")
    Call<BaseCallBack<JobData>> updateJobInfo(@Body RequestBody requestBody);

}





