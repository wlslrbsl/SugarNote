package com.isens.sugarnote;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterSecurityFragment extends Fragment implements View.OnClickListener, Animator.AnimatorListener {

    private Context mContext;
    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_root, editor_user;

    private Activity ac;
    private View view;

    private LinearLayout ll_input_security, btn_dot_register_4, ll_input_register;
    private TextView tv_register_security, tv_pw_del, tv_pw_clr;
    private TextView[] tv_pw_num = new TextView[10], tv_pw = new TextView[4];
    private Button btn_next_register, btn_back_register;

    private ObjectAnimator fadeInTextView, fadeOutTextView, fadeInLinearLayout, fadeOutLinearLayout, fadeOutRegisterInput;
    private AnimatorSet fadeInOutTextView, fadeOutAll;

    private static String animatorFlagSetting = null;
    private static int pwIndex;
    private String password = "pw", userAccount;
    private int[] pw = new int[4];

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        prefs_root = mContext.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN","ERROR");
        prefs_user = mContext.getSharedPreferences(userAccount, 0);
        editor_user = prefs_user.edit();
        editor_root = prefs_root.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ac = getActivity();

        ll_input_register = (LinearLayout) ac.findViewById(R.id.ll_input_register);
        btn_next_register = (Button) ac.findViewById(R.id.btn_next_register);
        btn_back_register = (Button) ac.findViewById(R.id.btn_back_register);
        btn_dot_register_4 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_4);

        btn_next_register.setOnClickListener(this);
        btn_back_register.setOnClickListener(this);


        if (!MyApplication.isRegisterDebugMode()) {
            btn_next_register.setEnabled(false);
            btn_back_register.setEnabled(true);
        }

        view = inflater.inflate(R.layout.fragment_register_security, container, false);

        ll_input_security = (LinearLayout) view.findViewById(R.id.ll_input_security);
        tv_register_security = (TextView) view.findViewById(R.id.tv_register_security);

        tv_pw[0] = (TextView) view.findViewById(R.id.tv_pw0);
        tv_pw[1] = (TextView) view.findViewById(R.id.tv_pw1);
        tv_pw[2] = (TextView) view.findViewById(R.id.tv_pw2);
        tv_pw[3] = (TextView) view.findViewById(R.id.tv_pw3);

        tv_pw_num[0] = (TextView) view.findViewById(R.id.tv_pw_num0);
        tv_pw_num[1] = (TextView) view.findViewById(R.id.tv_pw_num1);
        tv_pw_num[2] = (TextView) view.findViewById(R.id.tv_pw_num2);
        tv_pw_num[3] = (TextView) view.findViewById(R.id.tv_pw_num3);
        tv_pw_num[4] = (TextView) view.findViewById(R.id.tv_pw_num4);
        tv_pw_num[5] = (TextView) view.findViewById(R.id.tv_pw_num5);
        tv_pw_num[6] = (TextView) view.findViewById(R.id.tv_pw_num6);
        tv_pw_num[7] = (TextView) view.findViewById(R.id.tv_pw_num7);
        tv_pw_num[8] = (TextView) view.findViewById(R.id.tv_pw_num8);
        tv_pw_num[9] = (TextView) view.findViewById(R.id.tv_pw_num9);

        for (int i = 0; i < 10; i++) {
            tv_pw_num[i].setOnClickListener(this);
        }

        tv_pw_del = (TextView) view.findViewById(R.id.tv_pw_del);
        tv_pw_clr = (TextView) view.findViewById(R.id.tv_pw_clr);

        tv_pw_del.setOnClickListener(this);
        tv_pw_clr.setOnClickListener(this);

        creatAnimator();
        creatAnimatorSet();

        animatorFlagSetting = "start";
        pwIndex = 0;

        tv_register_security.setText("사용하실 비밀번호 4자리를\n입력해 주세요.");
        fadeInOutTextView.start();

        return view;
    }

    private void creatAnimator() {
        fadeInTextView = ObjectAnimator.ofFloat(tv_register_security, "alpha", 0f, 1f);
        fadeInTextView.setDuration(MyApplication.getAnimatorSpeed() * 2);
        fadeOutTextView = ObjectAnimator.ofFloat(tv_register_security, "alpha", 1f, 0f);
        fadeOutTextView.setDuration(MyApplication.getAnimatorSpeed());

        fadeInLinearLayout = ObjectAnimator.ofFloat(ll_input_security, "alpha", 0f, 1f);
        fadeInLinearLayout.setDuration(MyApplication.getAnimatorSpeed() * 2);
        fadeOutLinearLayout = ObjectAnimator.ofFloat(ll_input_security, "alpha", 1f, 0f);
        fadeOutLinearLayout.setDuration(MyApplication.getAnimatorSpeed());
        fadeOutRegisterInput = ObjectAnimator.ofFloat(ll_input_register, "alpha", 1f, 0f);
        fadeOutRegisterInput.setDuration(MyApplication.getAnimatorSpeed());
        fadeOutLinearLayout.addListener(this);
    }

    private void creatAnimatorSet() {
        fadeInOutTextView = new AnimatorSet();
        fadeInOutTextView.play(fadeInTextView).before(fadeOutTextView);
        fadeInOutTextView.setDuration(MyApplication.getAnimatorSpeed() * 3);
        fadeInOutTextView.addListener(this);

        fadeOutAll = new AnimatorSet();
        fadeOutAll.playTogether(fadeOutRegisterInput, fadeInOutTextView);
        fadeOutAll.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        switch (animatorFlagSetting) {
            case "start":
                animatorFlagSetting = "end";
                tv_register_security.setVisibility(View.INVISIBLE);
                ll_input_security.setVisibility(View.VISIBLE);
                fadeInLinearLayout.start();
                break;
            case "end":
                animatorFlagSetting = "login";
                tv_register_security.setVisibility(View.VISIBLE);
                ll_input_security.setVisibility(View.INVISIBLE);
                tv_register_security.setText("자, 이제 시작합니다.");
                fadeOutAll.start();
                break;
            case "login":
                Intent intent = new Intent(ac, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ac.finish();
                break;
        }

    }

    @Override
    public void onAnimationCancel(Animator animator) {
    }

    @Override
    public void onAnimationRepeat(Animator animator) {
    }

    @Override
    public void onClick(View view) {

        int i = 0;

        switch (view.getId()) {
            case R.id.tv_pw_num9:
                i++;
            case R.id.tv_pw_num8:
                i++;
            case R.id.tv_pw_num7:
                i++;
            case R.id.tv_pw_num6:
                i++;
            case R.id.tv_pw_num5:
                i++;
            case R.id.tv_pw_num4:
                i++;
            case R.id.tv_pw_num3:
                i++;
            case R.id.tv_pw_num2:
                i++;
            case R.id.tv_pw_num1:
                i++;
            case R.id.tv_pw_num0:
                if (pwIndex == 4)
                    break;
                pw[pwIndex] = i;
                tv_pw[pwIndex].setText("*");
                pwIndex++;
                break;

            case R.id.btn_next_register:
                for (int j = 0; j < 4; j++) {
                    password += String.valueOf(pw[j]);
                }
                Toast.makeText(mContext, password, Toast.LENGTH_SHORT).show();
                editor_user.putBoolean("REGISTERED", true);
                editor_user.commit();
                fadeOutLinearLayout.start();
                break;

            case R.id.btn_back_register:
                btn_dot_register_4.setEnabled(true);
                btn_dot_register_4.callOnClick();
                break;

            case R.id.tv_pw_del:
                if (pwIndex == 0)
                    break;
                tv_pw[pwIndex - 1].setText("");
                pwIndex--;
                break;

            case R.id.tv_pw_clr:
                pwIndex = 0;
                for (int j = 0; j < 4; j++) {
                    tv_pw[j].setText("");
                }
                break;

            default:
                break;
        }

        if (pwIndex == 4) {
            btn_next_register.setEnabled(true);
        } else {
            btn_next_register.setEnabled(false);
        }
    }
}
