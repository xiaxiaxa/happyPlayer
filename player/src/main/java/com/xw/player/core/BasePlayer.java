package com.xw.player.core;

import android.content.Context;

/**
 * @description 播放器基类
 * @date: 2020/7/25 10:54
 * @author: Mr.xw
 */
public abstract class BasePlayer implements IBasePlayer {

    protected Context mContext;
    protected PlayerConstants.ViewType mViewType = PlayerConstants.ViewType.TYPE_SURFACE_VIEW;

    @Override
    public void init(Context context) {
        if (context == null) {
            return;
        }
        this.mContext = context;
    }

}
