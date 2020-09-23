package com.videokitnative.huawei;

import android.app.Application;
import android.util.Log;

import com.huawei.hms.videokit.player.InitFactoryCallback;
import com.huawei.hms.videokit.player.WisePlayerFactory;
import com.huawei.hms.videokit.player.WisePlayerFactoryOptions;

public class VideoKitPlayApplication extends Application {
    private static final String TAG = "VideoKitPlayApplication";
    private static WisePlayerFactory wisePlayerFactory = null;

    @Override
    public void onCreate() {
        super.onCreate();
        initPlayer();
    }

    private void initPlayer() {
        // DeviceId test is used in the demo, specific access to incoming deviceId after encryption
        Log.d(TAG, "initPlayer: VideoKitPlayApplication");
        WisePlayerFactoryOptions factoryOptions = new WisePlayerFactoryOptions.Builder().setDeviceId("xxx").build();
        WisePlayerFactory.initFactory(this, factoryOptions, initFactoryCallback);
    }
    /**
     * Player initialization callback
     */
    private static InitFactoryCallback initFactoryCallback = new InitFactoryCallback() {
        @Override
        public void onSuccess(WisePlayerFactory wisePlayerFactory) {
            Log.d(TAG, "init player factory success");
            setWisePlayerFactory(wisePlayerFactory);
        }

        @Override
        public void onFailure(int errorCode, String reason) {
            Log.d(TAG, "init player factory fail reason :" + reason + ", errorCode is " + errorCode);
        }
    };
    /**
     * Get WisePlayer Factory
     *
     * @return WisePlayer Factory
     */
    public static WisePlayerFactory getWisePlayerFactory() {
        return wisePlayerFactory;
    }

    private static void setWisePlayerFactory(WisePlayerFactory wisePlayerFactory) {
        VideoKitPlayApplication.wisePlayerFactory = wisePlayerFactory;
    }
}
