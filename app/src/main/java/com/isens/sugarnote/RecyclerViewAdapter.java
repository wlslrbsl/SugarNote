package com.isens.sugarnote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private Context context;
    private RecyclerViewHolder viewHolder;

    private String[] time, mealoption, sugar;

    private View view;

    private int size;
    private int count;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.recyclerview_custom, parent, false);
        viewHolder = new RecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.recycler_time.setText(time[position]);
        holder.recycler_mealoption.setText(mealoption[position]);
        holder.recycler_sugar.setText(sugar[position]);

        switch (mealoption[position]) {
            case "식전" :
                if(80 <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= 100)
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "식후" :
                if(100 <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= 120)
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "공복" :
                if(90 <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= 110)
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;

        time = new String[size];
        mealoption = new String[size];
        sugar = new String[size];

        count = 0;
    }

    public void addItem(String time, String mealoption, String sugar) {
        this.time[count] = time;
        this.mealoption[count] = mealoption;
        this.sugar[count] = sugar;

        count++;
    }
}
