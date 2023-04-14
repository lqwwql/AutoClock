package com.meteorshower.autoclock.activity;

import android.view.View;
import android.widget.ListView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.adapter.HeartBeatListViewAdapter;
import com.meteorshower.autoclock.bean.BaseCallBack;
import com.meteorshower.autoclock.bean.HeatBeat;
import com.meteorshower.autoclock.http.ApiService;
import com.meteorshower.autoclock.http.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckHeartActivity extends BaseActivity {

    @BindView(R.id.srl_check_heart)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.lv_check_heart)
    ListView checkHeart;

    private HeartBeatListViewAdapter heartBeatListViewAdapter;
    private List<HeatBeat> heatBeatList;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_check_heart;
    }

    @Override
    protected void initView() {
        heatBeatList = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHeatBeat();
            }
        });
        heartBeatListViewAdapter = new HeartBeatListViewAdapter(this, heatBeatList);
        checkHeart.setAdapter(heartBeatListViewAdapter);
        getHeatBeat();
    }

    @Override
    protected void initData() {

    }

    private void getHeatBeat() {
        heatBeatList.clear();
        heartBeatListViewAdapter.notifyDataSetChanged();
        Call call = RetrofitManager.getInstance().getService(ApiService.class).getHeartBeat();
        call.enqueue(new Callback<BaseCallBack<HeatBeat>>() {
            @Override
            public void onResponse(Call<BaseCallBack<HeatBeat>> call, Response<BaseCallBack<HeatBeat>> response) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (response != null && response.body() != null && response.body().getResult() == 1 && response.body().getData_list() != null) {
                    heatBeatList.addAll(response.body().getData_list());
                    heartBeatListViewAdapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onFailure(Call<BaseCallBack<HeatBeat>> call, Throwable t) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                String message = "";
                if (t != null) {
                    message = t.getMessage();
                }
            }
        });
    }

    @OnClick({R.id.tv_back})
    public void doClick(View view){
        switch (view.getId()){
            case R.id.tv_back:
                onBackPressed();
                break;
        }

    }

}
