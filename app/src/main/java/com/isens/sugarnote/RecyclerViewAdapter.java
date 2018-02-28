package com.isens.sugarnote;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private SharedPreferences prefs_root, prefs_user;
    private SharedPreferences.Editor editor_user;

    private Context context;
    private RecyclerViewHolder viewHolder;

    private String[] time, mealoption, sugar;
    private String userAccount;

    private View view;

    private int size;
    private int count;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
        prefs_root = context.getSharedPreferences("ROOT", 0);
        userAccount = prefs_root.getString("SIGNIN", "none");
        prefs_user = context.getSharedPreferences(userAccount, 0);
        editor_user = prefs_user.edit();
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
                if(prefs_user.getInt("PRELOW",50) <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= prefs_user.getInt("PREHIGH",100))
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "식후" :
                if(prefs_user.getInt("POSTLOW",100) <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= prefs_user.getInt("POSTHIGH",150))
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "공복" :
                if(prefs_user.getInt("NOLOW",75) <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= prefs_user.getInt("NOHIGH",125))
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.recycler_tag.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
        }

        /*switch (mealoption[position]) {
            case "식전":
                holder.ll_recycler_premeal.setBackgroundResource(R.drawable.border);
                holder.tv_premeal_sugar.setText(sugar[position]);
                holder.tv_premeal_date.setText(time[position]);
                holder.iv_postmeal_empty.setBackgroundResource(R.drawable.bg_recycler_empty);
                holder.iv_nomeal_empty.setBackgroundResource(R.drawable.bg_recycler_empty);
                if (prefs_user.getInt("PRELOW", 50) <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= prefs_user.getInt("PREHIGH", 100))
                    holder.tv_premeal_sugar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.tv_premeal_sugar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "식후":
                holder.ll_recycler_postmeal.setBackgroundResource(R.drawable.border);
                holder.tv_postmeal_sugar.setText(sugar[position]);
                holder.tv_postmeal_date.setText(time[position]);
                holder.iv_premeal_empty.setBackgroundResource(R.drawable.bg_recycler_empty);
                holder.iv_nomeal_empty.setBackgroundResource(R.drawable.bg_recycler_empty);
                if (prefs_user.getInt("POSTLOW", 100) <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= prefs_user.getInt("POSTHIGH", 150))
                    holder.tv_postmeal_sugar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.tv_postmeal_sugar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "공복":
                holder.ll_recycler_nomeal.setBackgroundResource(R.drawable.border);
                holder.tv_nomeal_sugar.setText(sugar[position]);
                holder.tv_nomeal_date.setText(time[position]);
                holder.iv_premeal_empty.setBackgroundResource(R.drawable.bg_recycler_empty);
                holder.iv_postmeal_empty.setBackgroundResource(R.drawable.bg_recycler_empty);
                if (prefs_user.getInt("NOLOW", 75) <= Integer.parseInt(sugar[position]) && Integer.parseInt(sugar[position]) <= prefs_user.getInt("NOHIGH", 125))
                    holder.tv_nomeal_sugar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                else
                    holder.tv_nomeal_sugar.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
        }*/
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
