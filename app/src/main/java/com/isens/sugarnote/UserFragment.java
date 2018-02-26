package com.isens.sugarnote;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class UserFragment extends Fragment implements View.OnClickListener {

    private Activity ac;
    private View view;

    private FragmentInterActionListener listener;

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private LinearLayout ll_male_profile, ll_female_profile;
    private EditText edt_profile_name, edt_profile_birth_year, edt_profile_birth_month, edt_profile_birth_day, edt_height, edt_weight;
    private TextView tv_male_profile, tv_female_profile;

    private Button btn_navi_right, btn_navi_center, btn_navi_left;

    private String userAccount, userName, userBirth, userGender, userHeight, userWeight;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (FragmentInterActionListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ac = getActivity();
        view = inflater.inflate(R.layout.fragment_user, container, false);

        prefs_root = ac.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "ERROR");

        prefs_user = ac.getSharedPreferences(userAccount, 0);
        editor_user = prefs_user.edit();

        ll_male_profile = (LinearLayout) view.findViewById(R.id.ll_male_profile);
        ll_female_profile = (LinearLayout) view.findViewById(R.id.ll_female_profile);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_save);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_cancel);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_goal);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);

        tv_male_profile = (TextView) view.findViewById(R.id.tv_male_profile);
        tv_female_profile = (TextView) view.findViewById(R.id.tv_female_profile);

        edt_profile_name = (EditText) view.findViewById(R.id.edt_profile_name);
        edt_profile_birth_year = (EditText) view.findViewById(R.id.edt_profile_birth_year);
        edt_profile_birth_month = (EditText) view.findViewById(R.id.edt_profile_birth_month);
        edt_profile_birth_day = (EditText) view.findViewById(R.id.edt_profile_birth_day);
        edt_height = (EditText) view.findViewById(R.id.edt_height);
        edt_weight = (EditText) view.findViewById(R.id.edt_weight);

        edt_profile_name.setText(prefs_user.getString("NAME", "none"));
        edt_profile_birth_year.setText(prefs_user.getString("BIRTH", "none").substring(0, 4));
        edt_profile_birth_month.setText(prefs_user.getString("BIRTH", "none").substring(4, 6));
        edt_profile_birth_day.setText(prefs_user.getString("BIRTH", "none").substring(6, 8));
        edt_height.setText(prefs_user.getString("HEIGHT", "none"));
        edt_weight.setText(prefs_user.getString("WEIGHT", "none"));

        userGender = prefs_user.getString("GENDER", "none");

        if (userGender.equals("남")) {
            ll_female_profile.setBackgroundResource(R.color.color_bright_gray);
            ll_male_profile.setBackgroundResource(R.color.color_bright_blue);
            tv_male_profile.setTextColor(Color.WHITE);
            tv_female_profile.setTextColor(Color.BLACK);
        } else {
            ll_female_profile.setBackgroundResource(R.color.color_bright_pink);
            ll_male_profile.setBackgroundResource(R.color.color_bright_gray);
            tv_female_profile.setTextColor(Color.WHITE);
            tv_male_profile.setTextColor(Color.BLACK);
        }

        ll_male_profile.setOnClickListener(this);
        ll_female_profile.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_navi_center:
                if (edt_profile_name.getText().toString().equals("") | edt_profile_birth_year.getText().toString().equals("") | edt_profile_birth_month.getText().toString().equals("")
                        | edt_profile_birth_day.getText().toString().equals("") | edt_height.getText().toString().equals("") | edt_weight.getText().toString().equals("")) {
                    Toast.makeText(ac, "빈 칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                userName = edt_profile_name.getText().toString();
                userBirth = edt_profile_birth_year.getText().toString() + edt_profile_birth_month.getText().toString() + edt_profile_birth_day.getText().toString();
                userHeight = edt_height.getText().toString();
                userWeight = edt_weight.getText().toString();
                editor_user.putString("NAME", userName);
                editor_user.putString("BIRTH", userBirth);
                editor_user.putString("HEIGHT", userHeight);
                editor_user.putString("WEIGHT", userWeight);
                editor_user.putString("GENDER", userGender);
                editor_user.commit();
                Toast.makeText(ac, "세이브", Toast.LENGTH_SHORT).show();
                listener.setFrag("HOME");
                break;

            case R.id.btn_navi_left:
                listener.setFrag("HOME");
                break;

            case R.id.btn_navi_right:
                listener.setFrag("GOAL");
                break;

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
        }
    }
}
