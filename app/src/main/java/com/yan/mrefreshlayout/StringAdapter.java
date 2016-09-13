package com.yan.mrefreshlayout;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class StringAdapter extends RecyclerView.Adapter<StringAdapter.ViewHolder> {
    private List<String> strings;

    public StringAdapter(List<String> strings) {
        this.strings = strings;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder itemViewHolder, int position) {
        itemViewHolder.tvText.setText(strings.get(position));
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;

        public ViewHolder(View itemView) {
            super(itemView);
            tvText = (TextView) itemView.findViewById(android.R.id.text1);
            tvText.setTextColor(Color.argb(160,0,0,0));
        }
    }
}
