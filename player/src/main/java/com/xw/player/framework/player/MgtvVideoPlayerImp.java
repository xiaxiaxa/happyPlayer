package com.xw.player.framework.player;

import android.content.Context;
import android.os.Build;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.RequiresApi;

import com.xw.helper.utils.MLog;
import com.xw.player.core.IBasePlayer;
import com.xw.player.core.MgtvPlayer;
import com.xw.player.core.TargetTimeBean;
import com.xw.player.framework.IMgtvVideoPlayer;
import com.xw.player.framework.ListenerCallBack;
import com.xw.player.framework.bean.MgtvPlayerInfo;
import com.xw.player.framework.bean.PlayInfo;

/**
 * @Description SDK播放器核心实现类
 * @date: 2020/08/06/18:01
 * @author: Mr.xw
 */
public class MgtvVideoPlayerImp implements IBasePlayer, IMgtvVideoPlayer {

    private static final String TAG = "MgtvVideoPlayerImp";
    private Context mContext;
    private String playUrl;
    private MgtvPlayer mgtvPlayer;
    private MgtvPlayerInfo mgtvPlayerInfo;
    private PlayInfo playerInfo;

    /**
     * @Method: 获得底层播放器相关能力，初始化二次封装的播放器MgtvPlayer
     * @Time: 2020/7/31 9:21
     * @Description:
     * @Author: Mr.xw
     */
    @Override
    public void init(Context context) {
        this.mContext = context;
            if (mgtvPlayer == null) {
                mgtvPlayer = new MgtvPlayer();
            }
            mgtvPlayer.init(mContext);
            initPlayInfo();
    }

    public void setListenerCallBack(ListenerCallBack listenerCallBack) {
        mgtvPlayer.setListenerCallBack(listenerCallBack);
    }

    private void initPlayInfo() {
        if (playerInfo == null) {
            playerInfo = new PlayInfo();
        }
        if (mgtvPlayerInfo == null) {
            mgtvPlayerInfo = new MgtvPlayerInfo();
        }
    }

/**
 * ==================这里是封装原Mediaplayer的公共使用方法出来======begin======
 * ============================================================================
 */

    /**
     * @method onStart
     * @description 播放器开始播放
     * @date: 2020/7/26 20:37
     * @author: Mr.xw
     */
    @Override
    public void start() {
        mgtvPlayer.start();
    }

    /**
     * @method onPause
     * @description 暂停播放器
     * @date: 2020/7/26 20:35
     * @author: Mr.xw
     */
    @Override
    public void pause() {
        mgtvPlayer.pause();
    }

    public void prepareAsync() {
        mgtvPlayer.prepareAsync();
    }


    /**
     * @method onStop
     * @description 停止播放器
     * @date: 2020/7/26 20:36
     * @author: Mr.xw
     */
    @Override
    public void stop() {
        mgtvPlayer.stop();
    }

    /**
     * @method release
     * @description 释放播放器资源
     * @date: 2020/7/26 20:36
     * @author: Mr.xw
     */
    @Override
    public void release() {
        if (mgtvPlayer != null) {
            mgtvPlayer.reset();
            mgtvPlayer.release();
        }
    }

    /**
     * @method switchSpeed
     * @description 倍速设定，传倍速值float
     * @date: 2020/7/25 23:51
     * @author: Mr.xw
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void switchSpeed(float speed) {
        if (mgtvPlayer != null) {
            mgtvPlayer.switchSpeed(speed);
        }
    }

    /**
     * @method isPrepared
     * @description 播放器是否准备完毕
     * @date: 2020/7/26 20:38
     * @author: Mr.xw
     */
    @Override
    public boolean isPrepared() {
        if (mgtvPlayer != null) {
            return mgtvPlayer.isPrepared();
        }
        return false;
    }

    /**
     * @method isPlaying
     * @description 播放器是否正在播放
     * @date: 2020/7/26 20:38
     * @author: Mr.xw
     */
    @Override
    public boolean isPlaying() {
        if (mgtvPlayer != null) {
            return mgtvPlayer.isPlaying();
        }
        return false;
    }

    /**
     * @method getCurrentState
     * @description 获取播放器当前状态
     * @date: 2020/7/26 20:39
     * @author: Mr.xw
     */
    @Override
    public int getCurrentState() {
        if (mgtvPlayer != null) {
            return mgtvPlayer.getCurrentState();
        }
        return -1;
    }

