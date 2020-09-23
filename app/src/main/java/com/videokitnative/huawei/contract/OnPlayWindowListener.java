package com.videokitnative.huawei.contract;

import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;

public interface OnPlayWindowListener
        extends SurfaceHolder.Callback, View.OnClickListener, TextureView.SurfaceTextureListener, OnItemClickListener, OnPlaySettingListener {
}
