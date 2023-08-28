package com.hw.datepickerlibrary.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {
    public CalendarUtil() {
    }

    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0L;

        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / 86400000L;
        } catch (Exception var7) {
            return "";
        }

        return day + "";
    }

    public static int getWeekNoFormat(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException var4) {
            var4.printStackTrace();
        }

        return c.get(7);
    }

    public static Calendar toDate(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(format.parse(pTime));
            return c;
        } catch (ParseException var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public static String FormatDateMD(String date) {
        if (TextUtils.isEmpty(date)) {
            new Throwable();
        }

        String month = date.split("-")[1];
        String day = date.split("-")[2];
        return month + "月" + day + "日";
    }

    public static String FormatDateYMD(String date) {
        if (TextUtils.isEmpty(date)) {
            new Throwable();
        }

        String year = date.split("-")[0];
        String month = date.split("-")[1];
        String day = date.split("-")[2];
        return year + "年" + month + "月" + day + "日";
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String getWeekByFormat(String pTime) {
        String week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException var5) {
            var5.printStackTrace();
        }

        if (c.get(7) == 1) {
            week = week + "日";
        }

        if (c.get(7) == 2) {
            week = week + "一";
        }

        if (c.get(7) == 3) {
            week = week + "二";
        }

        if (c.get(7) == 4) {
            week = week + "三";
        }

        if (c.get(7) == 5) {
            week = week + "四";
        }

        if (c.get(7) == 6) {
            week = week + "五";
        }

        if (c.get(7) == 7) {
            week = week + "六";
        }

        return week;
    }
}
