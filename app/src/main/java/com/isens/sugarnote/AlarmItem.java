package com.isens.sugarnote;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by BSPL on 2017-07-18.
 */

public class AlarmItem {

    private boolean enableFlag = true;
    private Drawable icon;
    private String hour, minute, ampm;
    private boolean[] dayFlag = new boolean[7];

    public void setEnableFlag(boolean enableFlag) {
        this.enableFlag = enableFlag;
    }

    public boolean getEnableFlag() {
        return enableFlag;
    }

    public void setIcon(boolean flag, ImageView iv_img, Drawable icon_on, Drawable icon_off) {
        if(flag == true){
            iv_img.setImageDrawable(icon_on);
        }
        else{
            iv_img.setImageDrawable(icon_off);
        }
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }

    public boolean[] getDayFlag() {
        return dayFlag;
    }

    public void setDayFlag(boolean[] dayFlag) {
        this.dayFlag = dayFlag;
    }

    public void setTv_day(boolean[] dayFlag, TextView[] tv_day) {

        if(dayFlag[0] == true) {
            tv_day[0].setTextColor(Color.RED);
        } else {
            tv_day[0].setTextColor(Color.WHITE);
        }

        for (int i = 1; i < 6; i++) {
            if(dayFlag[i] == true) {
                tv_day[i].setTextColor(Color.BLACK);
            } else {
                tv_day[i].setTextColor(Color.WHITE);
            }
        }

        if(dayFlag[6] == true) {
            tv_day[6].setTextColor(Color.BLUE);
        } else {
            tv_day[6].setTextColor(Color.WHITE);
        }

    }

}
