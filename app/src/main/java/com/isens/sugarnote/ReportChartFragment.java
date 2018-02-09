package com.isens.sugarnote;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReportChartFragment extends Fragment implements View.OnClickListener {

    private FragmentInterActionListener listener;

    private ListView m_ListView;
    private CustomAdapterReport m_Adapter;
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private int gls_val;
    private String date, meal, gls_val_txt;

    private View view;
    private Activity ac;

    private Button btn_navi_right, btn_navi_center, btn_navi_left;

    public ReportChartFragment() {

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
        view = inflater.inflate(R.layout.fragment_report_chart, container, false);

        m_ListView = (ListView) view.findViewById(R.id.report_list);

        m_Adapter = new CustomAdapterReport();

        m_ListView.setAdapter(m_Adapter);
        m_ListView.setOnItemLongClickListener(new ReportChartFragment.ListViewItemLongClickListener());
        m_ListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        btn_navi_center = (Button) ac.findViewById(R.id.btn_navi_center);
        btn_navi_right = (Button) ac.findViewById(R.id.btn_navi_right);
        btn_navi_left = (Button) ac.findViewById(R.id.btn_navi_left);

        btn_navi_center.setBackgroundResource(R.drawable.state_btn_navi_home);
        btn_navi_left.setBackgroundResource(R.drawable.state_btn_navi_user);
        btn_navi_right.setBackgroundResource(R.drawable.state_btn_navi_graph);

        btn_navi_center.setOnClickListener(this);
        btn_navi_right.setOnClickListener(this);
        btn_navi_left.setOnClickListener(this);

        set_Data();

        return view;
    }

    public void read_glucosedb(Cursor c) {
        date = c.getString(1);
        gls_val = c.getInt(2);
        meal = c.getString(3);
    }

    public void set_Data() {
        if (dbHelper == null) dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);
        db = dbHelper.getWritableDatabase();

        String query = "SELECT * FROM GLUCOSEDATA ORDER BY create_at ASC;";

        Cursor cursor = db.rawQuery(query, null);

        int size = cursor.getCount();

        if (size == 0)
            Toast.makeText(ac, "No Data", Toast.LENGTH_SHORT).show();

        //cursor.moveToFirst();

        while (cursor.moveToNext()) {
            read_glucosedb(cursor);

            gls_val_txt = Integer.toString(gls_val);

            m_Adapter.addItem(date, meal, gls_val_txt);

        }

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
                Toast.makeText(ac, "미구현", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private class ListViewItemLongClickListener implements AdapterView.OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

            // 터치 시 해당 아이템 이름 출력
            final Dialog dialog = new Dialog(ac);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_timelog);

            Button btn_timelog_modify = (Button) dialog.findViewById(R.id.btn_timelog_modify);
            Button btn_timelog_delete = (Button) dialog.findViewById(R.id.btn_timelog_delete);
            Button btn_timelog_cancel = (Button) dialog.findViewById(R.id.btn_timelog_cancel);

            final Spinner spin_timelog_meal = (Spinner) dialog.findViewById(R.id.spin_timelog_meal);
            final EditText et_timelog_glsval = (EditText) dialog.findViewById(R.id.et_gls_val);
            TextView tv_timelog_time = (TextView) dialog.findViewById(R.id.tv_timelog_time);

            final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ac, R.array.meal_option, android.R.layout.simple_spinner_dropdown_item);
            spin_timelog_meal.setAdapter(adapter);

            if (dbHelper == null)
                dbHelper = new DBHelper(ac, "GLUCOSEDATA.db", null, 1);

            db = dbHelper.getWritableDatabase();


//            mtimelogItems.add(m_Adapter.getItem(position));

            final String itemDate = m_Adapter.getItem(position).getDate();

            Log.i("JJ", "position : " + position);

            String query = "SELECT * FROM GLUCOSEDATA" +
                    " WHERE create_at = '" + itemDate + "';";

            Cursor cursor = db.rawQuery(query, null);

            cursor.moveToFirst();

            final String nDate = cursor.getString(1);
            int nVal = cursor.getInt(2);
            String nMeal = cursor.getString(3);

            tv_timelog_time.setText(nDate);
            et_timelog_glsval.setText(String.valueOf(nVal));

            if (nMeal.equals("식전"))
                spin_timelog_meal.setSelection(0);
            else if (nMeal.equals("식후"))
                spin_timelog_meal.setSelection(1);
            else if (nMeal.equals("공복"))
                spin_timelog_meal.setSelection(2);

            btn_timelog_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            btn_timelog_modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ac, "Modify Data", Toast.LENGTH_SHORT).show();
                    String mMeal = ((TextView) (spin_timelog_meal.getSelectedView())).getText().toString();
                    int mVal = Integer.valueOf(et_timelog_glsval.getText().toString());
                    gls_val = mVal;

                    db.execSQL("UPDATE GLUCOSEDATA SET glucose_val=" + mVal + ", meal = '" + mMeal
                            + "' WHERE create_at ='" + nDate + "';");
                    db.close();

                    m_Adapter.editItem(gls_val_txt, mMeal, position);

                    m_Adapter.notifyDataSetChanged();

                    dialog.dismiss();


                }
            });

            btn_timelog_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ac, "Delete Data", Toast.LENGTH_SHORT).show();
                    db.execSQL("DELETE FROM GLUCOSEDATA WHERE create_at='" + nDate + "';");
                    m_Adapter.deleteItem(position);
                    m_Adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });

            dialog.show();

            return false;
        }
    }

}
