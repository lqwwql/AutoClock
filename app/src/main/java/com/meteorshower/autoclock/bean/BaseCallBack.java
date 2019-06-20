package com.meteorshower.autoclock.bean;

import java.util.List;

public class BaseCallBack<T> {

    private int result;
    private List<T> data_list;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<T> getData_list() {
        return data_list;
    }

    public void setData_list(List<T> data_list) {
        this.data_list = data_list;
    }
}
