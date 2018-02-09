package com.isens.sugarnote;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.isens.sugarnote.R.id.chart;
import static com.isens.sugarnote.R.id.end;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportGraphFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Activity ac;

    private FragmentInterActionListener listener;

    private Button btn_navi_right, btn_navi_center, btn_navi_left;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;

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
    private int x_Axix_option = 1, during_week = 1, during_month = 2;
    int max_yVal, min_yVal, max_yVal_premeal, min_yVal_premeal, max_yVal_postmeal, min_yVal_postmeal, max_yVal_nomeal, min_yVal_nomeal;
    private int mealoption = 1;

    public ReportGraphFragment() {
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
        view = inflater.inflate(R.layout.fragment_report_graph, container, false);

        prefs = ac.getSharedPreferences("PrefName", 0);
        editor = prefs.edit();

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_home);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_premeal);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_chart);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);

        init_view();

        if (dbHelper == null) dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
        db = dbHelper.getWritableDatabase();

        if (isExistdata() == true) {
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
            Toast.makeText(ac, "데이터 없음", Toast.LENGTH_SHORT).show();
        }

        return view;
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

        mChart = (LineChart) view.findViewById(chart);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setOnChartGestureListener(new OnChartGestureListener() {

            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

                if(x_Axix_option == during_week) {
                    x_Axix_option = during_month;
                    mChart.setVisibleXRangeMinimum(30f);
                    mChart.moveViewToX(end);
                    mChart.invalidate();
                    Toast.makeText(ac, "1달", Toast.LENGTH_SHORT).show();
                }
                else{
                    x_Axix_option = during_week;
                    mChart.setVisibleXRangeMinimum(7f);
                    mChart.moveViewToX(end);
                    mChart.invalidate();
                    Toast.makeText(ac, "1주", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
            }


        });

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
        lineDataSet1.setColor(Color.parseColor("#FF002060"));
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setCircleRadius(5);
        lineDataSet1.setCircleColorHole(Color.WHITE);
        lineDataSet1.setDrawCircleHole(true);
        lineDataSet1.setCircleColor(Color.parseColor("#FF002060"));

        lineDataSet2 = new LineDataSet(glucose_postmeal, "식후");
        lineDataSet2.setColor(Color.parseColor("#FF002060"));
        lineDataSet2.setLineWidth(2);
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setCircleRadius(5);
        lineDataSet2.setCircleColorHole(Color.WHITE);
        lineDataSet2.setDrawCircleHole(true);
        lineDataSet2.setCircleColor(Color.parseColor("#FF002060"));

        lineDataSet3 = new LineDataSet(glucose_nomeal, "공복");
        lineDataSet3.setColor(Color.parseColor("#FF002060"));
        lineDataSet3.setLineWidth(2);
        lineDataSet3.setDrawValues(false);
        lineDataSet3.setCircleRadius(5);
        lineDataSet3.setCircleColorHole(Color.WHITE);
        lineDataSet3.setDrawCircleHole(true);
        lineDataSet3.setCircleColor(Color.parseColor("#FF002060"));

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
        mChart.setScaleYEnabled(false);
        mChart.setScaleXEnabled(false);
        mChart.setAutoScaleMinMaxEnabled(false);
        mChart.getAxisLeft().setAxisMinValue(min_yVal_premeal);
        mChart.getAxisLeft().setAxisMaxValue(max_yVal_premeal);
        mChart.getAxisLeft().setTextSize(13);

        if (x_Axix_option == during_week) // 확인요
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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_navi_center:
                listener.setFrag("HOME");
                break;

            case R.id.btn_navi_right:
                listener.setFrag("CHART");
                break;

            case R.id.btn_navi_left:

                if(meal_option == premeal) {
                    meal_option = postmeal;
                    set_XAxis();
                    lineDataSets.remove(0);
                    lineDataSets.add(lineDataSet2);
                    mChart.setData(new LineData(xAXES, lineDataSets));
                    mChart.getAxisLeft().setAxisMinValue(min_yVal_postmeal);
                    mChart.getAxisLeft().setAxisMaxValue(max_yVal_postmeal);
                    mChart.getData().setHighlightEnabled(false);

                    btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_postmeal);
                    Toast.makeText(ac, "식후", Toast.LENGTH_SHORT).show();
                }
                else if(meal_option == postmeal){
                    meal_option = nomeal;
                    set_XAxis();
                    lineDataSets.remove(0);
                    lineDataSets.add(lineDataSet3);
                    mChart.setData(new LineData(xAXES, lineDataSets));
                    mChart.getAxisLeft().setAxisMinValue(min_yVal_nomeal);
                    mChart.getAxisLeft().setAxisMaxValue(max_yVal_nomeal);
                    mChart.getData().setHighlightEnabled(false);
                    btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_nomeal);
                    Toast.makeText(ac, "공복", Toast.LENGTH_SHORT).show();
                }
                else{
                    meal_option = premeal;
                    set_XAxis();
                    lineDataSets.remove(0);
                    lineDataSets.add(lineDataSet1);
                    mChart.setData(new LineData(xAXES, lineDataSets));
                    mChart.getAxisLeft().setAxisMinValue(min_yVal_premeal);
                    mChart.getAxisLeft().setAxisMaxValue(max_yVal_premeal);
                    btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_premeal);
                    Toast.makeText(ac, "식전", Toast.LENGTH_SHORT).show();
                }

                show_limitline();
                mChart.invalidate();

                break;
        }
    }
}
