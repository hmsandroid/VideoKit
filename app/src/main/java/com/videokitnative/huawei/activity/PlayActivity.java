package com.videokitnative.huawei.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.huawei.hms.videokit.player.WisePlayer;
import com.huawei.hms.videokit.player.common.PlayerConstants;
import com.videokitnative.huawei.R;
import com.videokitnative.huawei.contract.OnPlayWindowListener;
import com.videokitnative.huawei.contract.OnWisePlayerListener;
import com.videokitnative.huawei.control.PlayControl;
import com.videokitnative.huawei.utils.Constants;
import com.videokitnative.huawei.utils.DeviceUtil;
import com.videokitnative.huawei.utils.LogUtil;
import com.videokitnative.huawei.utils.PlayControlUtil;
import com.videokitnative.view.PlayView;

import android.view.TextureView.SurfaceTextureListener;
import android.widget.Toast;

import java.io.Serializable;

public class PlayActivity extends AppCompatActivity implements Callback , SurfaceTextureListener , OnWisePlayerListener , OnPlayWindowListener{
    private static final String TAG = "MainActivity";
    // Play view
    private PlayView playView;


    private int systemUiVisibility = 0;
    String url;
    String name;

    // Play status
    private boolean isPlaying = false;
    // Play control
    private PlayControl playControl;
    // Play the View is created
    private boolean hasSurfaceCreated = false;
    // Whether the suspend state
    private boolean isSuspend = false;
    // Whether the user is touch seekbar
    private boolean isUserTrackingTouch = false;
    // Whether at the front desk
    private boolean isResume = false;
    // Play complete
    private boolean isPlayComplete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Keep the screen on
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();
        if (getIntent().hasExtra("url") && getIntent().hasExtra("name")) {
            name = getIntent().getStringExtra("name");
            url = getIntent().getStringExtra("url");
        }

        playControl = new PlayControl(this, this,name,url);
        // Some of the properties of preserving vertical screen
        systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        // Set the current vertical screen
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // If failed to initialize, exit the current interface directly
        if (playControl.initPlayFail()) {
            Toast.makeText(this, getResources().getString(R.string.init_play_fail), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            ready();
        }
    }


    /**`
     * Prepare playing
     */
    private void ready() {
        playControl.setCurrentPlayData(getIntentExtra());
        playView.showBufferingView();
        playControl.ready();
    }

