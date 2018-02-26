package com.isens.sugarnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveClient;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, ListView.OnItemClickListener {

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private Activity ac;
    private View view;

    private CustomAdapterSetting listviewadapter_power;

    private ListView listview;

    private Dialog dialog_logout, dialog_createDB, dialog_deleteLOG, dialog_Sync, dialog_power;

    private WifiManager wifi;
    private FragmentInterActionListener listener;
    private int progress_interval;
    private LinearLayout btn_setting, btn_calendar, btn_measure, btn_report, btn_new;
    private Button btn_navi_right, btn_navi_center, btn_navi_left;
    private ProgressBar sync_progbar;
    private TextView tv_dialog, btn_dialog_ok, btn_dialog_cancel;

    private String userAccount;

    private DBHelper dbHelper, dbHelper2;
    private SQLiteDatabase db, db2;
    private int progress_val = 0;
    private boolean download_complete = false;
    private boolean createDBFlag = false;
    private boolean deleteLogFlag = false;
    private boolean syncFlag = false;
    private Handler handler = new Handler(); // Thread 에서 화면에 그리기 위해서 필요

    private String dbfilepath;
    private static String dbfilepath2;
    public DriveId mDriveId;

    private static Drive service;

    public HomeFragment() {
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
        view = inflater.inflate(R.layout.fragment_home, container, false);

        prefs_root = ac.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        prefs_user = ac.getSharedPreferences(userAccount, 0);
        editor_user = prefs_user.edit();

        listviewadapter_power = new CustomAdapterSetting(ac);
        listviewadapter_power.addItem("전원 끄기");
        listviewadapter_power.addItem("재부팅");
        listviewadapter_power.addItem("로그아웃");

        btn_new = (LinearLayout) view.findViewById(R.id.btn_kakao);
        btn_measure = (LinearLayout) view.findViewById(R.id.btn_measure);
        btn_report = (LinearLayout) view.findViewById(R.id.btn_report);
        btn_setting = (LinearLayout) view.findViewById(R.id.btn_setting);
        btn_calendar = (LinearLayout) view.findViewById(R.id.btn_calendar);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_power);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_user);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_sync);
        btn_navi_right.setEnabled(true);
        btn_navi_left.setEnabled(true);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);
        btn_new.setOnClickListener(this);
        btn_measure.setOnClickListener(this);
        btn_report.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_calendar.setOnClickListener(this);

        wifi = (WifiManager) ac.getApplicationContext().getSystemService(ac.WIFI_SERVICE);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_kakao:
                listener.setFrag("SHARE");
                break;

            case R.id.btn_measure:
                listener.setFrag("MEASURE");
                break;

            case R.id.btn_report:
                listener.setFrag("STATISTICS");
                break;

            case R.id.btn_setting:
                listener.setFrag("SETTING");
                break;

            case R.id.btn_calendar:
                listener.setFrag("CALENDAR");
                break;

            case R.id.btn_navi_center:
                dialog_logout = new Dialog(ac);
                dialog_logout.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_logout.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_logout.findViewById(R.id.tv_dialog);
                tv_dialog.setText("로그인 화면으로 돌아가시겠습니까?");

                dialog_power = new Dialog(ac);
                dialog_power.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_power.setContentView(R.layout.dialog_power);

                listview = (ListView) dialog_power.findViewById(R.id.Listview_Power);
                listview.setAdapter(listviewadapter_power);

                listview.setOnItemClickListener(this);

                dialog_power.show();

                break;

            case R.id.btn_navi_left:
                listener.setFrag("USER");
                break;

            case R.id.btn_navi_right:

                if (wifi.isWifiEnabled()) {
                    syncFlag = true;
                    dialog_Sync = new Dialog(ac);
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

                } else {

                    WifiDialog dialog = new WifiDialog(ac);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_wifi);
                    dialog.show();

                }

                break;

            case R.id.btn_dialog_ok:
                if (syncFlag) {
                    syncFlag = false;
                    sync_progbar = (ProgressBar) dialog_Sync.findViewById(R.id.sync_progress);
                    tv_dialog.setText("구글 드라이브 데이터 동기화중...");
                    btn_dialog_ok.setVisibility(View.INVISIBLE);
                    btn_dialog_cancel.setVisibility(View.INVISIBLE);
                    sync_progbar.setVisibility(View.VISIBLE);

                    listener.connectAPIClient();

                    Query query = new Query.Builder()
                            .addFilter(Filters.eq(SearchableField.TITLE, "GLUCOSEDATA.db"))
                            .build();
                    Log.i("JJ", "sync 버튼 클릭");

                    Drive.DriveApi.query(listener.getAPIClient(), query)
                            .setResultCallback(metadataCallback);


                } else {
                    ac.finish();
                }

                break;

            case R.id.btn_dialog_cancel:
                if (syncFlag) {
                    syncFlag = false;
                    dialog_Sync.dismiss();
                } else {
                    dialog_logout.dismiss();
                }
                break;
        }
    }

    /* 구글 드라이브에 파일 없을 경우 파일 업로드*/
    void saveToDrive(final DriveFolder pFldr, final String titl,
                     final String mime, final File file) {
        Log.i("JJ", "save to drive");
        if (listener.getAPIClient() != null && pFldr != null && titl != null && mime != null && file != null)
            try {
                // create content from file
                Drive.DriveApi.newDriveContents(listener.getAPIClient()).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
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
                            pFldr.createFile(listener.getAPIClient(), meta, cont).setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {

                                @Override
                                public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                                    if (driveFileResult != null && driveFileResult.getStatus().isSuccess()) {
                                        DriveFile dFil = driveFileResult != null && driveFileResult.getStatus().isSuccess() ?
                                                driveFileResult.getDriveFile() : null;
                                        //showMessage("DB Upload");
                                        if (dFil != null) {
                                            // BINGO , file uploaded
                                            dFil.getMetadata(listener.getAPIClient()).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                                @Override
                                                public void onResult(DriveResource.MetadataResult metadataResult) {
                                                    if (metadataResult != null && metadataResult.getStatus().isSuccess()) {
                                                        mDriveId = metadataResult.getMetadata().getDriveId();
                                                    }
                                                    Log.i("JJ", "Disconnect");
                                                    if (listener.getAPIClient() != null) {
                                                        listener.getAPIClient().disconnect();
                                                    }
                                                    tv_dialog.setText("데이터 동기화가 완료되었습니다.");
                                                    sync_progbar.setVisibility(View.INVISIBLE);
                                                    db.close();
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            dialog_Sync.dismiss();
                                                        }
                                                    }, 500);
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

    /* 구글 드라이브에 파일 있을 경우 파일 다운로드*/
    private void downloadFromDrive(final DriveFile file) {

        if (dbHelper == null) dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
        db = dbHelper.getWritableDatabase();

        file.open(listener.getAPIClient(), DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            int status_code = result.getStatus().getStatusCode();
                            if (status_code == 1502) {
                                db.getPath();
                                dbfilepath = db.getPath();
                                dbfilepath2 = db.getPath();
                                File dbFile = new File(dbfilepath);

                                saveToDrive(Drive.DriveApi.getRootFolder(listener.getAPIClient()), "GLUCOSEDATA.db", "application/x-sqlite3", dbFile);
                            } else {
                                showMessage(" ERROR jj ");
                            }
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

                                    int download_length = input.available() / buffer.length;
                                    progress_interval = 40 / download_length;
                                    while ((read = input.read(buffer)) != -1) {
                                        output.write(buffer, 0, read);
                                        progress_val = progress_val + progress_interval;
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
                                //showMessage("DB Download");
                                download_complete = true;
                                Log.i("JJ", "다운로드 완료");
                                if (download_complete) {
                                    download_complete = false;

                                    if (dbHelper2 == null)
                                        dbHelper2 = new DBHelper(ac, "GLUCOSEDATA2.db", null, 1);
                                    db2 = dbHelper2.getWritableDatabase();
                                    merge_db(db, db2);
                                    db2.close();
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

    /* 구글 드라이브에 파일 있을 경우 파일 업데이트*/
    private void updateFile(DriveFile file) {
        Log.i("JJ", "업데이트 파일");

        file.open(listener.getAPIClient(), DriveFile.MODE_WRITE_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult result) {
                        if (!result.getStatus().isSuccess()) {
                            showMessage(" ERROR ");
                            return;
                        }

                        if (dbHelper == null)
                            dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
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

                                int update_length = is.available() / buf.length;
                                progress_interval = 40 / update_length;
                                while ((c = is.read(buf, 0, buf.length)) > 0) {
                                    oos.write(buf, 0, c);
                                    oos.flush();
                                    progress_val = progress_val + progress_interval;

                                }
                            } finally {
                                oos.close();
                            }
                            contents.commit(listener.getAPIClient(), null).setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status result) {
                                    // Handle the response status
                                    //showMessage("DB Update");

                                    Log.i("JJ", "Disconnect");
                                    if (listener.getAPIClient() != null) {
                                        listener.getAPIClient().disconnect();
                                    }
                                    tv_dialog.setText("데이터 동기화가 완료되었습니다.");
                                    sync_progbar.setVisibility(View.INVISIBLE);
                                    db.close();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog_Sync.dismiss();
                                        }
                                    }, 500);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void showMessage(String message) {
        Toast.makeText(ac, message, Toast.LENGTH_LONG).show();
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
                            dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
                        db = dbHelper.getWritableDatabase();
                        db.getPath();
                        dbfilepath = db.getPath();
                        dbfilepath2 = db.getPath();
                        File dbFile = new File(dbfilepath);

                        saveToDrive(Drive.DriveApi.getRootFolder(listener.getAPIClient()), "GLUCOSEDATA.db", "application/x-sqlite3", dbFile);
                    } else {
                        String file_name = "";
                        file_name = mdbf.get(0).getTitle();
                        mDriveId = mdbf.get(0).getDriveId();
                        DriveFile file = mDriveId.asDriveFile();

                        downloadFromDrive(file);

                    }

                }
            };

    public void merge_db(SQLiteDatabase mdb, SQLiteDatabase mdb2) {
        Log.i("JJ", "db merge");
        Cursor cursor = mdb2.rawQuery("SELECT * FROM GLUCOSEDATA", null);
        while (cursor.moveToNext()) {
            Cursor curChk = mdb.rawQuery("SELECT * FROM GLUCOSEDATA WHERE create_at= '" + cursor.getString(1) + "' ;", null);
            if (!curChk.moveToFirst()) {
                mdb.execSQL("INSERT INTO GLUCOSEDATA VALUES( null, '" + cursor.getString(1) + "', " + cursor.getInt(2) + ", '" + cursor.getString(3) + "');");
            }
            curChk.close();
        }
        cursor.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        dialog_power.dismiss();

        if (i == 0) {
            showMessage("test");
        }
        if (i == 1) {
            try {
                Process p = Runtime.getRuntime().exec("su");
                OutputStream os = p.getOutputStream();
                os.write("reboot -p\n".getBytes());
                os.flush();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (i == 2) {

            dialog_logout = new Dialog(ac);
            dialog_logout.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_logout.setContentView(R.layout.dialog_default);

            tv_dialog = (TextView) dialog_logout.findViewById(R.id.tv_dialog);
            tv_dialog.setText("로그인 화면으로 돌아가시겠습니까?");

            btn_dialog_ok = (TextView) dialog_logout.findViewById(R.id.btn_dialog_ok);
            btn_dialog_cancel = (TextView) dialog_logout.findViewById(R.id.btn_dialog_cancel);
            btn_dialog_cancel.setText("아니요");

            btn_dialog_ok.setOnClickListener(this);
            btn_dialog_cancel.setOnClickListener(this);

            dialog_logout.show();
        }
    }
}
