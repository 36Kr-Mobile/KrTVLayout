## KrTVLayout
使用VideoView播放视频,交互类似于网易新闻播放器

## 关于该Demo
1. 视频全部出自KrTV.
2. 示例代码较为粗糙,之后有机会精细化.

## 特点
使用MediaControllerLayout包裹VideoPlayer,实现对视频播放的控制.
你可以使用自己的VideoPlayer,仅仅用该ControllerLayout包裹OK啦.

```
    <com.baiiu.krtvdemo.view.MediaControllerLayout
        android:id="@+id/mediaControllerLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        myapp:playerMaxHeight="200dp"
        myapp:playerMiniHeight="100dp"
        myapp:playerMiniWidth="150dp">

        <com.baiiu.krtvdemo.view.VideoPlayer
            android:id="@+id/videoPlayer"
            android:layout_width="match_parent"
            android:layout_height="200dp" />

    </com.baiiu.krtvdemo.view.MediaControllerLayout>

```

## ScreenShot
![KrTVDemo](images/KrTVLayout.gif "krTVLayout")