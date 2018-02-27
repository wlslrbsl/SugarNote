package com.isens.sugarnote;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by BSPL on 2018-02-07.
 */

public class ReportStatisticsFragment extends Fragment implements View.OnClickListener {
    private FragmentInterActionListener listener;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private ProgressBar mProgress_premeal, mProgress_postmeal, mProgress_nomeal, mProgress_count;
    private TextView txt_premeal_progress,txt_postmeal_progress,txt_nomeal_progress;
    private TextView txt_premeal_mean, txt_postmeal_mean, txt_nomeal_mean;
    private TextView txt_mean_count, txt_premeal_count, txt_postmeal_count, txt_nomeal_count;
    private TextView tv_header;
    private int during_option = 1;

    private View view;
    private Activity ac;

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private int premeal_cnt, postmeal_cnt, nomeal_cnt;
    private int premeal_progress_val,postmeal_progress_val,nomeal_progress_val;
    private double premeal_count_portion, postmeal_count_portion;
    private long premeal_count_portion_int, postmeal_count_portion_int;
    private int premeal_mean_val, postmeal_mean_val, nomeal_mean_val;
    private double mean_count_val, mean_count_val_round, premeal_count_val, premeal_count_val_round, postmeal_count_val, postmeal_count_val_round, nomeal_count_val, nomeal_count_val_round;
    private Button btn_navi_right, btn_navi_center, btn_navi_left;
    private String start_date, end_date;
    private static final int During_1week = 1, During_1month = 2;
    private final String premeal = "식전", postmeal = "식후", nomeal = "공복";
    private int ll_max_premeal, ll_min_premeal, ll_max_postmeal, ll_min_postmeal, ll_max_nomeal, ll_min_nomeal;
    private int mValue_premeal=0, mValue_postmeal=0, mValue_nomeal=0, max_mValue=0;
    private String userAccount;
    private Handler handler = new Handler(); // Thread 에서 화면에 그리기 위해서 필요
    int value = 0; // progressBar 값
    int add = 1; // 증가량, 방향


    public ReportStatisticsFragment() {

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
        view = inflater.inflate(R.layout.fragment_report_statistics, container, false);

        prefs_root = ac.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        prefs_user = ac.getSharedPreferences(userAccount, 0);
        editor_user = prefs_user.edit();

        if (dbHelper == null) dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
        db = dbHelper.getWritableDatabase();

        Load_Pref();
        init_view();
        set_Data(During_1week);
        set_View();

        return view;
    }

