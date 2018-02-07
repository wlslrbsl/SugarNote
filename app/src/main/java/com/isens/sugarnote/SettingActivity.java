package com.isens.sugarnote;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {

    BloodGlucoseMonitor _bloodGlucoseMonitor;
    private int _bgm_value = 0;
    private int _bgm_status = 0;

    CustomAdapterSetting customAdapterSetting;
    Dialog dialog_setting;

    private SeekBar bright_sb, sound_sb, vibrate_sb;

    private LinearLayout home_btn;

    private ListView lv_setting;
    private TextView title;
    private Button btn_setting_save;

    private float init_progress = 65;

    private final BloodGlucoseMonitorCallBack _bgm_callBack = new BloodGlucoseMonitorCallBack() {
        @Override
        public void bgmcallBackMethod(String str, int status, int value) {
            _bgm_status = status;
            _bgm_value = value;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkBGMStatus();
                        }
                    });
                }
            }).start();
        }

        @Override
        public void bgmBootLoadercallBackMethod(String str, int status, BgmBootLoader bootloader) {
        }
    };

    private void checkBGMStatus() {
        try {
            switch (_bgm_status) {
                case BloodGlucoseMonitor.BGM_STATUS_INSERT_STRIP:

                    Intent intent = new Intent(this, MeasureActivity.class);
                    intent.putExtra("Strip", true);
                    intent.putExtra("BGM_value", _bgm_value);
                    intent.putExtra("BGM_status", _bgm_status);
                    startActivity(intent);
                    finish();

                    break;

                case BloodGlucoseMonitor.BGM_STATUS_OUT_STRIP:
                case BloodGlucoseMonitor.BGM_STATUS_DROP_BLOOD:
                case BloodGlucoseMonitor.BGM_STATUS_PROCESS_START:
                case BloodGlucoseMonitor.BGM_STATUS_RESULT_TEMPERATURE:
                case BloodGlucoseMonitor.BGM_STATUS_RESULT_GLUCOSE:
                case BloodGlucoseMonitor.BGM_STATUS_RESULT_CONTROLSOLUTION:
                case BloodGlucoseMonitor.BGM_STATUS_RESULT_CURRENT:
                case BloodGlucoseMonitor.BGM_STATUS_RESULT_KETONE:
                case BloodGlucoseMonitor.BGM_STATUS_RESULT_KETONE_CS:
                case BloodGlucoseMonitor.BGM_STATUS_ERROR:
                case BloodGlucoseMonitor.BGM_STATUS_PARSE_ERROR:
                    if (_bgm_status == BloodGlucoseMonitor.BGM_STATUS_RAWDATA) {
                        _bgm_status = BloodGlucoseMonitor.BGM_STATUS_PROCESS_START;
                    }

                    Intent intent2 = new Intent(this, MeasureActivity.class);
                    intent2.putExtra("Strip", true);
                    intent2.putExtra("BGM_value", _bgm_value);
                    intent2.putExtra("BGM_status", _bgm_status);
                    startActivity(intent2);
                    finish();

            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        home_btn = (LinearLayout) findViewById(R.id.btn_home);
        title = (TextView) findViewById(R.id.title_txt);
        title.setText("설 정");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF17AC29));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        _bloodGlucoseMonitor = BloodGlucoseMonitor.getInstance();
        BloodGlucoseMonitor.setCallbackInterface(_bgm_callBack);
        _bloodGlucoseMonitor.enableBGM(_bloodGlucoseMonitor.BGM_INT_SWITCH);

        home_btn.setOnClickListener(this);

        lv_setting = (ListView) findViewById(R.id.lv_setting);
        lv_setting.setOnItemClickListener(this);

        view_list();


    }


    private void view_list() {
        customAdapterSetting = new CustomAdapterSetting();

        customAdapterSetting.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_google), "구글 드라이브 연동");
        customAdapterSetting.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_wifi), "WiFi 설정");
        customAdapterSetting.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_alarm), "알람 설정");
        customAdapterSetting.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_device), "기기 설정");
        customAdapterSetting.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_help), "도움말");

        lv_setting.setAdapter(customAdapterSetting);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                Intent intent_google = new Intent(getApplicationContext(), GoogleActivity.class);
                startActivity(intent_google);
                break;
            case 1:
                WifiDialog dialog = new WifiDialog(SettingActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_wifi);
                dialog.show();
                break;
            case 2:
                Intent intent_alarm = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent_alarm);
                break;
            case 3:
                dialog_setting = new Dialog(this);
                dialog_setting.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_setting.setContentView(R.layout.dialog_setting);

                bright_sb = (SeekBar) dialog_setting.findViewById(R.id.seekbar_bright);
                sound_sb = (SeekBar) dialog_setting.findViewById(R.id.seekbar_sound);
                vibrate_sb = (SeekBar) dialog_setting.findViewById(R.id.seekbar_vibrate);

                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = getWindow().getAttributes().screenBrightness;
                init_progress = params.screenBrightness * 100;
                bright_sb.setProgress((int) init_progress);

                bright_sb.setOnSeekBarChangeListener(this);
                sound_sb.setOnSeekBarChangeListener(this);
                vibrate_sb.setOnSeekBarChangeListener(this);

                btn_setting_save = (Button) dialog_setting.findViewById(R.id.btn_setting_save);
                btn_setting_save.setOnClickListener(this);

                dialog_setting.show();
                break;

            case 4:
                Toast.makeText(this, "도움말 추가 예정", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_home:
                finish();
                break;
            case R.id.btn_setting_save:
                dialog_setting.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (seekBar == bright_sb) {
            if (progress < 10) {
                progress = 10;
                bright_sb.setProgress(progress);
            }
            //status.setText("밝기 수준 : " + progress);

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.screenBrightness = (float) progress / 100;
            getWindow().setAttributes(params);
        }

        if (seekBar == sound_sb)
            Toast.makeText(this, "구현 예정", Toast.LENGTH_SHORT).show();

        if (seekBar == vibrate_sb)
            Toast.makeText(this, "구현 예정", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
