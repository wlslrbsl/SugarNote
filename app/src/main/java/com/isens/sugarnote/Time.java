package com.isens.sugarnote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by chong on 2017-10-28.
 */

public class Time {

    private long time;
    private Date date;
    private DateFormat dateFormat;
    private String time_now;
    static private IntentFilter intentFilter;
    private int year_now, month_now, day_now, hour_now, min_now, sec_now;

    static {
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
    }

    Time() {
        setTime_now();
    }

    private final BroadcastReceiver timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED) || action.equals(Intent.ACTION_TIME_TICK)) {
                setTime_now();
                Toast.makeText(context, "시간변화", Toast.LENGTH_SHORT).show();
            }
        }
    };

    void setTime_now() {
        this.time = System.currentTimeMillis();
        this.date = new Date(this.time);
        this.dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        this.time_now = this.dateFormat.format(date);

        this.year_now = Integer.parseInt(time_now.substring(0,4));
        this.month_now = Integer.parseInt(time_now.substring(5,7));
        this.day_now = Integer.parseInt(time_now.substring(8,10));
        this.hour_now = Integer.parseInt(time_now.substring(11,13));
        this.min_now = Integer.parseInt(time_now.substring(14,16));
        this.sec_now = Integer.parseInt(time_now.substring(17,19));

    }

    int getYear_now() {
        return year_now;
    }

    int getMonth_now() {
        return month_now;
    }

    int getDay_now() {
        return day_now;
    }

    int getHour_now() {
        return hour_now;
    }

    int getMin_now() {
        return min_now;
    }

    int getSec_now() {
        return sec_now;
    }

    BroadcastReceiver getTimeChangedReceiver() {
        return this.timeChangedReceiver;
    }

    IntentFilter getIntentFilter() {
        return this.intentFilter;
    }

}