    private Serializable getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getSerializableExtra(Constants.VIDEO_PLAY_DATA);
        } else {
            return null;
        }
    }
    /**
     * init the layout
     */
    private void initView() {
        playView = new PlayView(this, this, this);
        setContentView(playView.getContentView());
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        LogUtil.d(TAG, "surface created");
        hasSurfaceCreated = true;
        playControl.setSurfaceView(playView.getSurfaceView());
        if (isSuspend) {
            isSuspend = false;
            playControl.playResume(PlayerConstants.ResumeType.KEEP);
            if (!updateViewHandler.hasMessages(Constants.PLAYING_WHAT)) {
                updateViewHandler.sendEmptyMessage(Constants.PLAYING_WHAT);
            }
        }
    }
    /**
     * Update the player view
     */
    private Handler updateViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg == null) {
                LogUtil.i(TAG, "current mss is empty.");
                return;
            }
            switch (msg.what) {
                case Constants.PLAYING_WHAT:
                    if (!isUserTrackingTouch) {
                        playView.updatePlayProgressView(playControl.getCurrentTime(), playControl.getBufferTime(),
                                playControl.getBufferingSpeed(), playControl.getCurrentBitrate());
                        sendEmptyMessageDelayed(Constants.PLAYING_WHAT, Constants.DELAY_MILLIS_500);
                    }
                    break;
                case Constants.UPDATE_PLAY_STATE:
                    playView.updatePlayCompleteView();
                    removeCallbacksAndMessages(null);
                    break;
                case Constants.PLAY_ERROR_FINISH:
                    finish();
                    break;
                case Constants.UPDATE_SWITCH_BITRATE_SUCCESS:
                    playView.hiddenSwitchingBitrateTextView();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        hasSurfaceCreated = false;
        LogUtil.d(TAG, "surfaceDestroyed");
        isSuspend = true;
        playControl.suspend();
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


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        LogUtil.d(TAG, "onStopTrackingTouch Progress:" + seekBar.getProgress());
        isPlayComplete = false;
        playControl.updateCurProgress(seekBar.getProgress());
        playView.showBufferingView();
        playView.updatePlayProgressView(seekBar.getProgress(), playControl.getBufferTime(),
                playControl.getBufferingSpeed(), playControl.getCurrentBitrate());
        isUserTrackingTouch = false;
        updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING_WHAT, Constants.DELAY_MILLIS_500);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogUtil.d(TAG, "onStopTrackingTouch Progress:" + seekBar.getProgress());
        isPlayComplete = false;
        playControl.updateCurProgress(seekBar.getProgress());
        playView.showBufferingView();
        playView.updatePlayProgressView(seekBar.getProgress(), playControl.getBufferTime(),
                playControl.getBufferingSpeed(), playControl.getCurrentBitrate());
        isUserTrackingTouch = false;
        updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING_WHAT, Constants.DELAY_MILLIS_500);
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
    public void onLoadingUpdate(WisePlayer wisePlayer, final int percent) {
        LogUtil.d(TAG, "update buffering percent :" + percent);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (percent < 100) {
                    playView.updateBufferingView(percent);
                } else {
                    playView.dismissBufferingView();
                }
            }
        });
    }
    @Override
    public void onStartPlaying(WisePlayer wisePlayer) {
        LogUtil.d(TAG, "onStartPlaying");
        isPlayComplete = false;
    }
    @Override
    public void onPlayEnd(WisePlayer wisePlayer) {
        LogUtil.d(TAG, "onPlayEnd " + wisePlayer.getCurrentTime());
        playControl.clearPlayProgress();
        isPlaying = false;
        isPlayComplete = true;
        updateViewHandler.sendEmptyMessageDelayed(Constants.UPDATE_PLAY_STATE, Constants.DELAY_MILLIS_1000);
    }

    @Override
    public void onReady(final WisePlayer wisePlayer) {
        Log.d(TAG, "onReady: PLAYER.START");
        LogUtil.d(TAG, "onReady");
        playControl.start();
        isPlaying = true;
        // Make sure the main thread to update
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playView.updatePlayView(wisePlayer);
                if (isResume) {
                    playView.setPauseView();
                }
                playView.setContentView(wisePlayer, playControl.getName());
                updateViewHandler.sendEmptyMessageDelayed(Constants.PLAYING_WHAT, Constants.DELAY_MILLIS_500);
            }
        });
    }

    @Override
    public void onResolutionUpdated(WisePlayer wisePlayer, int w, int h) {
        LogUtil.d(TAG, "current video width:" + w + " height:" + h);
        playView.setContentView(wisePlayer, playControl.getName());
    }

    @Override
    public void onSeekEnd(WisePlayer wisePlayer) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume isSuspend:" + isSuspend);
        isResume = true;
        if (hasSurfaceCreated) {
            playControl.setBookmark();
            playControl.playResume(PlayerConstants.ResumeType.KEEP);
            if (!updateViewHandler.hasMessages(Constants.PLAYING_WHAT)) {
                updateViewHandler.sendEmptyMessage(Constants.PLAYING_WHAT);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isPlayComplete) {
            playControl.savePlayProgress();
            isPlayComplete = false;
        }
        playControl.stop();
        playControl.release();
        // Mute only on the current video effect
        PlayControlUtil.setIsMute(false);
        if (updateViewHandler != null) {
            updateViewHandler.removeCallbacksAndMessages(null);
            updateViewHandler = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
        isResume = false;
        playView.onPause();
        playControl.onPause();
        if (updateViewHandler != null) {
            updateViewHandler.removeCallbacksAndMessages(null);
        }
    }



    @Override
    public void onItemClick(int pos) {

    }

    @Override
    public void onSettingItemClick(String itemSelect, int settingType) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                changePlayState();
                break;
            case R.id.back_tv:
                backPress();
                break;
            case R.id.fullscreen_btn:
                setFullScreen();
                break;
            default:
                break;
        }
    }
    /**
     * Modify the state of play
     */
    private void changePlayState() {
        playControl.setPlayData(isPlaying);
        if (isPlaying) {
            isPlaying = false;
            updateViewHandler.removeCallbacksAndMessages(null);
            playView.setPlayView();
        } else {
            isPlaying = true;
            isPlayComplete = false;
            updateViewHandler.sendEmptyMessage(Constants.PLAYING_WHAT);
            playView.setPauseView();
        }
    }
    private void backPress() {
        if (!DeviceUtil.isPortrait(getApplicationContext())) {
            playView.hiddenSwitchBitrateTextView();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            playView.setPortraitView();
            // Remove the full screen
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            playControl.setSurfaceChange();
        } else {
            finish();
        }
    }
    /**
     * Set up the full screen
     */
    private void setFullScreen() {
        if (DeviceUtil.isPortrait(getApplicationContext())) {
            playView.setFullScreenView(playControl.getName());
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // Set up the full screen
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            playControl.setSurfaceChange();
            playView.showSwitchBitrateTextView();
        }
    }

}
