package com.isens.sugarnote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment implements View.OnClickListener, GridView.OnItemClickListener {

    private Activity ac;
    private View view;
    private SharedPreferences prefs_root;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private RecyclerViewAdapter recyclerViewAdapter;

    private DrawerLayout drawer_layout;
    private RecyclerView recycler_view;
    private LinearLayoutManager linearLayoutManager;

    private GridView gv_calendar;
    private TextView tv_drawer_month, tv_drawer_year, tv_calendar_date, tv_calendar_month, tv_calendar_day;
    private ImageView iv_no_data_calendar;

    private Button btn_navi_center, btn_navi_right, btn_navi_left, btn_recycler_backward, btn_recycler_forward;
    private LinearLayout btn_calendar_next, btn_calendar_pre, ll_recycler_view;

    private FragmentInterActionListener listener;
    private Calendar calendar;
    private Cursor cursor;

    private String[] days = {"일", "월", "화", "수", "목", "금", "토"};
    private String month = "", date = "", time, mealoption, calendar_head;
    private int whatDay, whatDate, tmpDate, tmpDay, maxDate, firstDay, whatMonth, whatYear, selYear, selMonth, selDate, sugar, cursorSize, selDay;
    private long now;
    private String db_name, userAccount;

    public CalendarFragment() {
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
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        prefs_root = ac.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        db_name = "GLUCOSEDATA_" + userAccount + ".db";
        drawer_layout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        gv_calendar = (GridView) view.findViewById(R.id.gv_calendar);
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);

        tv_drawer_month = (TextView) view.findViewById(R.id.tv_drawer_month);
        tv_drawer_year = (TextView) view.findViewById(R.id.tv_drawer_year);
        tv_calendar_date = (TextView) view.findViewById(R.id.tv_calendar_date);
        tv_calendar_month = (TextView) view.findViewById(R.id.tv_calendar_month);
        tv_calendar_day = (TextView) view.findViewById(R.id.tv_calendar_day);

        iv_no_data_calendar = (ImageView) view.findViewById(R.id.iv_no_data_calendar);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);
        btn_recycler_backward = (Button) view.findViewById(R.id.btn_recycler_backward);
        btn_recycler_forward = (Button) view.findViewById(R.id.btn_recycler_forward);

        btn_calendar_next = (LinearLayout) view.findViewById(R.id.btn_calendar_next);
        btn_calendar_pre = (LinearLayout) view.findViewById(R.id.btn_calendar_pre);
        ll_recycler_view = (LinearLayout) view.findViewById(R.id.ll_recycler_view);

        gv_calendar.setOnItemClickListener(this);

        btn_navi_center.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_calendar_next.setOnClickListener(this);
        btn_calendar_pre.setOnClickListener(this);
        btn_recycler_backward.setOnClickListener(this);
        btn_recycler_forward.setOnClickListener(this);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_home);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_alarm);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_calendar);
        btn_navi_left.setEnabled(true);
        btn_navi_right.setEnabled(true);

        linearLayoutManager = new LinearLayoutManager(ac);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recycler_view.setLayoutManager(linearLayoutManager);

        if (dbHelper == null)
            dbHelper = new DBHelper(ac, db_name, null, 1);

        setToday();
        setDrawer();
        calenderView();

        db = dbHelper.getWritableDatabase();

        recyclerViewAdapter = new RecyclerViewAdapter(ac);

        set_recycler();

        return view;
    }

    public void setDrawer() {
        gv_calendar.setAdapter(new GridViewAdapter(ac, maxDate, firstDay, selDate, whatMonth, selMonth, whatYear, selYear));
        tv_drawer_month.setText(String.valueOf(whatMonth + 1));
        tv_drawer_year.setText(String.valueOf(whatYear));
    }

    public void setToday() {
        now = System.currentTimeMillis();
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(now));

        calendarCalculate();

        selYear = whatYear;
        selMonth = whatMonth;
        selDate = whatDate;
        selDay = whatDay;
    }

    public void calendarCalculate() {
        whatMonth = calendar.get(Calendar.MONTH);
        whatDay = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday ... 7 = Saturday
        whatDate = calendar.get(Calendar.DATE);
        whatYear = calendar.get(Calendar.YEAR);

        maxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        tmpDay = whatDay;
        tmpDate = whatDate;

        while (tmpDate % 7 != 1) {
            tmpDate++;
            tmpDay++;
        }
        firstDay = tmpDay % 7;

        if (firstDay == 0)
            firstDay = 7;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_navi_center:
                drawer_layout.closeDrawer(Gravity.START);
                listener.setFrag("HOME");
                break;

            case R.id.btn_navi_right:
                if (!drawer_layout.isDrawerOpen(Gravity.START)) {
                    drawer_layout.openDrawer(Gravity.START);
                    ll_recycler_view.setVisibility(View.INVISIBLE);
                    btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_listview);
                    btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_today);
                    setDrawer();
                } else if (drawer_layout.isDrawerOpen(Gravity.START)) {
                    drawer_layout.closeDrawer(Gravity.START);
                    ll_recycler_view.setVisibility(View.VISIBLE);
                    btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_calendar);
                    btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_alarm);
                    whatYear = selYear;
                    whatMonth = selMonth;
                    whatDate = selDate;
                    selDay = whatDay;
                }
                break;

            case R.id.btn_navi_left:
                if (drawer_layout.isDrawerOpen(Gravity.START)) {
                    setToday();
                    calendarCalculate();
                    selMonth = whatMonth;
                    selDate = whatDate;
                    setDrawer();
                    calenderView();
                    set_recycler();
                } else {
                    listener.setFrag("ALARM");
                }
                break;

            case R.id.btn_calendar_next:
                whatMonth = whatMonth + 1;
                calendar.set(Calendar.MONTH, whatMonth);
                calendarCalculate();
                setDrawer();
                break;

            case R.id.btn_calendar_pre:
                whatMonth = whatMonth - 1;
                calendar.set(Calendar.MONTH, whatMonth);
                calendarCalculate();
                setDrawer();
                break;

            case R.id.btn_recycler_backward:
                calendar.set(selYear, selMonth, selDate);
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                calendarCalculate();

                selYear = whatYear;
                selMonth = whatMonth;
                selDate = whatDate;
                selDay = whatDay;

                calenderView();
                set_recycler();
                break;

            case R.id.btn_recycler_forward:
                calendar.set(selYear, selMonth, selDate);
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                calendarCalculate();

                selYear = whatYear;
                selMonth = whatMonth;
                selDate = whatDate;
                selDay = whatDay;

                calenderView();
                set_recycler();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ll_recycler_view.setVisibility(View.VISIBLE);
        if (position < firstDay + 6 || maxDate + firstDay + 5 < position) {
            Log.d("calendar", "blank");
        } else {
            selDate = Integer.parseInt((String) parent.getAdapter().getItem(position));
            selMonth = whatMonth;
            selYear = whatYear;
            selDay = whatDay;
            calenderView();
            set_recycler();
            drawer_layout.closeDrawer(Gravity.START);
            btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_calendar);
            btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_alarm);
        }
    }

    public void calenderView() {
        if (selMonth < 10)
            month = "0" + (selMonth + 1);
        else
            month = String.valueOf(selMonth + 1);
        if (selDate < 10)
            date = "0" + selDate;
        else
            date = String.valueOf(selDate);

        calendar_head = selYear + "/ " + month + "/ " + date;

        tv_calendar_month.setText(selYear + ". " + month + ".");
        tv_calendar_date.setText(String.valueOf(selDate));
        tv_calendar_day.setText("(" + days[selDay - 1] + ")");
    }

    public void read_glucosedb(Cursor c) {
        time = c.getString(1);
        sugar = c.getInt(2);
        mealoption = c.getString(3);
    }

    void set_recycler() {
        String querry = "SELECT * FROM GLUCOSEDATA WHERE create_at LIKE '%" + calendar_head + "%';";
        cursor = db.rawQuery(querry, null);

        cursorSize = cursor.getCount();
        recyclerViewAdapter.setSize(cursorSize);

        while (cursor.moveToNext()) {
            read_glucosedb(cursor);
            recyclerViewAdapter.addItem(time.substring(14, 19), mealoption, String.valueOf(sugar));
        }

        if (cursorSize == 0) {
            iv_no_data_calendar.setVisibility(View.VISIBLE);
        } else
            iv_no_data_calendar.setVisibility(View.INVISIBLE);

        recycler_view.setAdapter(recyclerViewAdapter);
    }

}

