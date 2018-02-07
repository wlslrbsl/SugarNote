package com.isens.sugarnote;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_root, editor_user;

    private Dialog dialog_nouser, dialog_addLog, dialog_deleteAll, dialog_debug;

    private Button btn_logIn, btn_register;
    private TextView btn_dialog_ok, btn_dialog_cancel, tv_dialog, tv_copyright, tv_version;
    private ImageView btn_logo;

    private String userId;
    private int userCount;
    private boolean addLogFlag = false, deleteAllFlag = false, debugFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);

        tv_copyright = (TextView) findViewById(R.id.tv_copyright);
        tv_version = (TextView) findViewById(R.id.tv_version);

        btn_logIn = (Button) findViewById(R.id.btn_logIn);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_logo = (ImageView) findViewById(R.id.btn_logo);

        btn_logIn.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_logo.setOnLongClickListener(this);

        tv_version.setOnLongClickListener(this);
        tv_copyright.setOnLongClickListener(this);

        prefs_root = getSharedPreferences("ROOT", 0);
        editor_root = prefs_root.edit();

    }


    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {
            case R.id.tv_copyright:
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

            case R.id.tv_version:
                dialog_deleteAll = new Dialog(this);
                dialog_deleteAll.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_deleteAll.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_deleteAll.findViewById(R.id.tv_dialog);
                tv_dialog.setText("모든 계정을 삭제하시겠습니까?");

                btn_dialog_ok = (TextView) dialog_deleteAll.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_deleteAll.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                deleteAllFlag = true;
                dialog_deleteAll.show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logIn:
                if (prefs_root.getInt("USERCOUNT", 0) == 0) {
                    dialog_nouser = new Dialog(this);
                    dialog_nouser.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog_nouser.setContentView(R.layout.dialog_default);

                    btn_dialog_ok = (TextView) dialog_nouser.findViewById(R.id.btn_dialog_ok);
                    btn_dialog_cancel = (TextView) dialog_nouser.findViewById(R.id.btn_dialog_cancel);
                    tv_dialog = (TextView) dialog_nouser.findViewById(R.id.tv_dialog);

                    tv_dialog.setText("등록된 계정이 없습니다.\n새로운 계정을 생성하시겠습니까?");

                    btn_dialog_ok.setOnClickListener(this);
                    btn_dialog_cancel.setOnClickListener(this);

                    dialog_nouser.show();
                    break;
                }
                CustomDialogLogIn dialog = new CustomDialogLogIn(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                break;

            case R.id.btn_dialog_ok:
                if (addLogFlag == true) {
                    addLogFlag = false;
                    createUser(); // dummy user 생산
                    dialog_addLog.dismiss();
                } else if (deleteAllFlag == true) {
                    deleteAllFlag = false;
                    deleteAllUser(); // 모든 유저 정보 삭제
                    dialog_deleteAll.dismiss();
                } else if(debugFlag == true) {
                    debugFlag = false;
                    MyApplication.setRegisterDebugMode(true);
                    Toast.makeText(this, "디버그 모드 : enable", Toast.LENGTH_SHORT).show();
                    dialog_debug.dismiss();
                } else {
                    dialog_nouser.dismiss();
                    Intent intent = new Intent(this, RegisterActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.btn_dialog_cancel:
                if (addLogFlag == true) {
                    addLogFlag = false;
                    dialog_addLog.dismiss();
                } else if (deleteAllFlag == true) {
                    dialog_deleteAll.dismiss();
                } else if(debugFlag == true) {
                    debugFlag =false;
                    MyApplication.setRegisterDebugMode(false);
                    Toast.makeText(this, "디버그 모드 : disable", Toast.LENGTH_SHORT).show();
                    dialog_debug.dismiss();
                } else {
                    dialog_nouser.dismiss();
                }
                break;

            case R.id.btn_register:
                Intent intent_register = new Intent(this, RegisterActivity.class);
                startActivity(intent_register);
                break;

            case R.id.tv_version:
                Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(intent);
                break;
        }
    }

    private void deleteAllUser() {
        for (int i = 1; i <= prefs_root.getInt("USERCOUNT", 0); i++) {
            prefs_user = getSharedPreferences(String.valueOf(i), 0);
            editor_user = prefs_user.edit();
            editor_user.clear();
            editor_user.commit();

            editor_root.putInt("USERCOUNT", 0);
            editor_root.commit();
        }
    }

    private void createUser() {
        userCount = prefs_root.getInt("USERCOUNT", 0);
        userId = String.valueOf(userCount + 1);

        prefs_user = getSharedPreferences(userId, 0);
        editor_user = prefs_user.edit();
        editor_user.putString("NAME", "이총명");
        editor_user.putString("BIRTH", "19910516");
        editor_user.putString("HEIGHT", "173");
        editor_user.putString("WEIGHT", "65");
        editor_user.putString("GENDER", "남");
        editor_user.commit();

        editor_root.putInt("USERCOUNT", userCount + 1);
        editor_root.commit();
    }

/*    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Y < 30) {
                    MyApplication.setFrag(true);
                }
                break;

            case MotionEvent.ACTION_MOVE :
                if(MyApplication.isFlag()) {
                    Intent intent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                    sendBroadcast(intent);
                }
                break;

            case MotionEvent.ACTION_UP :
                MyApplication.setFrag(false);
                break;

            default:
                break;
        }

        return super.onTouchEvent(event);
    }*/
}
