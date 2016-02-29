package com.baiiu.krtvdemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * author: baiiu
 * date: on 16/2/29 10:12
 * description:
 */
public class MediaControllerLayout extends FrameLayout {

//    private ViewDragHelper mDragHelper;


    private VideoPlayer videoPlayer;
    private FrameLayout.LayoutParams videoPlayerLayoutParams;

    private int orginalHeight;

    private boolean isMinimum = false;
//    private boolean isDragging = false;

    public MediaControllerLayout(Context context) {
        this(context, null);
    }

    public MediaControllerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MediaControllerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
//        mDragHelper = ViewDragHelper.create(this, callback);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                orginalHeight = videoPlayer.getMeasuredHeight();
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
                videoPlayerLayoutParams.width = -1;
                videoPlayerLayoutParams.height = -1;
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

                if (isMinimum) {
                    videoPlayerLayoutParams.gravity = Gravity.BOTTOM | Gravity.END;
                    videoPlayerLayoutParams.width = 600;
                    videoPlayerLayoutParams.height = 600;
                } else {
                    videoPlayerLayoutParams.gravity = Gravity.TOP | Gravity.START;
                    videoPlayerLayoutParams.width = -1;
                    videoPlayerLayoutParams.height = orginalHeight;
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
        videoPlayer.setVideoURI(videoURI);
    }


    public void maximum(int topMargin) {
        if (topMargin == 0 && videoPlayerLayoutParams.topMargin == 0) {
            return;
        }

        ensureVideoPlayer();

        isMinimum = false;

        videoPlayerLayoutParams.gravity = Gravity.TOP | Gravity.START;
        videoPlayerLayoutParams.width = -1;
        videoPlayerLayoutParams.height = orginalHeight;

        videoPlayerLayoutParams.topMargin = topMargin;
        videoPlayer.setLayoutParams(videoPlayerLayoutParams);
    }

    public void minimum() {
        if (isMinimum /*|| isDragging*/) {
            return;
        }

        ensureVideoPlayer();

        isMinimum = true;

        videoPlayerLayoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        videoPlayerLayoutParams.topMargin = 0;
        videoPlayerLayoutParams.width = 600;
        videoPlayerLayoutParams.height = 600;

        videoPlayer.setLayoutParams(videoPlayerLayoutParams);
    }

    private void ensureVideoPlayer() {
        if (videoPlayer == null) {
            throw new IllegalStateException("videoPlayer不能为空");
        }

        if (videoPlayerLayoutParams == null) {
            videoPlayerLayoutParams = new FrameLayout.LayoutParams(-1, orginalHeight);
        }
    }


//    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
//        private int mOrignalLeft;
//
//        @Override
//        public boolean tryCaptureView(View child, int pointerId) {
//            return isMinimum && child == videoPlayer;
//        }
//
//        @Override
//        public void onViewCaptured(View capturedChild, int activePointerId) {
//            isDragging = true;
//            mOrignalLeft = capturedChild.getLeft();
//        }
//
//        @Override
//        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            videoPlayerLayoutParams.rightMargin = left;
//            videoPlayer.setLayoutParams(videoPlayerLayoutParams);
//            return mOrignalLeft;
//        }
//
//        @Override
//        public void onViewReleased(View releasedChild, float xvel, float yvel) {
//            isDragging = false;
//        }
//
//
//        @Override
//        public void onViewDragStateChanged(int state) {
//        }
//
//
//        @Override
//        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
////            LogUtil.d("top " + top + ", left " + left);
//        }
//    };


//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent event) {
//        return mDragHelper.shouldInterceptTouchEvent(event);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mDragHelper.processTouchEvent(event);
//
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//
//        return isMinimum && mDragHelper.isViewUnder(videoPlayer, x, y) || super.onTouchEvent(event);
//    }


}
