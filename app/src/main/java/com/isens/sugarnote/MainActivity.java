package com.isens.sugarnote;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.kakaolink.internal.LinkObject;
import com.kakao.util.KakaoParameterException;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.kakao.util.helper.Utility.getPackageInfo;

public class MainActivity extends AppCompatActivity implements FragmentInterActionListener, View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private KakaoLink kakaoLink;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    private DBHelper dbHelper, dbHelper2;
    private SQLiteDatabase db, db2;
    private String dbfilepath;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private boolean isAPÏConnected;
    public GoogleApiClient mGoogleApiClient;
    public DriveId mDriveId;
    private boolean download_complete = false;

    private HomeFragment homeFragment;
    private UserFragment userFragment;
    private MeasureFragment measureFragment;
    private ReportChartFragment reportChartFragment;
    private ReportStatisticsFragment reportStatisticsFragment;
    private ReportGraphFragment reportGraphFragment;
    private CalendarFragment calendarFragment;
    private Dialog dialog_Sync;
    private TextView tv_dialog, btn_dialog_ok, btn_dialog_cancel;
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
        reportStatisticsFragment = new ReportStatisticsFragment();
        calendarFragment = new CalendarFragment();

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
            case "CHART":
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
            case "LIST":
                try {
                    KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
                    KakaoTalkLinkMessageBuilder messageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                    messageBuilder.addText("카카오톡으로 공유해요3.");
                    kakaoLink.sendMessage(messageBuilder,getApplicationContext());
                } catch (KakaoParameterException e) {
                    e.printStackTrace();
                }

