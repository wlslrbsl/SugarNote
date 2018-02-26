package com.isens.sugarnote;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by BSPL on 2018-02-26.
 */

public class PowerAdapter extends BaseAdapter {
    private ArrayList<String> list;
    private Activity activity;

    PowerAdapter(Activity activity){
        this.activity = activity;
        list = new ArrayList<String>();
    }

    // 리스트에 값을 추가할 메소드
    public void setName(String name)    {
        list.add(name);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewHolder    holder  = null;
        final int pos = position;
        TextView name;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_power, parent, false);
            name    = (TextView) convertView.findViewById(R.id.txt_title);
            holder = new ListViewHolder();
            holder.name = name;
            // list values save
            convertView.setTag(holder);
            // 텍스트 보이기
            name.setVisibility(View.VISIBLE);
        }
        else
        {
            // list values get
            holder = (ListViewHolder) convertView.getTag();
            name = holder.name;
        }
        // 리스트 이름 보이기
        name.setText(list.get(pos));
        // 리스트 아이템을 터치 했을 때 이벤트 발생
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(activity.getApplicationContext(), "선택한 이름:" + list.get(pos), Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    private class ListViewHolder {
        TextView name;
    }
}


