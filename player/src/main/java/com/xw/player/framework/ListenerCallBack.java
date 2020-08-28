package com.xw.player.framework;

import com.xw.player.core.PlayerConstants;
import com.xw.player.core.PlayerListenerCallBack;

/**
 * @Description
 * @date: 2020/08/05/15:19
 * @author: Mr.xw
 */
public abstract class ListenerCallBack implements PlayerListenerCallBack {

    public abstract void onEvent(PlayerConstants.EventType type, Object... params);

    public abstract boolean onInfo(int what, int extra);

    public abstract boolean onError(int what, int extra);

    public abstract void onBufferingUpdate(int percent);

    public abstract void onVideoSizeChanged(int width, int heigth);

    public abstract void onCompletion();

    public abstract void onSeekComplete();

    public abstract void onPrepare();

    public abstract void onStart();

}