//                String s = "카카오 API TEST";
//
//                final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder
//                        = kakaoLink.createKakaoTalkLinkMessageBuilder();
//                try {
//                    kakaoTalkLinkMessageBuilder.addText(s);//링크 객체에 날씨정보가 담긴 문자 넣기
//                } catch (KakaoParameterException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);// 메시지 전송
//                } catch (KakaoParameterException e) {
//                    e.printStackTrace();
//                }
//
//                TextTemplate params = TextTemplate.newBuilder("Text", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()).setButtonTitle("This is button").build();
//
//                KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
//                    @Override
//                    public void onFailure(ErrorResult errorResult) {
//                        Logger.e(errorResult.toString());
//                    }
//
//                    @Override
//                    public void onSuccess(KakaoLinkResponse result) {
//                    }
//                });


                break;

            case "SYNC":
                dialog_Sync = new Dialog(this);
                dialog_Sync.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_Sync.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_Sync.findViewById(R.id.tv_dialog);
                tv_dialog.setText("구글 드라이브 데이터 동기화를\n진행하시겠습니까?");

                btn_dialog_ok = (TextView) dialog_Sync.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_Sync.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                dialog_Sync.show();

                break;

            case "SETTING":
                Intent intent_setting = new Intent(this, SettingActivity.class);
                startActivity(intent_setting);
                break;

            default:
                break;
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_dialog_ok:

                connectAPIClient();

                Query query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, "GLUCOSEDATA.db"))
                        .build();
                Log.i("JJ", "sync 버튼 클릭");
                Drive.DriveApi.query(getGoogleApiClient(), query)
                        .setResultCallback(metadataCallback);

                dialog_Sync.dismiss();

                break;

            case R.id.btn_dialog_cancel:

                dialog_Sync.dismiss();

                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("JJ", "onAcitivity Result");

        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("JJ", "GoogleApiClient connection failed: " + result.toString());

        //login_btn.setText("로그인");
        isAPÏConnected = false;
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

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("JJ", "on connected");
        isAPÏConnected = true;
        //text_account.setText(getAccountName());

        //login_btn.setText("계정 변경");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("JJ", "GoogleApiClient connection suspended");
    }

    public void connectAPIClient() {
        Log.i("JJ", "conncect api client");
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();

    }

    public void merge_db(SQLiteDatabase mdb, SQLiteDatabase mdb2) {
        Log.i("JJ", "db merge");
        Cursor cursor = mdb2.rawQuery("SELECT * FROM GLUCOSEDATA", null);
        while (cursor.moveToNext()) {
            Cursor curChk = mdb.rawQuery("SELECT * FROM GLUCOSEDATA WHERE create_at= '" + cursor.getString(1) + "' ;", null);
            if (!curChk.moveToFirst())
                mdb.execSQL("INSERT INTO GLUCOSEDATA VALUES( null, '" + cursor.getString(1) + "', " + cursor.getInt(2) + ", '" + cursor.getString(3) + "');");
        }
    }

    final private ResultCallback<DriveApi.MetadataBufferResult>
            metadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }
                    Log.i("JJ", "Meta call back");
                    MetadataBuffer mdbf = null;
                    mdbf = result.getMetadataBuffer();
                    int iCount = mdbf.getCount();

                    if (iCount == 0) {
                        if (dbHelper == null)
                            dbHelper = new DBHelper(getApplication(), "GLUCOSEDATA.db", null, 1);
                        db = dbHelper.getWritableDatabase();
                        db.getPath();
                        dbfilepath = db.getPath();
                        File dbFile = new File(dbfilepath);

                        saveToDrive(Drive.DriveApi.getRootFolder(getGoogleApiClient()), "GLUCOSEDATA.db","application/x-sqlite3", dbFile);
                    } else {
                        mDriveId = mdbf.get(0).getDriveId();
                        DriveFile file = mDriveId.asDriveFile();

                        downloadFromDrive(file);

                    }

                }
            };


    public GoogleApiClient getGoogleApiClient() {
        Log.i("JJ", "get google api");
        return mGoogleApiClient;

    }

    /* 구글 드라브에 파일 없을 경우 파일 업로드*/
    void saveToDrive(final DriveFolder pFldr, final String titl,
                     final String mime, final File file) {
        Log.i("JJ", "save to drive");
        if (getGoogleApiClient() != null && pFldr != null && titl != null && mime != null && file != null)
            try {
                // create content from file
                Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        DriveContents cont = driveContentsResult != null && driveContentsResult.getStatus().isSuccess() ?
                                driveContentsResult.getDriveContents() : null;

                        // write file to content, chunk by chunk
                        if (cont != null) try {
                            OutputStream oos = cont.getOutputStream();
                            if (oos != null) try {
                                InputStream is = new FileInputStream(file);
                                byte[] buf = new byte[4096];
                                int c;
                                while ((c = is.read(buf, 0, buf.length)) > 0) {
                                    oos.write(buf, 0, c);
                                    oos.flush();
                                }
                            } finally {
                                oos.close();
                            }

                            // content's COOL, create metadata
                            MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(titl).setMimeType(mime).build();

                            // now create file on GooDrive
                            pFldr.createFile(getGoogleApiClient(), meta, cont).setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {

                                @Override
                                public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                                    if (driveFileResult != null && driveFileResult.getStatus().isSuccess()) {
                                        DriveFile dFil = driveFileResult != null && driveFileResult.getStatus().isSuccess() ?
                                                driveFileResult.getDriveFile() : null;
                                        showMessage("DB Upload");
                                        if (dFil != null) {
                                            // BINGO , file uploaded
                                            dFil.getMetadata(getGoogleApiClient()).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                                @Override
                                                public void onResult(DriveResource.MetadataResult metadataResult) {
                                                    if (metadataResult != null && metadataResult.getStatus().isSuccess()) {
                                                        mDriveId = metadataResult.getMetadata().getDriveId();

                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        showMessage("Error");
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /* 구글 드라브에 파일 있을 경우 파일 다운로드*/
    private void downloadFromDrive(final DriveFile file) {

        if (dbHelper == null) dbHelper = new DBHelper(getApplication(), "GLUCOSEDATA.db", null, 1);
        db = dbHelper.getWritableDatabase();

        file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showMessage(" ERROR ");
                            return;
                        }

                        // DriveContents object contains pointers
                        // to the actual byte stream
                        DriveContents contents = result.getDriveContents();
                        InputStream input = contents.getInputStream();


                        try {
                            String test_debug = db.getPath();
                            //File file = new File(db.getPath());
                            File file_db = new File("/data/data/com.isens.sugarnote/databases/GLUCOSEDATA2.db");
                            OutputStream output = new FileOutputStream(file_db);
                            try {
                                try {
                                    byte[] buffer = new byte[4 * 1024]; // or other buffer size
                                    int read;

                                    while ((read = input.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                    }
                                    output.flush();
                                } finally {
                                    output.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                input.close();
                                showMessage("DB Download");
                                download_complete = true;
                                Log.i("JJ", "다운로드 완료");
                                if (download_complete) {
                                    download_complete = false;

                                    if (dbHelper2 == null)
                                        dbHelper2 = new DBHelper(getApplication(), "GLUCOSEDATA2.db", null, 1);
                                    db2 = dbHelper2.getWritableDatabase();
                                    merge_db(db, db2);

                                    db2.getPath();
                                    dbfilepath = db2.getPath();
                                    File mdbFile = new File(dbfilepath);
                                    mdbFile.delete();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        updateFile(file);
                    }
                });
    }

    /* 구글 드라브에 파일 있을 경우 파일 업데이트*/
    private void updateFile(DriveFile file) {
        Log.i("JJ", "업데이트 파일");

        file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showMessage(" ERROR ");
                            return;
                        }

                        if (dbHelper == null)
                            dbHelper = new DBHelper(getApplication(), "GLUCOSEDATA.db", null, 1);
                        db = dbHelper.getWritableDatabase();
                        db.getPath();
                        dbfilepath = db.getPath();


                        File dbFile = new File(dbfilepath);

                        // DriveContents object contains pointers
                        // to the actual byte stream
                        DriveContents contents = result.getDriveContents();

                        // write file to content, chunk by chunk
                        if (contents != null) try {
                            OutputStream oos = contents.getOutputStream();
                            if (oos != null) try {
                                InputStream is = new FileInputStream(dbFile);
                                byte[] buf = new byte[4096];
                                int c;
                                while ((c = is.read(buf, 0, buf.length)) > 0) {
                                    oos.write(buf, 0, c);
                                    oos.flush();
                                }
                            } finally {
                                oos.close();
                            }
                            contents.commit(mGoogleApiClient, null).setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status result) {
                                    // Handle the response status
                                    showMessage("DB Update");

                                    Log.i("JJ", "Disconnect");
                                    if (mGoogleApiClient != null) {
                                        mGoogleApiClient.disconnect();
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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