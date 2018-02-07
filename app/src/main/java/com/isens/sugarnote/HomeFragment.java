package com.isens.sugarnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_root, editor_user;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private Activity ac;
    private View view;

    private Dialog dialog_logout, dialog_createDB, dialog_deleteLOG;

    private FragmentInterActionListener listener;

    private LinearLayout btn_setting, btn_calendar, btn_measure, btn_report, btn_new;
    private Button btn_navi_right, btn_navi_center, btn_navi_left;

    private TextView tv_dialog, btn_dialog_ok, btn_dialog_cancel;

    private static boolean createDBFlag = false;
    private static boolean deleteLogFlag = false;

    private String userId;
    private int userCount;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (FragmentInterActionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ac = getActivity();
        view = inflater.inflate(R.layout.fragment_home, container, false);

        prefs_root = ac.getSharedPreferences("ROOT", 0);
        editor_root = prefs_root.edit();
        userId = prefs_root.getString("LOGIN", "none");
        prefs_user = ac.getSharedPreferences(userId, 0);

        btn_new = (LinearLayout) view.findViewById(R.id.btn_new);
        btn_measure = (LinearLayout) view.findViewById(R.id.btn_measure);
        btn_report = (LinearLayout) view.findViewById(R.id.btn_report);
        btn_setting = (LinearLayout) view.findViewById(R.id.btn_setting);
        btn_calendar = (LinearLayout) view.findViewById(R.id.btn_calendar);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_power);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_user);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_help);
        btn_navi_right.setEnabled(true);
        btn_navi_left.setEnabled(true);

        btn_navi_left.setOnLongClickListener(this);
        btn_navi_right.setOnLongClickListener(this);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);
        btn_new.setOnClickListener(this);
        btn_measure.setOnClickListener(this);
        btn_report.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_new:
                Toast.makeText(ac, "아직안함", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_measure:
                listener.setFrag("MEASURE");
                break;

            case R.id.btn_report:
                listener.setFrag("CHART");
                break;

            case R.id.btn_setting:
                listener.setFrag("SETTING");
                break;

            case R.id.btn_calendar:
                listener.setFrag("CALENDAR");
                break;

            case R.id.btn_navi_center:
                dialog_logout = new Dialog(ac);
                dialog_logout.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_logout.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_logout.findViewById(R.id.tv_dialog);
                tv_dialog.setText("로그인 화면으로 돌아가시겠습니까?");

                btn_dialog_ok = (TextView) dialog_logout.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_logout.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                dialog_logout.show();
                break;

            case R.id.btn_navi_left:
                listener.setFrag("USER");
                break;

            case R.id.btn_navi_right:
                Toast.makeText(ac, "미구현", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_dialog_ok:
                if (createDBFlag == true) {
                    DB_Create();
                    createDBFlag = false;
                    dialog_createDB.dismiss();
                } else if (deleteLogFlag == true) {
                    editor_user = prefs_user.edit();
                    editor_user.clear();

                    userCount = prefs_root.getInt("USERCOUNT", 0);
                    editor_root.putInt("USERCOUNT", userCount - 1);
                    editor_root.commit();

                    deleteLogFlag = false;
                    ac.finish();
                } else {
                    ac.finish();
                }
                break;

            case R.id.btn_dialog_cancel:
                if (createDBFlag == true) {
                    createDBFlag = false;
                    dialog_createDB.dismiss();
                } else if (deleteLogFlag == true) {
                    deleteLogFlag = false;
                    dialog_deleteLOG.dismiss();
                } else {
                    dialog_logout.dismiss();
                }
                break;
        }
    }

    public void DB_Create() {
        String save_date;
        if (dbHelper == null) dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
        db = dbHelper.getWritableDatabase();

        dbHelper.clear_db();

        if (dbHelper == null) dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
        db = dbHelper.getWritableDatabase();

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/ MM/ dd, HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        Date caldate = cal.getTime();
        double ran = Math.random(); // 0< ran<1 사이의 실수
        int val = (int) (ran * 20);
        int before_val = val + 90;
        int after_val = val + 100;
        int empty_val = val + 110;

        for (int i = 0; i < 40; i++) {

            ran = Math.random(); // 0< ran<1 사이의 실수
            val = (int) (ran * 20);
            before_val = val + 90;
            ran = Math.random(); // 0< ran<1 사이의 실수
            val = (int) (ran * 20);
            after_val = val + 100;
            ran = Math.random(); // 0< ran<1 사이의 실수
            val = (int) (ran * 20);
            empty_val = val + 110;

            cal.add(Calendar.DATE, -1);
            caldate = cal.getTime();
            save_date = simpleDateFormat.format(caldate);
            dbHelper.insert(save_date, empty_val, "공복");

            cal.add(Calendar.SECOND, 5);
            caldate = cal.getTime();
            save_date = simpleDateFormat.format(caldate);
            dbHelper.insert(save_date, before_val, "식전");

            cal.add(Calendar.SECOND, 5);
            caldate = cal.getTime();
            save_date = simpleDateFormat.format(caldate);
            dbHelper.insert(save_date, after_val, "식후");

            ran = Math.random(); // 0< ran<1 사이의 실수
            val = (int) (ran * 20);
            before_val = val + 90;
            ran = Math.random(); // 0< ran<1 사이의 실수
            val = (int) (ran * 20);
            after_val = val + 100;
            ran = Math.random(); // 0< ran<1 사이의 실수
            val = (int) (ran * 20);
            empty_val = val + 110;

            cal.add(Calendar.SECOND, 5);
            caldate = cal.getTime();
            save_date = simpleDateFormat.format(caldate);
            dbHelper.insert(save_date, empty_val, "공복");

            cal.add(Calendar.SECOND, 5);
            caldate = cal.getTime();
            save_date = simpleDateFormat.format(caldate);
            dbHelper.insert(save_date, before_val, "식전");

            cal.add(Calendar.SECOND, 5);
            caldate = cal.getTime();
            save_date = simpleDateFormat.format(caldate);
            dbHelper.insert(save_date, after_val, "식후");
        }

        db.close();

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_navi_right:
                dialog_createDB = new Dialog(ac);
                dialog_createDB.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_createDB.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_createDB.findViewById(R.id.tv_dialog);
                tv_dialog.setText("혈당 데이터를 생성하시겠습니까?");

                btn_dialog_ok = (TextView) dialog_createDB.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_createDB.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                createDBFlag = true;
                dialog_createDB.show();
                break;

            case R.id.btn_navi_left:
                dialog_deleteLOG = new Dialog(ac);
                dialog_deleteLOG.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_deleteLOG.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_deleteLOG.findViewById(R.id.tv_dialog);
                tv_dialog.setText("로그인된 계정을 삭제하시겠습니까?");

                btn_dialog_ok = (TextView) dialog_deleteLOG.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_deleteLOG.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                deleteLogFlag = true;
                dialog_deleteLOG.show();
                break;
        }
        return false;
    }

}
