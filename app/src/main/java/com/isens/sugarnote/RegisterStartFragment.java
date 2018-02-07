package com.isens.sugarnote;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterStartFragment extends Fragment implements AnimatorSet.AnimatorListener {

    private TextView tv_register;
    private ObjectAnimator fadeInAnimator, fadeOutAnimator, inputFadeInAnimator, inputFadeOutAnimator;
    private LinearLayout ll_input_register, btn_dot_register_1;
    private AnimatorSet fadeInOutAnimatorSet;
    private static String animatorFlagStart = null;

    private Activity ac;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ac = getActivity();
        ll_input_register = (LinearLayout) ac.findViewById(R.id.ll_input_register);
        btn_dot_register_1 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_1);

        view = inflater.inflate(R.layout.fragment_register_start, container, false);

        tv_register = (TextView) view.findViewById(R.id.tv_register);

        creatAnimator();
        creatAnimatorSet();

        animatorFlagStart = "start";
        tv_register.setText("안녕하세요.\n케어센스 K 입니다.");
        fadeInOutAnimatorSet.start();

        return view;
    }

    private void creatAnimator() {
        fadeInAnimator = ObjectAnimator.ofFloat(tv_register, "alpha", 0f, 1f);
        fadeInAnimator.setDuration(MyApplication.getAnimatorSpeed()*2);
        fadeOutAnimator = ObjectAnimator.ofFloat(tv_register, "alpha", 1f, 0f);
        fadeOutAnimator.setDuration(MyApplication.getAnimatorSpeed());

        inputFadeInAnimator = ObjectAnimator.ofFloat(ll_input_register, "alpha", 0f, 1f);
        inputFadeInAnimator.setDuration(MyApplication.getAnimatorSpeed()*2);
        inputFadeOutAnimator = ObjectAnimator.ofFloat(ll_input_register, "alpha", 1f, 0f);
        inputFadeOutAnimator.setDuration(MyApplication.getAnimatorSpeed());
    }

    private void creatAnimatorSet() {
        fadeInOutAnimatorSet = new AnimatorSet();
        fadeInOutAnimatorSet.play(fadeInAnimator).before(fadeOutAnimator);
        fadeInOutAnimatorSet.setDuration(MyApplication.getAnimatorSpeed()*3);
        fadeInOutAnimatorSet.addListener(this);
    }

    @Override
    public void onAnimationStart(Animator animator) {
    }

    @Override
    public void onAnimationEnd(Animator animator) {
        switch (animatorFlagStart) {
            case "start":
                animatorFlagStart = "end";
                tv_register.setText("다음 단계를 따라\n정보를 입력해 주세요.");
                fadeInOutAnimatorSet.start();
                break;
            case "end":
                animatorFlagStart = null;
                ll_input_register.setVisibility(View.VISIBLE);
                inputFadeInAnimator.start();
                btn_dot_register_1.setEnabled(true);
                btn_dot_register_1.callOnClick();
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
    public void onStop() {
        animatorFlagStart = "NULL";
        super.onStop();
    }
}
