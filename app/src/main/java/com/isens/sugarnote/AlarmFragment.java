package com.isens.sugarnote;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Activity ac;
    private View view;

    private SharedPreferences prefs_root;
    private static SharedPreferences prefs_user;
    private SharedPreferences.Editor editor_user;
    private String userAccount;

    private AlarmDialog dialog_alarm;
    private static AlarmAdapter alarmAdapter;
    private static ListView list_alarm;

    private Button btn_navi_center, btn_navi_left, btn_navi_right;
    private TextView btn_alarm_save, btn_alarm_cancel;

    private static int alarm_cnt = 0;

    private FragmentInterActionListener listener;

    public AlarmFragment() {
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
        view = inflater.inflate(R.layout.fragment_alarm, container, false);

        prefs_root = ac.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        prefs_user = ac.getSharedPreferences(userAccount, 0);
        editor_user = prefs_user.edit();

        list_alarm = (ListView) view.findViewById(R.id.list_alarm);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);

        list_alarm.setOnItemClickListener(this);
        btn_navi_center.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_add_alarm);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_leftarrow);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_calendar);

        setListView();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_navi_center:
                dialog_alarm = new AlarmDialog(ac);
                dialog_alarm.show();
                btn_alarm_save = (TextView) dialog_alarm.findViewById(R.id.btn_alarm_save);
                btn_alarm_cancel = (TextView) dialog_alarm.findViewById(R.id.btn_alarm_cancel);
                btn_alarm_save.setOnClickListener(this);
                btn_alarm_cancel.setOnClickListener(this);
                break;

            case R.id.btn_navi_left:
                listener.setFrag("CALENDAR");
                break;

            case R.id.btn_navi_right:
                editor_user.putInt("ALARM#",0);
                editor_user.commit();
                setListView();
                break;

            case R.id.btn_alarm_cancel:
                dialog_alarm.dismiss();
                break;

            case R.id.btn_alarm_save:
                alarm_cnt = prefs_user.getInt("ALARM#", 0);
                AlarmItem mitem = dialog_alarm.getmItem();
                mitem.setAlarm_num(alarm_cnt + 1);
                //alarmAdapter.addItem(mitem);
                String str = mitem.getAmpm() + mitem.getHour() + mitem.getMinute();
                for (int i = 0; i < 7; i++)
                    str += mitem.getDayFlag()[i];
                str = str + mitem.getEnableFlag() + String.valueOf(alarm_cnt + 1);
                editor_user.putInt("ALARM#", alarm_cnt + 1);
                editor_user.putString("ALARM" + String.valueOf(alarm_cnt + 1), str);
                editor_user.commit();
                dialog_alarm.dismiss();
                setListView();
                break;

            default:
                break;
        }
    }

    public static void setListView() {

        alarmAdapter = new AlarmAdapter();

        alarm_cnt = prefs_user.getInt("ALARM#", 0);

        for (int i = 1; i < alarm_cnt + 1; i++) {
            String str = prefs_user.getString("ALARM" + i, "");

            AlarmItem alarmItem = new AlarmItem();
            alarmItem.setAmpm(str.substring(0, 2));
            alarmItem.setHour(str.substring(2, 4));
            alarmItem.setMinute(str.substring(4, 6));
            alarmItem.setEnableFlag(Integer.valueOf(String.valueOf(str.charAt(13))));
            alarmItem.setAlarm_num(Integer.valueOf(String.valueOf(str.charAt(14))));

            int[] dayFlag = new int[7];
            for (int j = 0; j < 7; j++) {
                dayFlag[j] = Integer.valueOf(String.valueOf(str.charAt(6 + j)));
            }

            alarmItem.setDayFlag(dayFlag);
            alarmAdapter.addItem(alarmItem);
        }

        list_alarm.setAdapter(alarmAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}