    public void init_view(){
        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_home);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_month);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_graph);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);

        mProgress_count = (ProgressBar) view.findViewById(R.id.circularProgressbar_count);
        mProgress_premeal = (ProgressBar) view.findViewById(R.id.circularProgressbar_premeal);
        mProgress_postmeal = (ProgressBar) view.findViewById(R.id.circularProgressbar_postmeal);
        mProgress_nomeal = (ProgressBar) view.findViewById(R.id.circularProgressbar_nomeal);

        txt_mean_count = (TextView) view.findViewById(R.id.txt_mean_count);
        txt_premeal_count = (TextView) view.findViewById(R.id.txt_premeal_count);
        txt_postmeal_count = (TextView) view.findViewById(R.id.txt_postmeal_count);
        txt_nomeal_count = (TextView) view.findViewById(R.id.txt_nomeal_count);

        txt_premeal_mean = (TextView) view.findViewById(R.id.txt_premeal_mean_val);
        txt_postmeal_mean = (TextView) view.findViewById(R.id.txt_postmeal_mean_val);
        txt_nomeal_mean = (TextView) view.findViewById(R.id.txt_nomeal_mean_val);
        txt_premeal_progress = (TextView) view.findViewById(R.id.txt_premeal_progressbar);
        txt_postmeal_progress = (TextView) view.findViewById(R.id.txt_postmeal_progressbar);
        txt_nomeal_progress = (TextView) view.findViewById(R.id.txt_nomeal_progressbar);

        tv_header = (TextView) view.findViewById(R.id.tv_statistic_header);
        tv_header.setText("주간 통계");
    }

    public void Load_Pref() {
        /*목표치*/
        ll_max_premeal = Integer.valueOf(prefs_user.getInt("PREHIGH", 100));
        ll_min_premeal = Integer.valueOf(prefs_user.getInt("PRELOW", 50));
        ll_max_postmeal = Integer.valueOf(prefs_user.getInt("POSTHIGH", 150));
        ll_min_postmeal = Integer.valueOf(prefs_user.getInt("POSTLOW", 100));
        ll_max_nomeal = Integer.valueOf(prefs_user.getInt("NOHIGH", 125));
        ll_min_nomeal = Integer.valueOf(prefs_user.getInt("NOLOW", 75));
    }

    public void set_Data(int duringoption) {
        get_duringtime(duringoption);
        premeal_mean_val = get_Avg(premeal, start_date, end_date);
        postmeal_mean_val = get_Avg(postmeal, start_date, end_date);
        nomeal_mean_val = get_Avg(nomeal, start_date, end_date);

        premeal_count_val = get_Count(premeal, start_date, end_date, duringoption);
        postmeal_count_val = get_Count(postmeal, start_date, end_date, duringoption);
        nomeal_count_val = get_Count(nomeal, start_date, end_date, duringoption);
        mean_count_val = premeal_count_val + postmeal_count_val + nomeal_count_val;

        mean_count_val_round = Math.round(mean_count_val*10d)/10d;
        premeal_count_val_round = Math.round(premeal_count_val*10d)/10d;
        postmeal_count_val_round = Math.round(postmeal_count_val*10d)/10d;
        nomeal_count_val_round = Math.round(nomeal_count_val*10d)/10d;

        premeal_count_portion = premeal_count_val_round/mean_count_val_round * 100;
        postmeal_count_portion = (postmeal_count_val_round/mean_count_val_round * 100) + premeal_count_portion;
        premeal_count_portion_int = Math.round(premeal_count_portion);
        postmeal_count_portion_int = Math.round(postmeal_count_portion);

        premeal_progress_val = get_count_range(ll_max_premeal, ll_min_premeal, premeal, start_date, end_date);
        postmeal_progress_val = get_count_range(ll_max_postmeal, ll_min_postmeal, postmeal, start_date, end_date);
        nomeal_progress_val = get_count_range(ll_max_nomeal, ll_min_nomeal, nomeal, start_date, end_date);
    }

    public void set_View() {
        txt_mean_count.setText(String.format("%.1f",mean_count_val_round));
        txt_premeal_count.setText(String.format("%.1f",premeal_count_val_round));
        txt_postmeal_count.setText(String.format("%.1f",postmeal_count_val_round));
        txt_nomeal_count.setText(String.format("%.1f",nomeal_count_val_round));

        txt_premeal_mean.setText(Integer.toString(premeal_mean_val));
        txt_postmeal_mean.setText(Integer.toString(postmeal_mean_val));
        txt_nomeal_mean.setText(Integer.toString(nomeal_mean_val));

        //mProgress_premeal.setProgress(premeal_progress_val);
        txt_premeal_progress.setText(Integer.toString(premeal_progress_val));

        //mProgress_postmeal.setProgress(postmeal_progress_val);
        txt_postmeal_progress.setText(Integer.toString(postmeal_progress_val));

        //mProgress_nomeal.setProgress(nomeal_progress_val);
        txt_nomeal_progress.setText(Integer.toString(nomeal_progress_val));

        value = 0;
        mValue_premeal = 0;
        mValue_postmeal = 0;
        mValue_nomeal = 0;

        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(premeal_progress_val);
        list.add(postmeal_progress_val);
        list.add(nomeal_progress_val);

        max_mValue = Collections.max(list);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() { // Thread 로 작업할 내용을 구현
                mProgress_count.setProgress((int)premeal_count_portion_int);
                mProgress_count.setSecondaryProgress((int)postmeal_count_portion_int);
                while(value <= max_mValue) {
                    value = value + 1;

                    if(mValue_premeal < premeal_progress_val)
                        mValue_premeal = mValue_premeal + 1;

                    if(mValue_postmeal < postmeal_progress_val)
                        mValue_postmeal = mValue_postmeal + 1;

                    if(mValue_nomeal < nomeal_progress_val)
                        mValue_nomeal = mValue_nomeal + 1;

                    handler.post(new Runnable() {
                        @Override
                        public void run() { // 화면에 변경하는 작업을 구현
                            mProgress_premeal.setProgress(mValue_premeal);
                            mProgress_postmeal.setProgress(mValue_postmeal);
                            mProgress_nomeal.setProgress(mValue_nomeal);
                        }
                    });

                    try {
                        Thread.sleep(8); // 시간지연
                    } catch (InterruptedException e) {    }
                } // end of while
            }
        });

        t.start(); // 쓰레드 시작
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_navi_center:
                listener.setFrag("HOME");
                break;
            case R.id.btn_navi_right:
                listener.setFrag("GRAPH");
                break;
            case R.id.btn_navi_left:

                if(during_option == During_1week)
                {
                    during_option = During_1month;
                    set_Data(During_1month);
                    set_View();
                    btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_week);
                    tv_header.setText("월간 통계");
                }
                else{
                    during_option = During_1week;
                    set_Data(During_1week);
                    set_View();
                    btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_month);
                    tv_header.setText("주간 통계");
                }

                break;
        }
    }

    public void get_duringtime(int duringoption)
    {
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/ MM/ dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, 1);
        Date caldate = cal.getTime();
        end_date = simpleDateFormat.format(caldate);

        cal.setTime(date);

        switch(duringoption)
        {
            case During_1week:
                cal.add(Calendar.DATE, -7);
                break;

            case During_1month:
                cal.add(Calendar.MONTH, -1);
                break;
        }

        caldate = cal.getTime();

        start_date = simpleDateFormat.format(caldate);

    }

    public int get_Avg(String mealoption, String st_date, String ed_date){

        String query = "SELECT AVG(glucose_val) FROM GLUCOSEDATA" +
                " WHERE meal = '" + mealoption +
                "'AND create_at BETWEEN '" + st_date + "' AND '" + ed_date + "';";

        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int AverageValue=c.getInt(0);
        return AverageValue;
    }

    public double get_Count(String mealoption, String st_date, String ed_date, int duringoption){

        String query = "SELECT * FROM GLUCOSEDATA" +
                " WHERE meal = '" + mealoption +
                "'AND create_at BETWEEN '" + st_date + "' AND '" + ed_date + "';";

        Cursor c = db.rawQuery(query, null);

        double size = c.getCount();

        switch(mealoption)
        {
            case premeal:
                premeal_cnt = (int) size;
                break;

            case postmeal:
                postmeal_cnt = (int) size;
                break;
            case nomeal:
                nomeal_cnt = (int) size;
                break;
        }

        double avg_count_val = 0.0;
        switch(duringoption)
        {
            case During_1week:
                avg_count_val = size / (double)7;
                break;

            case During_1month:
                avg_count_val = size / (double)30;
                break;
        }

        return avg_count_val;
    }

    public int get_count_range(int max, int min, String mealoption, String st_date, String ed_date) {
        int cnt = 0, result = 0;
        double temp = 0.0;

        String query = "SELECT count(*) FROM GLUCOSEDATA" +
                " WHERE glucose_val >= " + min + " AND glucose_val <= " + max +
                " AND meal = '" + mealoption +
                "' AND create_at BETWEEN '" + st_date + "' AND '" + ed_date + "';";

        Cursor c1 = db.rawQuery(query, null);
        c1.moveToFirst();
        cnt = c1.getInt(0);

        switch(mealoption)
        {
            case premeal:
                temp = (double) cnt / (double) premeal_cnt * 100;
                break;

            case postmeal:
                temp = (double) cnt / (double) postmeal_cnt * 100;
                break;

            case nomeal:
                temp = (double) cnt / (double) nomeal_cnt * 100;
                break;
        }

        result = (int) Math.round(temp);

        return result;
    }


}
