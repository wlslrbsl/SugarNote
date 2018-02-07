package com.isens.sugarnote;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;

public class MainActivity extends AppCompatActivity implements FragmentInterActionListener {

    private HomeFragment homeFragment;
    private UserFragment userFragment;
    private MeasureFragment measureFragment;
    private ReportChartFragment reportChartFragment;
    private ReportGraphFragment reportGraphFragment;
    private CalendarFragment calendarFragment;

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_root, editor_user;

    private FragmentManager fm;
    private FragmentTransaction tran;

    private BloodGlucoseMonitor _bloodGlucoseMonitor;

    // BGM CB register

    private final BloodGlucoseMonitorCallBack _bgm_callBack = new BloodGlucoseMonitorCallBack() {
        @Override
        public void bgmcallBackMethod(String str, int status, int value) {

            if (status == BloodGlucoseMonitor.BGM_STATUS_INSERT_STRIP) {
                MyApplication.setIsStrip(true);
                setFrag("MEASURE");
            }
        }

        @Override
        public void bgmBootLoadercallBackMethod(String str, int status, BgmBootLoader bootloader) {
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        userFragment = new UserFragment();
        measureFragment = new MeasureFragment();
        reportChartFragment = new ReportChartFragment();
        reportGraphFragment = new ReportGraphFragment();
        calendarFragment = new CalendarFragment();

        setFrag("HOME");

    }

    @Override
    public void setFrag(String state) {

        _bloodGlucoseMonitor = BloodGlucoseMonitor.getInstance();
        BloodGlucoseMonitor.setCallbackInterface(_bgm_callBack);
        _bloodGlucoseMonitor.enableBGM(_bloodGlucoseMonitor.BGM_INT_SWITCH);

        fm = getSupportFragmentManager();
        tran = fm.beginTransaction();

        switch (state) {
            case "HOME":
                tran.replace(R.id.fragment_container_main, homeFragment);
                tran.commit();
                break;
            case "MEASURE":
                tran.replace(R.id.fragment_container_main, measureFragment);
                tran.commit();
                break;
            case "CHART":
                tran.replace(R.id.fragment_container_main, reportChartFragment);
                tran.commit();
                break;
            case "GRAPH":
                tran.replace(R.id.fragment_container_main, reportGraphFragment);
                tran.commit();
                break;
            case "USER":
                tran.replace(R.id.fragment_container_main, userFragment);
                tran.commit();
                break;
            case "CALENDAR":
                tran.replace(R.id.fragment_container_main, calendarFragment);
                tran.commit();
                break;
            default:
                break;
        }
    }
}