package com.isens.sugarnote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
/*import com.google.android.gms.plus.Plus;*/
import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity implements FragmentInterActionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private HomeFragment homeFragment;
    private UserFragment userFragment;
    private MeasureFragment measureFragment;
    private ReportStatisticsFragment reportStatisticsFragment;
    private ReportGraphFragment reportGraphFragment;
    private CalendarFragment calendarFragment;
    private SettingFragment settingFragment;
    private UserGoalFragment userGoalFragment;
    private AlarmFragment alarmFragment;

    private Time time;

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private FragmentManager fm;
    private FragmentTransaction tran;

    private BloodGlucoseMonitor _bloodGlucoseMonitor;

    private GoogleApiClient mGoogleApiClient;
    private boolean isAPIConnected;

    private String month, date, calendar_head, s_time, sugar, mealoption, Kakaostring;
    private int today_Month, today_Date, today_Year, cursorSize;
    private Calendar calendar;
    private Cursor cursor;
    private long now;
    private String userAccount;
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

        prefs_root = getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        prefs_user = getSharedPreferences(userAccount, 0);

        homeFragment = new HomeFragment();
        userFragment = new UserFragment();
        measureFragment = new MeasureFragment();
        reportGraphFragment = new ReportGraphFragment();
        reportStatisticsFragment = new ReportStatisticsFragment();
        calendarFragment = new CalendarFragment();
        settingFragment = new SettingFragment();
        userGoalFragment = new UserGoalFragment();
        alarmFragment = new AlarmFragment();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

        registerReceiver(timeChangedReceiver, intentFilter);

        try {
            kakaoLink = KakaoLink.getKakaoLink(MainActivity.this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }

        time = new Time();

        String keynum = getKeyHash(this);

        setFrag("HOME");
    }

    private final BroadcastReceiver timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Calendar calendar = Calendar.getInstance();
            long now = System.currentTimeMillis();
            calendar.setTime(new Date(now));

            int today = calendar.get(Calendar.DAY_OF_WEEK); // 1 : sunday
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            String str_hour, str_min;

            if (min < 10) {
                str_min = "0" + min;
            } else {
                str_min = String.valueOf(min);
            }

            if (hour >= 12) {
                if (hour == 12) {
                    str_hour = "PM12";
                } else {
                    if ((hour % 12) < 10) {
                        str_hour = "PM0" + (hour % 12);
                    } else {
                        str_hour = "PM" + (hour % 12);
                    }
                }
            } else {
                if (hour == 0) {
                    str_hour = "AM12";
                } else {
                    if (hour < 10) {
                        str_hour = "AM0" + hour;
                    } else {
                        str_hour = "AM" + hour;
                    }
                }
            }

            int alarm_cnt = prefs_user.getInt("ALARM#", 0);

            for (int i = 0; i < alarm_cnt; i++) {

                String str = prefs_user.getString("ALARM" + String.valueOf(i + 1), null);
                if (str.charAt(13) == '1') {
                    if (str.charAt(today + 5) == '1') {
                        if (str.substring(4, 6).equals(str_min)) {
                            if (str.substring(0, 4).equals(str_hour)) {
                                Intent intent_alarm = new Intent(MainActivity.this, AlarmActivity.class);
                                startActivity(intent_alarm);
                            }
                        }
                    }
                }
            }
        }
    };



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
            case "STATISTICS":
                tran.replace(R.id.fragment_container_main, reportStatisticsFragment);
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
            case "SETTING":
                tran.replace(R.id.fragment_container_main, settingFragment);
                tran.commit();
                break;
            case "SHARE":

                get_Today_Glucose_Data();

                try {
                    KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
                    kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                    kakaoTalkLinkMessageBuilder.addText(Kakaostring);
                    kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), this);

                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }
                break;

            case "GOAL":
                tran.replace(R.id.fragment_container_main, userGoalFragment);
                tran.commit();
                break;
            case "ALARM":
                tran.replace(R.id.fragment_container_main, alarmFragment);
                tran.commit();
            default:
                break;
        }
    }

    private void get_Today_Glucose_Data(){

        if (dbHelper == null)
            dbHelper = new DBHelper(this, "GLUCOSEDATA.db", null, 1);

        db = dbHelper.getWritableDatabase();

        now = System.currentTimeMillis();
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(now));

        today_Month = calendar.get(Calendar.MONTH);
        today_Date = calendar.get(Calendar.DATE);
        today_Year = calendar.get(Calendar.YEAR);

        if (today_Month  < 10)
            month = "0" + (today_Month  + 1);
        else
            month = String.valueOf(today_Month  + 1);
        if (today_Date < 10)
            date = "0" + today_Date;
        else
            date = String.valueOf(today_Date);

        calendar_head = today_Year + "/ " + month + "/ " + date;

        String querry = "SELECT * FROM GLUCOSEDATA WHERE create_at LIKE '%" + calendar_head + "%';";
        Log.d("querry", querry);
        cursor = db.rawQuery(querry, null);

        cursorSize = cursor.getCount();

        Kakaostring = calendar_head;

        while (cursor.moveToNext()) {
            s_time = cursor.getString(1);
            sugar = String.valueOf(cursor.getInt(2));
            mealoption = cursor.getString(3);
            Kakaostring = Kakaostring +"\n" + s_time.substring(14,22) + ",  " + mealoption + ",  " + sugar + "mg/dL";
        }

        if (cursorSize == 0)
            Kakaostring = Kakaostring + "\nNo Data";

    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {

            }
        }
        return null;
    }

    @Override
    public void connectAPIClient() {
        Log.i("JJ", "try connect api client");
        if (true) {
            Log.i("JJ", "new api client");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    /*.addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)*/
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();
        }
        mGoogleApiClient.connect();
        Log.i("JJ", "connect api client");
    }

    @Override
    public GoogleApiClient getAPIClient() {
        return mGoogleApiClient;
    }

    @Override
    public boolean getIsAPIConnected() {
        return isAPIConnected;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("JJ", "on connected");
        isAPIConnected = true;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("JJ", "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("JJ", "GoogleApiClient connection failed: " + result.toString());

        isAPIConnected = false;
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        // Called typically when the app is not yet authorized, and authorization dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, 1);
//            startActivityForResult(AccountPicker.newChooseAccountIntent(null, null,
//                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null), REQ_ACCPICK);

        } catch (IntentSender.SendIntentException e) {
            Log.e("JJ", "Exception while starting resolution activity", e);
        }
    }
}