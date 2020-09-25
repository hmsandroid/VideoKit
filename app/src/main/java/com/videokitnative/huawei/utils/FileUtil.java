package com.videokitnative.huawei.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * File tools
 */
public class FileUtil {
    public static final String PLAY_FILE_NAME = "video_kit_demo.txt";

    public static final String ENCODE_UTF_8 = "utf-8";

    /**
     * Get assets files in the directory
     *
     * @param context Context
     * @param charsetName Encode type
     * @return The file content string
     */
    public static String parseAssetsFile(Context context, String charsetName) {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(context.getAssets().open(PLAY_FILE_NAME), charsetName);
            char[] buffer = new char[1024];
            int count = 0;
            StringBuilder builder = new StringBuilder();
            while ((count = reader.read(buffer, 0, 1024)) > 0) {
                builder.append(buffer, 0, count);
            }
            return builder.toString();
        } catch (IOException e) {
            LogUtil.i("get assets file error :" + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                LogUtil.i("close InputStreamReader error :" + e.getMessage());
            }
        }
        return StringUtil.emptyStringValue();
    }

    /**
     * Create file
     * @param filePath File path
     * @return Create or not
     */
    public static boolean createFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (!file.exists()) {
                return file.mkdirs();
            } else {
                return true;
            }
        }
        return false;
    }
}
