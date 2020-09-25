package com.videokitnative.huawei.utils;

import android.content.Context;
import android.content.res.Configuration;

public class DeviceUtil {
    private static final float FLOAT_VALUE = 0.5f;

    /**
     * Get the resolution of the mobile phone from the dp value converted to the px
     *
     * @param context Context
     * @param dpValue Dp value
     * @return Px value
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + FLOAT_VALUE);
    }

    /**
     * Whether the vertical screen
     *
     * @param context Context
     * @return boolean Whether the vertical screen
     */
    public static boolean isPortrait(Context context) {
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
