package com.isens.sugarnote;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    public TextView tv_recycler;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        tv_recycler = (TextView) itemView.findViewById(R.id.tv_recycler);
    }

}
