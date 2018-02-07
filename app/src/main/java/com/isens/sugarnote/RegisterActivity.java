package com.isens.sugarnote;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences prefs_root, prefs_user;
    SharedPreferences.Editor editor_root, editor_user;

    private Dialog dialog_endRegister;

    private TextView tv_dialog, btn_dialog_ok, btn_dialog_cancel;
    private ImageView iv_dot_register_1, iv_dot_register_2, iv_dot_register_3, iv_dot_register_4, iv_dot_register_5;
    private LinearLayout btn_dot_register_1, btn_dot_register_2, btn_dot_register_3, btn_dot_register_4, btn_dot_register_5;
    private Button btn_home_register, btn_next_register, btn_back_register;
    private String[] state_register = {"Start", "Profile", "Body", "Goal", "Account", "Setting"};
    private static int index_register = 0;
    private static String registerFlag;
    private RegisterStartFragment registerStartFragment;
    private RegisterProfileFragment registerProfileFragment;
    private RegisterGoalFragment registerGoalFragment;
    private RegisterAccountFragment registerAccountFragment;
    private RegisterBodyFragment registerBodyFragment;
    private RegisterSecurityFragment registerSecurityFragment;
    private FragmentManager fm;
    private FragmentTransaction tran;

    private int userId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        index_register = 0;

        registerFlag = state_register[index_register];

        registerStartFragment = new RegisterStartFragment();
        registerProfileFragment = new RegisterProfileFragment();
        registerBodyFragment = new RegisterBodyFragment();
        registerGoalFragment = new RegisterGoalFragment();
        registerAccountFragment = new RegisterAccountFragment();
        registerSecurityFragment = new RegisterSecurityFragment();

        iv_dot_register_1 = (ImageView) findViewById(R.id.iv_dot_register_1);
        iv_dot_register_2 = (ImageView) findViewById(R.id.iv_dot_register_2);
        iv_dot_register_3 = (ImageView) findViewById(R.id.iv_dot_register_3);
        iv_dot_register_4 = (ImageView) findViewById(R.id.iv_dot_register_4);
        iv_dot_register_5 = (ImageView) findViewById(R.id.iv_dot_register_5);

        btn_dot_register_1 = (LinearLayout) findViewById(R.id.btn_dot_register_1);
        btn_dot_register_2 = (LinearLayout) findViewById(R.id.btn_dot_register_2);
        btn_dot_register_3 = (LinearLayout) findViewById(R.id.btn_dot_register_3);
        btn_dot_register_4 = (LinearLayout) findViewById(R.id.btn_dot_register_4);
        btn_dot_register_5 = (LinearLayout) findViewById(R.id.btn_dot_register_5);

        btn_home_register = (Button) findViewById(R.id.btn_home_register);
        btn_next_register = (Button) findViewById(R.id.btn_next_register);
        btn_back_register = (Button) findViewById(R.id.btn_back_register);

        btn_dot_register_1.setOnClickListener(this);
        btn_dot_register_2.setOnClickListener(this);
        btn_dot_register_3.setOnClickListener(this);
        btn_dot_register_4.setOnClickListener(this);
        btn_dot_register_5.setOnClickListener(this);

        btn_home_register.setOnClickListener(this);
        btn_next_register.setOnClickListener(this);
        btn_back_register.setOnClickListener(this);

        setFrag(registerFlag);

    }

    private void setDisableDot() {
        btn_dot_register_1.setEnabled(false);
        btn_dot_register_2.setEnabled(false);
        btn_dot_register_3.setEnabled(false);
        btn_dot_register_4.setEnabled(false);
        btn_dot_register_5.setEnabled(false);
    }

    private void setFrag(String str) {

        setDisableDot();
        fm = getSupportFragmentManager();
        tran = fm.beginTransaction();
        switch (str) {
            case "Start":
                tran.replace(R.id.fragment_container_register, registerStartFragment);
                tran.commit();
                break;
            case "Profile":
                tran.replace(R.id.fragment_container_register, registerProfileFragment);
                tran.commit();
                break;
            case "Body":
                tran.replace(R.id.fragment_container_register, registerBodyFragment);
                tran.commit();
                break;
            case "Goal":
                tran.replace(R.id.fragment_container_register, registerGoalFragment);
                tran.commit();
                break;
            case "Account":
                tran.replace(R.id.fragment_container_register, registerAccountFragment);
                tran.commit();
                break;
            case "Setting":
                tran.replace(R.id.fragment_container_register, registerSecurityFragment);
                tran.commit();
                break;
        }
    }


    private void clearDot() {
        iv_dot_register_1.setImageResource(R.drawable.bg_dot_empty);
        iv_dot_register_2.setImageResource(R.drawable.bg_dot_empty);
        iv_dot_register_3.setImageResource(R.drawable.bg_dot_empty);
        iv_dot_register_4.setImageResource(R.drawable.bg_dot_empty);
        iv_dot_register_5.setImageResource(R.drawable.bg_dot_empty);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dot_register_1:
                clearDot();
                iv_dot_register_1.setImageResource(R.drawable.bg_dot_filled);
                index_register = 1;
                registerFlag = state_register[index_register];
                setFrag(registerFlag);
                break;

            case R.id.btn_dot_register_2:
                clearDot();
                iv_dot_register_2.setImageResource(R.drawable.bg_dot_filled);
                index_register = 2;
                registerFlag = state_register[index_register];
                setFrag(registerFlag);
                break;

            case R.id.btn_dot_register_3:
                clearDot();
                iv_dot_register_3.setImageResource(R.drawable.bg_dot_filled);
                index_register = 3;
                registerFlag = state_register[index_register];
                setFrag(registerFlag);
                break;

            case R.id.btn_dot_register_4:
                clearDot();
                iv_dot_register_4.setImageResource(R.drawable.bg_dot_filled);
                index_register = 4;
                registerFlag = state_register[index_register];
                setFrag(registerFlag);
                break;

            case R.id.btn_dot_register_5:
                clearDot();
                iv_dot_register_5.setImageResource(R.drawable.bg_dot_filled);
                index_register = 5;
                registerFlag = state_register[index_register];
                setFrag(registerFlag);
                break;

            case R.id.btn_home_register:

                dialog_endRegister = new Dialog(this);
                dialog_endRegister.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_endRegister.setContentView(R.layout.dialog_default);

                tv_dialog = (TextView) dialog_endRegister.findViewById(R.id.tv_dialog);
                tv_dialog.setText("초기화면으로 돌아가시겠습니까?");

                btn_dialog_ok = (TextView) dialog_endRegister.findViewById(R.id.btn_dialog_ok);
                btn_dialog_cancel = (TextView) dialog_endRegister.findViewById(R.id.btn_dialog_cancel);
                btn_dialog_cancel.setText("아니요");

                btn_dialog_ok.setOnClickListener(this);
                btn_dialog_cancel.setOnClickListener(this);

                dialog_endRegister.show();

                break;

            case R.id.btn_dialog_ok:
                index_register = 0;
                finish();
                break;

            case R.id.btn_dialog_cancel:
                dialog_endRegister.dismiss();
                break;

            default:
                break;

        }
    }

    @Override
    public void onBackPressed() {
        index_register = 0;
        super.onBackPressed();
    }
}
