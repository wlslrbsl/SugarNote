package com.isens.sugarnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Activity ac;
    private View view;

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private Dialog dialog_deleteLOG, dialog_DBset, dialog_initLog;
    private SoundPool soundpool;
    private int tak;
    private SeekBar bright_sb;
    private Switch sw_setting_sound, sw_setting_vibe;
    private FragmentInterActionListener listener;
    private Button btn_navi_center, btn_navi_right, btn_navi_left, btn_log_delete, btn_db_state_set, btn_log_initial;
    private TextView tv_dialog, btn_dialog_ok, btn_dialog_cancel;

    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private String userAccount, querry;
    private int cursorSize;
    private boolean deleteLogFlag = false, emptyDBFLag = false, initLogFlag;
    private float brightness_val;

    public SettingFragment() {
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
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        prefs_root = ac.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        prefs_user = ac.getSharedPreferences(userAccount, 0);
        editor_user = prefs_user.edit();

        bright_sb = (SeekBar) view.findViewById(R.id.seekbar_bright);
        sw_setting_sound = (Switch) view.findViewById(R.id.sw_setting_sound);
        sw_setting_vibe = (Switch) view.findViewById(R.id.sw_setting_vibe);

        brightness_val = prefs_user.getFloat("BRIGHTNESS",100);

        WindowManager.LayoutParams params = ac.getWindow().getAttributes();
        params.screenBrightness = (float) brightness_val / 100;
        ac.getWindow().setAttributes(params);
        bright_sb.setProgress((int) brightness_val);

        bright_sb.setOnSeekBarChangeListener(this);
        sw_setting_sound.setOnClickListener(this);
        sw_setting_vibe.setOnClickListener(this);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_log_delete = (Button) view.findViewById(R.id.btn_log_delete);
        btn_db_state_set = (Button) view.findViewById(R.id.btn_db_state_set);
        btn_log_initial = (Button) view.findViewById(R.id.btn_log_initial);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_home);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_wifi);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_info);

        btn_log_delete.setOnClickListener(this);
        btn_db_state_set.setOnClickListener(this);
        btn_log_initial.setOnClickListener(this);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);

        if (dbHelper == null)
            dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);

        db = dbHelper.getWritableDatabase();

        querry = "SELECT * FROM GLUCOSEDATA;";
        cursor = db.rawQuery(querry, null);

        cursorSize = cursor.getCount();

        if (cursorSize == 0) {
            emptyDBFLag = true;
            btn_db_state_set.setText("DB생성");
        } else {
            emptyDBFLag = false;
            btn_db_state_set.setText("DB삭제");
        }

        soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        tak = soundpool.load(ac, R.raw.beep, 1);

        if(prefs_user.getBoolean("VIBE",true))
            sw_setting_vibe.setChecked(true);
        else
            sw_setting_vibe.setChecked(false);

        if(prefs_user.getBoolean("SOUND",true))
            sw_setting_sound.setChecked(true);
        else
            sw_setting_sound.setChecked(false);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_log_delete:
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

            case R.id.btn_db_state_set:
                dialog_DBset = new Dialog(ac);
                dialog_DBset.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_DBset.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_DBset.findViewById(R.id.tv_dialog);

                if (emptyDBFLag)
                    tv_dialog.setText("혈당 데이터를 생성하시겠습니까?");
                else
                    tv_dialog.setText("혈당 데이터를 삭제하시겠습니까?");

                btn_dialog_ok = (TextView) dialog_DBset.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_DBset.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                dialog_DBset.show();
                break;

            case R.id.btn_log_initial:
                dialog_initLog = new Dialog(ac);
                dialog_initLog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_initLog.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_deleteLOG.findViewById(R.id.tv_dialog);
                tv_dialog.setText("계정을 초기화 하시겠습니까?");

                btn_dialog_ok = (TextView) dialog_deleteLOG.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_deleteLOG.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                initLogFlag = true;
                dialog_initLog.show();
                break;

            case R.id.btn_dialog_ok:
                if (deleteLogFlag) {
                    editor_user.clear();
                    editor_user.commit();
                    deleteLogFlag = false;
                    ac.finish();
                } else if (initLogFlag) {
                    initLogFlag = true;
                } else if (emptyDBFLag) {
                    DB_Create();
                    emptyDBFLag = false;
                    btn_db_state_set.setText("DB삭제");
                    dialog_DBset.dismiss();
                } else {
                    if (dbHelper == null)
                        dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
                    db = dbHelper.getWritableDatabase();
                    dbHelper.clear_db();
                    emptyDBFLag = true;
                    btn_db_state_set.setText("DB생성");
                    dialog_DBset.dismiss();
                }

                break;

            case R.id.btn_dialog_cancel:
                if (deleteLogFlag) {
                    deleteLogFlag = false;
                    dialog_deleteLOG.dismiss();
                } else if (initLogFlag) {
                    initLogFlag = false;
                    dialog_initLog.dismiss();
                } else {
                    dialog_DBset.dismiss();
                }
                break;

            case R.id.btn_navi_center:
                editor_user.putFloat("BRIGHTNESS",brightness_val);
                editor_user.commit();
                listener.setFrag("HOME");
                break;

            case R.id.btn_navi_right:
                if (dbHelper == null) dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
                db = dbHelper.getWritableDatabase();
                dbHelper.clear_db();
                Toast.makeText(ac, "기기정보", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_navi_left:
                WifiDialog dialog = new WifiDialog(ac);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_wifi);
                dialog.show();
                break;

            case R.id.sw_setting_sound:
                if(sw_setting_sound.isChecked()) {
                    editor_user.putBoolean("SOUND",true);
                    soundpool.play(tak, 1, 1, 0, 0, 1);
                }
                else
                    editor_user.putBoolean("SOUND",false);

                editor_user.commit();
                break;

            case R.id.sw_setting_vibe:
                if(sw_setting_vibe.isChecked()){
                    editor_user.putBoolean("VIBE",true);
                    Vibrator vibrator = (Vibrator) ac.getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(500);
                }
                else
                    editor_user.putBoolean("VIBE",false);

                editor_user.commit();
                break;

            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (progress < 10) {
            brightness_val = 10;
            //bright_sb.setProgress(progress);
        } else {
            brightness_val = progress;
        }

        WindowManager.LayoutParams params = ac.getWindow().getAttributes();
        params.screenBrightness = (float) brightness_val / 100;
        ac.getWindow().setAttributes(params);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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
}
