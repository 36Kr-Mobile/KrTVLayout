package com.baiiu.krtvdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.baiiu.krtvdemo.pojo.TV;

import java.util.List;


/**
 * author: baiiu
 * date: on 16/2/24 10:50
 * description:
 */
public class KrTVAdapter extends RecyclerView.Adapter<KrTVViewHolder> {
    private Context context;
    private List<TV> list;
    private View.OnClickListener onClickListener;

    public KrTVAdapter(Context context, List<TV> list, View.OnClickListener onClickListener) {
        this.context = context;
        this.list = list;
        this.onClickListener = onClickListener;
    }

    @Override
    public KrTVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new KrTVViewHolder(context, parent, onClickListener);
    }

    @Override
    public void onBindViewHolder(KrTVViewHolder holder, int position) {
        if (list == null || list.get(position) == null) {
            return;
        }
        holder.bindData(list.get(position).tv);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

}