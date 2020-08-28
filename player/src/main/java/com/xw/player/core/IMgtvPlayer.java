package com.xw.player.core;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

/**
 * @description 芒果底层播放器核心功能接口
 * @date: 2020/7/25 10:53
 * @author: Mr.xw
 */
interface IMgtvPlayer {

    void start();//启播

    void switchSpeed(float speed);//切换倍速播

    void pause();

    void stop();

    void release();

    void reset();

    void seekTo(int ms);

    /**
     * 获取播放信息与状态
     */
    boolean isPrepared();//是否准备就绪

    boolean isPlaying();//是否正在播放

    boolean isCompleted();//是否播放完成

    boolean hasFirstFrame();//是否有第一帧回调

    int getCurrentState();//获取当前播放器状态

    int getDuration();

    int getCurrentPosition();

    void setSurfaceView(SurfaceView surfaceView, int x, int y, boolean isLive);

    void setTextureView(TextureView textureView);//由外层设置TextureView

    SurfaceView getSurfaceView();

    SurfaceHolder getSurfaceHolder();

    TextureView getTextureView();

    /**
     * 动态设置相关参数
     */
    void setViewType(PlayerConstants.ViewType viewType);//设置播放的View类型

    /**
     * MediaPlayer相关监听
     */
    boolean onInfo(int what, int extra);

    boolean onError(int what, int extra);

    void onBufferingUpdate(int percent);

    void onVideoSizeChanged(int width, int heigth);

    void onCompletion();

    void onSeekComplete();




}
