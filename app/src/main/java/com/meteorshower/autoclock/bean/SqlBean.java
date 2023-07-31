package com.meteorshower.autoclock.bean;

import java.util.List;

public class SqlBean {

    int version;
    List<String> sql;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<String> getSql() {
        return sql;
    }

    public void setSql(List<String> sql) {
        this.sql = sql;
    }
}
