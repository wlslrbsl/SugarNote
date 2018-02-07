package com.isens.sugarnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.isens.module.bloodglucosemonitor.BgmBootLoader;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitor;
import com.isens.module.bloodglucosemonitor.BloodGlucoseMonitorCallBack;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.isens.sugarnote.R.id.chart;
import static com.isens.sugarnote.R.id.end;

/**
 * Created by jeongjick on 2017-11-25.
 */

public class GraphActivity extends AppCompatActivity implements View.OnClickListener {
    BloodGlucoseMonitor _bloodGlucoseMonitor;
    private LinearLayout home_btn, graphset_btn;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private final String premeal = "식전", postmeal = "식후", nomeal = "공복";
    private String meal_option = "";
    private ArrayList<Entry> glucose_premeal, glucose_postmeal, glucose_nomeal;
    private LineChart mChart;
    private int ll_max_premeal, ll_min_premeal, ll_max_postmeal, ll_min_postmeal, ll_max_nomeal, ll_min_nomeal;
    private LineDataSet lineDataSet1, lineDataSet2, lineDataSet3;
    private ArrayList<String> xAXES;
    private ArrayList<ILineDataSet> lineDataSets;
    private final long timestamp_const = 86400000; //1day = 1000millisec * 60 sec * 60 minute * 24 hour
    private String firstdata_date, lastdata_date, firstdate_premeal, lastdate_premeal, firstdate_postmeal, lastdate_postmeal, firstdate_nomeal, lastdate_nomeal = null;
    private long reference_timestamp = 0, last_idx = 0, last_idx_premeal = 0, last_idx_postmeal = 0, last_idx_nomeal = 0;
    private RadioGroup rg_drawer_during, rg_drawer_meal;
    private RadioButton rb_drawer_1week, rb_drawer_1month, rb_drawer_premeal, rb_drawer_postmeal, rb_drawer_nomeal;
    int max_yVal, min_yVal, max_yVal_premeal, min_yVal_premeal, max_yVal_postmeal, min_yVal_postmeal, max_yVal_nomeal, min_yVal_nomeal;
    private FrameLayout flContainer;

    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    // BGM CB register
    private final BloodGlucoseMonitorCallBack _bgm_callBack = new BloodGlucoseMonitorCallBack() {
        @Override
        public void bgmcallBackMethod(String str, int status, int value) {

            if (status == BloodGlucoseMonitor.BGM_STATUS_INSERT_STRIP) {
                finish();
                Intent intent = new Intent(getBaseContext(), MeasureActivity.class);
                intent.putExtra("Strip", true);
                startActivity(intent);
            }
        }

        @Override
        public void bgmBootLoadercallBackMethod(String str, int status, BgmBootLoader bootloader) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_actionbar);
        home_btn = (LinearLayout) findViewById(R.id.btn_home);
        graphset_btn = (LinearLayout) findViewById(R.id.btn_graphset);
        TextView title = (TextView) findViewById(R.id.title_txt);
        title.setText("혈 당 차 트");
        graphset_btn.setVisibility(View.VISIBLE);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFFE66D21));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);       // 화면을 landscape(가로) 화면으로 고정하고 싶은 경우

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph);

        _bloodGlucoseMonitor = BloodGlucoseMonitor.getInstance();
        BloodGlucoseMonitor.setCallbackInterface(_bgm_callBack);
        _bloodGlucoseMonitor.enableBGM(_bloodGlucoseMonitor.BGM_INT_SWITCH);

        flContainer = (FrameLayout) findViewById(R.id.activity_graph_container);

        home_btn.setOnClickListener(this);
        graphset_btn.setOnClickListener(this);

        prefs = getSharedPreferences("PrefName", 0);
        editor = prefs.edit();

        init_view();

        if (dbHelper == null) dbHelper = new DBHelper(this, "GLUCOSEDATA.db", null, 1);
        db = dbHelper.getWritableDatabase();

        boolean exist_data = isExistdata();

        if (exist_data == true) {
            get_max_min();
            get_firstdatatime();
            meal_option = premeal;
            glucose_premeal = get_Dataset(premeal);
            glucose_postmeal = get_Dataset(postmeal);
            glucose_nomeal = get_Dataset(nomeal);
            set_XAxis();
            set_linedata();
            show_limitline();
        } else {
            Toast.makeText(this, "데이터 없음", Toast.LENGTH_SHORT).show();
        }

        rg_drawer_during.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdb_drawer_week:
                        mChart.setVisibleXRangeMinimum(7f);
                        mChart.moveViewToX(end);
                        mChart.invalidate();
                        break;
                    case R.id.rdb_drawer_month:
                        mChart.setVisibleXRangeMinimum(30f);
                        mChart.moveViewToX(end);
                        mChart.invalidate();

                        break;
                }
            }
        });

        rg_drawer_meal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdb_drawer_premeal:

                        meal_option = premeal;
                        set_XAxis();
                        lineDataSets.remove(0);
                        lineDataSets.add(lineDataSet1);
                        mChart.setData(new LineData(xAXES, lineDataSets));
                        mChart.getAxisLeft().setAxisMinValue(min_yVal_premeal);
                        mChart.getAxisLeft().setAxisMaxValue(max_yVal_premeal);

                        break;

                    case R.id.rdb_drawer_postmeal:

                        meal_option = postmeal;
                        set_XAxis();
                        lineDataSets.remove(0);
                        lineDataSets.add(lineDataSet2);
                        mChart.setData(new LineData(xAXES, lineDataSets));
                        mChart.getAxisLeft().setAxisMinValue(min_yVal_postmeal);
                        mChart.getAxisLeft().setAxisMaxValue(max_yVal_postmeal);

                        break;

                    case R.id.rdb_drawer_nomeal:

                        meal_option = nomeal;
                        set_XAxis();
                        lineDataSets.remove(0);
                        lineDataSets.add(lineDataSet3);
                        mChart.setData(new LineData(xAXES, lineDataSets));
                        mChart.getAxisLeft().setAxisMinValue(min_yVal_nomeal);
                        mChart.getAxisLeft().setAxisMaxValue(max_yVal_nomeal);

                        break;
                }
                show_limitline();
                mChart.invalidate();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_home:
                finish();
                break;

            case R.id.btn_graphset:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_graph_drawer);

                if (!drawer.isDrawerOpen(Gravity.RIGHT))
                    drawer.openDrawer(Gravity.RIGHT);
                else if (drawer.isDrawerOpen(Gravity.RIGHT))
                    drawer.closeDrawer(Gravity.RIGHT);
                break;
        }
    }

    public boolean isExistdata() {
        String query = "SELECT * FROM GLUCOSEDATA ORDER BY create_at ASC;";
        Cursor c = db.rawQuery(query, null);
        int size = c.getCount();

        if (size == 0)
            return false;
        else
            return true;
    }

    public void get_firstdatatime() {
        String query = "SELECT * FROM GLUCOSEDATA ORDER BY create_at ASC;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        String date = c.getString(1);
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/ MM/ dd");

        date = date.substring(0, date.indexOf(","));
        firstdata_date = date;
        try {
            Date time_stamp = simpleDateFormat.parse(firstdata_date);

            long milliseconds = time_stamp.getTime();

            reference_timestamp = milliseconds;
            Log.i("JJ", "firstRef: " + time_stamp);
            Log.i("JJ", "firstRef: " + reference_timestamp);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        c.moveToLast();
        date = c.getString(1);
        date = date.substring(0, date.indexOf(","));
        lastdata_date = date;
        last_idx = 0;
        try {
            Date time_stamp = simpleDateFormat.parse(lastdata_date);

            last_idx = (time_stamp.getTime() - reference_timestamp) / timestamp_const;

            Log.i("JJ", "lastRef: " + time_stamp);
            Log.i("JJ", "lastRef: " + last_idx);

        } catch (Exception e1) {
            e1.printStackTrace();
        }


        query = "SELECT * FROM GLUCOSEDATA WHERE meal = '식전' ORDER BY create_at ASC;";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        date = c.getString(1);

        date = date.substring(0, date.indexOf(","));
        firstdate_premeal = date;
        try {
            Date time_stamp = simpleDateFormat.parse(firstdate_premeal);

            long milliseconds = time_stamp.getTime();

            reference_timestamp = milliseconds;
            Log.i("JJ", "firstRef: " + time_stamp);
            Log.i("JJ", "firstRef: " + reference_timestamp);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        c.moveToLast();
        date = c.getString(1);
        date = date.substring(0, date.indexOf(","));
        lastdate_premeal = date;
        last_idx_premeal = 0;
        try {
            Date time_stamp = simpleDateFormat.parse(lastdate_premeal);

            last_idx_premeal = (time_stamp.getTime() - reference_timestamp) / timestamp_const;

            Log.i("JJ", "lastRef: " + time_stamp);
            Log.i("JJ", "lastRef: " + last_idx_premeal);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        query = "SELECT * FROM GLUCOSEDATA WHERE meal = '식후' ORDER BY create_at ASC;";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        date = c.getString(1);

        date = date.substring(0, date.indexOf(","));
        firstdate_postmeal = date;
        try {
            Date time_stamp = simpleDateFormat.parse(firstdate_postmeal);

            long milliseconds = time_stamp.getTime();

            reference_timestamp = milliseconds;
            Log.i("JJ", "firstRef: " + time_stamp);
            Log.i("JJ", "firstRef: " + reference_timestamp);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        c.moveToLast();
        date = c.getString(1);
        date = date.substring(0, date.indexOf(","));
        lastdate_postmeal = date;
        last_idx_postmeal = 0;
        try {
            Date time_stamp = simpleDateFormat.parse(lastdate_postmeal);

            last_idx_postmeal = (time_stamp.getTime() - reference_timestamp) / timestamp_const;

            Log.i("JJ", "lastRef: " + time_stamp);
            Log.i("JJ", "lastRef: " + last_idx_postmeal);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        query = "SELECT * FROM GLUCOSEDATA WHERE meal = '공복' ORDER BY create_at ASC;";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        date = c.getString(1);

        date = date.substring(0, date.indexOf(","));
        firstdate_nomeal = date;
        try {
            Date time_stamp = simpleDateFormat.parse(firstdate_nomeal);

            long milliseconds = time_stamp.getTime();

            reference_timestamp = milliseconds;
            Log.i("JJ", "firstRef: " + time_stamp);
            Log.i("JJ", "firstRef: " + reference_timestamp);

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        c.moveToLast();
        date = c.getString(1);
        date = date.substring(0, date.indexOf(","));
        lastdate_nomeal = date;
        last_idx_nomeal = 0;
        try {
            Date time_stamp = simpleDateFormat.parse(lastdate_nomeal);

            last_idx_nomeal = (time_stamp.getTime() - reference_timestamp) / timestamp_const;

            Log.i("JJ", "lastRef: " + time_stamp);
            Log.i("JJ", "lastRef: " + last_idx_nomeal);

        } catch (Exception e1) {
            e1.printStackTrace();
        }


    }

    public ArrayList<Entry> get_Dataset(String mealoption) {

        ArrayList<Entry> array = new ArrayList<Entry>();
        int prev_val = 0;
        int gls_val = 0;
        long x_idx = 0;
        String date;
        long prev_idx = -1;
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/ MM/ dd");

        String query = "SELECT * FROM GLUCOSEDATA" +
                " WHERE meal = '" + mealoption + "'ORDER BY create_at ASC;";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {

            date = cursor.getString(1);
            date = date.substring(0, date.indexOf(","));

            try {
                Date time_stamp = simpleDateFormat.parse(date);
                x_idx = (time_stamp.getTime() - reference_timestamp) / timestamp_const;
                Log.i("JJ", "Date and Time: " + time_stamp);
                Log.i("JJ", "Date and Time: " + x_idx);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if (prev_idx == x_idx) {
                gls_val = cursor.getInt(2);
                array.get(array.size() - 1).setVal((gls_val + prev_val) / 2);
                prev_val = (gls_val + prev_val) / 2;
            } else {
                gls_val = cursor.getInt(2);
                array.add(new Entry(gls_val, (int) x_idx));
                prev_val = gls_val;

            }
            prev_idx = x_idx;
        }
        return array;
    }

    public void set_XAxis() {
        xAXES = new ArrayList<>();
        Date date;
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy/ MM/ dd");
        String xVal = null;
        Calendar cal = Calendar.getInstance();

        if (meal_option == premeal) {
            try {

                date = simpleDateFormat.parse(firstdate_premeal);
                cal.setTime(date);
                cal.add(Calendar.DATE, -1);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for (int i = 0; i <= last_idx_premeal; i++) {
                cal.add(Calendar.DATE, 1);
                Date caldate = cal.getTime();
                xVal = simpleDateFormat.format(caldate);
                xVal = xVal.substring(6);
                xAXES.add(xVal);

            }
        }

        if (meal_option == postmeal) {
            try {

                date = simpleDateFormat.parse(firstdate_postmeal);
                cal.setTime(date);
                cal.add(Calendar.DATE, -1);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for (int i = 0; i <= last_idx_postmeal; i++) {
                cal.add(Calendar.DATE, 1);
                Date caldate = cal.getTime();
                xVal = simpleDateFormat.format(caldate);
                xVal = xVal.substring(6);
                xAXES.add(xVal);

            }
        }

        if (meal_option == nomeal) {
            try {

                date = simpleDateFormat.parse(firstdate_nomeal);
                cal.setTime(date);
                cal.add(Calendar.DATE, -1);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for (int i = 0; i <= last_idx_nomeal; i++) {
                cal.add(Calendar.DATE, 1);
                Date caldate = cal.getTime();
                xVal = simpleDateFormat.format(caldate);
                xVal = xVal.substring(6);
                xAXES.add(xVal);

            }
        }


    }

    public void init_view() {

        rg_drawer_during = (RadioGroup) findViewById(R.id.radio_group_drawer1);
        rb_drawer_1week = (RadioButton) findViewById(R.id.rdb_drawer_week);
        rb_drawer_1month = (RadioButton) findViewById(R.id.rdb_drawer_month);

        rg_drawer_meal = (RadioGroup) findViewById(R.id.radio_group_drawer2);
        rb_drawer_premeal = (RadioButton) findViewById(R.id.rdb_drawer_premeal);
        rb_drawer_postmeal = (RadioButton) findViewById(R.id.rdb_drawer_postmeal);
        rb_drawer_nomeal = (RadioButton) findViewById(R.id.rdb_drawer_nomeal);

        mChart = (LineChart) findViewById(chart);

        glucose_premeal = new ArrayList<Entry>();
        glucose_postmeal = new ArrayList<Entry>();
        glucose_nomeal = new ArrayList<Entry>();

         /*목표치*/
        ll_max_premeal = Integer.valueOf(prefs.getString("KEY_BEFORE_MAX", "100"));
        ll_min_premeal = Integer.valueOf(prefs.getString("KEY_BEFORE_MIN", "80"));
        ll_max_postmeal = Integer.valueOf(prefs.getString("KEY_AFTER_MAX", "120"));
        ll_min_postmeal = Integer.valueOf(prefs.getString("KEY_AFTER_MIN", "100"));
        ll_max_nomeal = Integer.valueOf(prefs.getString("KEY_EMPTY_MAX", "110"));
        ll_min_nomeal = Integer.valueOf(prefs.getString("KEY_EMPTY_MIN", "90"));

    }

    public void set_linedata() {

        lineDataSets = new ArrayList<>();

        lineDataSet1 = new LineDataSet(glucose_premeal, "식전");
        lineDataSet1.setColor(Color.parseColor("#E66D21"));
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setCircleRadius(5);
        lineDataSet1.setCircleColorHole(Color.WHITE);
        lineDataSet1.setDrawCircleHole(true);
        lineDataSet1.setCircleColor(Color.parseColor("#E66D21"));

        lineDataSet2 = new LineDataSet(glucose_postmeal, "식후");
        lineDataSet2.setColor(Color.parseColor("#E66D21"));
        lineDataSet1.setLineWidth(2);
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setCircleRadius(5);
        lineDataSet2.setCircleColorHole(Color.WHITE);
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setCircleColor(Color.parseColor("#E66D21"));


        lineDataSet3 = new LineDataSet(glucose_nomeal, "공복");
        lineDataSet3.setColor(Color.parseColor("#E66D21"));
        lineDataSet1.setLineWidth(2);
        lineDataSet3.setDrawValues(false);
        lineDataSet3.setCircleRadius(8);
        lineDataSet3.setCircleColorHole(Color.WHITE);
        lineDataSet3.setDrawCircleHole(true);
        lineDataSet3.setCircleColor(Color.parseColor("#E66D21"));


        lineDataSets.add(lineDataSet1);
        //lineDataSets.add(lineDataSet2);
        //lineDataSets.add(lineDataSet3);

        YAxis yAxisRight = mChart.getAxisRight();
        yAxisRight.setEnabled(false);


        mChart.setData(new LineData(xAXES, lineDataSets));
        mChart.setDescription("");
        mChart.setDrawGridBackground(false);
        mChart.setDrawBorders(true);
        mChart.getData().setHighlightEnabled(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setTextSize(10);
        mChart.getLegend().setEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(false);
        mChart.getAxisLeft().setAxisMinValue(min_yVal_premeal);
        mChart.getAxisLeft().setAxisMaxValue(max_yVal_premeal);
        mChart.getAxisLeft().setTextSize(13);

        if (rb_drawer_1week.isChecked())
            mChart.setVisibleXRangeMaximum(7f);
        else
            mChart.setVisibleXRangeMaximum(30f);

        mChart.moveViewToX(end);
        mChart.invalidate();
    }

    public void show_limitline() {
        LimitLine ll_max, ll_min;
        if (meal_option == premeal) {
            mChart.getAxisLeft().removeAllLimitLines();
            ll_max = new LimitLine(ll_max_premeal, "Max Target");
            mChart.getAxisLeft().addLimitLine(ll_max);
            ll_max.setLineWidth(1f);
            ll_max.setTextSize(10f);
            ll_max.setLineColor(Color.RED);
            ll_min = new LimitLine(ll_min_premeal, "Min Target");
            mChart.getAxisLeft().addLimitLine(ll_min);
            ll_min.setLineWidth(1f);
            ll_min.setTextSize(10f);
            ll_min.setLineColor(Color.GREEN);

            float ymax = mChart.getAxisLeft().getAxisMaximum();
            float ymin = mChart.getAxisLeft().getAxisMinimum();
            if (ll_max_premeal >= ymax) {
                mChart.getAxisLeft().setAxisMaxValue(ll_max_premeal + 10);
            }

            if (ll_min_premeal <= ymin) {
                mChart.getAxisLeft().setAxisMinValue(ll_min_premeal - 10);
            }

            mChart.notifyDataSetChanged();
            mChart.invalidate();
//            mChart.setAutoScaleMinMaxEnabled(true);
        } else if (meal_option == postmeal) {
            mChart.getAxisLeft().removeAllLimitLines();
            ll_max = new LimitLine(ll_max_postmeal, "Max Target");
            mChart.getAxisLeft().addLimitLine(ll_max);
            ll_max.setLineWidth(1f);
            ll_max.setTextSize(10f);
            ll_max.setLineColor(Color.RED);
            ll_min = new LimitLine(ll_min_postmeal, "Min Target");
            mChart.getAxisLeft().addLimitLine(ll_min);
            ll_min.setLineWidth(1f);
            ll_min.setTextSize(10f);
            ll_min.setLineColor(Color.GREEN);

            float ymax = mChart.getAxisLeft().getAxisMaximum();
            float ymin = mChart.getAxisLeft().getAxisMinimum();
            if (ll_max_postmeal >= ymax) {
                mChart.getAxisLeft().setAxisMaxValue(ll_max_postmeal + 10);
            }

            if (ll_min_postmeal <= ymin) {
                mChart.getAxisLeft().setAxisMinValue(ll_min_postmeal - 10);
            }
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        } else { // mealoption == nomeal
            mChart.getAxisLeft().removeAllLimitLines();
            ll_max = new LimitLine(ll_max_nomeal, "Max Target");
            mChart.getAxisLeft().addLimitLine(ll_max);
            ll_max.setLineWidth(1f);
            ll_max.setTextSize(10f);
            ll_max.setLineColor(Color.RED);
            ll_min = new LimitLine(ll_min_nomeal, "Min Target");
            mChart.getAxisLeft().addLimitLine(ll_min);
            ll_min.setLineWidth(1f);
            ll_min.setTextSize(10f);
            ll_min.setLineColor(Color.GREEN);

            float ymax = mChart.getAxisLeft().getAxisMaximum();
            float ymin = mChart.getAxisLeft().getAxisMinimum();
            if (ll_max_nomeal >= ymax) {
                mChart.getAxisLeft().setAxisMaxValue(ll_max_nomeal + 10);
            }

            if (ll_min_nomeal <= ymin) {
                mChart.getAxisLeft().setAxisMinValue(ll_min_nomeal - 10);
            }
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    void get_max_min() {
        String query = "SELECT MAX(glucose_val) FROM GLUCOSEDATA;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        max_yVal = c.getInt(0) + 10;

        query = "SELECT MIN(glucose_val) FROM GLUCOSEDATA;";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        min_yVal = c.getInt(0) - 10;
        Log.i("JJ", "Max :" + Integer.toString(max_yVal));
        Log.i("JJ", "Min :" + Integer.toString(min_yVal));

        query = "SELECT MIN(glucose_val) FROM GLUCOSEDATA WHERE meal = '식전';";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        min_yVal_premeal = c.getInt(0) - 10;

        query = "SELECT MAX(glucose_val) FROM GLUCOSEDATA WHERE meal = '식전';";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        max_yVal_premeal = c.getInt(0) + 10;

        query = "SELECT MIN(glucose_val) FROM GLUCOSEDATA WHERE meal = '식후';";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        min_yVal_postmeal = c.getInt(0) - 10;

        query = "SELECT MAX(glucose_val) FROM GLUCOSEDATA WHERE meal = '식후';";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        max_yVal_postmeal = c.getInt(0) + 10;

        query = "SELECT MIN(glucose_val) FROM GLUCOSEDATA WHERE meal = '공복';";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        min_yVal_nomeal = c.getInt(0) - 10;

        query = "SELECT MAX(glucose_val) FROM GLUCOSEDATA WHERE meal = '공복';";
        c = db.rawQuery(query, null);
        c.moveToFirst();
        max_yVal_nomeal = c.getInt(0) + 10;

    }

}
