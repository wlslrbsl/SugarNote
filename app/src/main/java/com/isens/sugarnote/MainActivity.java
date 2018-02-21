package com.isens.sugarnote;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
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

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity implements FragmentInterActionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;

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

    private FragmentManager fm;
    private FragmentTransaction tran;

    private BloodGlucoseMonitor _bloodGlucoseMonitor;

    private GoogleApiClient mGoogleApiClient;
    private boolean isAPIConnected;

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
            /*Intent intent_alarm = new Intent(MainActivity.this, TestActivity.class);
            startActivity(intent_alarm);*/
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

//                final String str = "공유할 내용";
//                final String imgSrc = "http://cfile3.uf.tistory.com/image/215BCC41578ACAE31DFDBC";
//                final String siteUrl = "등록한 url";
//
//                try {
//                    kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
//                    kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//                    kakaoTalkLinkMessageBuilder.addText(str);
//                    kakaoTalkLinkMessageBuilder.addImage(imgSrc, 300, 200);
//                    kakaoTalkLinkMessageBuilder.addWebButton("자세히 보기", siteUrl);
//                    kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), this);
//                } catch (KakaoParameterException e) {
//                    e.printStackTrace();
//                }

                try {
                    KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
                    kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                    kakaoTalkLinkMessageBuilder.addText("카카오톡으로 공유해요.");
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