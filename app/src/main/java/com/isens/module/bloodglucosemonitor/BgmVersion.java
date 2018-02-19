package com.isens.module.bloodglucosemonitor;

/**
 * Created by admin on 2016-11-11.
 */

public class BgmVersion {
    public int bgmFWversionDigit1;
    public int bgmFWversionDigit2;
    public int bgmFWversionDigit3;
    public int bgmFWversionDigit4;
    public int bgmHWversion;
    public int bgmCompileDate;
    public String bgmSerialNumber;
    public int bgmFlagVersion; //barozen
    public int bgmFlagUseStrip1; //barozen
    public int bgmFlagUseStrip2; //pro
    public int bgmFlagUseStrip3; //pro ketone
    public int bgmFlagUseStrip4; //barozen ketone
    public int reserved1;
    public int reserved2;
    public int reserved3;
    public int reserved4;

    public BgmVersion(){
        bgmFWversionDigit1=0;
        bgmFWversionDigit2=0;
        bgmFWversionDigit3=0;
        bgmFWversionDigit4=0;
        bgmHWversion=0;
        bgmFlagVersion=0;
        bgmFlagUseStrip1=0; //barozen
        bgmFlagUseStrip2=0; //pro
        bgmFlagUseStrip3=0; //pro ketone
        bgmFlagUseStrip4=0; //barozen ketone
        reserved1=0;
        reserved2=0;
        reserved3=0;
        reserved4=0;

    }
}
