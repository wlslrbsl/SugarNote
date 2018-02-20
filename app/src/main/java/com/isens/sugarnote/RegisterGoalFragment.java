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
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterGoalFragment extends Fragment implements View.OnClickListener, AnimatorSet.AnimatorListener {

    private Context mContext;
    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private Activity ac;
    private View view;

    private LinearLayout ll_seekbar_premeal, ll_seekbar_postmeal, ll_seekbar_nomeal, ll_input_goal, btn_dot_register_4, btn_dot_register_2;
    private TextView tv_register_goal;
    private Button btn_next_register, btn_back_register;

    private RangeSeekBar<Integer> rangeSeekBarPremeal, rangeSeekBarPostmeal, rangeSeekNomeal;
    private ObjectAnimator fadeInTextView, fadeOutTextView, fadeInLinearLayout, fadeOutLinearLayout;
    private AnimatorSet fadeInOutTextView;

    private String goalPremealLow, goalPostmealLow, goalNomealLow, goalPremealHigh, goalPostmealHigh, goalNomealHigh, userAccount;
    private static String animatorFlagGoal;

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
        view = inflater.inflate(R.layout.fragment_register_goal, container, false);

        btn_next_register = (Button) ac.findViewById(R.id.btn_next_register);
        btn_back_register = (Button) ac.findViewById(R.id.btn_back_register);
        btn_dot_register_4 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_4);
        btn_dot_register_2 = (LinearLayout) ac.findViewById(R.id.btn_dot_register_2);

        btn_next_register.setOnClickListener(this);
        btn_back_register.setOnClickListener(this);
        btn_back_register.setEnabled(true);
        btn_next_register.setEnabled(true);

        ll_seekbar_premeal = (LinearLayout) view.findViewById(R.id.ll_seekbar_premeal);
        ll_seekbar_postmeal = (LinearLayout) view.findViewById(R.id.ll_seekbar_postmeal);
        ll_seekbar_nomeal = (LinearLayout) view.findViewById(R.id.ll_seekbar_nomeal);
        ll_input_goal = (LinearLayout) view.findViewById(R.id.ll_input_goal);
        tv_register_goal = (TextView) view.findViewById(R.id.tv_register_goal);

        createAnimator();
        creatAnimatorSet();
        createRangeSeekbar();

        animatorFlagGoal = "start";
        tv_register_goal.setText("혈당 목표치를 설정해주세요.");
        fadeInOutTextView.start();

        return view;
    }

    private void createRangeSeekbar() {
        rangeSeekBarPremeal = new RangeSeekBar<Integer>(ac);

        rangeSeekBarPremeal.setRangeValues(0, 200);
        rangeSeekBarPremeal.setSelectedMaxValue(100);
        rangeSeekBarPremeal.setSelectedMinValue(50);

        rangeSeekBarPostmeal = new RangeSeekBar<Integer>(ac);

        rangeSeekBarPostmeal.setRangeValues(0, 200);
        rangeSeekBarPostmeal.setSelectedMaxValue(150);
        rangeSeekBarPostmeal.setSelectedMinValue(100);

        rangeSeekNomeal = new RangeSeekBar<Integer>(ac);

        rangeSeekNomeal.setRangeValues(0, 200);
        rangeSeekNomeal.setSelectedMaxValue(125);
        rangeSeekNomeal.setSelectedMinValue(75);

        ll_seekbar_premeal.addView(rangeSeekBarPremeal);
        ll_seekbar_postmeal.addView(rangeSeekBarPostmeal);
        ll_seekbar_nomeal.addView(rangeSeekNomeal);
    }

    private void createAnimator() {
        fadeInTextView = ObjectAnimator.ofFloat(tv_register_goal, "alpha", 0f, 1f);
        fadeInTextView.setDuration(MyApplication.getAnimatorSpeed()*2);
        fadeOutTextView = ObjectAnimator.ofFloat(tv_register_goal, "alpha", 1f, 0f);
        fadeOutTextView.setDuration(MyApplication.getAnimatorSpeed());

        fadeInLinearLayout = ObjectAnimator.ofFloat(ll_input_goal, "alpha", 0f, 1f);
        fadeInLinearLayout.setDuration(MyApplication.getAnimatorSpeed()*2);
        fadeOutLinearLayout = ObjectAnimator.ofFloat(ll_input_goal, "alpha", 1f, 0f);
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
        switch (animatorFlagGoal) {
            case "start":
                animatorFlagGoal = "end";
                tv_register_goal.setVisibility(View.INVISIBLE);
                ll_input_goal.setVisibility(View.VISIBLE);
                fadeInLinearLayout.start();
                break;
            case "end":
                animatorFlagGoal = null;
                btn_dot_register_4.callOnClick();
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
                goalPremealLow = rangeSeekBarPremeal.getSelectedMinValue().toString();
                goalPremealHigh = rangeSeekBarPremeal.getSelectedMaxValue().toString();
                goalPostmealLow = rangeSeekBarPostmeal.getSelectedMinValue().toString();
                goalPostmealHigh = rangeSeekBarPostmeal.getSelectedMaxValue().toString();
                goalNomealLow = rangeSeekNomeal.getSelectedMinValue().toString();
                goalNomealHigh = rangeSeekNomeal.getSelectedMaxValue().toString();

                editor_user.putString("PRELOW", goalPremealLow);
                editor_user.putString("PREHIGH", goalPremealHigh);
                editor_user.putString("POSTLOW", goalPostmealLow);
                editor_user.putString("POSTHIGH", goalPostmealHigh);
                editor_user.putString("NOLOW", goalNomealLow);
                editor_user.putString("NOHIGH", goalNomealHigh);
                editor_user.commit();

                fadeOutLinearLayout.start();
                break;

            case R.id.btn_back_register:
                btn_dot_register_2.callOnClick();
                break;

            default:
                break;
        }
    }

}
