package com.xw.player.core;

/**
 * @Time: 2020/7/31 18:51
 * @Description: 播放器监听回调类
 * @Author: Mr.xw
 */
public interface PlayerListenerCallBack {

    void onEvent(PlayerConstants.EventType type, Object... params);

    boolean onInfo(int what, int extra);

    boolean onError(int what, int extra);

    void onBufferingUpdate(int percent);

    void onVideoSizeChanged(int width, int heigth);

    void onCompletion();

    void onSeekComplete();

    void onPrepare();

    void onStart();

}
