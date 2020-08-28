package com.xw.player.framework.bean;


import com.xw.player.core.TargetTimeBean;

import java.util.ArrayList;

/**
 * @Time: 2020/8/10 11:03
 * @Description: 提供给业务层使用的预留部分业务需要的底层能力,补充TargetTimeBean
 * @Author: Mr.xw
 */
public class MgtvPlayerInfo extends TargetTimeBean {

    private PlayInfo playInfo;
    private long timeout;//设置超时时间，单位ms

    public enum PLAYMODE {
        PLAY_LOOP_LIST,//列表循环播放
        PLAY_LOOP_SINGLE,//单集循环
        PLAY_AUTO_NEXT,//列表播放,自动播放下一集
        PLAY_RANDOM,//随机播放
        PLAY_DEFAULT//默认播放模式，无论设置的是单集还是集合播放完当前设置的。
    }

    private PLAYMODE playmode = PLAYMODE.PLAY_DEFAULT;//播放模式
    private int videoIndex = 0;//播放的索引
    private ArrayList<PlayInfo> playInfos = new ArrayList<PlayInfo>();

    public MgtvPlayerInfo() {
        playInfo = new PlayInfo();
    }

    /**
     * @return 当前播放视频信息
     */
    public PlayInfo getCurrVideoInfo() {
        if (videoIndex < playInfos.size()) {
            return playInfos.get(videoIndex);
        }
        return null;
    }

    /**
     * @param videoInfoList 需要播放的视频集
     */
    public void setVideoInfos(ArrayList<PlayInfo> videoInfoList) {
        if (playInfos != null) {
            playInfos.clear();
            playInfos.addAll(videoInfoList);
        }
    }

    /**
     * @param videoInfo 需要播放的单个视频
     */
    public void setVideoInfo(PlayInfo videoInfo) {
        if (playInfos != null) {
            playInfos.clear();
            playInfos.add(videoInfo);
        }
    }

    public ArrayList<PlayInfo> getVideoInfos() {
        return playInfos;
    }

    public PLAYMODE getPlaymode() {
        return playmode;
    }

    public void setPlaymode(PLAYMODE playmode) {
        this.playmode = playmode;
    }

    public int getVideoIndex() {
        return videoIndex;
    }

    public void setVideoIndex(int videoIndex) {
        this.videoIndex = videoIndex;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}
