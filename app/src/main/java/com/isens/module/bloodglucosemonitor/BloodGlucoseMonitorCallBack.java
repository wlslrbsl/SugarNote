package com.isens.module.bloodglucosemonitor;

/**
 * Created by admin on 2016-06-28.
 */
public interface BloodGlucoseMonitorCallBack {

    void bgmcallBackMethod(String str,int status, int value);
    void bgmBootLoadercallBackMethod(String str,int status, BgmBootLoader bootloader);
}