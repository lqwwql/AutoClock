package com.meteorshower.autoclock.http;

import com.meteorshower.autoclock.bean.BaseCallBack;
import com.meteorshower.autoclock.bean.JobData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpManager {

    static HttpManager mHttpManager;

    public static HttpManager getInstance() {
        if (mHttpManager == null)
            mHttpManager = new HttpManager();
        return mHttpManager;
    }

    public static JobData getJob(){
        Call call = RetrofitManager.getInstance().getService(ApiService.class).getJobInfo();
        call.enqueue(new Callback<BaseCallBack<JobData>>() {
            @Override
            public void onResponse(Call call, Response response) {

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
        return null;
    }

}
