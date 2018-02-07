package com.isens.sugarnote;

import android.app.Application;

/**
 * Created by BSPL on 2017-07-04.
 * 전역변수관리 클래스
 */

public class MyApplication extends Application {

    private static MyApplication instance = null;
    private static int animatorSpeed = 100;
    private static boolean registerDebugMode = true;
    private static boolean flag = false;
    private static boolean isStrip = false;

    private MyApplication() {

    }

    public static MyApplication getInstance() {
        if(instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public static int getAnimatorSpeed() {
        return animatorSpeed;
    }

    public static void setAnimatorSpeed(int animatorSpeed) {
        MyApplication.animatorSpeed = animatorSpeed;
    }

    public static boolean isRegisterDebugMode() {
        return registerDebugMode;
    }

    public static void setRegisterDebugMode(boolean registerDebugMode) {
        MyApplication.registerDebugMode = registerDebugMode;
    }

    public static boolean isFlag() {
        return flag;
    }

    public static void setFlag(boolean flag) {
        MyApplication.flag = flag;
    }

    public static boolean isIsStrip() {
        return isStrip;
    }

    public static void setIsStrip(boolean isStrip) {
        MyApplication.isStrip = isStrip;
    }
}
