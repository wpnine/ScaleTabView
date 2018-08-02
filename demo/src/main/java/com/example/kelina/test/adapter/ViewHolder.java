package com.example.kelina.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {

    private TextView mItemView;

    public ViewHolder(TextView itemView) {
        super(itemView);
        mItemView = itemView;
    }

    public void setText(String text) {
        mItemView.setText(text);
    }
}
