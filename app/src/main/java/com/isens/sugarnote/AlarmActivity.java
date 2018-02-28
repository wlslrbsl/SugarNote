package com.isens.sugarnote;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_alarm_check;
    private SharedPreferences prefs_root, prefs_user;
    private TextView tv_active_alarm;
    private String userAccount;
    private SoundPool soundpool;
    private int wavfile;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();
    private boolean sound_flag = true, vibe_flag = true;
    private Vibrator vibrator;

    private int sound_cnt = 0, vibe_cnt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm);

        tv_active_alarm = (TextView) findViewById(R.id.tv_active_alarm);

        Time time = new Time();

        String active_time = time.getHour_now() + " : " + time.getMin_now();
        tv_active_alarm.setText(active_time + "\n알람이 울렸습니다.");

        soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        wavfile = soundpool.load(this, R.raw.beep, 1);

        prefs_root = getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        prefs_user = getSharedPreferences(userAccount, 0);

        btn_alarm_check = (Button) findViewById(R.id.btn_alarm_check);
        btn_alarm_check.setOnClickListener(this);

        sound_cnt = 0;
        vibe_cnt = 0;
        if(prefs_user.getBoolean("SOUND",true)){
        if (prefs_user.getBoolean("SOUND", true)) {
            sound_flag = true;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() { // Thread 로 작업할 내용을 구현
                    while (sound_flag) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() { // 화면에 변경하는 작업을 구현
                                soundpool.play(wavfile, 1, 1, 0, 0, 1);
                                sound_cnt = sound_cnt + 1;
                                if(sound_cnt == 10)
                                    sound_flag = false;
                            }
                        });
                        try {
                            Thread.sleep(1000); // 시간지연
                        } catch (InterruptedException e) {
                        }
                    } // end of while
                }
            });
            t.start(); // 쓰레드 시작
        }

        if (prefs_user.getBoolean("VIBE", true)) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe_flag = true;
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() { // Thread 로 작업할 내용을 구현
                    while (sound_flag) {
                        handler2.post(new Runnable() {
                            @Override
                            public void run() { // 화면에 변경하는 작업을 구현
                                vibrator.vibrate(500);
                                vibe_cnt = vibe_cnt + 1;
                                if(vibe_cnt == 10)
                                    sound_flag = false;
                            }
                        });
                        try {
                            Thread.sleep(1000); // 시간지연
                        } catch (InterruptedException e) {
                        }
                    } // end of while
                }
            });
            t2.start(); // 쓰레드 시작
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_alarm_check:
                sound_flag = false;
                this.finish();
                break;
            default:
                break;
        }
    }
}
