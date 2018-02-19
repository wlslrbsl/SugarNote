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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterAccountFragment extends Fragment implements View.OnClickListener, AnimatorSet.AnimatorListener {

    private Context mContext;
    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private Activity ac;
    private View view;

    private LinearLayout ll_input_account, btn_dot_register_5, btn_dot_register_3;
    private TextView tv_register_account, tv_account_corfirm;
    private ImageView btn_google_account, btn_isens_account;
    private Button btn_next_register, btn_back_register;

    private ObjectAnimator fadeInTextView, fadeOutTextView, fadeInLinearLayout, fadeOutLinearLayout;
    private AnimatorSet fadeInOutTextView;

    private String accountSelected, userAccount;
    private static String animatorFlagAccount;
    private static boolean googleChecked, isensChecked;
    private int userId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        prefs_root = mContext.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN","ERROR");
        prefs_user = mContext.getSharedPreferences(userAccount, 0);
        editor_user = prefs_user.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ac = getActivity();
        btn_next_register = (Button) ac.findViewById(R.id.btn_next_register);
        btn_back_register = (Button) ac.findViewById(R.id.btn_back_register);
        btn_dot_register_3 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_3);
        btn_dot_register_5 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_5);

        btn_next_register.setOnClickListener(this);
        btn_back_register.setOnClickListener(this);
        btn_next_register.setEnabled(true);
        btn_back_register.setEnabled(true);

        view = inflater.inflate(R.layout.fragment_register_account, container, false);

        ll_input_account = (LinearLayout) view.findViewById(R.id.ll_input_account);
        tv_register_account = (TextView) view.findViewById(R.id.tv_register_account);
        tv_account_corfirm = (TextView) view.findViewById(R.id.tv_account_confirm);
        btn_google_account = (ImageView) view.findViewById(R.id.btn_google_account);
        btn_isens_account = (ImageView) view.findViewById(R.id.btn_isens_account);

        btn_google_account.setOnClickListener(this);
        btn_isens_account.setOnClickListener(this);

        googleChecked = false;
        isensChecked = false;

        creatAnimator();
        creatAnimatorSet();

        animatorFlagAccount = "start";
        setConfirm();
        tv_register_account.setText("개인정보 및 혈당데이터를\n연동할 계정을 선택하세요.");
        fadeInOutTextView.start();

        return view;
    }

    private void creatAnimator() {
        fadeInTextView = ObjectAnimator.ofFloat(tv_register_account, "alpha", 0f, 1f);
        fadeInTextView.setDuration(MyApplication.getAnimatorSpeed()*2);
        fadeOutTextView = ObjectAnimator.ofFloat(tv_register_account, "alpha", 1f, 0f);
        fadeOutTextView.setDuration(MyApplication.getAnimatorSpeed());

        fadeInLinearLayout = ObjectAnimator.ofFloat(ll_input_account, "alpha", 0f, 1f);
        fadeInLinearLayout.setDuration(MyApplication.getAnimatorSpeed()*2);
        fadeOutLinearLayout = ObjectAnimator.ofFloat(ll_input_account, "alpha", 1f, 0f);
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
        switch (animatorFlagAccount) {
            case "start":
                animatorFlagAccount = "end";
                tv_register_account.setVisibility(View.INVISIBLE);
                ll_input_account.setVisibility(View.VISIBLE);
                fadeInLinearLayout.start();
                break;
            case "end":
                animatorFlagAccount = null;
                btn_dot_register_5.setEnabled(true);
                btn_dot_register_5.callOnClick();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_next_register:
                editor_user.putString("ACCOUNT", accountSelected);
                editor_user.commit();
                fadeOutLinearLayout.start();
                break;
            case R.id.btn_google_account:
                if (googleChecked == false) {
                    googleChecked = true;
                    isensChecked = false;
                    accountSelected = "Google";
                    btn_google_account.setImageResource(R.drawable.btn_google);
                    btn_isens_account.setImageResource(R.drawable.btn_isens_gray);
                    setConfirm();
                } else {
                    googleChecked = false;
                    btn_google_account.setImageResource(R.drawable.btn_google_gray);
                    setConfirm();
                }
                break;
            case R.id.btn_isens_account:
                if (isensChecked == false) {
                    isensChecked = true;
                    googleChecked = false;
                    accountSelected = "I-sens";
                    btn_isens_account.setImageResource(R.drawable.btn_isens);
                    btn_google_account.setImageResource(R.drawable.btn_google_gray);
                    setConfirm();
                } else {
                    isensChecked = false;
                    btn_isens_account.setImageResource(R.drawable.btn_isens_gray);
                    setConfirm();
                }
                break;
            case R.id.btn_back_register:
                btn_dot_register_3.setEnabled(true);
                btn_dot_register_3.callOnClick();
                break;
            default:
                break;
        }
    }

    private void setConfirm() {
        if (googleChecked || isensChecked) {
            tv_account_corfirm.setText(accountSelected + "계정으로\n데이터를 저장합니다.");
        } else {
            tv_account_corfirm.setText("로컬저장소에\n데이터를 저장합니다.");
            accountSelected = "LocalDB";
        }
    }
}
