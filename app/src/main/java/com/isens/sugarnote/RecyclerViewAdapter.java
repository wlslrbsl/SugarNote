package com.isens.sugarnote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private Context context;
    private String[] str;
    private RecyclerViewHolder viewHolder;

    private View view;

    public RecyclerViewAdapter(Context context, String[] str) {
        this.context = context;
        this.str = str;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.recyclerview_custom, parent, false);
        viewHolder = new RecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.tv_recycler.setText(str[position]);
    }

    @Override
    public int getItemCount() {
        return str.length;
    }
}
