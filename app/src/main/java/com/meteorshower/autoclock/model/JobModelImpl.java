package com.meteorshower.autoclock.model;

import com.google.gson.Gson;
import com.meteorshower.autoclock.bean.BaseCallBack;
import com.meteorshower.autoclock.bean.JobData;
import com.meteorshower.autoclock.bean.PostData;
import com.meteorshower.autoclock.http.ApiService;
import com.meteorshower.autoclock.http.RetrofitManager;
import com.meteorshower.autoclock.view.JobListener;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobModelImpl implements JobModel {
    private List<Call> calllist = new ArrayList<>();

    @Override
    public void getJob(final JobListener.GetJobListener getJobListener) {
        Call call = RetrofitManager.getInstance().getService(ApiService.class).getJobInfo();
        calllist.add(call);
        call.enqueue(new Callback<BaseCallBack<JobData>>() {
            @Override
            public void onResponse(Call<BaseCallBack<JobData>> call, Response<BaseCallBack<JobData>> response) {
                if (response != null && response.body() != null && response.body().getResult() == 1) {
                    getJobListener.onSuccess(response.body().getData_list());
                } else {
                    getJobListener.onFailure("请求失败");
                }
            }

            @Override
            public void onFailure(Call<BaseCallBack<JobData>> call, Throwable t) {
                String message = "";
                if (t != null) {
                    message = t.getMessage();
                }
                getJobListener.onFailure("on Faildure message:" + message);
            }
        });
    }

    @Override
    public void addJob(PostData jobData, final JobListener.AddJobListener addJobListener) {
        String json = new Gson().toJson(jobData);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Call call = RetrofitManager.getInstance().getService(ApiService.class).addJobInfo(body);
        calllist.add(call);
        call.enqueue(new Callback<BaseCallBack<JobData>>() {
            @Override
            public void onResponse(Call<BaseCallBack<JobData>> call, Response<BaseCallBack<JobData>> response) {
                if (response != null && response.body() != null && response.body().getResult() == 1) {
                    addJobListener.onSuccess();
                } else {
                    addJobListener.onFailure("请求失败");
                }
            }

            @Override
            public void onFailure(Call<BaseCallBack<JobData>> call, Throwable t) {
                String message = "";
                if (t != null) {
                    message = t.getMessage();
                }
                addJobListener.onFailure("on Faildure message:" + message);
            }
        });
    }

    @Override
    public void updateJob(PostData jobData, final JobListener.UpdateJobListener updateJobListener) {
        String json = new Gson().toJson(jobData);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Call call = RetrofitManager.getInstance().getService(ApiService.class).updateJobInfo(body);
        calllist.add(call);
        call.enqueue(new Callback<BaseCallBack<JobData>>() {
            @Override
            public void onResponse(Call<BaseCallBack<JobData>> call, Response<BaseCallBack<JobData>> response) {
                if (response != null && response.body() != null && response.body().getResult() == 1) {
                    updateJobListener.onSuccess();
                } else {
                    updateJobListener.onFailure("请求失败");
                }
            }

            @Override
            public void onFailure(Call<BaseCallBack<JobData>> call, Throwable t) {
                String message = "";
                if (t != null) {
                    message = t.getMessage();
                }
                updateJobListener.onFailure("on Faildure message:" + message);
            }
        });
    }

    @Override
    public void callAll() {
        if (calllist.size() > 0) {
            for (Call call : calllist) {
                if (!call.isCanceled()) {
                    call.cancel();
                }
            }
        }
    }
}
