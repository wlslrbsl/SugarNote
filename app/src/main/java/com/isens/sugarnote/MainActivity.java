package com.isens.sugarnote;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.WindowManager;


import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity implements FragmentInterActionListener {

    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;

    private HomeFragment homeFragment;
    private UserFragment userFragment;
    private MeasureFragment measureFragment;
    private ReportStatisticsFragment reportStatisticsFragment;
    private ReportGraphFragment reportGraphFragment;
    private CalendarFragment calendarFragment;
    private SettingFragment settingFragment;

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
        reportGraphFragment = new ReportGraphFragment();
        reportStatisticsFragment = new ReportStatisticsFragment();
        calendarFragment = new CalendarFragment();
        settingFragment =new SettingFragment();

        try {
            kakaoLink = KakaoLink.getKakaoLink(MainActivity.this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }

        String keynum = getKeyHash(this);

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

}