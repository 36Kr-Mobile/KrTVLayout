package com.baiiu.krtvdemo.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.baiiu.krtvdemo.R;
import com.baiiu.krtvdemo.util.LogUtil;

/**
 * author: baiiu
 * date: on 16/2/29 10:12
 * description:
 */
public class MediaControllerLayout extends FrameLayout {

    private ViewDragHelper mDragHelper;

    private VideoPlayer videoPlayer;
    private FrameLayout.LayoutParams videoPlayerLayoutParams;

    //最小化的宽度和高度
    private int playerMiniWidth;
    private int playerMiniHeight;
    private int playerMaxiHeight;

    private boolean isMinimum = false;
    private boolean isDragging = false;
    private int topMargin;
    private int leftMargin;

    public MediaControllerLayout(Context context) {
        this(context, null);
    }

    public MediaControllerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MediaControllerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MediaControllerLayout);
        if (typedArray != null) {
            playerMiniWidth = (int) typedArray.getDimension(R.styleable.MediaControllerLayout_playerMiniWidth, 600);
            playerMiniHeight = (int) typedArray.getDimension(R.styleable.MediaControllerLayout_playerMiniHeight, 600);
            playerMaxiHeight = (int) typedArray.getDimension(R.styleable.MediaControllerLayout_playerMaxHeight, 600);
            typedArray.recycle();
        }

        mDragHelper = ViewDragHelper.create(this, callback);
        mDragHelper.cancel();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int measuredWidth = getMeasuredWidth();
                int measuredHeight = getMeasuredHeight();
                topMargin = measuredHeight - playerMiniHeight;
                leftMargin = measuredWidth - playerMiniWidth;

                LogUtil.d(topMargin + ", " + leftMargin);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        videoPlayer = (VideoPlayer) findViewById(R.id.videoPlayer);
        videoPlayerLayoutParams = (FrameLayout.LayoutParams) videoPlayer.getLayoutParams();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //也可以在这里调整
        Context context = getContext();
        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                videoPlayerLayoutParams.topMargin = 0;
                videoPlayerLayoutParams.leftMargin = 0;
                videoPlayerLayoutParams.width = -1;
                videoPlayerLayoutParams.height = -1;
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

                if (isMinimum) {
                    videoPlayerLayoutParams.topMargin = topMargin;
                    videoPlayerLayoutParams.leftMargin = leftMargin;
                    videoPlayerLayoutParams.width = playerMiniWidth;
                    videoPlayerLayoutParams.height = playerMiniHeight;
                } else {
                    videoPlayerLayoutParams.width = -1;
                    videoPlayerLayoutParams.height = playerMaxiHeight;
                }
            }

            videoPlayer.setLayoutParams(videoPlayerLayoutParams);
        }
    }

    public void close() {
        if (videoPlayer != null) {
            videoPlayer.close();
        }
    }

    public void setVideoURI(Uri videoURI) {
        if (videoPlayer != null) {
            videoPlayer.setVideoURI(videoURI);
        }
    }

    public void setViewPlayerCallBack(VideoPlayer.IViewPlayerCallBack viewPlayerCallBack) {
        if (videoPlayer != null) {
            videoPlayer.setViewPlayerCallBack(viewPlayerCallBack);
        }

    }

    public void maximum(int topMargin) {
        if (topMargin == 0 && videoPlayerLayoutParams.topMargin == 0) {
            return;
        }

        ensureVideoPlayer();

        isMinimum = false;

        videoPlayerLayoutParams.width = -1;
        videoPlayerLayoutParams.height = playerMaxiHeight;
        videoPlayerLayoutParams.leftMargin = 0;
        videoPlayerLayoutParams.topMargin = topMargin;

        videoPlayer.setLayoutParams(videoPlayerLayoutParams);
    }

    public void minimum() {
        if (isMinimum || isDragging) {
            return;
        }

        ensureVideoPlayer();

        isMinimum = true;

        videoPlayerLayoutParams.topMargin = topMargin;
        videoPlayerLayoutParams.leftMargin = leftMargin;
        videoPlayerLayoutParams.width = playerMiniWidth;
        videoPlayerLayoutParams.height = playerMiniHeight;

        videoPlayer.setLayoutParams(videoPlayerLayoutParams);
    }

    private void ensureVideoPlayer() {
        if (videoPlayer == null) {
            throw new IllegalStateException("videoPlayer不能为空");
        }

        if (videoPlayerLayoutParams == null) {
            videoPlayerLayoutParams = new FrameLayout.LayoutParams(-1, playerMaxiHeight);
        }
    }


    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        private int mOriginalLeft;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return isMinimum && child == videoPlayer;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            isDragging = true;
            mOriginalLeft = capturedChild.getLeft();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            isDragging = false;
        }


        @Override
        public void onViewDragStateChanged(int state) {
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
        }
    };


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);

        int x = (int) event.getX();
        int y = (int) event.getY();

        return isMinimum && mDragHelper.isViewUnder(videoPlayer, x, y) || super.onTouchEvent(event);
    }


}
