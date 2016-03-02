package com.baiiu.krtvdemo;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baiiu.krtvdemo.pojo.KrTV;
import com.baiiu.krtvdemo.pojo.KrTVData;
import com.baiiu.krtvdemo.util.AssetsUtil;
import com.baiiu.krtvdemo.util.GsonUtil;
import com.baiiu.krtvdemo.view.MediaControllerLayout;
import com.baiiu.krtvdemo.view.VideoPlayer;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MediaControllerLayout mediaControllerLayout;

    private int layoutPosition = -1;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        mediaControllerLayout = (MediaControllerLayout) findViewById(R.id.mediaControllerLayout);
        mediaControllerLayout.setVisibility(View.INVISIBLE);
        mediaControllerLayout.setViewPlayerCallBack(new VideoPlayer.IViewPlayerCallBack() {
            @Override
            public void onClose() {
                mediaControllerLayout.setVisibility(View.INVISIBLE);
            }
        });

        String s = AssetsUtil.readAssets(this, "example.json");

        KrTV krTV = GsonUtil.parseJson(s, KrTV.class);
        List<KrTVData> list = krTV.data;
        KrTVAdapter krTVAdapter = new KrTVAdapter(this, list, this);
        recyclerView.setAdapter(krTVAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (layoutPosition == -1) {
                    return;
                }

                try {
                    int top = getClickedItemTop();
                    mediaControllerLayout.maximum(top);
                } catch (NullPointerException e) {
                    mediaControllerLayout.minimum();
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mediaControllerLayout != null) {
            mediaControllerLayout.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }

        if (mediaControllerLayout.isShown()) {
            mediaControllerLayout.close();
            mediaControllerLayout.setVisibility(View.INVISIBLE);
            return;
        }

        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mediaControllerLayout.close();
        mediaControllerLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        KrTVViewHolder holder = (KrTVViewHolder) v.getTag();

        layoutPosition = holder.getLayoutPosition();

        mediaControllerLayout.maximum(getClickedItemTop());
        mediaControllerLayout.setVisibility(View.VISIBLE);

        mediaControllerLayout.setVideoURI(Uri.parse(holder.videoSource));
    }


    private int getClickedItemTop() {
        View viewByPosition = linearLayoutManager.findViewByPosition(layoutPosition);
        if (viewByPosition != null) {
            int top = viewByPosition.getTop();
//            return top > 0 ? top : 0;
            return top;
        } else {
            throw new NullPointerException();
        }
    }
}
