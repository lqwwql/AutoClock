package com.meteorshower.autoclock.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.meteorshower.autoclock.view.HomeActivity;

/**
 * 辅助功能类
 * */
public class ControllerAccessibilityService extends AccessibilityService {

    /**
     * 启动辅助功能连接成功时
     * */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);//启动Activity
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

}
