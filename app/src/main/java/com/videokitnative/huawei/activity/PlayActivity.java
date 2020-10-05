package com.videokitnative.huawei.activity;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.videokitnative.huawei.contract.OnDialogInputValueListener;
import com.videokitnative.huawei.contract.OnPlayWindowListener;
import com.videokitnative.huawei.contract.OnWisePlayerListener;
import com.videokitnative.huawei.control.PlayControl;
import com.videokitnative.huawei.utils.Constants;
import com.videokitnative.huawei.utils.DataFormatUtil;
import com.videokitnative.huawei.utils.DeviceUtil;
import com.videokitnative.huawei.utils.DialogUtil;
import com.videokitnative.huawei.utils.LogUtil;
import com.videokitnative.huawei.utils.PlayControlUtil;
import com.videokitnative.huawei.utils.StringUtil;
import com.videokitnative.view.PlayView;

import android.view.TextureView.SurfaceTextureListener;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.videokitnative.huawei.utils.PlayControlUtil.setPlayMode;

public class PlayActivity extends AppCompatActivity implements Callback, SurfaceTextureListener, OnWisePlayerListener, OnPlayWindowListener {
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
    // Whether to suspend the buffer
    private int streamRequestMode = 0;

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

        playControl = new PlayControl(this, this, name, url);
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


