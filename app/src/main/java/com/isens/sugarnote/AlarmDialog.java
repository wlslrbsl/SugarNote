package com.isens.sugarnote;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by BSPL on 2017-07-17.
 */

public class AlarmDialog extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button btn_alarm_save, btn_alarm_cancel;
    private ToggleButton tgb_sun, tgb_mon, tgb_tue, tgb_wed, tgb_thu, tgb_fri, tgb_sat;
    private TimePicker tp_alarm;
    private boolean[] dayFlag = new boolean[7];
    private Context mContext;
    private AlarmManager alarmManager;

    private NotificationManager notificationManager;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alarm);

        tp_alarm = (TimePicker) dialog.findViewById(R.id.tp_alarm);

        btn_alarm_cancel = (Button) dialog.findViewById(R.id.btn_alarm_cancel);
        btn_alarm_save = (Button) dialog.findViewById(R.id.btn_alarm_save);

        tgb_sun = (ToggleButton) dialog.findViewById(R.id.tgb_sun);
        tgb_mon = (ToggleButton) dialog.findViewById(R.id.tgb_mon);
        tgb_tue = (ToggleButton) dialog.findViewById(R.id.tgb_tue);
        tgb_wed = (ToggleButton) dialog.findViewById(R.id.tgb_wed);
        tgb_thu = (ToggleButton) dialog.findViewById(R.id.tgb_thu);
        tgb_fri = (ToggleButton) dialog.findViewById(R.id.tgb_fri);
        tgb_sat = (ToggleButton) dialog.findViewById(R.id.tgb_sat);

        tgb_sun.setOnCheckedChangeListener(this);
        tgb_mon.setOnCheckedChangeListener(this);
        tgb_tue.setOnCheckedChangeListener(this);
        tgb_wed.setOnCheckedChangeListener(this);
        tgb_thu.setOnCheckedChangeListener(this);
        tgb_fri.setOnCheckedChangeListener(this);
        tgb_sat.setOnCheckedChangeListener(this);

        btn_alarm_cancel.setOnClickListener(this);
        btn_alarm_save.setOnClickListener(this);

        mContext = this.getContext();

        alarmManager = (AlarmManager) mContext.getApplicationContext().getSystemService(Context.ALARM_SERVICE);


        return dialog;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.tgb_sun:
                if (tgb_sun.isChecked()) {
                    tgb_sun.setTextColor(Color.RED);
                    dayFlag[0] = true;
                } else {
                    tgb_sun.setTextColor(Color.WHITE);
                    dayFlag[0] = false;
                }
                break;

            case R.id.tgb_mon:
                if (tgb_mon.isChecked()) {
                    tgb_mon.setTextColor(Color.BLACK);
                    dayFlag[1] = true;
                } else {
                    tgb_mon.setTextColor(Color.WHITE);
                    dayFlag[1] = false;
                }
                break;

            case R.id.tgb_tue:
                if (tgb_tue.isChecked()) {
                    tgb_tue.setTextColor(Color.BLACK);
                    dayFlag[2] = true;
                } else {
                    tgb_tue.setTextColor(Color.WHITE);
                    dayFlag[2] = false;
                }
                break;

            case R.id.tgb_wed:
                if (tgb_wed.isChecked()) {
                    tgb_wed.setTextColor(Color.BLACK);
                    dayFlag[3] = true;
                } else {
                    tgb_wed.setTextColor(Color.WHITE);
                    dayFlag[3] = false;
                }
                break;

            case R.id.tgb_thu:
                if (tgb_thu.isChecked()) {
                    tgb_thu.setTextColor(Color.BLACK);
                    dayFlag[4] = true;
                } else {
                    tgb_thu.setTextColor(Color.WHITE);
                    dayFlag[4] = false;
                }
                break;

            case R.id.tgb_fri:
                if (tgb_fri.isChecked()) {
                    tgb_fri.setTextColor(Color.BLACK);
                    dayFlag[5] = true;
                } else {
                    tgb_fri.setTextColor(Color.WHITE);
                    dayFlag[5] = false;
                }
                break;

            case R.id.tgb_sat:
                if (tgb_sat.isChecked()) {
                    tgb_sat.setTextColor(Color.BLUE);
                    dayFlag[6] = true;
                } else {
                    tgb_sat.setTextColor(Color.WHITE);
                    dayFlag[6] = false;
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_alarm_cancel:
                dismiss();
                break;

            case R.id.btn_alarm_save:
                Integer setHour = tp_alarm.getCurrentHour();
                Integer setMin = tp_alarm.getCurrentMinute();

                int flag = 0;
                for (int i=0; i<7; i++) {
                    if (dayFlag[i] == true) {
                        flag = 1;
                    }
                }

                if(flag == 1) {

                    String ampm = "";
                    if (setHour >= 12) {
                        ampm = "PM";
                        setHour = setHour - 12;
                    } else {
                        ampm = "AM";
                    }
                    if (setHour < 10) {
                        if (setMin < 10) {
                            AlarmActivity.getAlarmAdapter().addItem(ampm, "0" + setHour.toString(), "0" + setMin.toString(), dayFlag);
                        } else {
                            AlarmActivity.getAlarmAdapter().addItem(ampm, "0" + setHour.toString(), setMin.toString(), dayFlag);
                        }
                    } else {
                        if (setMin < 10) {
                            AlarmActivity.getAlarmAdapter().addItem(ampm, setHour.toString(), "0" + setMin.toString(), dayFlag);
                        } else {
                            AlarmActivity.getAlarmAdapter().addItem(ampm, setHour.toString(), setMin.toString(), dayFlag);
                        }
                    }
                    AlarmActivity.getList_alarm().setAdapter(AlarmActivity.getAlarmAdapter());
                    Intent intent = new Intent(getContext(), AlarmActivity.class);
                    startActivity(intent);

                    dismiss();
                    break;
                }
                else {
                    Toast.makeText(getContext(), "요일을 선택하세요", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }



    public void setDialog(int hour, int min) {
        tp_alarm.setCurrentHour(hour);
        tp_alarm.setCurrentMinute(min);
    }
}
