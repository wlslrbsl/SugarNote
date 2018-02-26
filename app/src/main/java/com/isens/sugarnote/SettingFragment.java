package com.isens.sugarnote;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, GoogleApiClient.ConnectionCallbacks {

    private Activity ac;
    private View view;

    private SeekBar bright_sb, sound_sb, vibrate_sb;
    private FragmentInterActionListener listener;
    private Button btn_navi_center, btn_navi_right, btn_navi_left, btn_setting_init;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

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

        bright_sb = (SeekBar) view.findViewById(R.id.seekbar_bright);
        sound_sb = (SeekBar) view.findViewById(R.id.seekbar_sound);
        vibrate_sb = (SeekBar) view.findViewById(R.id.seekbar_vibrate);

        WindowManager.LayoutParams params = ac.getWindow().getAttributes();
        params.screenBrightness = ac.getWindow().getAttributes().screenBrightness;
        float init_progress = params.screenBrightness * 100;
        bright_sb.setProgress((int) init_progress);

        bright_sb.setOnSeekBarChangeListener(this);
        sound_sb.setOnSeekBarChangeListener(this);
        vibrate_sb.setOnSeekBarChangeListener(this);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_home);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_wifi);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_info);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

                /*if (dbHelper == null)
                    dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
                db = dbHelper.getWritableDatabase();

                dbHelper.clear_db();*/


            case R.id.btn_navi_center:
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

        if (seekBar == sound_sb);


        if (seekBar == vibrate_sb);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("JJ", "connected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
