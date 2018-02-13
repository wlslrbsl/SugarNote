package com.isens.sugarnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Activity ac;
    private View view;

    private Dialog dialog_setting;

    private SeekBar bright_sb, sound_sb, vibrate_sb;

    private FragmentInterActionListener listener;

    private Button btn_google, btn_wifi, btn_alarm, btn_device, btn_help, btn_navi_center, btn_navi_right, btn_navi_left;

    private TextView tv_google_log;

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

        btn_google = (Button) view.findViewById(R.id.btn_google);
        btn_wifi = (Button) view.findViewById(R.id.btn_wifi);
        btn_alarm = (Button) view.findViewById(R.id.btn_alarm);
        btn_device = (Button) view.findViewById(R.id.btn_device);
        btn_help = (Button) view.findViewById(R.id.btn_help);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_save);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_cancel);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_init);

        btn_google.setOnClickListener(this);
        btn_wifi.setOnClickListener(this);
        btn_alarm.setOnClickListener(this);
        btn_device.setOnClickListener(this);
        btn_help.setOnClickListener(this);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);

        tv_google_log = (TextView) view.findViewById(R.id.tv_google_log);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_google:
                /*Intent intent_google = new Intent(ac, GoogleActivity.class);
                startActivity(intent_google);*/

                break;

            case R.id.btn_wifi:
                WifiDialog dialog = new WifiDialog(ac);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_wifi);
                dialog.show();
                break;

            case R.id.btn_alarm:
                Intent intent_alarm = new Intent(ac, AlarmActivity.class);
                startActivity(intent_alarm);
                break;

            case R.id.btn_device:

                dialog_setting = new Dialog(ac);
                dialog_setting.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_setting.setContentView(R.layout.dialog_setting);

                bright_sb = (SeekBar) dialog_setting.findViewById(R.id.seekbar_bright);
                sound_sb = (SeekBar) dialog_setting.findViewById(R.id.seekbar_sound);
                vibrate_sb = (SeekBar) dialog_setting.findViewById(R.id.seekbar_vibrate);

                WindowManager.LayoutParams params = ac.getWindow().getAttributes();
                params.screenBrightness = ac.getWindow().getAttributes().screenBrightness;
                float init_progress = params.screenBrightness * 100;
                bright_sb.setProgress((int) init_progress);

                bright_sb.setOnSeekBarChangeListener(this);
                sound_sb.setOnSeekBarChangeListener(this);
                vibrate_sb.setOnSeekBarChangeListener(this);

                Button btn_setting_save = (Button) dialog_setting.findViewById(R.id.btn_setting_save);
                btn_setting_save.setOnClickListener(this);

                dialog_setting.show();
                break;

            case R.id.btn_help:
                Toast.makeText(ac, "도움말 추가 예정", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_setting_save:
                dialog_setting.dismiss();
                break;

            case R.id.btn_navi_center:
                Toast.makeText(ac, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                listener.setFrag("HOME");
                break;

            case R.id.btn_navi_right:
                Toast.makeText(ac, "설정 초기화", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_navi_left:
                listener.setFrag("HOME");
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

            WindowManager.LayoutParams params = ac.getWindow().getAttributes();
            params.screenBrightness = (float) progress / 100;
            ac.getWindow().setAttributes(params);
        }

        if (seekBar == sound_sb)
            Toast.makeText(ac, "구현 예정", Toast.LENGTH_SHORT).show();

        if (seekBar == vibrate_sb)
            Toast.makeText(ac, "구현 예정", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
