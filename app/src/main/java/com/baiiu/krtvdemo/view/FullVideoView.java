package com.baiiu.krtvdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * author: baiiu
 * date: on 16/2/25 15:52
 * description:
 */
public class FullVideoView extends VideoView {
    public FullVideoView(Context context) {
        super(context);
    }

    public FullVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FullVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /*
        这段代码为经测试,不能确保使该videoView按最大的尺寸播放.尽量全部写为MeasureSpec.EXACTLY的
         */
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();//经过super按比例缩放的
        int measuredHeight = getMeasuredHeight();//经过super按比例缩放的


        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = widthSpecSize;
        int height = heightSpecSize;

        if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(width, height);//扩展为全屏
        } else if (widthSpecMode == MeasureSpec.EXACTLY) {
            width = Math.max(widthMeasureSpec, measuredWidth);
            height = measuredHeight;
        } else if (heightSpecMode == MeasureSpec.EXACTLY) {
            width = measuredWidth;
            height = Math.max(heightMeasureSpec, measuredHeight);
        } else {
            width = measuredWidth;
            height = measuredHeight;
        }

        setMeasuredDimension(width, height);
    }

}
