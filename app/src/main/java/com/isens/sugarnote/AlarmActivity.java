package com.isens.sugarnote;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_alarm_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm);

        btn_alarm_check = (Button) findViewById(R.id.btn_alarm_check);
        btn_alarm_check.setOnClickListener(this);

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_alarm_check:
                this.finish();
                break;
            default:
                break;
        }
    }
}
