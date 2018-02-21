package com.isens.sugarnote;


import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Activity ac;
    private View view;

    private AlarmDialog dialog_alarm;
    private AlarmAdapter alarmAdapter;

    private Button btn_navi_center, btn_navi_left, btn_navi_right;
    private TextView btn_alarm_save, btn_alarm_cancel;
    private ListView list_alarm;

    private int alarm_cnt = 0;

    private FragmentInterActionListener listener;

    public AlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (FragmentInterActionListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        list_alarm.setAdapter(alarmAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ac = getActivity();
        view = inflater.inflate(R.layout.fragment_alarm, container, false);

        alarmAdapter = new AlarmAdapter();

        list_alarm = (ListView) view.findViewById(R.id.list_alarm);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);

        list_alarm.setOnItemClickListener(this);
        btn_navi_center.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_add_alarm);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_leftarrow);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_calendar);

/*        alarm_cnt = list_alarm.getAdapter().getCount();

        if (alarm_cnt > 0) {
            alarmSetting();
        }*/

        return view;
    }

    public void alarmSetting() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_navi_center:
                dialog_alarm = new AlarmDialog(ac);
                dialog_alarm.show();
                btn_alarm_save = (TextView) dialog_alarm.findViewById(R.id.btn_alarm_save);
                btn_alarm_cancel = (TextView) dialog_alarm.findViewById(R.id.btn_alarm_cancel);
                btn_alarm_save.setOnClickListener(this);
                btn_alarm_cancel.setOnClickListener(this);
                break;

            case R.id.btn_navi_left:
                listener.setFrag("CALENDAR");
                break;

            case R.id.btn_navi_right:
                Toast.makeText(ac, "고민중", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_alarm_cancel:
                dialog_alarm.dismiss();
                break;

            case R.id.btn_alarm_save:
                DBHelper_alarm dbHelperAlarm = new DBHelper_alarm(getContext());

                SQLiteDatabase db = dbHelperAlarm.getWritableDatabase();
                db.execSQL("insert into member values('12','30','0','0','0','0','0','0','0');");
                db.close();
                alarmAdapter.addItem(dialog_alarm.getmItem());
                dialog_alarm.dismiss();
                list_alarm.setAdapter(alarmAdapter);
                break;

            default:
                break;
        }
    }

    public AlarmAdapter getAlarmAdapter() {
        return this.alarmAdapter;
    }

    private class DBHelper_alarm extends SQLiteOpenHelper {

        public DBHelper_alarm(Context context) {
            super(context, "ALARM", null, 0);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE ALARM(" +
                    "_hour integer," +
                    "_min integer," +
                    "_sun integer," +
                    "_mon integer," +
                    "_tue integer," +
                    "_wed integer," +
                    "_thu integer," +
                    "_fri integer," +
                    "_sat integer,";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS member");
            onCreate(db);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
