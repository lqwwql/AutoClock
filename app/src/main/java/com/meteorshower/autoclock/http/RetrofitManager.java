package com.meteorshower.autoclock.http;

import android.util.Log;

import com.meteorshower.autoclock.constant.AppConstant;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {

    private Retrofit mRetrofit;
    private OkHttpClient client;
    private String mToken;
    private boolean isEffected = true;

    private RetrofitManager() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                if (!isEffected) {
                }
                Request origin = chain.request();
                mToken = AppConstant.TOKEN;
                Request request = origin.newBuilder()
                        .addHeader("Authorization", AppConstant.TOKEN)
                        .build();
                return chain.proceed(request);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(50, TimeUnit.SECONDS); // 设置连接超时时间为50秒
        builder.readTimeout(50, TimeUnit.SECONDS); // 设置读取超时时间为50秒
        builder.writeTimeout(50, TimeUnit.SECONDS); // 设置写超时时间为50秒
        builder.addInterceptor(interceptor);
        builder.retryOnConnectionFailure(false);
        client = builder.build();

        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加rxjava转换器
                .baseUrl(AppConstant.BASE_URL)
                .client(client)
                .build();
    }

    public String getToken() {
        return mToken;
    }


    public void resetRetrofit() {
        SingletonHolder.sInstance = new RetrofitManager();
    }

    public OkHttpClient getClient() {
        return client;
    }

    public static RetrofitManager getInstance() {
        return SingletonHolder.sInstance;
    }

    public <T> T getService(Class<T> tclass) {
        return mRetrofit.create(tclass);
    }

    private static class SingletonHolder {
        private static RetrofitManager sInstance = new RetrofitManager();
    }

    public Retrofit getRetrofit() {
        if (mRetrofit != null) {
            return mRetrofit;
        }
        Log.e("lqwtest", "mRetrofit:==null ");
        return null;
    }

    public void setEffected(boolean isEffected) {
        this.isEffected = isEffected; // 设置当前为无效状态
    }
}
