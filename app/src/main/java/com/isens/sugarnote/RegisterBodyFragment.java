package com.isens.sugarnote;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
public class RegisterBodyFragment extends Fragment implements View.OnClickListener, AnimatorSet.AnimatorListener, TextWatcher {

    private Context mContext;
    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private Activity ac;
    private View view;

    private LinearLayout ll_input_body, btn_dot_register_3, btn_dot_register_1;
    private TextView tv_register_body;
    private EditText edt_body_height, edt_body_weight;
    private Button btn_next_register, btn_back_register;

    private ObjectAnimator fadeInTextView, fadeOutTextView, fadeInLinearLayout, fadeOutLinearLayout;
    private AnimatorSet fadeInOutTextView;

    private String userHeight, userWeight;
    private static String animatorFlagBody = null;

    private int userId;

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
        btn_dot_register_3 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_3);
        btn_dot_register_1 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_1);

        btn_back_register.setOnClickListener(this);
        btn_next_register.setOnClickListener(this);

        if (!MyApplication.isRegisterDebugMode()) {
            btn_next_register.setEnabled(false);
            btn_back_register.setEnabled(true);
        }

        view = inflater.inflate(R.layout.fragment_register_body, container, false);

        ll_input_body = (LinearLayout) view.findViewById(R.id.ll_input_body);
        tv_register_body = (TextView) view.findViewById(R.id.tv_register_body);
        edt_body_height = (EditText) view.findViewById(R.id.edt_height);
        edt_body_weight = (EditText) view.findViewById(R.id.edt_weight);

        edt_body_height.addTextChangedListener(this);
        edt_body_weight.addTextChangedListener(this);

        creatAnimator();
        creatAnimatorSet();

        animatorFlagBody = "start";
        // 이름불러주기

        tv_register_body.setText("신장과 체중을 입력해주세요.");
        fadeInOutTextView.start();

        return view;
    }

    private void creatAnimator() {
        fadeInTextView = ObjectAnimator.ofFloat(tv_register_body, "alpha", 0f, 1f);
        fadeInTextView.setDuration(MyApplication.getAnimatorSpeed()*2);
        fadeOutTextView = ObjectAnimator.ofFloat(tv_register_body, "alpha", 1f, 0f);
        fadeOutTextView.setDuration(MyApplication.getAnimatorSpeed());

        fadeInLinearLayout = ObjectAnimator.ofFloat(ll_input_body, "alpha", 0f, 1f);
        fadeInLinearLayout.setDuration(MyApplication.getAnimatorSpeed()*2);
        fadeOutLinearLayout = ObjectAnimator.ofFloat(ll_input_body, "alpha", 1f, 0f);
        fadeOutLinearLayout.setDuration(MyApplication.getAnimatorSpeed());
        fadeOutLinearLayout.addListener(this);
    }

    private void creatAnimatorSet() {
        fadeInOutTextView = new AnimatorSet();
        fadeInOutTextView.play(fadeInTextView).before(fadeOutTextView);
        fadeInOutTextView.setDuration(MyApplication.getAnimatorSpeed()*3);
        fadeInOutTextView.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        switch (animatorFlagBody) {
            case "start":
                animatorFlagBody = "end";
                tv_register_body.setVisibility(View.INVISIBLE);
                ll_input_body.setVisibility(View.VISIBLE);
                fadeInLinearLayout.start();
                break;
            case "end":
                animatorFlagBody = null;
                btn_dot_register_3.setEnabled(true);
                btn_dot_register_3.callOnClick();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_register:
                userHeight = edt_body_height.getText().toString();
                userWeight = edt_body_weight.getText().toString();
                editor_user.putString("HEIGHT", userHeight);
                editor_user.putString("WEIGHT", userWeight);
                editor_user.commit();
                fadeOutLinearLayout.start();
                break;
            case R.id.btn_back_register:
                btn_dot_register_1.setEnabled(true);
                btn_dot_register_1.callOnClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (edt_body_height.getText().toString().equals("") || edt_body_weight.getText().toString().equals("")) {
            if(!MyApplication.isRegisterDebugMode())
                btn_next_register.setEnabled(false);
        } else {
            btn_next_register.setEnabled(true);
        }
    }
}
