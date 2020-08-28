package com.xw.player.framework;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.xw.player.core.TargetTimeBean;
import com.xw.player.framework.bean.MgtvPlayerInfo;
import com.xw.player.framework.bean.PlayInfo;

/**
 * @Description SDK核心功能实现接口，上抛给业务层用
 * @date: 2020/08/04/16:48
 * @author: Mr.xw
 */
public interface IMgtvVideoPlayer {

    void init(Context context);

    void start();

    void pause();

    void stop();

    void reset();

    void release();

    void switchSpeed(float speed);

    void openMediaPlayer(final SurfaceView surfaceView, int x, int y, boolean isLive);

    void switchPlayer(final SurfaceView surfaceView, String url, boolean isLive);

    void setListenerCallBack(ListenerCallBack listenerCallBack);

    boolean isPrepared();

    boolean isPlaying();

    int getCurrentState();

    int getDuration();

    int getCurrentPosition();

    SurfaceView getSurfaceView();

    SurfaceHolder getSurfaceHolder();

    PlayInfo getPlayerInfo();

    void setMgtvPlayerInfo(MgtvPlayerInfo mgtvPlayerInfo);

    void setPlayerInfo(PlayInfo playerInfo);

    MgtvPlayerInfo getMgtvPlayerInfo();

    String getPlayUrl();

    void setPlayUrl(String playUrl);

    void setmAudioType(int AudioType);

    void setLoopPlay(boolean isLoop);

    int getPlayNetBandWidth();

    int getOnVideoSizeChangedheigth();

    int getOnVideoSizeChangedwidth();

    void seekTo(int ms);

    /**
     * 设置片尾位置ms
     */
    void setEndPostion(int postion);

    /**
     * 固定seek长度seek，并显示View
     */
    void seekFixedLengthAndShow(boolean isShow, int length);

    /**
     * 跳到指定时长位置，并显示View
     */
    void seekToSpecfyLengthAndShow(boolean isShow, int duration);

    /**
     * 设置倍速播放
     */
    void setSpeedPlay(float speed);

    /**
     * 隐藏倍速UI
     */

    void hideSpeedView(float isHide);

    /**
     * 更新倍速值
     */

    void updateSpeedPlay(float speed);

    /**
     * 设置无缝模式，false：非无缝模式，true: 无缝模式
     */
    void SeamlessPlay(boolean isSeamlessPlay);

    /**
     * 获取大小屏切换模式 false：非无缝模式，true: 无缝模式
     */
    boolean getSeamlessPlay();

    /**
     * 展示进度条
     */
    void playSeekBarView();

    /**
     * 是否支持试看/部分渠道例如移动和移动ott是不支持直播试看的
     */
    boolean tryPlayEnable();

    /**
     * 直播是否使能组播转单播,,电信不支持，移动iptv支持，移动ott走的单播
     */
    boolean liveMulticastToUnicast();

    /**
     * 播放器报错重试机制
     */
    void retryPlayWhenErro();

    /**
     * 播放目标时间的内容,并且设定默认播放时长
     * 当到了时间点，会触发OnplayToTargetTimeDataListener监听器
     *
     * @param tag        设置的目标时间点标识值
     * @param targetTime 设置的目标时间点，单位ms
     * @param length     设定播放特定总时长，单位ms
     */
    void playToTargetTimeFromDataWithLength(int tag, int targetTime, int length);

    /**
     * 播放目标时间的内容
     * 当到了时间点，会触发OnplayToTargetTimeDataListener监听器
     *
     * @param tag        设置的目标时间点标识值
     * @param targetTime 设置的目标时间点，单位ms
     */
    void playToTargetTime(int tag, int targetTime);


    /**
     * @Time: 2020/8/20 11:06
     * @Description: 无缝模式切换使用
     * @Author: Mr.xw
     */
    void specialPlay(SurfaceView surfaceView);


    /**
     * 移除播放的目标时间点监听
     *
     * @param tag        设置的目标时间点标识值
     */
    void rmPlayToTargetTime(int tag);

    /**
     * 移除播放的目标时间点监听
     *
     * @param tag        设置的目标时间点标识值
     * @param targetTime 设置的目标时间点，单位ms
     */
    void rmPlayToTargetTime(int tag, int targetTime);

    /**
     * 移除播放的目标时间点监听
     *
     * @param timeBean 设置的目标时间点实体
     */
    void rmPlayToTargetTime(TargetTimeBean timeBean);

    /**
     * 添加播放的目标时间点，
     * 当到了时间点，会触发OnPlayToTargetTimeListener监听器
     *
     * @param tag        设置的目标时间点标识值
     * @param targetTime 设置的目标时间点，单位ms
     */
    void addPlayToTargetTime(int tag, int targetTime);

    /**
     * 添加播放的目标时间点，
     * 当到了时间点，会触发OnPlayToTargetTimeListener监听器
     *
     * @param timeBean 设置的目标时间点实体
     */
    void addPlayToTargetTime(TargetTimeBean timeBean);

}
