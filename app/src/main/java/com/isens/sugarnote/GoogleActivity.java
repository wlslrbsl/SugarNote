package com.isens.sugarnote;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
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

/*import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;*/
import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by jeongjick on 2017-07-11.
 */

public class GoogleActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, View.OnClickListener {

    static final int REQ_ACCPICK = 999;

    private static final String TAG = "MainActivity";
    private DBHelper dbHelper, dbHelper2;
    private SQLiteDatabase db, db2;
    private String dbfilepath;
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private boolean isAPÏConnected;
    public GoogleApiClient mGoogleApiClient;
    public static Context mContext;
    public Button login_btn;
    private boolean download_complete = false;
    public DriveId mDriveId;
    public TextView text_account;
    private LinearLayout home_btn;
    BloodGlucoseMonitor _bloodGlucoseMonitor;
    private int _bgm_value = 0;
    private int _bgm_status = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        home_btn = (LinearLayout) findViewById(R.id.btn_home);
        TextView title = (TextView) findViewById(R.id.title_txt);
        title.setText("구글 드라이브");
        home_btn.setVisibility(View.INVISIBLE);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF17AC29));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_google);

        Log.i("JJ", "onCreate");
        mContext = this;
        //Initialize Google Drive API Client!
        connectAPIClient();

        text_account = (TextView) findViewById(R.id.account_txt);
        login_btn = (Button) findViewById(R.id.btn_login);

        findViewById(R.id.btn_back_home).setOnClickListener(this);
        findViewById(R.id.btn_file_delete).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_file_sync).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_back_home:
                finish();
                break;

            case R.id.btn_file_delete:

                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        /*.addApi(Plus.API)
                        .addScope(Plus.SCOPE_PLUS_PROFILE)*/
                        .build();

                mGoogleApiClient.connect();

                break;

            case R.id.btn_login:
                if (isAPÏConnected == true)
                    mGoogleApiClient.clearDefaultAccountAndReconnect();
                else
                    connectAPIClient();
                break;

            case R.id.btn_file_sync:
//                Query query = new Query.Builder()
//                        .addFilter(Filters.eq(SearchableField.TITLE, "GLUCOSEDATA.db"))
//                        .build();
//                Log.i("JJ", "sync 버튼 클릭");
//                Drive.DriveApi.query(getGoogleApiClient(), query)
//                        .setResultCallback(metadataCallback);

                if (isAPÏConnected == true) {
                    /*String email = Plus.AccountApi.getAccountName(mGoogleApiClient);*//*
                    showMessage(email);*/
                }
                else
                    showMessage("Logout상태");


                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        Log.i("JJ", "onResume");
        super.onResume();
    }

    //Disconnect only when the application is closed!
    @Override
    protected void onDestroy() {
        Log.i("JJ", "on Destroy");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
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
        //text_account.setText("Log out 상태");
        login_btn.setText("로그인");
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

        login_btn.setText("계정 변경");
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
/*                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)*/
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

    final private ResultCallback<MetadataBufferResult>
            metadataCallback = new
            ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
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

                        saveToDrive(Drive.DriveApi.getRootFolder(getGoogleApiClient()), "GLUCOSEDATA.db",
                                "application/x-sqlite3", dbFile);
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
                Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(new ResultCallback<DriveContentsResult>() {
                    @Override
                    public void onResult(DriveContentsResult driveContentsResult) {
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
                .setResultCallback(new ResultCallback<DriveContentsResult>() {
                    @Override
                    public void onResult(DriveContentsResult result) {
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
                .setResultCallback(new ResultCallback<DriveContentsResult>() {
                    @Override
                    public void onResult(DriveContentsResult result) {
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

    private String getAccountName() {

        String accountName = null;

        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        int i = 0;
        for (Account account : list) {
            if (account.type.equalsIgnoreCase("com.google")) {
                accountName = account.name;
                //manager.notifyAll();
                String prev_name = manager.getPreviousName(list[i]);
                i = i + 1;
                break;
            }
        }
        return accountName;
    }


}
