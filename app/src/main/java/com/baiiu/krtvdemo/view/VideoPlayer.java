package com.baiiu.krtvdemo.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.baiiu.krtvdemo.R;

import java.util.Formatter;
import java.util.Locale;

/**
 * author: baiiu
 * date: on 16/2/24 16:43
 * description:
 */
public class VideoPlayer extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;

    private static final int sDefaultTimeout = 3000;

    private View container_top;
    //    private TextView tv_title;
    private ImageButton ibt_close;
    private View container_bottom;
    private TextView tv_time_current;
    private SeekBar mProgress;
    private TextView tv_time_total;
    private ImageButton ibt_share;
    private ImageButton ibt_shrink;
    private ImageButton ibt_pause_play;
    private ProgressBar progressBar;
    private View progressBar_container;

    private VideoView videoView;

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    private boolean isShowing;//悬浮控制层是否在展示
    private boolean mDragging;
    private boolean isReleased = true;
    private boolean isMini;

    private Uri currentUri;

    public IViewPlayerCallBack mViewPlayerCallBack;
    private ImageButton ibt_close_mini;

    public interface IViewPlayerCallBack {
        void onClose();
    }

    public void setViewPlayerCallBack(IViewPlayerCallBack viewPlayerCallBack) {
        this.mViewPlayerCallBack = viewPlayerCallBack;
    }

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.layout_controller, this);

        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        videoView = (VideoView) findViewById(R.id.videoView);

        container_top = findViewById(R.id.container_top);
//        tv_title = (TextView) findViewById(R.id.tv_title);
        ibt_close = (ImageButton) findViewById(R.id.ibt_close);

        container_bottom = findViewById(R.id.container_bottom);
        tv_time_current = (TextView) findViewById(R.id.tv_time_current);
        mProgress = (SeekBar) findViewById(R.id.seekbar);
        tv_time_total = (TextView) findViewById(R.id.tv_time_total);
        ibt_share = (ImageButton) findViewById(R.id.ibt_share);
        ibt_shrink = (ImageButton) findViewById(R.id.ibt_shrink);

        ibt_pause_play = (ImageButton) findViewById(R.id.ibt_pause_play);

        progressBar_container = findViewById(R.id.progressBar_container);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        ibt_close_mini = (ImageButton) findViewById(R.id.ibt_close_mini);

        container_top.setVisibility(INVISIBLE);
        container_bottom.setVisibility(INVISIBLE);
        ibt_pause_play.setVisibility(INVISIBLE);

        ibt_close.setOnClickListener(this);
        ibt_shrink.setOnClickListener(this);
        ibt_pause_play.setOnClickListener(this);
        ibt_close_mini.setOnClickListener(this);

        mProgress.setOnSeekBarChangeListener(this);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    /*
                     * add what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING
                     * fix : return what == 700 in Lenovo low configuration Android System
                     */
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START
                                || what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
                            progressBar.setVisibility(INVISIBLE);
                            progressBar_container.setVisibility(INVISIBLE);
                            isReleased = false;
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!fromUser) {
            return;
        }

        long duration = videoView.getDuration();
        long newposition = (duration * progress) / 1000L;
        videoView.seekTo((int) newposition);

        if (tv_time_current != null) {
            tv_time_current.setText(stringForTime((int) newposition));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mDragging = true;
        mHandler.removeMessages(SHOW_PROGRESS);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mDragging = false;
        setProgress();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibt_close:
            case R.id.ibt_close_mini:
                close();
                if (mViewPlayerCallBack != null) {
                    mViewPlayerCallBack.onClose();
                }
                break;
            case R.id.ibt_shrink:
                Context context = getContext();
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }
                break;
            case R.id.ibt_pause_play:
                if (videoView == null) {
                    return;
                }

                if (isReleased) {
                    setVideoURI(currentUri);
                    return;
                }

                if (videoView.isPlaying()) {
                    videoView.pause();
                    ibt_pause_play.setImageLevel(1);
                } else {
                    videoView.start();
                    ibt_pause_play.setImageLevel(0);
                }
                break;
        }
    }


    private final Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    dismiss();
                    break;
                case SHOW_PROGRESS:
                    int pos = setProgress();

                    if (!mDragging && isShowing && videoView != null && videoView.isPlaying()) {
                        msg = mHandler.obtainMessage(SHOW_PROGRESS);
                        mHandler.sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }

            return true;
        }
    });

    private int setProgress() {
        if (videoView == null || mDragging) {
            return 0;
        }
        int position = videoView.getCurrentPosition();
        int duration = videoView.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        if (tv_time_total != null)
            tv_time_total.setText(stringForTime(duration));
        if (tv_time_current != null)
            tv_time_current.setText(stringForTime(position));

        return position;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isReleased) {
            return true;
        }

        if (isMini) {
            ibt_shrink.performClick();
            return true;
        }

        //在手指按下时即显示
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isShowing) {
                    dismiss();
                } else {
                    occure();
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeMessages(FADE_OUT);
                if (isShowing) {
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), sDefaultTimeout);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                dismiss();
                break;
        }

        return true;
    }

    //展出,展出动画
    private void occure() {
        if (isReleased) {
            return;
        }

        isShowing = true;
        container_top.setVisibility(VISIBLE);
        container_bottom.setVisibility(VISIBLE);
        ibt_pause_play.setVisibility(VISIBLE);

        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }

    public void setMini(boolean mini) {
        this.isMini = mini;
        if (isMini) {
            mHandler.removeMessages(FADE_OUT);
            ibt_close_mini.setVisibility(VISIBLE);
            dismiss();
        } else {
            ibt_close_mini.setVisibility(GONE);
        }
    }

    //消失,消失动画
    private void dismiss() {
        if (isReleased) {
            return;
        }

        isShowing = false;
        container_bottom.setVisibility(INVISIBLE);
        container_top.setVisibility(INVISIBLE);
        ibt_pause_play.setVisibility(INVISIBLE);

        mHandler.removeMessages(SHOW_PROGRESS);
    }

    public void setVideoURI(Uri uri) {
        if (uri == null) {
            return;
        }

        if (uri.equals(currentUri)) {
            return;
        }

        this.currentUri = uri;
        progressBar_container.setVisibility(VISIBLE);
        progressBar.setVisibility(VISIBLE);
        ibt_pause_play.setVisibility(INVISIBLE);
        videoView.setVideoURI(uri);
        isReleased = true;
        videoView.start();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && isMini) {
//            mHandler.removeMessages(FADE_OUT);
//            dismiss();
//        }
    }

    public void close() {
        if (videoView != null) {
            if (videoView.canPause()) {
                videoView.pause();
            }
            videoView.stopPlayback();
            progressBar_container.setVisibility(VISIBLE);
            progressBar.setVisibility(INVISIBLE);
            ibt_pause_play.setVisibility(View.VISIBLE);
            ibt_pause_play.setImageLevel(1);
            mHandler.removeMessages(FADE_OUT);
            isReleased = true;
        }
    }

}
