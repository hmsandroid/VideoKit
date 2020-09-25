package com.videokitnative.huawei.utils;

import android.util.Log;

public class LogUtil {
    private static final String TAG = "VideoKitNative";

    public static final void d(String tag, String msg) {
        Log.d(getTag(tag), msg);
    }

    public static final void d(String msg) {
        Log.d(getTag(""), msg);
    }

    public static final void i(String tag, String msg) {
        Log.i(getTag(tag), msg);
    }

    public static final void i(String msg) {
        Log.i(getTag(""), msg);
    }

    public static final void w(String tag, String msg) {
        Log.w(getTag(tag), msg);
    }

    public static final void e(String tag, String msg) {
        Log.e(getTag(tag), msg);
    }

    private static String getTag(String tag) {
        if (StringUtil.isEmpty(tag)) {
            return TAG;
        } else {
            return TAG + ":" + tag;
        }
    }
}