package com.videokitnative.huawei.contract;

public interface OnPlaySettingListener {
    /**
     * Dialog select listener
     *
     * @param itemSelect The selected text
     * @param settingType The corresponding operation type of player
     */
    void onSettingItemClick(String itemSelect, int settingType);
}