    /**
     * @method getDuration
     * @description 获取播放时长
     * @date: 2020/7/26 20:39
     * @author: Mr.xw
     */
    @Override
    public int getDuration() {
        if (mgtvPlayer != null) {
            return mgtvPlayer.getDuration();
        }
        return 0;
    }

    /**
     * @method getCurrentPosition
     * @description 获取当前的播放位置
     * @date: 2020/7/26 20:40
     * @author: Mr.xw
     */
    @Override
    public int getCurrentPosition() {
        if (mgtvPlayer != null) {
            return mgtvPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * @method getSurfaceView
     * @description 标准SurfaceView get
     * @date: 2020/7/26 20:41
     * @author: Mr.xw
     */
    @Override
    public SurfaceView getSurfaceView() {
        if (mgtvPlayer != null) {
            return mgtvPlayer.getSurfaceView();
        }
        return null;
    }

    /**
     * @method getSurfaceHolder
     * @description 标准SurfaceHolder get
     * @date: 2020/7/26 20:41
     * @author: Mr.xw
     */
    @Override
    public SurfaceHolder getSurfaceHolder() {
        if (mgtvPlayer != null) {
            return mgtvPlayer.getSurfaceHolder();
        }
        return null;
    }

    /**
     * @method setlayerInfo
     * @description 设置自定义媒资Info信息
     * @date: 2020/7/26 20:45
     * @author: Mr.xw
     */
    @Override
    public PlayInfo getPlayerInfo() {
        return playerInfo;
    }

    @Override
    public void setPlayerInfo(PlayInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    /**
     * @method getMgtvPlayerInfo
     * @description 获取自定义媒资Info信息（例如循环播放，单集/卡里表播放等
     * @date: 2020/7/26 20:44
     * @author: Mr.xw
     */
    @Override
    public void setMgtvPlayerInfo(MgtvPlayerInfo mgtvPlayerInfo) {
        this.mgtvPlayerInfo = mgtvPlayerInfo;
    }

    @Override
    public void reset() {
        if (mgtvPlayer != null) {
            mgtvPlayer.reset();
        }
    }

    @Override
    public MgtvPlayerInfo getMgtvPlayerInfo() {
        return mgtvPlayerInfo;
    }

    /**
     * @method getPlayUrl
     * @description 提供单独取播放串的方法
     * @date: 2020/7/25 23:45
     * @author: Mr.xw
     */
    @Override
    public String getPlayUrl() {
        return playUrl;
    }

    /**
     * @method setPlayUrl
     * @description 提供单独设置播放串的方法
     * @date: 2020/7/25 23:46
     * @author: Mr.xw
     */
    @Override
    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    /**
     * ========================================================================
     * =================上面是封装原Mediaplayer的公共使用方法出来===========end
     */


    public void setDisplay(SurfaceView surfaceView) {
        try {
            mgtvPlayer.setDisplay(surfaceView.getHolder());
        } catch (IllegalStateException e) {
            MLog.d("setDisplay e:" + e.getMessage());
        } catch (IllegalArgumentException ex) {
            MLog.d("setDisplay failed:");
        } catch (SecurityException ex) {
            MLog.d("setDisplay failed:");
        }
    }

    /**
     * @Method: initMediaplayer
     * @Time: 2020/7/31 9:13
     * @Description: 提供给业务层使用的，第一次初始化播放器，设置SurfaceView以及播放串
     * @Author: Mr.xw
     */
    @Override
    public void openMediaPlayer(final SurfaceView surfaceView, int x, int y, boolean isLive) {
        if (surfaceView == null || mgtvPlayer == null) {
            return;
        }
        mgtvPlayer.setSurfaceView(surfaceView, x, y, isLive);
        setDisplay(surfaceView);
        if (playerInfo != null) {
            mgtvPlayer.openMediaPlayer(surfaceView, playerInfo.getUrl());
        }
        /**直播拿不到第一帧*/
        if (!isLive) {
            mgtvPlayer.dealTimeRefreshed();
            if (mgtvPlayerInfo != null) {
                mgtvPlayer.setTimeoutMsg(mgtvPlayerInfo.getTimeout());
            }
        }
    }

    @Override
    public void switchPlayer(final SurfaceView surfaceView, String playUrl, boolean isLive) {
        if (surfaceView == null || mgtvPlayer == null) {
            return;
        }
        mgtvPlayer.stop();
        mgtvPlayer.reset();
        if (playerInfo != null) {
            mgtvPlayer.openMediaPlayer(surfaceView, playUrl);
        }
        if (!isLive) {
            mgtvPlayer.dealTimeRefreshed();
            if (mgtvPlayerInfo != null) {
                mgtvPlayer.setTimeoutMsg(mgtvPlayerInfo.getTimeout());
            }
        }
    }

    /**
     * @Method: setmAudioType
     * @Time: 2020/7/31 10:08
     * @Description: 提供给业务层自定音频模式
     * @Author: Mr.xw
     */
    @Override
    public void setmAudioType(int AudioType) {
        if (mgtvPlayer != null) {
            mgtvPlayer.setAudioType(AudioType);
        }
    }

    /**
     * @Method:setLoopPlay
     * @Time: 2020/8/6 10:08
     * @Description:设置循环播放模式
     * @Author: Mr.xw
     */
    @Override
    public void setLoopPlay(boolean isLoop) {
        if (mgtvPlayer != null) {
            mgtvPlayer.setLoopPlay(isLoop);
        }
    }

    /**
     * @Method:getPlayNetBandWidth
     * @Time: 2020/7/31 16:13
     * @Description: hls留获取流媒体下载带宽，单位：5157366B
     * @Author: Mr.xw
     */
    @Override
    public int getPlayNetBandWidth() {
        return mgtvPlayer.getPlayNetBandWidth();
    }

    /**
     * @Method:getOnVideoSizeChangedheigth
     * @Time: 2020/7/31 17:03
     * @Description: onVideoSizeChanged发生变化的时候，获取变化的视屏输出层的高度
     * @Author: Mr.xw
     */
    @Override
    public int getOnVideoSizeChangedheigth() {
        return mgtvPlayer.getOnVideoSizeChangedheigth();
    }

    /**
     * @Method:getOnVideoSizeChangedheigth
     * @Time: 2020/7/31 17:03
     * @Description: onVideoSizeChanged发生变化的时候，获取变化的视屏输出层的宽度
     * @Author: Mr.xw
     */
    @Override
    public int getOnVideoSizeChangedwidth() {
        return mgtvPlayer.getOnVideoSizeChangedwidth();
    }

    /**
     * @Method: seekTo指定的时间
     * @Time: 2020/8/3 16:38
     * @Description:
     * @Author: Mr.xw
     */
    @Override
    public void seekTo(int ms) {
        if (mgtvPlayer != null) {
            mgtvPlayer.seekTo(ms);
        }
    }

    @Override
    public void setEndPostion(int postion) {

    }

    @Override
    public void seekFixedLengthAndShow(boolean isShow, int length) {

    }

    @Override
    public void seekToSpecfyLengthAndShow(boolean isShow, int duration) {

    }

    @Override
    public void setSpeedPlay(float speed) {

    }

    @Override
    public void hideSpeedView(float isHide) {

    }

    @Override
    public void updateSpeedPlay(float speed) {

    }

    @Override
    public void SeamlessPlay(boolean isSeamlessPlay) {

    }

    @Override
    public boolean getSeamlessPlay() {
        return false;
    }

    @Override
    public void playSeekBarView() {

    }

    @Override
    public boolean tryPlayEnable() {
        return false;
    }

    @Override
    public boolean liveMulticastToUnicast() {
        return false;
    }

    @Override
    public void retryPlayWhenErro() {

    }

    @Override
    public void playToTargetTimeFromDataWithLength(int tag, int targetTime, int length) {

    }

    @Override
    public void playToTargetTime(int tag, int targetTime) {

    }

    @Override
    public void specialPlay(SurfaceView surfaceView) {
        mgtvPlayer.specialPlay(surfaceView);
    }


    @Override
    public void rmPlayToTargetTime(int tag) {
        mgtvPlayer.rmPlayToTargetTime(tag);
    }

    @Override
    public void rmPlayToTargetTime(int tag, int targetTime) {
        mgtvPlayer.rmPlayToTargetTime(tag,targetTime);
    }

    @Override
    public void rmPlayToTargetTime(TargetTimeBean timeBean) {
        mgtvPlayer.rmPlayToTargetTime(timeBean);
    }

    @Override
    public void addPlayToTargetTime(int tag, int targetTime) {
        mgtvPlayer.addPlayToTargetTime(tag,targetTime);
    }

    @Override
    public void addPlayToTargetTime(TargetTimeBean timeBean) {
        mgtvPlayer.addPlayToTargetTime(timeBean);
    }

}
