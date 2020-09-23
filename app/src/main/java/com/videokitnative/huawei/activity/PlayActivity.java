package com.videokitnative.huawei.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.widget.SeekBar;

import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.videokitnative.huawei.R;
import com.videokitnative.huawei.VideoKitPlayApplication;
import com.videokitnative.huawei.contract.OnPlayWindowListener;
import com.videokitnative.huawei.contract.OnWisePlayerListener;

public class PlayActivity extends AppCompatActivity implements Callback , TextureView.SurfaceTextureListener , OnWisePlayerListener , OnPlayWindowListener{
    private static final String TAG = "MainActivity";
    private static WisePlayerFactory factory;
    SurfaceView surfaceView ;
    WisePlayer player;
    // Player entity
    private WisePlayer wisePlayer;
    // Player listener
    private OnWisePlayerListener onWisePlayerListener;
    private OnPlayWindowListener onPlayWindowListener;
    private int systemUiVisibility = 0;
    private SurfaceHolder surfaceHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        onWisePlayerListener = this;
        onPlayWindowListener =this;
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        // Set the current vertical screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
          initPlayer();
          initVideo();
    }

    private void initPlayer() {
        if (VideoKitPlayApplication.getWisePlayerFactory() == null) {
            return;
        }
        Log.d(TAG, "initPlayer:  wiseplayer CREATED..");
        player = VideoKitPlayApplication.getWisePlayerFactory().createWisePlayer();

    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        player.setView(surfaceView);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }

    public void playVideo(View view) {
        initPlayer();
        initVideo();
    }

    private void initVideo() {

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(onPlayWindowListener);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.setVisibility(View.VISIBLE);

        player.setErrorListener(onWisePlayerListener);
        player.setEventListener(onWisePlayerListener);
        player.setResolutionUpdatedListener(onWisePlayerListener);
        player.setReadyListener(onWisePlayerListener);
        player.setLoadingListener(onWisePlayerListener);
        player.setPlayEndListener(onWisePlayerListener);
        player.setSeekEndListener(onWisePlayerListener);

        player.setVideoType(PlayerConstants.PlayMode.PLAY_MODE_NORMAL);
        player.setBookmark(10000);
        player.setCycleMode(PlayerConstants.CycleMode.MODE_CYCLE);
        // Method 1: Set one URL for a video.
        player.setPlayUrl("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
        player.ready();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onError(WisePlayer wisePlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onEvent(WisePlayer wisePlayer, int i, int i1, Object o) {
        return false;
    }

    @Override
    public void onLoadingUpdate(WisePlayer wisePlayer, int i) {

    }

    @Override
    public void onStartPlaying(WisePlayer wisePlayer) {

    }

    @Override
    public void onPlayEnd(WisePlayer wisePlayer) {

    }

    @Override
    public void onReady(WisePlayer wisePlayer) {
        Log.d(TAG, "onReady: PLAYER.START");
        player.start();
    }

    @Override
    public void onResolutionUpdated(WisePlayer wisePlayer, int i, int i1) {

    }

    @Override
    public void onSeekEnd(WisePlayer wisePlayer) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        player = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // player.pause();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(int pos) {

    }

    @Override
    public void onSettingItemClick(String itemSelect, int settingType) {

    }
}
