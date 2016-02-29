package com.baiiu.krtvdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baiiu.krtvdemo.pojo.TVData;
import com.bumptech.glide.Glide;

/**
 * author: baiiu
 * date: on 16/2/24 10:52
 * description:
 */
public class KrTVViewHolder extends RecyclerView.ViewHolder {

    private final ImageView imageView;
    private final TextView tv_title;
    private final TextView tv_duration;
    public String videoSource;

    public KrTVViewHolder(Context context, ViewGroup parent, View.OnClickListener onClickListener) {
        super(LayoutInflater.from(context).inflate(R.layout.holder_krtv, parent, false));

        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        tv_title = (TextView) itemView.findViewById(R.id.tv_title);
        tv_duration = (TextView) itemView.findViewById(R.id.tv_duration);

        if (onClickListener != null) {
            itemView.setOnClickListener(onClickListener);
        }
    }


    public void bindData(TVData tv) {
        itemView.setTag(this);

        videoSource = tv.videoSource;

        Glide.with(imageView.getContext()).load(tv.featureImg).centerCrop().into(imageView);
        tv_title.setText(tv.title);
        tv_duration.setText(tv.duration);
    }


}
