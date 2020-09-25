package com.videokitnative.huawei.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

public class StringUtil {
    /**
     * Get empty string
     *
     * @return Empty string
     */
    public static String emptyStringValue() {
        return "";
    }

    private static final String TAG = "StringUtil";
    /**
     * whether a string is empty
     *
     * @param value String value
     * @return Whether a string is empty
     */
    public static boolean isEmpty(String value) {
        return TextUtils.isEmpty(value) || value.trim().length() == 0;
    }

    /**
     * Get not empty value
     *
     * @param value String value
     * @return Not empty value
     */
    public static String getNotEmptyString(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        } else {
            return value;
        }
    }

    /**
     * Split the string
     *
     * @param value string value
     * @param split Split value
     * @return String array
     */
    public static String[] getStringArray(String value, String split) {
        return value.split(split);
    }

    /**
     * Get a string from the resources
     *
     * @param context Context
     * @param resId Resource id
     * @return String value
     */
    public static String getStringFromResId(Context context, int resId) {
        String stringValue = "";
        try {
            stringValue = context.getResources().getString(resId);
        } catch (Resources.NotFoundException e) {
            Log.i(TAG,"get String from resId fail:" + e.getMessage());
        }
        return stringValue;
    }

    /**
     * String to float
     *
     * @param value String
     * @return float
     */
    public static float valueOf(String value) {
        float intValue = 0f;
        if (TextUtils.isEmpty(value)) {
            return intValue;
        }
        try {
            intValue = Float.valueOf(value);
        } catch (Exception e) {
            Log.i(TAG,"Integer parseInt error :" + e.getMessage());
        }
        return intValue;
    }

    /**
     * Set textView value
     *
     * @param textView TextView
     * @param value String value
     */
    public static void setTextValue(TextView textView, String value) {
        if (textView != null) {
            textView.setText(value);
        }
    }
}
