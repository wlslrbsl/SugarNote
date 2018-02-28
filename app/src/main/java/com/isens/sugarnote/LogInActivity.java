package com.isens.sugarnote;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class LogInActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private static final int RC_SIGN_IN = 9001;
    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_root, editor_user;

    private WifiManager wifi;

    private Dialog dialog_addLog, dialog_debug;

    private LinearLayout btn_log_in_local;
    private Button btn_log_in_google;
    private TextView btn_dialog_ok, btn_dialog_cancel, tv_dialog, tv_version;
    private ImageView btn_logo;

    private boolean addLogFlag = false, debugFlag = false, signInFlag = false;

    private GoogleSignInAccount account;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);

        tv_version = (TextView) findViewById(R.id.tv_version);
        btn_logo = (ImageView) findViewById(R.id.btn_logo);

        btn_log_in_local = (LinearLayout) findViewById(R.id.btn_log_in_local);
        btn_log_in_google = (Button) findViewById(R.id.btn_log_in_google);

        btn_log_in_google.setOnClickListener(this);
        btn_log_in_local.setOnClickListener(this);
        tv_version.setOnClickListener(this);
        btn_logo.setOnLongClickListener(this);
        tv_version.setOnLongClickListener(this);

        wifi = (WifiManager) getApplicationContext().getSystemService(this.WIFI_SERVICE);

        prefs_root = getSharedPreferences("ROOT", 0);
        editor_root = prefs_root.edit();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        MyApplication.mGoogleSignInClient.signOut();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_log_in_google:

                if (wifi.isWifiEnabled()) {
                    Intent signInIntent = MyApplication.mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, RC_SIGN_IN);

                    signInFlag = true;
                } else {
                    WifiDialog dialog = new WifiDialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_wifi);
                    dialog.show();
                }
                break;

            case R.id.btn_log_in_local:
                editor_root.putString("SIGNIN", "LOCAL");
                editor_root.commit();
                prefs_user = getSharedPreferences("LOCAL", 0);
                if (prefs_user.getBoolean("REGISTERED", false)) {
                    Intent intent_main = new Intent(LogInActivity.this, MainActivity.class);
                    startActivity(intent_main);
                } else {
                    Intent intent_register = new Intent(LogInActivity.this, RegisterActivity.class);
                    startActivity(intent_register);
                }
                break;

            case R.id.tv_version:
                break;

            case R.id.btn_dialog_ok:
                if (addLogFlag == true) {
                    addLogFlag = false;
                    createUser(); // dummy user 생산
                    dialog_addLog.dismiss();
                } else if (debugFlag == true) {
                    debugFlag = false;
                    MyApplication.setRegisterDebugMode(true);
                    Toast.makeText(this, "디버그 모드 : enable", Toast.LENGTH_SHORT).show();
                    dialog_debug.dismiss();
                } else {

                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.btn_dialog_cancel:
                if (addLogFlag == true) {
                    addLogFlag = false;
                    dialog_addLog.dismiss();
                } else if (debugFlag == true) {
                    debugFlag = false;
                    MyApplication.setRegisterDebugMode(false);
                    Toast.makeText(this, "디버그 모드 : disable", Toast.LENGTH_SHORT).show();
                    dialog_debug.dismiss();
                } else {

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

            if (signInFlag) {
                editor_root.putString("SIGNIN", account.getEmail());
                editor_root.commit();

                prefs_user = getSharedPreferences(account.getEmail(), 0);

                if (!prefs_user.getBoolean("REGISTERED", false)) {
                    Intent intentRegister = new Intent(this, RegisterActivity.class);
                    startActivity(intentRegister);
                } else {
                    Intent intentMain = new Intent(this, MainActivity.class);
                    startActivity(intentMain);
                }
            }
        }
    }

    void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
        } catch (ApiException e) {
            signInFlag = false;
        }
    }

    private void createUser() {

        prefs_user = getSharedPreferences("chong00516@gmail.com", 0);
        editor_user = prefs_user.edit();
        editor_user.putString("NAME", "이총명");
        editor_user.putString("BIRTH", "19910516");
        editor_user.putString("HEIGHT", "173");
        editor_user.putString("WEIGHT", "65");
        editor_user.putString("GENDER", "남");
        editor_user.putBoolean("REGISTERED", true);
        editor_user.commit();
    }

    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {

            case R.id.tv_version:
                dialog_addLog = new Dialog(this);
                dialog_addLog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_addLog.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_addLog.findViewById(R.id.tv_dialog);
                tv_dialog.setText("테스트 계정을 생성하시겠습니까?");

                btn_dialog_ok = (TextView) dialog_addLog.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_addLog.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                addLogFlag = true;
                dialog_addLog.show();
                break;

            case R.id.btn_logo:
                dialog_debug = new Dialog(this);
                dialog_debug.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_debug.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_debug.findViewById(R.id.tv_dialog);
                tv_dialog.setText("디버그 모드를 설정하십시오.");

                btn_dialog_ok = (TextView) dialog_debug.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_debug.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_ok.setText("활성");
                btn_dialog_cancel.setText("비활성");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                debugFlag = true;
                dialog_debug.show();
                break;

            default:
                break;

        }
        return false;
    }
}
