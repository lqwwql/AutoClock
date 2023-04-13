package com.meteorshower.autoclock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;


/**
 * 定时闹钟监听器
 */
public class AlarmReceiver extends BroadcastReceiver {

    public Handler mHandler;


    public AlarmReceiver() {
    }

    public AlarmReceiver(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equalsIgnoreCase(AppConstant.ALARM_RECEIVER_ACTION)) {
            Toaster.show("Receiver 收到广播");
            if (mHandler != null) {
                mHandler.sendEmptyMessage(ControllerAccessibilityService.SCROLL_WHAT);
            }
        }
    }

}
