package com.hw.datepickerlibrary.bean;

import java.util.List;

public class DateInfo {
    private String date;
    private List<DayInfo> list;

    public DateInfo() {
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<DayInfo> getList() {
        return this.list;
    }

    public void setList(List<DayInfo> list) {
        this.list = list;
    }
}
