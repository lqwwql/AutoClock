package com.hw.datepickerlibrary.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hw.datepickerlibrary.R;
import com.hw.datepickerlibrary.bean.DateInfo;
import com.hw.datepickerlibrary.bean.DayInfo;
import com.hw.datepickerlibrary.util.CalendarUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatePopupWindow extends PopupWindow {
    private View rootView;
    private TextView tvOk;
    private RecyclerView rv;
    private TextView tvStartDate;
    private TextView tvStartWeek;
    private TextView tvEndDate;
    private TextView tvEndWeek;
    private TextView tvTime;
    private LinearLayout llEnd;
    private TextView tvHintText;
    private TextView btnClose;
    private TextView btnClear;
    private boolean dayFalg;
    private Activity activity;
    private Date mSetDate;
    private String currentDate;
    private String startDesc;
    private String endDesc;
    private int startGroupPosition;
    private int endGroupPosition;
    private int startChildPosition;
    private int endChildPosition;
    private int c_stratChildPosition;
    private DatePopupWindow.DateAdapter mDateAdapter;
    private List<DateInfo> mList;
    private DatePopupWindow.DateOnClickListener mOnClickListener;

    private DatePopupWindow(DatePopupWindow.Builder builder) {
        this.startGroupPosition = -1;
        this.endGroupPosition = -1;
        this.startChildPosition = -1;
        this.endChildPosition = -1;
        this.c_stratChildPosition = -1;
        this.mOnClickListener = null;
        this.activity = builder.context;
        this.currentDate = builder.date;
        this.startDesc = builder.startDesc;
        this.endDesc = builder.endDesc;
        this.dayFalg = builder.dayFalg;
        this.startGroupPosition = builder.startGroupPosition;
        this.startChildPosition = builder.startChildPosition;
        this.endGroupPosition = builder.endGroupPosition;
        this.endChildPosition = builder.endChildPosition;
        this.mOnClickListener = builder.mOnClickListener;
        LayoutInflater inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.rootView = inflater.inflate(R.layout.popupwindow_hotel_date, (ViewGroup)null);
        this.setContentView(this.rootView);
        this.setWidth(-1);
        this.setHeight(-1);
        this.setAnimationStyle(R.style.dialogWindowAnim);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setOnDismissListener(new DatePopupWindow.ShareDismissListener());
        this.backgroundAlpha(this.activity, 0.5F);
        this.initView();
        this.setInitSelect();
        this.create(builder.parentView);
    }

    private void backgroundAlpha(Activity context, float bgAlpha) {
        LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().setAttributes(lp);
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        this.tvOk = (TextView)this.rootView.findViewById(R.id.tv_ok);
        this.btnClose = (TextView)this.rootView.findViewById(R.id.btn_close);
        this.btnClear = (TextView)this.rootView.findViewById(R.id.btn_clear);
        this.tvStartDate = (TextView)this.rootView.findViewById(R.id.tv_startDate);
        this.tvStartWeek = (TextView)this.rootView.findViewById(R.id.tv_startWeek);
        this.tvEndDate = (TextView)this.rootView.findViewById(R.id.tv_endDate);
        this.tvEndWeek = (TextView)this.rootView.findViewById(R.id.tv_endWeek);
        this.tvTime = (TextView)this.rootView.findViewById(R.id.tv_time);
        this.llEnd = (LinearLayout)this.rootView.findViewById(R.id.ll_end);
        this.tvHintText = (TextView)this.rootView.findViewById(R.id.tv_hintText);
        this.rv = (RecyclerView)this.rootView.findViewById(R.id.rv);
        TextView tvStartDateDesc = (TextView)this.rootView.findViewById(R.id.tv_startDateDesc);
        TextView tvEndDateDesc = (TextView)this.rootView.findViewById(R.id.tv_endDateDesc);
        tvStartDateDesc.setText(this.startDesc + "日期");
        tvEndDateDesc.setText(this.endDesc + "日期");
        this.tvOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (DatePopupWindow.this.mOnClickListener != null) {
                    String startDate = ((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.startGroupPosition)).getList().get(DatePopupWindow.this.startChildPosition)).getDate();
                    String endDate = ((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.endGroupPosition)).getList().get(DatePopupWindow.this.endChildPosition)).getDate();
                    DatePopupWindow.this.mOnClickListener.getDate(startDate, endDate, DatePopupWindow.this.startGroupPosition, DatePopupWindow.this.startChildPosition, DatePopupWindow.this.endGroupPosition, DatePopupWindow.this.endChildPosition);
                }

                DatePopupWindow.this.dismiss();
            }
        });
        this.btnClose.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DatePopupWindow.this.dismiss();
            }
        });
        this.btnClear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                DatePopupWindow.this.initView();
                DatePopupWindow.this.setDefaultSelect();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this.activity);
        manager.setOrientation(LinearLayout.VERTICAL);
        this.rv.setLayoutManager(manager);
        this.mList = new ArrayList();
        this.mDateAdapter = new DatePopupWindow.DateAdapter(this.mList);
        this.rv.setAdapter(this.mDateAdapter);
        this.rv.setItemViewCacheSize(200);
        this.rv.setHasFixedSize(true);
        this.rv.setNestedScrollingEnabled(false);
        this.initData();
    }

    @SuppressLint({"SimpleDateFormat"})
    private void initData() {
        SimpleDateFormat ymd_sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (this.currentDate == null) {
                new Throwable("please set one start time");
                return;
            }

            this.mSetDate = ymd_sdf.parse(this.currentDate);
        } catch (ParseException var17) {
            var17.printStackTrace();
        }

        Calendar c = Calendar.getInstance();
        c.setTime(this.mSetDate);
        int firstM = c.get(2) + 1;
        int days = c.get(5);
        int week = c.get(7);
        int maxDys = c.getActualMaximum(5);
        DateInfo info = new DateInfo();
        List<DayInfo> dayList = new ArrayList();
        info.setDate(c.get(1) + "年" + firstM + "月");
        int w = CalendarUtil.getWeekNoFormat(c.get(1) + "-" + (c.get(2) + 1) + "-01") - 1;

        int i;
        DayInfo dayInfo;
        for(i = 0; i < w; ++i) {
            dayInfo = new DayInfo();
            dayInfo.setName("");
            dayInfo.setEnable(false);
            dayInfo.setDate("");
            dayList.add(dayInfo);
        }

        int maxDays;
        int weeks;
        for(i = 1; i <= maxDys; ++i) {
            dayInfo = new DayInfo();
            dayInfo.setName(i + "");
            dayInfo.setDate(c.get(1) + "-" + (c.get(2) + 1) + "-" + i);
            int c_year = Integer.parseInt(this.currentDate.split("-")[0]);
            maxDays = Integer.parseInt(this.currentDate.split("-")[1]);
            weeks = Integer.parseInt(this.currentDate.split("-")[2]);
            if (c_year == c.get(1) && maxDays == c.get(2) + 1 && weeks == i) {
                this.c_stratChildPosition = dayList.size();
            }

            if (i < days) {
                dayInfo.setEnable(false);
            } else {
                dayInfo.setEnable(true);
            }

            dayList.add(dayInfo);
        }

        info.setList(dayList);
        this.mList.add(info);

        for(i = 1; i < 5; ++i) {
            c.add(2, 1);
            DateInfo nextInfo = new DateInfo();
            List<DayInfo> nextdayList = new ArrayList();
            maxDays = c.getActualMaximum(5);
            nextInfo.setDate(c.get(1) + "年" + (c.get(2) + 1) + "月");
            weeks = CalendarUtil.getWeekNoFormat(c.get(1) + "-" + (c.get(2) + 1) + "-01") - 1;

            int j;
            for(j = 0; j < weeks; ++j) {
                DayInfo dayInfow = new DayInfo();
                dayInfow.setName("");
                dayInfow.setEnable(false);
                dayInfow.setDate("");
                nextdayList.add(dayInfow);
            }

            for(j = 0; j < maxDays; ++j) {
                DayInfo dayInfom = new DayInfo();
                dayInfom.setName(j + 1 + "");
                dayInfom.setEnable(true);
                dayInfom.setDate(c.get(1) + "-" + (c.get(2) + 1) + "-" + (j + 1));
                nextdayList.add(dayInfom);
            }

            nextInfo.setList(nextdayList);
            this.mList.add(nextInfo);
        }

        this.mDateAdapter.updateData();
    }

    private void setInitSelect() {
        if (0 <= this.startGroupPosition && this.startGroupPosition < this.mList.size() && 0 <= this.endGroupPosition && this.endGroupPosition < this.mList.size()) {
            int maxEndChild = ((DateInfo)this.mList.get(this.endGroupPosition)).getList().size();
            int maxStartChild = ((DateInfo)this.mList.get(this.startGroupPosition)).getList().size();
            if (0 <= this.startChildPosition && this.startChildPosition < maxStartChild && 0 <= this.endChildPosition && this.endChildPosition < maxEndChild) {
                this.setInit();
            } else {
                this.setDefaultSelect();
            }
        } else {
            this.setDefaultSelect();
        }

    }

    private void setInit() {
        ((DayInfo)((DateInfo)this.mList.get(this.startGroupPosition)).getList().get(this.startChildPosition)).setStatus(1);
        ((DayInfo)((DateInfo)this.mList.get(this.endGroupPosition)).getList().get(this.endChildPosition)).setStatus(2);
        this.mDateAdapter.notifyDataSetChanged();
        this.getoffsetDate(((DayInfo)((DateInfo)this.mList.get(this.startGroupPosition)).getList().get(this.startChildPosition)).getDate(), ((DayInfo)((DateInfo)this.mList.get(this.endGroupPosition)).getList().get(this.endChildPosition)).getDate(), true);
        this.rv.scrollToPosition(this.startGroupPosition);
    }

    @SuppressLint({"SimpleDateFormat"})
    private void setDefaultSelect() {
        if (this.c_stratChildPosition != -1) {
            String date = ((DayInfo)((DateInfo)this.mList.get(0)).getList().get(this.c_stratChildPosition)).getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = null;

            try {
                curDate = sdf.parse(this.FormatDate(date));
            } catch (ParseException var8) {
                var8.printStackTrace();
            }

            if (curDate != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(curDate);
                calendar.add(5, 1);
                int year = Integer.parseInt(date.split("-")[0]);
                int month = Integer.parseInt(date.split("-")[1]);
                if (year == calendar.get(1) && month == calendar.get(2) + 1 && this.c_stratChildPosition < ((DateInfo)this.mList.get(0)).getList().size() - 1) {
                    this.startGroupPosition = 0;
                    this.startChildPosition = this.c_stratChildPosition;
                    this.endGroupPosition = 0;
                    this.endChildPosition = this.c_stratChildPosition + 1;
                    this.setInit();
                } else {
                    for(int i = 0; i < ((DateInfo)this.mList.get(1)).getList().size(); ++i) {
                        if (!TextUtils.isEmpty(((DayInfo)((DateInfo)this.mList.get(1)).getList().get(i)).getDate())) {
                            this.startGroupPosition = 0;
                            this.startChildPosition = this.c_stratChildPosition;
                            this.endGroupPosition = 1;
                            this.endChildPosition = i;
                            this.setInit();
                            break;
                        }
                    }
                }

            }
        }
    }

    @SuppressLint({"SetTextI18n"})
    private void getoffsetDate(String startDate, String endDate, boolean status) {
        Calendar sCalendar = CalendarUtil.toDate(startDate);
        Calendar eCalendar = CalendarUtil.toDate(endDate);
        this.tvStartDate.setText(sCalendar.get(2) + 1 + "月" + sCalendar.get(5) + "日");
        this.tvStartWeek.setText("周" + CalendarUtil.getWeekByFormat(startDate));
        this.tvEndDate.setText(eCalendar.get(2) + 1 + "月" + eCalendar.get(5) + "日");
        this.tvEndWeek.setText("周" + CalendarUtil.getWeekByFormat(endDate));
        int daysOffset = Integer.parseInt(CalendarUtil.getTwoDay(endDate, startDate));
        if (daysOffset >= 0) {
            if (this.dayFalg) {
                this.tvTime.setText("共" + (daysOffset + 1) + "天");
            } else {
                this.tvTime.setText("共" + daysOffset + "晚");
            }

            this.llEnd.setVisibility(View.VISIBLE);
            this.tvHintText.setVisibility(View.GONE);
            this.tvOk.setText("完成");
            this.tvOk.setEnabled(true);
            this.tvOk.setBackgroundResource(R.drawable.img_btn_bg_y);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            DayInfo info = (DayInfo)((DateInfo)this.mList.get(this.startGroupPosition)).getList().get(this.startChildPosition);

            try {
                c.setTime(sdf.parse(info.getDate()));
            } catch (ParseException var16) {
                var16.printStackTrace();
            }

            for(int i = 0; i < daysOffset; ++i) {
                c.add(5, 1);
                String d = c.get(1) + "-" + (c.get(2) + 1) + "-" + c.get(5);

                for(int j = 0; j < this.mList.size(); ++j) {
                    DayInfo dayInfo = (DayInfo)((DateInfo)this.mList.get(j)).getList().get(((DateInfo)this.mList.get(j)).getList().size() - 1);
                    boolean isCheck = false;
                    if (!TextUtils.isEmpty(dayInfo.getDate()) && Integer.valueOf(dayInfo.getDate().split("-")[0]) == c.get(1) && Integer.valueOf(dayInfo.getDate().split("-")[1]) == c.get(2) + 1) {
                        for(int t = 0; t < ((DateInfo)this.mList.get(j)).getList().size(); ++t) {
                            if (((DayInfo)((DateInfo)this.mList.get(j)).getList().get(t)).getDate().equals(d)) {
                                ((DayInfo)((DateInfo)this.mList.get(j)).getList().get(t)).setSelect(status);
                                isCheck = true;
                                break;
                            }
                        }
                    }

                    if (isCheck) {
                        this.mDateAdapter.notifyItemChanged(j);
                        break;
                    }
                }
            }

        }
    }

    private String FormatDate(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(date.split("-")[0]);
            stringBuffer.append("-");
            stringBuffer.append(date.split("-")[1].length() < 2 ? "0" + date.split("-")[1] : date.split("-")[1]);
            stringBuffer.append("-");
            stringBuffer.append(date.split("-")[2].length() < 2 ? "0" + date.split("-")[2] : date.split("-")[2]);
            return stringBuffer.toString();
        }
    }

    private void create(View view) {
        this.showAtLocation(view, 81, 0, 0);
    }

    private class ShareDismissListener implements OnDismissListener {
        private ShareDismissListener() {
        }

        public void onDismiss() {
            DatePopupWindow.this.backgroundAlpha(DatePopupWindow.this.activity, 1.0F);
        }
    }

    private class TempAdapter extends BaseQuickAdapter<DayInfo, BaseViewHolder> {
        TempAdapter(@Nullable List<DayInfo> data) {
            super(R.layout.adapter_hotel_select_date_child, data);
        }

        protected void convert(BaseViewHolder helper, DayInfo item) {
            String name = item.getName();
            boolean isSelect = item.isSelect();
            boolean isEnable = item.isEnable();
            int status = item.getStatus();
            helper.setText(R.id.tv_date, name);
            if (status == 0) {
                if (isSelect) {
                    helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                    ((TextView)helper.getView(R.id.tv_date)).setTextColor(DatePopupWindow.this.activity.getResources().getColor(R.color.white));
                    helper.getView(R.id.ll_bg).setBackgroundColor(DatePopupWindow.this.activity.getResources().getColor(R.color.title_bg2));
                } else {
                    helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                    ((TextView)helper.getView(R.id.tv_date)).setTextColor(DatePopupWindow.this.activity.getResources().getColor(R.color.black));
                    helper.getView(R.id.ll_bg).setBackgroundColor(DatePopupWindow.this.activity.getResources().getColor(R.color.white));
                }
            } else if (status == 1) {
                helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_status, DatePopupWindow.this.startDesc);
                helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                ((TextView)helper.getView(R.id.tv_status)).setTextColor(DatePopupWindow.this.activity.getResources().getColor(R.color.white));
                ((TextView)helper.getView(R.id.tv_date)).setTextColor(DatePopupWindow.this.activity.getResources().getColor(R.color.white));
                helper.getView(R.id.ll_bg).setBackgroundColor(DatePopupWindow.this.activity.getResources().getColor(R.color.title_bg));
            } else if (status == 2) {
                helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                helper.setText(R.id.tv_status, DatePopupWindow.this.endDesc);
                helper.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                ((TextView)helper.getView(R.id.tv_status)).setTextColor(DatePopupWindow.this.activity.getResources().getColor(R.color.white));
                ((TextView)helper.getView(R.id.tv_date)).setTextColor(DatePopupWindow.this.activity.getResources().getColor(R.color.white));
                helper.getView(R.id.ll_bg).setBackgroundColor(DatePopupWindow.this.activity.getResources().getColor(R.color.title_bg));
            }

            if (!isSelect && status == 0) {
                TextView textView;
                if (!isEnable) {
                    textView = (TextView)helper.getView(R.id.tv_dateDel);
                    if (TextUtils.isEmpty(name)) {
                        textView.setVisibility(View.GONE);
                    } else {
                        textView.setText(name);
                        textView.setVisibility(View.VISIBLE);
                    }

                    textView.setTextColor(DatePopupWindow.this.activity.getResources().getColor(R.color.text_enable));
                    helper.getView(R.id.tv_date).setVisibility(View.GONE);
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                } else {
                    helper.getView(R.id.tv_date).setVisibility(View.VISIBLE);
                    helper.getView(R.id.tv_status).setVisibility(View.GONE);
                    helper.getView(R.id.tv_dateDel).setVisibility(View.GONE);
                    textView = (TextView)helper.getView(R.id.tv_date);
                    textView.setTextColor(DatePopupWindow.this.activity.getResources().getColor(R.color.black));
                }
            }

        }
    }

    private class DateAdapter extends BaseQuickAdapter<DateInfo, BaseViewHolder> {
        DateAdapter(@Nullable List<DateInfo> data) {
            super(R.layout.adapter_hotel_select_date, data);
        }

        public void onBindViewHolder(BaseViewHolder holder, int positions) {
            super.onBindViewHolder(holder, positions);
            TextView tv = (TextView)holder.getView(R.id.tv_date);
            tv.setText(((DateInfo)DatePopupWindow.this.mList.get(positions)).getDate());
        }

        protected void convert(final BaseViewHolder helper, final DateInfo item) {
            RecyclerView rv = (RecyclerView)helper.getView(R.id.rv_date);
            GridLayoutManager manager = new GridLayoutManager(DatePopupWindow.this.activity, 7);
            rv.setLayoutManager(manager);
            DatePopupWindow.TempAdapter groupAdapter = DatePopupWindow.this.new TempAdapter(item.getList());
            rv.setAdapter(groupAdapter);
            rv.setItemViewCacheSize(200);
            rv.setHasFixedSize(true);
            rv.setNestedScrollingEnabled(false);
            groupAdapter.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (((DayInfo)item.getList().get(position)).isEnable()) {
                        if (!TextUtils.isEmpty(((DayInfo)item.getList().get(position)).getName())) {
                            if (!TextUtils.isEmpty(((DayInfo)item.getList().get(position)).getDate())) {
                                int status = ((DayInfo)item.getList().get(position)).getStatus();
                                if (status == 0 && DatePopupWindow.this.startGroupPosition == -1 && DatePopupWindow.this.startChildPosition == -1 && ((DayInfo)item.getList().get(position)).isEnable()) {
                                    ((DayInfo)item.getList().get(position)).setStatus(1);
                                    adapter.notifyItemChanged(position);
                                    DatePopupWindow.this.startGroupPosition = helper.getAdapterPosition();
                                    DatePopupWindow.this.startChildPosition = position;
                                    DatePopupWindow.this.tvStartDate.setText(CalendarUtil.FormatDateMD(((DayInfo)item.getList().get(position)).getDate()));
                                    DatePopupWindow.this.tvStartWeek.setText("周" + CalendarUtil.getWeekByFormat(((DayInfo)item.getList().get(position)).getDate()));
                                    DatePopupWindow.this.tvTime.setText("请选择" + DatePopupWindow.this.endDesc + "时间");
                                    DatePopupWindow.this.tvOk.setEnabled(false);
                                    DatePopupWindow.this.tvOk.setText("请选择" + DatePopupWindow.this.endDesc + "时间");
                                    DatePopupWindow.this.tvOk.setBackgroundResource(R.drawable.img_btn_bg_n);
                                    DatePopupWindow.this.llEnd.setVisibility(View.GONE);
                                    DatePopupWindow.this.tvHintText.setText(DatePopupWindow.this.endDesc + "日期");
                                    DatePopupWindow.this.tvHintText.setVisibility(View.VISIBLE);
                                } else if (status == 0 && DatePopupWindow.this.endGroupPosition == -1 && DatePopupWindow.this.endChildPosition == -1) {
                                    int offset = Integer.parseInt(CalendarUtil.getTwoDay(((DayInfo)item.getList().get(position)).getDate(), ((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.startGroupPosition)).getList().get(DatePopupWindow.this.startChildPosition)).getDate()));
                                    if (offset < 0) {
                                        ((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.startGroupPosition)).getList().get(DatePopupWindow.this.startChildPosition)).setStatus(0);
                                        DatePopupWindow.this.mDateAdapter.notifyItemChanged(DatePopupWindow.this.startGroupPosition);
                                        ((DayInfo)item.getList().get(position)).setStatus(1);
                                        DatePopupWindow.this.startGroupPosition = helper.getAdapterPosition();
                                        DatePopupWindow.this.startChildPosition = position;
                                        String mStartTime = CalendarUtil.FormatDateMD(((DayInfo)item.getList().get(position)).getDate());
                                        DatePopupWindow.this.tvStartDate.setText(mStartTime);
                                        DatePopupWindow.this.tvStartWeek.setText("周" + CalendarUtil.getWeekByFormat(((DayInfo)item.getList().get(position)).getDate()));
                                        adapter.notifyItemChanged(position);
                                        DatePopupWindow.this.tvTime.setText("请选择" + DatePopupWindow.this.endDesc + "时间");
                                        DatePopupWindow.this.tvOk.setText("请选择" + DatePopupWindow.this.endDesc + "时间");
                                        DatePopupWindow.this.tvOk.setEnabled(false);
                                        DatePopupWindow.this.tvOk.setBackgroundResource(R.drawable.img_btn_bg_n);
                                        DatePopupWindow.this.llEnd.setVisibility(View.GONE);
                                        DatePopupWindow.this.tvHintText.setText(DatePopupWindow.this.endDesc + "日期");
                                        DatePopupWindow.this.tvHintText.setVisibility(View.VISIBLE);
                                    } else {
                                        ((DayInfo)item.getList().get(position)).setStatus(2);
                                        adapter.notifyItemChanged(position);
                                        DatePopupWindow.this.endGroupPosition = helper.getAdapterPosition();
                                        DatePopupWindow.this.endChildPosition = position;
                                        DatePopupWindow.this.getoffsetDate(((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.startGroupPosition)).getList().get(DatePopupWindow.this.startChildPosition)).getDate(), ((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.endGroupPosition)).getList().get(DatePopupWindow.this.endChildPosition)).getDate(), true);
                                    }
                                } else if (status == 0 && DatePopupWindow.this.endGroupPosition != -1 && DatePopupWindow.this.endChildPosition != -1 && DatePopupWindow.this.startChildPosition != -1 && DatePopupWindow.this.startGroupPosition != -1) {
                                    ((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.startGroupPosition)).getList().get(DatePopupWindow.this.startChildPosition)).setStatus(0);
                                    ((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.endGroupPosition)).getList().get(DatePopupWindow.this.endChildPosition)).setStatus(0);
                                    DatePopupWindow.this.mDateAdapter.notifyItemChanged(DatePopupWindow.this.startGroupPosition);
                                    DatePopupWindow.this.mDateAdapter.notifyItemChanged(DatePopupWindow.this.endGroupPosition);
                                    DatePopupWindow.this.getoffsetDate(((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.startGroupPosition)).getList().get(DatePopupWindow.this.startChildPosition)).getDate(), ((DayInfo)((DateInfo)DatePopupWindow.this.mList.get(DatePopupWindow.this.endGroupPosition)).getList().get(DatePopupWindow.this.endChildPosition)).getDate(), false);
                                    ((DayInfo)item.getList().get(position)).setStatus(1);
                                    adapter.notifyItemChanged(position);
                                    String mStartTimex = CalendarUtil.FormatDateMD(((DayInfo)item.getList().get(position)).getDate());
                                    DatePopupWindow.this.tvStartDate.setText(mStartTimex);
                                    DatePopupWindow.this.tvStartWeek.setText("周" + CalendarUtil.getWeekByFormat(((DayInfo)item.getList().get(position)).getDate()));
                                    DatePopupWindow.this.startGroupPosition = helper.getAdapterPosition();
                                    DatePopupWindow.this.startChildPosition = position;
                                    DatePopupWindow.this.endGroupPosition = -1;
                                    DatePopupWindow.this.endChildPosition = -1;
                                    DatePopupWindow.this.tvTime.setText("请选择" + DatePopupWindow.this.endDesc + "时间");
                                    DatePopupWindow.this.tvOk.setText("请选择" + DatePopupWindow.this.endDesc + "时间");
                                    DatePopupWindow.this.tvOk.setEnabled(false);
                                    DatePopupWindow.this.tvOk.setBackgroundResource(R.drawable.img_btn_bg_n);
                                    DatePopupWindow.this.llEnd.setVisibility(View.GONE);
                                    DatePopupWindow.this.tvHintText.setText(DatePopupWindow.this.endDesc + "日期");
                                    DatePopupWindow.this.tvHintText.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            });
        }

        public void updateData() {
            this.notifyDataSetChanged();
        }
    }

    public static class Builder {
        private String date;
        private Activity context;
        private View parentView;
        private String startDesc;
        private String endDesc;
        private boolean dayFalg = true;
        private int startGroupPosition = -1;
        private int endGroupPosition = -1;
        private int startChildPosition = -1;
        private int endChildPosition = -1;
        private DatePopupWindow.DateOnClickListener mOnClickListener = null;

        @SuppressLint({"SimpleDateFormat"})
        public Builder(Activity context, Date date, View parentView) {
            this.date = (new SimpleDateFormat("yyyy-MM-dd")).format(date);
            this.context = context;
            this.parentView = parentView;
            this.startDesc = "开始";
            this.endDesc = "结束";
            this.dayFalg = true;
        }

        public DatePopupWindow builder() {
            return new DatePopupWindow(this);
        }

        public DatePopupWindow.Builder setInitSelect(int startGroup, int startChild, int endGroup, int endChild) {
            this.startGroupPosition = startGroup;
            this.startChildPosition = startChild;
            this.endGroupPosition = endGroup;
            this.endChildPosition = endChild;
            return this;
        }

        public DatePopupWindow.Builder setInitDay(boolean dayFalg) {
            this.dayFalg = dayFalg;
            if (dayFalg) {
                this.startDesc = "开始";
                this.endDesc = "结束";
            } else {
                this.startDesc = "入住";
                this.endDesc = "离开";
            }

            return this;
        }

        public DatePopupWindow.Builder setDateOnClickListener(DatePopupWindow.DateOnClickListener mlListener) {
            this.mOnClickListener = mlListener;
            return this;
        }
    }

    public interface DateOnClickListener {
        void getDate(String var1, String var2, int var3, int var4, int var5, int var6);
    }
}

