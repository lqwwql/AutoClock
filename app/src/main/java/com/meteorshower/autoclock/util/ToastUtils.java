package com.meteorshower.autoclock.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Toast工具类
 */
public class ToastUtils {
    private static Toast mToast = null;

    public static void show(int info) {
        show(UIUtils.getContext(),info);
    }

    public static void show(String info) {
        show(UIUtils.getContext(),info);
    }

    public static void show(Context context, int info) {
        show(context, context.getString(info));
    }

    public synchronized static void show(Context context, String info) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void show(View view, int gravity, int offsetX, int offsetY){
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
  //      mToast = Toast.makeText(UIUtils.getContext(), info, Toast.LENGTH_SHORT);
        mToast = new Toast(UIUtils.getContext());
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.getView().measure(0,0);


        int width = mToast.getView().getMeasuredWidth();
        int height = mToast.getView().getMeasuredHeight();
        mToast.setGravity(gravity,offsetX + (width / 2),offsetY);
        mToast.show();
    }

    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = null;
    }
}
