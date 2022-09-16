package com.meteorshower.autoclock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.bean.JobData;

import java.util.List;

public class JobListViewAdapter extends BaseAdapter {

    private Context context;
    private List<JobData> dataList;

    public JobListViewAdapter(Context context, List<JobData> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public void onDataChange(List<JobData> dataList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_view_job, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        JobData job = dataList.get(position);
        viewHolder.jobName.setText("任务名称：" + job.getJobName());
        viewHolder.jobTime.setText("任务时间：" + job.getJobTime());
        viewHolder.jobRemark.setText("任务备注：" + job.getExtraInfo());
        String typeStr = "";
        switch (job.getType()){
            case 1:
                typeStr = "早上";
                break;
            case 2:
                typeStr = "晚上";
                break;
        }
        viewHolder.jobType.setText("任务类型：" + typeStr);
        String statusStr = "";
        switch (job.getStatus()){
            case 1:
                statusStr = "未开始";
                break;
            case 2:
                statusStr = "已下发";
                break;
            case 3:
                statusStr = "已成功";
                break;
        }
        viewHolder.jobStatus.setText("任务状态：" + statusStr);

        return convertView;
    }

    class ViewHolder {
        TextView jobName, jobTime, jobRemark, jobType, jobStatus;

        public ViewHolder(View view) {
            this.jobName = view.findViewById(R.id.tv_job_name);
            this.jobTime = view.findViewById(R.id.tv_job_time);
            this.jobRemark = view.findViewById(R.id.tv_job_remark);
            this.jobType = view.findViewById(R.id.tv_job_type);
            this.jobStatus = view.findViewById(R.id.tv_job_status);
        }
    }
}
