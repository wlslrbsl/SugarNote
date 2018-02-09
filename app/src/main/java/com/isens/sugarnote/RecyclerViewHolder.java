package com.isens.sugarnote;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    public TextView recycler_mealoption, recycler_sugar, recycler_time;
    public LinearLayout recycler_tag;

    public RecyclerViewHolder(View itemView) {
        super(itemView);

        recycler_tag = (LinearLayout) itemView.findViewById(R.id.recycler_tag);
        recycler_mealoption = (TextView) itemView.findViewById(R.id.recycler_mealoption);
        recycler_sugar = (TextView) itemView.findViewById(R.id.recycler_sugar);
        recycler_time = (TextView) itemView.findViewById(R.id.recycler_time);

    }

}
