package com.isens.sugarnote;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by chong on 2017-12-11.
 */

public class CustomDialogLogIn extends Dialog implements View.OnClickListener {

    private Context mContext;
    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_root;

    private TextView btn_login_back, btn_login, tv_user_num, tv_user_name;
    private ImageView btn_before_user, btn_after_user, iv_user_avatar;

    private int userCount;
    private static int userIndex;

    public CustomDialogLogIn(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog_custom);

        mContext = getContext();
        prefs_root = mContext.getSharedPreferences("ROOT", 0);
        editor_root = prefs_root.edit();

        btn_after_user = (ImageView) findViewById(R.id.btn_after_user);
        btn_before_user = (ImageView) findViewById(R.id.btn_before_user);
        iv_user_avatar = (ImageView) findViewById(R.id.iv_user_avatar);

        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_user_num = (TextView) findViewById(R.id.tv_user_num);
        btn_login = (TextView) findViewById(R.id.tv_login);
        btn_login_back = (TextView) findViewById(R.id.tv_login_back);

        btn_after_user.setOnClickListener(this);
        btn_before_user.setOnClickListener(this);
        btn_login_back.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        userIndex = 1;
        btn_before_user.setEnabled(false);

        userCount = prefs_root.getInt("USERCOUNT", 0);

        setUserView();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_login_back:
                dismiss();
                break;

            case R.id.tv_login:
                dismiss();
                editor_root.putString("LOGIN", String.valueOf(userIndex));
                editor_root.commit();
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
                break;

            case R.id.btn_before_user:
                userIndex--;
                setUserView();
                break;

            case R.id.btn_after_user:
                userIndex++;
                setUserView();
                break;

            default:
                break;
        }

    }

    private void setUserView() {
        prefs_user = mContext.getSharedPreferences(String.valueOf(userIndex), 0);

        tv_user_num.setText(String.valueOf(userIndex) + " / " + String.valueOf(userCount));
        tv_user_name.setText(prefs_user.getString("NAME", "무명"));

        if (prefs_user.getString("GENDER", "?").equals("남"))
            iv_user_avatar.setImageResource(R.drawable.icon_user_male);
        else
            iv_user_avatar.setImageResource(R.drawable.icon_user_female);

        if (userIndex == userCount)
            btn_after_user.setEnabled(false);
        else
            btn_after_user.setEnabled(true);

        if (userIndex == 1)
            btn_before_user.setEnabled(false);
        else
            btn_before_user.setEnabled(true);
    }
}
