package com.isens.sugarnote;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterProfileFragment extends Fragment implements AnimatorSet.AnimatorListener, TextWatcher, View.OnClickListener {

    private Context mContext;
    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private Activity ac;
    private View view;

    private LinearLayout ll_input_profile, ll_male_profile, ll_female_profile, btn_dot_register_2;
    private TextView tv_register_profile, tv_male_profile, tv_female_profile;
    private EditText edt_profile_name, edt_profile_birth_year, edt_profile_birth_month, edt_profile_birth_day;
    private Button btn_next_register, btn_back_register;

    private ObjectAnimator fadeInTextView, fadeOutTextView, fadeInLinearLayout, fadeOutLinearLayout;
    private AnimatorSet fadeInOutTextView;

    private String userName, userBirth, userGender = "남";
    private static String animatorFlagPorfile = null;

    private int userId = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        prefs_root = mContext.getSharedPreferences("ROOT", 0);
        userId = prefs_root.getInt("USERCOUNT", 0) + 1;
        prefs_user = mContext.getSharedPreferences(String.valueOf(userId), 0);
        editor_user = prefs_user.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ac = getActivity();

        btn_next_register = (Button) ac.findViewById(R.id.btn_next_register);
        btn_back_register = (Button) ac.findViewById(R.id.btn_back_register);
        btn_dot_register_2 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_2);

        btn_next_register.setOnClickListener(this);
        if (!MyApplication.isRegisterDebugMode()) {
            btn_next_register.setEnabled(false);
            btn_back_register.setEnabled(false);
        }

        view = inflater.inflate(R.layout.fragment_register_profile, container, false);

        ll_input_profile = (LinearLayout) view.findViewById(R.id.ll_input_profile);
        ll_male_profile = (LinearLayout) view.findViewById(R.id.ll_male_profile);
        ll_female_profile = (LinearLayout) view.findViewById(R.id.ll_female_profile);
        tv_register_profile = (TextView) view.findViewById(R.id.tv_register_profile);
        tv_male_profile = (TextView) view.findViewById(R.id.tv_male_profile);
        tv_female_profile = (TextView) view.findViewById(R.id.tv_female_profile);
        edt_profile_name = (EditText) view.findViewById(R.id.edt_profile_name);
        edt_profile_birth_year = (EditText) view.findViewById(R.id.edt_profile_birth_year);
        edt_profile_birth_month = (EditText) view.findViewById(R.id.edt_profile_birth_month);
        edt_profile_birth_day = (EditText) view.findViewById(R.id.edt_profile_birth_day);

        edt_profile_name.addTextChangedListener(this);
        edt_profile_birth_year.addTextChangedListener(this);
        edt_profile_birth_month.addTextChangedListener(this);
        edt_profile_birth_day.addTextChangedListener(this);
        ll_female_profile.setOnClickListener(this);
        ll_male_profile.setOnClickListener(this);

        creatAnimator();
        creatAnimatorSet();

        animatorFlagPorfile = "start";
        tv_register_profile.setText("이름, 성별, 생년월일을\n입력해주세요.");
        fadeInOutTextView.start();

        return view;
    }

    private void creatAnimator() {
        fadeInTextView = ObjectAnimator.ofFloat(tv_register_profile, "alpha", 0f, 1f);
        fadeInTextView.setDuration(MyApplication.getAnimatorSpeed() * 2);
        fadeOutTextView = ObjectAnimator.ofFloat(tv_register_profile, "alpha", 1f, 0f);
        fadeOutTextView.setDuration(MyApplication.getAnimatorSpeed());

        fadeInLinearLayout = ObjectAnimator.ofFloat(ll_input_profile, "alpha", 0f, 1f);
        fadeInLinearLayout.setDuration(MyApplication.getAnimatorSpeed() * 2);
        fadeOutLinearLayout = ObjectAnimator.ofFloat(ll_input_profile, "alpha", 1f, 0f);
        fadeOutLinearLayout.setDuration(MyApplication.getAnimatorSpeed());
        fadeOutLinearLayout.addListener(this);
    }

    private void creatAnimatorSet() {
        fadeInOutTextView = new AnimatorSet();
        fadeInOutTextView.play(fadeInTextView).before(fadeOutTextView);
        fadeInOutTextView.setDuration(MyApplication.getAnimatorSpeed() * 3);
        fadeInOutTextView.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        switch (animatorFlagPorfile) {
            case "start":
                animatorFlagPorfile = "end";
                tv_register_profile.setVisibility(View.INVISIBLE);
                ll_input_profile.setVisibility(View.VISIBLE);
                fadeInLinearLayout.start();
                break;
            case "end":
                animatorFlagPorfile = null;
                btn_dot_register_2.setEnabled(true);
                btn_dot_register_2.callOnClick();
                break;
            default:
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (edt_profile_name.getText().toString().equals("") | edt_profile_birth_year.getText().toString().equals("") |
                edt_profile_birth_month.getText().toString().equals("") | edt_profile_birth_day.getText().toString().equals("")) {
            if(!MyApplication.isRegisterDebugMode())
                btn_next_register.setEnabled(false);
        } else {
            btn_next_register.setEnabled(true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_male_profile:
                userGender = "남";
                ll_female_profile.setBackgroundResource(R.color.color_bright_gray);
                ll_male_profile.setBackgroundResource(R.color.color_bright_blue);
                tv_male_profile.setTextColor(Color.WHITE);
                tv_female_profile.setTextColor(Color.BLACK);
                break;
            case R.id.ll_female_profile:
                userGender = "여";
                ll_female_profile.setBackgroundResource(R.color.color_bright_pink);
                ll_male_profile.setBackgroundResource(R.color.color_bright_gray);
                tv_female_profile.setTextColor(Color.WHITE);
                tv_male_profile.setTextColor(Color.BLACK);
                break;
            case R.id.btn_next_register:
                userName = edt_profile_name.getText().toString();
                userBirth = edt_profile_birth_year.getText().toString() + edt_profile_birth_month.getText().toString() + edt_profile_birth_day.getText().toString();
                editor_user.putString("NAME", userName);
                editor_user.putString("BIRTH", userBirth);
                editor_user.putString("GENDER", userGender);
                editor_user.commit();
                fadeOutLinearLayout.start();
                break;
            default:
                break;
        }
    }
}