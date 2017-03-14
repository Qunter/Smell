package com.yufa.smell.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by luyufa on 2016/8/29.
 */
public abstract class Adapter<T> extends RecyclerView.Adapter<ViewHolder> {

    private Context context;
    private List<T> data;
    private int layoutId;

    public Adapter(Context context, List<T> data, int layoutId) {
        this.context = context;
        this.data = data;
        this.layoutId = layoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = ViewHolder.createViewHolder(context,parent,layoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.updatePosition(position);
        convert(holder, data.get(position));
    }

    public abstract void convert(ViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return data.size();
    }
}
