package com.videokitnative.huawei.utils;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.videokit.player.CacheInfo;
import com.huawei.hms.videokit.player.Preloader;
import com.videokitnative.huawei.utils.LogUtil;
import com.videokitnative.huawei.R;
import com.videokitnative.huawei.VideoKitPlayApplication;
import com.videokitnative.huawei.contract.OnDialogConfirmListener;
import com.videokitnative.huawei.contract.OnDialogInputValueListener;
import com.videokitnative.huawei.contract.OnHomePageListener;
import com.videokitnative.huawei.contract.OnPlaySettingListener;
import com.videokitnative.view.PlaySettingDialog;


/**
 * Dialog tools
 */
public class DialogUtil {
    /**
     * Set Bitrate dialog
     *
     * @param context Context
     */
    private static int last_state = 50;
    private static final String TAG = "DialogUtil";

    public static void setInitBitrate(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.init_bitrate, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.video_init_bitrate_setting))
                .setView(view)
                .create();
        dialog.show();

        final CheckBox checkUpTo = (CheckBox) view.findViewById(R.id.check_up_to);
        if (PlayControlUtil.getInitType() == 0) {
            checkUpTo.setChecked(true);
        } else {
            checkUpTo.setChecked(false);
        }
        final EditText initBitrateEt = (EditText) view.findViewById(R.id.init_bitrate_et);
        initBitrateEt.setText(String.valueOf(PlayControlUtil.getInitBitrate()));
        final EditText widthEt = (EditText) view.findViewById(R.id.init_width_et);
        widthEt.setText(String.valueOf(PlayControlUtil.getInitWidth()));
        final EditText heightEt = (EditText) view.findViewById(R.id.init_height_et);
        heightEt.setText(String.valueOf(PlayControlUtil.getInitHeight()));

        Button okBt = (Button) view.findViewById(R.id.ok_bt);
        Button cancelBt = (Button) view.findViewById(R.id.cancel_bt);

        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayControlUtil.setInitType(checkUpTo.isChecked() ? 0 : 1);
                PlayControlUtil.setInitBitrate(Integer.parseInt(initBitrateEt.getText().toString()));
                PlayControlUtil.setInitHeight(Integer.parseInt(heightEt.getText().toString()));
                PlayControlUtil.setInitWidth(Integer.parseInt(widthEt.getText().toString()));
                dialog.dismiss();
            }
        });

        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Home page setup dialog
     *
     * @param context            Context
     * @param playSettingType    Set the play type
     * @param settingList        Set the list of options
     * @param defaultSelect      The default Settings string
     * @param onHomePageListener Click listener
     */
    public static void showVideoTypeDialog(Context context, int playSettingType, List<String> settingList,
                                           int defaultSelect, OnHomePageListener onHomePageListener) {
        PlaySettingDialog playSettingDialog = new PlaySettingDialog(context);
        playSettingDialog.initDialog(onHomePageListener, playSettingType);
        playSettingDialog.setList(settingList);
        playSettingDialog.setSelectIndex(defaultSelect);
        playSettingDialog.setNegativeButton(StringUtil.getStringFromResId(context, R.string.setting_cancel), null);
        playSettingDialog.show();
    }

    /**
     * Play activity Settings dialog
     *
     * @param context               Context
     * @param settingType           Set the play type
     * @param showTextList          Set the list of options
     * @param selectIndex           The default Settings index
     * @param onPlaySettingListener Click listener
     */
    public static void onSettingDialogSelectIndex(Context context, int settingType, List<String> showTextList,
                                                  int selectIndex, OnPlaySettingListener onPlaySettingListener) {
        PlaySettingDialog dialog = new PlaySettingDialog(context).setList(showTextList);
        dialog.setTitle(StringUtil.getStringFromResId(context, R.string.settings));
        dialog.setSelectIndex(selectIndex);
        dialog.setNegativeButton(StringUtil.getStringFromResId(context, R.string.setting_cancel), null);
        dialog.initDialog(onPlaySettingListener, settingType);
        dialog.show();
    }

    /**
     * Play activity Settings dialog
     *
     * @param context               Context
     * @param settingType           Set the play type
     * @param showTextList          Set the list of options
     * @param selectValue           The default Settings string
     * @param onPlaySettingListener Click listener
     */
    public static void onSettingDialogSelectValue(Context context, int settingType, List<String> showTextList,
                                                  String selectValue, OnPlaySettingListener onPlaySettingListener) {
        PlaySettingDialog dialog = new PlaySettingDialog(context).setList(showTextList);
        dialog.setTitle(StringUtil.getStringFromResId(context, R.string.video_set_play_speed));
        dialog.setSelectValue(selectValue)
                .setNegativeButton(StringUtil.getStringFromResId(context, R.string.setting_cancel), null);
        dialog.initDialog(onPlaySettingListener, settingType);
        dialog.show();
    }

    /**
     * Get the volume Settings dialog
     *
     * @param context                    Context
     * @param onDialogInputValueListener Click listener
     */
    public static void showSetVolumeDialog(Context context,
                                           final OnDialogInputValueListener onDialogInputValueListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.set_volume_dialog, null);
        final AlertDialog dialog =
                new AlertDialog.Builder(context).setTitle(StringUtil.getStringFromResId(context, R.string.video_set_volume))
                        .setView(view)
                        .create();
        dialog.show();
        final SeekBar volumeSeek = (SeekBar) view.findViewById(R.id.seek_bar_volume);
        volumeSeek.setProgress(0);
        volumeSeek.setMax(100);
        volumeSeek.setProgress(last_state);
        volumeSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                float progress_f;
                progress_f = (float) progress / 100;
                onDialogInputValueListener.dialogInputListener(Float.toString(progress_f));
                last_state = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Set the bitrate range dialog
     *
     * @param context Context
     */
    public static void showBitrateRangeDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.bitrate_range_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.video_bitrate_range_setting_title))
                .setView(view)
                .create();
        dialog.show();
        final EditText bitrateMinSetting = (EditText) view.findViewById(R.id.bitrate_min_setting);
        final EditText bitrateMaxSetting = (EditText) view.findViewById(R.id.bitrate_max_setting);
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitrateMinSetting.getText() != null) {
                    PlayControlUtil.setMinBitrate(Integer.parseInt(bitrateMinSetting.getText().toString()));
                }
                if (bitrateMaxSetting.getText() != null) {
                    PlayControlUtil.setMaxBitrate(Integer.parseInt(bitrateMaxSetting.getText().toString()));
                }
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * init preloader
     *
     * @param context                 Context
     * @param onDialogConfirmListener Dialog confirm listener
     */
    public static void initPreloaderDialog(final Context context,
                                           final OnDialogConfirmListener onDialogConfirmListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.bitrate_range_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.video_init_preloader))
                .setView(view)
                .create();
        dialog.show();
        final TextView minTextView = view.findViewById(R.id.bitrate_min_tv);
        final TextView maxTextView = view.findViewById(R.id.bitrate_max_tv);
        final EditText bitrateMinSetting = (EditText) view.findViewById(R.id.bitrate_min_setting);
        final EditText bitrateMaxSetting = (EditText) view.findViewById(R.id.bitrate_max_setting);
        bitrateMinSetting.setInputType(InputType.TYPE_CLASS_TEXT);
        minTextView.setText(R.string.video_preloader_path);
        maxTextView.setText(R.string.video_preloader_total_size);
        bitrateMinSetting.setText("/sdcard/preloader");
        bitrateMaxSetting.setText("209715200");
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Preloader preloader = VideoKitPlayApplication.getWisePlayerFactory().createPreloader();
                if (preloader != null && !TextUtils.isEmpty(bitrateMinSetting.getText())
                        && !TextUtils.isEmpty(bitrateMaxSetting.getText())) {
                    PlayControlUtil.setPreloader(preloader);
                    String path = bitrateMinSetting.getText().toString();
                    if (FileUtil.createFile(path)) {
                        int size = Integer.parseInt(bitrateMaxSetting.getText().toString());
                        int result = preloader.initCache(path, size);
                        PlayControlUtil.setInitResult(result);
                    } else {
                        Toast.makeText(context, context.getString(R.string.create_file_fail), Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                onDialogConfirmListener.onConfirm();
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Add single cache
     *
     * @param context Context
     */
    public static void addSingleCacheDialog(final Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_single_cache_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(StringUtil.getStringFromResId(context, R.string.video_add_single_cache))
                .setView(view)
                .create();
        dialog.show();
        final EditText cacheUrl = (EditText) view.findViewById(R.id.et_cache_url);
        final EditText cachePlayParam = (EditText) view.findViewById(R.id.et_cache_playparam);
        final EditText cacheAppId = (EditText) view.findViewById(R.id.et_cache_appid);
        final EditText cacheWidth = (EditText) view.findViewById(R.id.et_cache_width);
        final EditText cacheHeight = (EditText) view.findViewById(R.id.et_cache_height);
        final EditText cacheSize = (EditText) view.findViewById(R.id.et_cache_size);
        final EditText cachePriority = (EditText) view.findViewById(R.id.et_cache_priority);
        Button okBt = (Button) view.findViewById(R.id.set_volume_bt_ok);
        Button cancelBt = (Button) view.findViewById(R.id.set_volume_bt_cancel);
        okBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CacheInfo cacheInfo = new CacheInfo();
                int priority = 0;
                if (!TextUtils.isEmpty(cacheUrl.getText())) {
                    cacheInfo.setUrl(cacheUrl.getText().toString());
                }
                if (!TextUtils.isEmpty(cachePlayParam.getText())) {
                    cacheInfo.setPlayParam(cachePlayParam.getText().toString());
                }
                if (!TextUtils.isEmpty(cacheAppId.getText())) {
                    cacheInfo.setAppId(cacheAppId.getText().toString());
                }
                if (!TextUtils.isEmpty(cacheWidth.getText())) {
                    cacheInfo.setWidth(Integer.parseInt(cacheWidth.getText().toString()));
                }
                if (!TextUtils.isEmpty(cacheHeight.getText())) {
                    cacheInfo.setHeight(Integer.parseInt(cacheHeight.getText().toString()));
                }
                if (!TextUtils.isEmpty(cacheSize.getText())) {
                    cacheInfo.setCacheSize(Integer.parseInt(cacheSize.getText().toString()));
                }
                if (!TextUtils.isEmpty(cachePriority.getText())) {
                    priority = Integer.parseInt(cachePriority.getText().toString());
                }
                if (!TextUtils.isEmpty(cacheInfo.getUrl())) {
                    if (PlayControlUtil.getPreloader() != null) {
                        PlayControlUtil.getPreloader().addCache(cacheInfo, priority);
                    } else {
                        Toast
                                .makeText(context, context.getString(R.string.video_add_single_cache_fail),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.video_preload_url), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        cancelBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}