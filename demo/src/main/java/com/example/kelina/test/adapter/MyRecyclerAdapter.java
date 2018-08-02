package com.example.kelina.test.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kelina.test.R;
import com.example.kelina.test.util.DensityUtils;

/**
 * @author cginechen
 * @date 2016-12-29
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtils.dp2px(parent.getContext(), 50)));
        textView.setBackgroundResource(R.drawable.list_item_bg_with_border_bottom);
        int paddingHor = DensityUtils.dp2px(parent.getContext(), 16);
        textView.setPadding(paddingHor, 0, paddingHor, 0);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextSize(16);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setText("item " + position);
    }


    @Override
    public int getItemCount() {
        return 50;
    }
}
