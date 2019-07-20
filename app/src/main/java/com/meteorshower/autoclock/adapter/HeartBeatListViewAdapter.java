package com.meteorshower.autoclock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.bean.HeatBeat;
import com.meteorshower.autoclock.bean.JobData;

import java.util.List;

public class HeartBeatListViewAdapter extends BaseAdapter {

    private Context context;
    private List<HeatBeat> dataList;

    public HeartBeatListViewAdapter(Context context, List<HeatBeat> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public void onDataChange(List<HeatBeat> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_view_heartbeat, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HeatBeat heatBeat = dataList.get(position);
        viewHolder.heatTime.setText("心跳时间：" + heatBeat.getHeart_time());
        viewHolder.jobDoing.setText("任务执行状态：" + (heatBeat.getIs_doing_job().equalsIgnoreCase("True") ? "执行中" : "空闲中"));
        viewHolder.jobGetting.setText("任务获取状态：" + (heatBeat.getIs_getting_job().equalsIgnoreCase("True") ? "可获取" : "不获取"));

        return convertView;
    }

    class ViewHolder {
        TextView heatTime, jobDoing, jobGetting;

        public ViewHolder(View view) {
            this.heatTime = view.findViewById(R.id.tv_hear_time);
            this.jobDoing = view.findViewById(R.id.tv_heat_do_job);
            this.jobGetting = view.findViewById(R.id.tv_job_remark);
        }
    }
}
