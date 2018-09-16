package com.avik.recycleritemswipping;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Admin on 9/9/2018.
 */

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    private List<RecyclerItem> recyclerItems;

    public ViewAdapter(List<RecyclerItem> recyclerItems) {
        this.recyclerItems = recyclerItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerItem recyclerItem = recyclerItems.get(position);
        holder.tvItem.setText(recyclerItem.getItemText());
    }


    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvItem;
        public ViewHolder(View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.tvItem);
        }
    }
}
