package com.xw.player.core;

/**
 * 类型常量池
 */
public class PlayerConstants {

    public static final String UNKNOW = "unknow";
    public static final int REFRESH_TIME_INTERVAL = 500;
    public static final int MSG_REFRESH_TIME = 100;//定时刷新时间
    public static final int MSG_FIRST_FRAME_TIMEOUT = 101;//第一帧加载超时消息
    public static final int MSG_TIME_NOTIFY_STATUS = 102;//恢复判断当前时间点
    public static final int MSG_PLAYER_FIRSTFRAME_PREPARE = 2;//把(onPrepared)作为第一帧的消息
    public static final int MSG_PLAYER_TIMEOUT = 1;//加载超时消息

    /**
     * View类型
     */
    public enum ViewType {
        TYPE_SURFACE_VIEW,
        TYPE_TEXTURE_VIEW
    }

    /**
     * 监听播放事件类型
     */
    public enum EventType {
        EVENT_TYPE_START,
        EVENT_TYPE_PAUSE,
        EVENT_TYPE_STOP,
        EVENT_TYPE_PREPARED,
        EVENT_TYPE_COMPLETED,
        EVENT_INFO_PLAYER_URL_EMPTY,
        EVENT_TYPE_VIDEO_SIZE_CHANGED,
        EVENT_TYPE_SEEK_COMPLETED,
        EVENT_TYPE_ERROR,
        EVENT_TYPE_INFO,
        EVENT_TYPE_BUFFERING_START,
        EVENT_TYPE_BUFFERING_UPDATE,
        EVENT_TYPE_BUFFERING_END,
        EVENT_TYPE_NETWORK_BANDWIDTH,
        EVENT_TYPE_BUFFERING_TIMEOUT,
        EVENT_TYPE_FIRST_FRAME,
        EVENT_TYPE_SURFACE_CREATED,
        EVENT_TYPE_SURFACE_CHANGED,
        EVENT_TYPE_SURFACE_DESTROYED,
        EVENT_TYPE_TEXTURE_AVAILABLE,
        EVENT_TYPE_TEXTURE_CHANGED,
        EVENT_TYPE_TEXTURE_DESTROYED,
        EVENT_TYPE_TEXTURE_UPDATE,
        EVENT_TYPE_PLAYER_EXCEPTION,
        EVENT_TYPE_ON_PLAY_TO_TARGET_TIME,//当前播放到了目标时间
        EVENT_TYPE_ON_SWITCH_PLAYER,//切换播放器
    }

    /**
     * onError错误类型
     */
    public static class Error {
        public static final int ERROR_TIMEOUT = 1003007;
        public static final int ERROR_RTP = 11111;
    }

    /**
     * 第一帧状态
     */
    public static class FrameState {
        public static final int FRAME_STATE_NONE = 0;

        /**
         * 获取到第一帧数据
         */
        public static final int FRAME_STATE_RENDERING = 1;

        public static final int FRAME_STATE_PREPARED = 2;
        public static int retryTimes = 0;
        public static final int MAXRETRY_TIMES = 1;
    }

    /**
     * @description 播放器状态
     * @date: 2020/7/25 11:42
     * @author: Mr.xw
     */
    public static class PlayStatus {

        public static int mCurrentState = PlayStatus.STATE_IDLE;
        public static final int STATE_ERROR = -1;
        public static final int STATE_IDLE = 0;
        public static final int STATE_PREPARING = 1;
        public static final int STATE_PREPARED = 2;
        public static final int STATE_PLAYING = 3;
        public static final int STATE_PAUSED = 4;
        public static final int STATE_COMPLETED = 5;
        public static final int STATE_SEEKING = 6;
        public static final int STATE_STOP = 7;
        public static final int STATE_START = 8;
        public static final int STATE_RESET = 9;
        public static final int STATE_RELEASE = 10;
        public static final int STATE_SEEK_COMPLETED = 11;
    }


    public static class DelayTime {
        public static final int DELAY_PLAY_FIST_START_MS = 300;
        public static final int DELAY_PLAY_SPEED_MS = 300;

    }

    /**
     * @Time: 2020/7/31 16:08
     * @Description: 用于自定义播放器监听状态值
     * @Author: Mr.xw
     */
    public static class MediaPlayerInfo {
        public static final int MEDIA_INFO_NETWORK_BANDWIDTH = 703;

    }

    /**
     * @Time: 2020/8/3 11:03
     * @Description: 流媒体类型
     * @Author: Mr.xw
     */
    public static class MediaPlayerUrl {
        public static final String DREAM_RTSP = "rtsp";
        public static final String DREAM_RTP = "rtp";
        public static final String DREAM_IGMP = "igmp";

    }

    /**
     * @Time: 2020/8/3 15:02
     * @Description: 自定义错误
     * @Author: Mr.xw
     */
    public static class ErroConstants {
        public static final int MEDIA_ERROR_FIRST_FRAME_TIMEOUT = 15002001;//(包括重试的)第一帧加载超时
        public static final int MEDIA_ERROR_PLAYER_TIMEOUT = 51002002;//(单次播放)第一帧加载超时
    }


}
