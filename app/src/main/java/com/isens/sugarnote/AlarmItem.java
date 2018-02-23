package com.isens.sugarnote;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by BSPL on 2017-07-18.
 */

public class AlarmItem {

    private int enableFlag, alarm_num;
    private String hour, minute, ampm;
    private int[] dayFlag = new int[7];

    public AlarmItem() {
        enableFlag = 0;
        hour = "";
        minute = "";
        ampm = "";
    }

    public void setEnableFlag(int enableFlag) {
        this.enableFlag = enableFlag;
    }

    public int getEnableFlag() {
        return enableFlag;
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

    public int[] getDayFlag() {
        return dayFlag;
    }

    public void setDayFlag(int[] dayFlag) {
        this.dayFlag = dayFlag;
    }

    public void setTv_day(int[] dayFlag, TextView[] tv_day) {

        if (dayFlag[0] == 1) {
            tv_day[0].setTextColor(Color.RED);
        } else {
            tv_day[0].setTextColor(Color.WHITE);
        }

        for (int i = 1; i < 6; i++) {
            if (dayFlag[i] == 1) {
                tv_day[i].setTextColor(Color.BLACK);
            } else {
                tv_day[i].setTextColor(Color.WHITE);
            }
        }

        if (dayFlag[6] == 1) {
            tv_day[6].setTextColor(Color.BLUE);
        } else {
            tv_day[6].setTextColor(Color.WHITE);
        }

    }

    public int getAlarm_num() {
        return alarm_num;
    }

    public void setAlarm_num(int alarm_num) {
        this.alarm_num = alarm_num;
    }
}
