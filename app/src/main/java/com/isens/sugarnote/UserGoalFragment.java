package com.isens.sugarnote;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserGoalFragment extends Fragment implements View.OnClickListener{

    private Activity ac;
    private View view;

    private FragmentInterActionListener listener;

    private RangeSeekBar rangeSeekBarPremeal, rangeSeekBarPostmeal, rangeSeekNomeal;
    private LinearLayout ll_seekbar_premeal, ll_seekbar_postmeal, ll_seekbar_nomeal;

    private Button btn_navi_center, btn_navi_left, btn_navi_right;

    public UserGoalFragment() {
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
        // Inflate the layout for this fragment
        ac = getActivity();
        view = inflater.inflate(R.layout.fragment_user_goal, container, false);

        ll_seekbar_premeal = (LinearLayout) view.findViewById(R.id.ll_seekbar_premeal);
        ll_seekbar_postmeal = (LinearLayout) view.findViewById(R.id.ll_seekbar_postmeal);
        ll_seekbar_nomeal = (LinearLayout) view.findViewById(R.id.ll_seekbar_nomeal);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_save);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_cancel);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_user);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);

        createRangeSeekbar();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_navi_center :
                listener.setFrag("USER");
                Toast.makeText(ac, "저장", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_navi_left :
                listener.setFrag("HOME");
                break;
            case R.id.btn_navi_right :
                listener.setFrag("USER");
                break;
            default:
                break;
        }
    }
}
