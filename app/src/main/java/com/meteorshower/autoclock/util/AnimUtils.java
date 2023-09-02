package com.meteorshower.autoclock.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

public class AnimUtils {

    /**
     * 下拉
     */
    public static void scaleToDown(View view, long duration, Animator.AnimatorListener listener) {
        view.setPivotX(1f);
        view.setPivotY(0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        objectAnimator.setDuration(duration);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        objectAnimator.start();
    }

    /**
     * 上拉
     */
    public static void scaleToTop(View view, long duration, Animator.AnimatorListener listener) {
        view.setPivotX(1f);
        view.setPivotY(0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY);
        objectAnimator.setDuration(duration);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        objectAnimator.start();
    }

    //平移
    public static void transDown(View view, long duration, float distance, Animator.AnimatorListener listener) {
        PropertyValuesHolder transY = PropertyValuesHolder.ofFloat("translationY", 0f, distance);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, transY);
        objectAnimator.setDuration(duration);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        objectAnimator.start();
    }

    /**
     * 下拉
     */
    public static void transToDown(View view, long duration, float distance, Animator.AnimatorListener listener) {
        PropertyValuesHolder transY = PropertyValuesHolder.ofFloat("translationY", -distance, 0f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, transY);
        objectAnimator.setDuration(duration);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        objectAnimator.start();
    }

    /**
     * 上拉
     */
    public static void transToTop(View view, long duration, float distance, Animator.AnimatorListener listener) {
        PropertyValuesHolder transY = PropertyValuesHolder.ofFloat("translationY", 0f, -distance);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, transY);
        objectAnimator.setDuration(duration);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        objectAnimator.start();
    }

    /**
     * 顺时针旋转
     */
    public static void rotateClockWise(View view, long duration, Animator.AnimatorListener listener) {
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat("rotation", 0, 180);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, rotation);
        objectAnimator.setDuration(duration);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        objectAnimator.start();
    }

    /**
     * 逆时针旋转
     */
    public static void rotateAnticlockwise(View view, long duration, Animator.AnimatorListener listener) {
        PropertyValuesHolder rotation = PropertyValuesHolder.ofFloat("rotation", 180, 0);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, rotation);
        objectAnimator.setDuration(duration);
        if (listener != null) {
            objectAnimator.addListener(listener);
        }
        objectAnimator.start();
    }

}
