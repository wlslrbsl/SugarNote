package com.isens.sugarnote;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    public TextView recycler_mealoption, recycler_sugar, recycler_time;
    public LinearLayout recycler_tag;

    public LinearLayout ll_recycler_premeal, ll_recycler_postmeal, ll_recycler_nomeal;

    public TextView tv_premeal_sugar, tv_postmeal_sugar, tv_nomeal_sugar, tv_premeal_date, tv_postmeal_date, tv_nomeal_date;
    public ImageView iv_premeal_empty, iv_postmeal_empty, iv_nomeal_empty;

    public RecyclerViewHolder(View itemView) {
        super(itemView);

        recycler_tag = (LinearLayout) itemView.findViewById(R.id.recycler_tag);
        recycler_mealoption = (TextView) itemView.findViewById(R.id.recycler_mealoption);
        recycler_sugar = (TextView) itemView.findViewById(R.id.recycler_sugar);
        recycler_sugar = (TextView) itemView.findViewById(R.id.recycler_sugar);
        recycler_time = (TextView) itemView.findViewById(R.id.recycler_time);

        /*ll_recycler_premeal = (LinearLayout) itemView.findViewById(R.id.ll_recycler_premeal);
        ll_recycler_postmeal = (LinearLayout) itemView.findViewById(R.id.ll_recycler_postmeal);
        ll_recycler_nomeal = (LinearLayout) itemView.findViewById(R.id.ll_recycler_nomeal);

        tv_premeal_sugar = (TextView) itemView.findViewById(R.id.tv_premeal_sugar);
        tv_postmeal_sugar = (TextView) itemView.findViewById(R.id.tv_postmeal_sugar);
        tv_nomeal_sugar = (TextView) itemView.findViewById(R.id.tv_nomeal_sugar);
        tv_premeal_date = (TextView) itemView.findViewById(R.id.tv_premeal_date);
        tv_postmeal_date = (TextView) itemView.findViewById(R.id.tv_postmeal_date);
        tv_nomeal_date = (TextView) itemView.findViewById(R.id.tv_nomeal_date);

        iv_premeal_empty = (ImageView) itemView.findViewById(R.id.iv_premeal_empty);
        iv_postmeal_empty = (ImageView) itemView.findViewById(R.id.iv_postmeal_empty);
        iv_nomeal_empty = (ImageView) itemView.findViewById(R.id.iv_nomeal_empty);*/

    }

}