    /**
     * `
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
        switch (settingType) {
            case Constants.MSG_SETTING:
                Log.d(TAG, "onSettingItemClick: MSG_SETTING CASE.");
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_mute_setting))) {
                    switchVideoMute();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_set_bandwidth_mode))) {
                    switchBandwidthMode();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_set_play_speed))) {
                    setPlaySpeed();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_stop_downloading))) {
                    stopRequestStream();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_set_volume))) {
                    setVideoVolume();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.play_mode))) {
                    switchPlayMode();
                } else if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_set_loop_play))) {
                    switchPlayLoop();
                } else {
                    LogUtil.i(TAG, "current settings type is " + itemSelect);
                }
                break;
            case Constants.PLAYER_SWITCH_PLAY_SPEED:
                onSwitchPlaySpeed(itemSelect);
                break;
            case Constants.PLAYER_SWITCH_BANDWIDTH_MODE:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.open_adaptive_bandwidth))) {
                    playControl.setBandwidthSwitchMode(PlayerConstants.BandwidthSwitchMode.AUTO_SWITCH_MODE, true);
                } else {
                    playControl.setBandwidthSwitchMode(PlayerConstants.BandwidthSwitchMode.MANUAL_SWITCH_MODE, true);
                }
                break;
            case Constants.PLAYER_SWITCH_PLAY_MODE:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.play_audio))) {
                    setPlayMode(PlayerConstants.PlayMode.PLAY_MODE_AUDIO_ONLY);
                } else {
                    setPlayMode(PlayerConstants.PlayMode.PLAY_MODE_NORMAL);
                }
                break;
            case Constants.PLAYER_SWITCH_LOOP_PLAY_MODE:
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_loop_play))) {
                    setCycleMode(true);
                } else {
                    setCycleMode(false);
                }
                break;
            case Constants.PLAYER_SWITCH_VIDEO_MUTE_MODE:
                Log.d(TAG, "onSettingItemClick: PLAYER_SWITCH_VIDEO_MUTE_MODE CASE.");
                if (TextUtils.equals(itemSelect,
                        StringUtil.getStringFromResId(PlayActivity.this, R.string.video_mute))) {
                    playControl.setMute(true);
                } else {
                    playControl.setMute(false);
                }
                break;
            case Constants.PLAYER_SWITCH_STOP_REQUEST_STREAM:
                onSwitchRequestMode(itemSelect);
                break;
            default:
                break;
        }
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
            case R.id.play_speed_btn:
                setPlaySpeed();
                break;
            case R.id.setting_tv:
                onSettingDialog();
                break;
            case R.id.volume_btn:
                setVideoVolume();
                break;
            default:
                break;
        }
    }

    /**
     * Play the Settings dialog
     */
    private void onSettingDialog() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_bandwidth_mode));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_stop_downloading));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_play_speed));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.play_mode));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_loop_play));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_mute_setting));
        showTextList.add(StringUtil.getStringFromResId(this, R.string.video_set_volume));
        playView.showSettingDialog(Constants.MSG_SETTING, showTextList, 0);
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

    /**
     * Select the play speed
     */
    private void setPlaySpeed() {
        try {
            String[] showTextArray = getResources().getStringArray(R.array.play_speed_text);
            String speedValue = DataFormatUtil.getPlaySpeedString(playControl.getPlaySpeed());
            LogUtil.d("current play speed : float is " + playControl.getPlaySpeed() + ", and String is" + speedValue);
            playView.showSettingDialogValue(Constants.PLAYER_SWITCH_PLAY_SPEED, Arrays.asList(showTextArray),
                    speedValue);
        } catch (Resources.NotFoundException e) {
            LogUtil.w(TAG, "get string array error :" + e.getMessage());
        }
    }

    /**
     * Set the play speed
     *
     * @param speedValue the play speed
     */
    private void onSwitchPlaySpeed(String speedValue) {
        playControl.setPlaySpeed(speedValue);
        LogUtil.d(TAG,
                "current set speed value:" + speedValue + ", and get player speed value:" + playControl.getPlaySpeed());
        playView.setSpeedButtonText(speedValue);
    }

    /**
     * Select whether the mute
     */
    private void switchVideoMute() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.video_mute));
        showTextList.add(getResources().getString(R.string.video_not_mute));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_VIDEO_MUTE_MODE, showTextList,
                PlayControlUtil.isMute() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
    }

    /**
     * Whether to stop the downloading
     *
     * @param selectValue Select text value
     */
    private void onSwitchRequestMode(String selectValue) {
        if (selectValue.equals(StringUtil.getStringFromResId(PlayActivity.this, R.string.video_keep_download))) {
            streamRequestMode = 0;
        } else if (selectValue.equals(StringUtil.getStringFromResId(PlayActivity.this, R.string.video_stop_download))) {
            streamRequestMode = 1;
        }
        LogUtil.i(TAG, "mStreamRequestMode:" + streamRequestMode);

        playControl.setBufferingStatus(streamRequestMode == 0 ? true : false, true);
    }

    /**
     * Whether to stop the download dialog
     */
    private void stopRequestStream() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(StringUtil.getStringFromResId(PlayActivity.this, R.string.video_keep_download));
        showTextList.add(StringUtil.getStringFromResId(PlayActivity.this, R.string.video_stop_download));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_STOP_REQUEST_STREAM, showTextList, streamRequestMode);

    }

    /**
     * Show the volume dialog
     */
    private void setVideoVolume() {
        DialogUtil.showSetVolumeDialog(this, new OnDialogInputValueListener() {
            @Override
            public void dialogInputListener(String inputText) {
                playControl.setVolume(StringUtil.valueOf(inputText));
            }
        });
    }

    /**
     * Set the play mode
     */
    private void switchPlayMode() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.play_video));
        showTextList.add(getResources().getString(R.string.play_audio));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_PLAY_MODE, showTextList, playControl.getPlayMode());
    }

    /**
     * Set the play mode
     *
     * @param playMode The play mode
     */
    private void setPlayMode(int playMode) {
        playControl.setPlayMode(playMode, true);
    }

    /**
     * Set cycle mode
     *
     * @param isCycleMode Is cycle mode
     */
    public void setCycleMode(boolean isCycleMode) {
        playControl.setCycleMode(isCycleMode);
    }

    /**
     * If set up a video loop
     */
    private void switchPlayLoop() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.video_loop_play));
        showTextList.add(getResources().getString(R.string.video_not_loop_play));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_LOOP_PLAY_MODE, showTextList,
                playControl.isCycleMode() ? Constants.DIALOG_INDEX_ONE : Constants.DIALOG_INDEX_TWO);
    }

    /**
     * Set the bandwidth switch
     */
    private void switchBandwidthMode() {
        List<String> showTextList = new ArrayList<>();
        showTextList.add(getResources().getString(R.string.close_adaptive_bandwidth));
        showTextList.add(getResources().getString(R.string.open_adaptive_bandwidth));
        playView.showSettingDialog(Constants.PLAYER_SWITCH_BANDWIDTH_MODE, showTextList, Constants.DIALOG_INDEX_ONE);
    }

}
