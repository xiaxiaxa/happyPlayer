package com.xw.player.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.xw.helper.utils.MLog;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.xw.player.core.PlayerConstants.FrameState.FRAME_STATE_NONE;
import static com.xw.player.core.PlayerConstants.DelayTime.DELAY_PLAY_FIST_START_MS;
import static com.xw.player.core.PlayerConstants.DelayTime.DELAY_PLAY_SPEED_MS;
import static com.xw.player.core.PlayerConstants.ErroConstants.MEDIA_ERROR_FIRST_FRAME_TIMEOUT;
import static com.xw.player.core.PlayerConstants.EventType.EVENT_TYPE_ON_PLAY_TO_TARGET_TIME;
import static com.xw.player.core.PlayerConstants.MSG_FIRST_FRAME_TIMEOUT;
import static com.xw.player.core.PlayerConstants.MSG_PLAYER_FIRSTFRAME_PREPARE;
import static com.xw.player.core.PlayerConstants.MSG_PLAYER_TIMEOUT;
import static com.xw.player.core.PlayerConstants.MSG_REFRESH_TIME;
import static com.xw.player.core.PlayerConstants.MSG_TIME_NOTIFY_STATUS;
import static com.xw.player.core.PlayerConstants.MediaPlayerUrl.DREAM_IGMP;
import static com.xw.player.core.PlayerConstants.MediaPlayerUrl.DREAM_RTP;
import static com.xw.player.core.PlayerConstants.MediaPlayerUrl.DREAM_RTSP;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_COMPLETED;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_ERROR;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_IDLE;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_PAUSED;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_PLAYING;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_PREPARED;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_PREPARING;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_RELEASE;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_RESET;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_SEEKING;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_SEEK_COMPLETED;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_START;
import static com.xw.player.core.PlayerConstants.PlayStatus.STATE_STOP;
import static com.xw.player.core.PlayerConstants.PlayStatus.mCurrentState;
import static com.xw.player.core.PlayerConstants.REFRESH_TIME_INTERVAL;
import static com.xw.player.core.PlayerConstants.UNKNOW;
import static com.xw.player.core.PlayerUtil.getTotalTimeout;
import static com.xw.player.core.TargetTimeBean.MATCH_TYPE_TIME_TO;
import static com.xw.player.core.TargetTimeBean.TYPE_DURING;
import static com.xw.player.core.TargetTimeBean.TYPE_OUT;
import static com.xw.player.core.TargetTimeBean.TYPE_OVER_TARGET;
import static com.xw.player.core.TargetTimeBean.TYPE_TARGET;
import static com.xw.player.core.TargetTimeBean.TYPE_TARGET_TO_TIME;


/**
 * @description 基于Mediaplayer二次封装的播放器功能类
 * @date: 2020/7/25 10:57
 * @author: Mr.xw
 */
public class MgtvPlayer extends BasePlayer implements IMgtvPlayer, IBasePlayer {

    private MediaPlayer mediaPlayer;
    private PlayerListenerCallBack playerListenerCallBack;
    private Handler mHandler;
    private Context mContext;
    private SurfaceView mSurfaceView;
    private int mHasFirstFrame = FRAME_STATE_NONE;
    private boolean hasFirstFrame;//是否已有第一帧回调
    private int playNetBandWidth;
    private int onVideoSizeChangedwidth;
    private int onVideoSizeChangedheigth;
    private int mX, mY;
    private int mLastPosition;//获取的上次播放位置
    private boolean mJudgeTimesPause;//暂停判断当前时间点
    private List<TargetTimeBean> mTimeList;//存储播放的目标时间点集合
    private long mFirstFrameTimeout;//第一帧超时时间
    private SurfaceHolder mSurfaceHolder = null;
    /**
     * 音频模式如果是12345，为自定义的关闭自定义音频，系统自适应音频
     */
    int mAudioType = DEFAULT_AUDIO_CLOSE;
    static final int DEFAULT_AUDIO_CLOSE = 12345;
    /**
     * 是否循环播放模式
     */
    boolean isLoopPlay = false;

    @Override
    public void init(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mContext = context;
        if (mHandler == null) {
            getHandler();
        }
    }

    private Handler getHandler() {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message message) {
                    switch (message.what) {
                        case MSG_REFRESH_TIME:
                            judgeTimesNotify();
                            dealTimeRefreshed();
                            break;
                        case MSG_FIRST_FRAME_TIMEOUT:
                            dealFirstFrameTimeout();
                            break;
                        case MSG_TIME_NOTIFY_STATUS:
                            mJudgeTimesPause = false;
                            break;
                    }
                }
            };
        }
        return mHandler;
    }

    public void setListenerCallBack(PlayerListenerCallBack listenerCallBack) {
        this.playerListenerCallBack = listenerCallBack;
    }


    public void setDisplay(SurfaceHolder surfaceHolder) {
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(surfaceHolder);
        }
    }

    @Override
    public void start() {
        try {
            if (mediaPlayer != null) {
                mCurrentState = STATE_PLAYING;
                mediaPlayer.start();
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_START);
                }
            } else if (mCurrentState == STATE_STOP) {
                mediaPlayer.prepareAsync();
                mCurrentState = STATE_PREPARING;
            }
        } catch (IllegalStateException e) {
            MLog.e("resume e:" + e.getMessage());
        }
    }


    /**
     * 获取播放器内部异常对应的异常方法
     *
     * @param claz
     * @param e
     * @return
     */
    protected String getErrorMethod(Class claz, Exception e) {
        if (claz != null && e != null) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace.length > 0) {
                return claz.getSimpleName() + ":" + stackTrace[0].getMethodName();
            }
        }
        return UNKNOW;
    }

    public void prepareAsync() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.prepareAsync();
            } catch (IllegalStateException e) {
                MLog.d("prepareAsync e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                MLog.d("prepareAsync failed:");
            } catch (SecurityException ex) {
                MLog.d("prepareAsync failed:");
            }
        }
    }


    @Override
    public void pause() {
        mCurrentState = STATE_PAUSED;
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    if (playerListenerCallBack != null) {
                        playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_PAUSE);
                    }
                }
            } catch (IllegalStateException e) {
                MLog.d( "pause e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                MLog.d("pause failed:");
            } catch (SecurityException ex) {
                MLog.d("pause failed:");
            }
        }
    }

    @Override
    public void stop() {
        mCurrentState = STATE_STOP;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_STOP);
                }
            } catch (IllegalStateException e) {
                MLog.d( "stop e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                MLog.d("stop failed:");
            } catch (SecurityException ex) {
                MLog.d("stop failed:");
            }
        }
    }

    /**
     * @Method: release
     * @Time: 2020/7/31 18:10
     * @Description: 播放器资源释放
     * @Author: Mr.xw
     */
    @Override
    public void release() {
        mCurrentState = STATE_RELEASE;
        if (mContext != null) {
            AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (IllegalStateException e) {
                MLog.d( "release e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                MLog.d("release failed:");
            } catch (SecurityException ex) {
                MLog.d("release failed:");
            }
            mediaPlayer = null;
            mSurfaceView = null;
            mContext = null;
        }
    }

    @Override
    public void reset() {
        mCurrentState = STATE_RESET;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
            } catch (IllegalStateException e) {
                MLog.d( "reset e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                MLog.d("reset failed:");
            } catch (SecurityException ex) {
                MLog.d("reset failed:");
            }
        }
    }

    @Override
    public void seekTo(int ms) {
        mCurrentState = STATE_SEEKING;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.seekTo(ms);
            } catch (IllegalStateException e) {
                MLog.d("seekTo e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                MLog.d("seekTo failed:");
            } catch (SecurityException ex) {
                MLog.d("seekTo failed:");
            }
        }
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer == null ? false : mediaPlayer.isPlaying();
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean hasFirstFrame() {
        return mHasFirstFrame == PlayerConstants.FrameState.FRAME_STATE_RENDERING;
    }

    @Override
    public int getCurrentState() {
        return mCurrentState;
    }

    @Override
    public int getDuration() {
        if (mediaPlayer != null) {
            try {
                if (isNormalState()) {
                    return mediaPlayer.getDuration();
                }
            } catch (IllegalStateException e) {
                MLog.d("getDuration e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                MLog.d("getDuration failed:");
            } catch (SecurityException ex) {
                MLog.d("getDuration failed:");
            }
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            try {
                if (isNormalState()) {
                    return mediaPlayer.getCurrentPosition();
                }
            } catch (IllegalStateException e) {
                MLog.d("getCurrentPosition e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                MLog.d("getCurrentPosition failed:");
            } catch (SecurityException ex) {
                MLog.d("getCurrentPosition failed:");
            }
        }
        return 0;
    }

    @Override
    public void setSurfaceView(SurfaceView surfaceView, int x, int y, boolean isLive) {
        this.mSurfaceView = surfaceView;
        if (isLive) {
            mX = x;
            mY = y;
        }
    }

    public SurfaceHolder getSurfaceHolder() {
        return mSurfaceHolder;
    }

    public void setSurfaceHolder(SurfaceView surfaceView) {
        this.mSurfaceHolder = surfaceView.getHolder();
    }


    @Override
    public void setTextureView(TextureView textureView) {
    }

    @Override
    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    @Override
    public TextureView getTextureView() {
        return null;
    }

    @Override
    public void setViewType(PlayerConstants.ViewType viewType) {
        mViewType = viewType;
    }


    /**
     * @Method: setAudioType
     * @Time: 2020/7/31 10:06
     * @Description: 业务层自定义音频模式
     * @Author: Mr.xw
     */
    public void setAudioType(int AudioType) {
        this.mAudioType = AudioType;
    }

    public int getAudioType() {
        return mAudioType;
    }

    /**
     * @Method: setAudioMode
     * @Time: 2020/7/31 10:10
     * @Description: 设置音频模式
     * @Author: Mr.xw
     */
    private void setAudioMode(MediaPlayer mediaPlayer, int AudioType) {
        if (AudioType == DEFAULT_AUDIO_CLOSE) {
            return;
        }
        mediaPlayer.setAudioStreamType(AudioType);
    }

    /**
     * @Method: setLoopPlayer
     * @Time: 2020/7/31 9:29
     * @Description: 设置Mediaplayer是否循环播放模式
     * @Author: Mr.xw
     */
    public void setLoopPlay(boolean loopPlay) {
        isLoopPlay = loopPlay;
        MLog.d("Mr.xw==loopPlay" + loopPlay);
    }

    /**
     * @Method: getLoopPlay
     * @Time: 2020/7/31 9:37
     * @Author: Mr.xw
     */
    public boolean getLoopPlay() {
        MLog.d("Mr.xw==isLoopPlay111" + isLoopPlay);
        return isLoopPlay;
    }

    /**
     * @Method: firstInitMediaPlayer
     * @Time: 2020/7/31 9:12
     * @Description: 第一次初始化播放器，走标准的MediaPlayer使用流程
     * @Author: Mr.xw
     */
    public void openMediaPlayer(SurfaceView surfaceView, String playUrl) {
        if (mediaPlayer == null || surfaceView == null) {
            return;
        }
        if (TextUtils.isEmpty(playUrl)) {
            if (playerListenerCallBack != null) {
                playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_INFO_PLAYER_URL_EMPTY);
            }
            return;
        }
        setSurfaceHolder(surfaceView);
        surfaceView.getHolder().addCallback(mSurfaceCallback);
        if (playerListenerCallBack != null) {
            playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_SURFACE_CREATED);
        }
        if (surfaceView.getVisibility() != View.VISIBLE) {
            surfaceView.setVisibility(View.VISIBLE);
        }
        openMediaPlayer(mediaPlayer, playUrl);
    }

    /**
     * @Time: 2020/8/3 11:07
     * @Description: 区分直播流，再确定是否需要使能直播拼串
     * @Author: Mr.xw
     */
    private String getFinalPlayUrl(String playUrl) {

        if (playUrl != null && (playUrl.startsWith(DREAM_RTP) || playUrl.startsWith(DREAM_RTSP)
                || playUrl.startsWith(DREAM_IGMP))) {

            if (PlayerUtil.distinguishModel()) {
                playUrl = playUrl + PlayerUtil.getPlaySize(getSurfaceView(), mX, mY);
            }
        }
        return playUrl;
    }

    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            /**兼容修改，如果能收到surfaceCreated回调，系统默认会以这个为准，如果延迟加载播放器，不会
             * 收到surfaceCreated回调，会以前面setSurfaceHolder的为准
             */
            if (mediaPlayer != null && getSurfaceHolder() != null) {
                mediaPlayer.setDisplay(getSurfaceHolder());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            if (playerListenerCallBack != null) {
                playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_SURFACE_CHANGED, format, width, height);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (playerListenerCallBack != null) {
                playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_SURFACE_DESTROYED);
            }
        }
    };

    /**
     * @Method: prepareMediaplayer
     * @Time: 2020/7/31 9:25 Mediaplayer  prepare准备工作，setDataSource等
     * @Description:
     * @Author: Mr.xw
     */
    private void prepareMediaplayer(MediaPlayer mediaPlayer, String playUrl) {
        if (mediaPlayer != null) {
            if (mContext != null) {
                AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                am.abandonAudioFocus(null);
            }
            mContext = null;
        }
        setAudioMode(mediaPlayer, getAudioType());
        try {
            mediaPlayer.setDataSource(playUrl);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        mediaPlayer.prepareAsync();
/*        mediaPlayer.setLooping(isLoopPlay);
        MLog.d("Mr.xw==isLoopPlay333()" + isLoopPlay);*/
    }


    /**
     * @Method: initMgtvPlayer
     * @Time: 2020/7/31 13:56
     * @Description: 初始化播放器，准备开始播放
     * @Author: Mr.xw
     */
    private void openMediaPlayer(final MediaPlayer mediaPlayer, String playUrl) {
        if (playUrl == null || mediaPlayer == null) {
            return;
        }
        prepareMediaplayer(mediaPlayer, getFinalPlayUrl(playUrl));
        /** OnPreparedListener监听*/
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mCurrentState = STATE_PREPARED;
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onPrepare();
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_PREPARED);

                }

                getHandler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer != null) {
                                    mediaPlayer.start();
                                    mediaPlayer.setLooping(getLoopPlay());
                                    mCurrentState = STATE_START;
                                    if (playerListenerCallBack != null) {
                                        playerListenerCallBack.onStart();
                                        playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_START);
                                    }
                                    mCurrentState = STATE_PLAYING;
                                }
                            }
                        }, DELAY_PLAY_FIST_START_MS);
            }
        });
        /** OnInfoListener监听*/
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onInfo(what, extra);
                }
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        if (playerListenerCallBack != null) {
                            playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_BUFFERING_END);
                        }
                        break;
                    case PlayerConstants.MediaPlayerInfo.MEDIA_INFO_NETWORK_BANDWIDTH:
                        setPlayNetBandWidth(extra);
                        if (playerListenerCallBack != null) {
                            playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_NETWORK_BANDWIDTH);
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        if (playerListenerCallBack != null) {
                            playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_BUFFERING_START, extra);
                        }
                        break;
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        mCurrentState = STATE_PLAYING;
                        if (playerListenerCallBack != null) {
                            playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_FIRST_FRAME);
                        }
                        mHasFirstFrame = PlayerConstants.FrameState.FRAME_STATE_RENDERING;
                        break;
                    default:
                        if (playerListenerCallBack != null) {
                            playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_INFO, what, extra);
                        }
                        mHasFirstFrame = PlayerConstants.FrameState.FRAME_STATE_RENDERING;
                        break;
                }
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_INFO);
                }
                return false;
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onBufferingUpdate(percent);
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_BUFFERING_UPDATE);
                }
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
                mCurrentState = STATE_ERROR;
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onError(what, extra);
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_ERROR);
                }
                return false;
            }
        });

        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onVideoSizeChanged(width, height);
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_VIDEO_SIZE_CHANGED);
                }
                setOnVideoSizeChangedwidth(width);
                setOnVideoSizeChangedheigth(height);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mCurrentState = STATE_COMPLETED;
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onCompletion();
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_COMPLETED);
                }
            }
        });
        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mCurrentState = STATE_SEEK_COMPLETED;
                if (playerListenerCallBack != null) {
                    playerListenerCallBack.onSeekComplete();
                    playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_SEEK_COMPLETED);
                }
            }
        });

    }

    @Override
    public boolean onInfo(int what, int extra) {
        return false;
    }

    @Override
    public boolean onError(int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onVideoSizeChanged(int width, int heigth) {
    }

    @Override
    public void onCompletion() {
    }

    @Override
    public void onSeekComplete() {
    }


    /**
     * @Method:
     * @Time: 2020/7/31 17:03
     * @Description: onVideoSizeChanged发生变化的时候，获取变化的视屏输出层的匡高
     * @Author: Mr.xw
     */
    public int getOnVideoSizeChangedwidth() {
        return onVideoSizeChangedwidth;
    }

    public void setOnVideoSizeChangedwidth(int onVideoSizeChangedwidth) {
        this.onVideoSizeChangedwidth = onVideoSizeChangedwidth;
    }

    public int getOnVideoSizeChangedheigth() {
        return onVideoSizeChangedheigth;
    }

    public void setOnVideoSizeChangedheigth(int onVideoSizeChangedheigth) {
        this.onVideoSizeChangedheigth = onVideoSizeChangedheigth;
    }

    /**
     * @Method: getPlayNetBandWidth
     * @Time: 2020/7/31 15:59
     * @Description: 获取网络流媒体的下载速度，带宽,主要是点播hls流媒体用到
     * @Author: Mr.xw
     */
    public int getPlayNetBandWidth() {
        return playNetBandWidth;
    }

    private void setPlayNetBandWidth(int playNetBandWidth) {
        this.playNetBandWidth = playNetBandWidth;
    }

    /**
     * @Method: switchSpeed
     * @Time: 2020/7/31 16:51
     * @Description: 设置倍速播放
     * @Author: Mr.xw
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void switchSpeed(float speed) {
        if (mediaPlayer == null) {
            return;
        }
        try {
            PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
            playbackParams.setSpeed(speed);
            mediaPlayer.setPlaybackParams(playbackParams);
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    start();
                }
            }, DELAY_PLAY_SPEED_MS);
        } catch (Exception e) {
            mCurrentState = STATE_ERROR;
            if (playerListenerCallBack != null) {
                playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_PLAYER_EXCEPTION,
                        getErrorMethod(this.getClass(), e), e.getMessage());
            }
        }
    }

    //通知播放到时间点
    private void notifyPlayToTarget(TargetTimeBean bean) {
        if (playerListenerCallBack != null) {
            playerListenerCallBack.onEvent(EVENT_TYPE_ON_PLAY_TO_TARGET_TIME, bean);
        }
    }

    /**
     * @Method: judgeTimesNotify
     * @Time: 2020/8/3 14:44
     * @Description: 判断时间点到达的通知
     * @Author: Mr.xw
     */
    private void judgeTimesNotify() {
        int position = getCurrentPosition();
        if (!hasFirstFrame() && position > 0 && mLastPosition > 0
                && position != mLastPosition) {
            //系统播放器，兼容第一帧,当前播放位置>0时，认为已有第一帧
            dealNotifyFirstFrame();
        }
        mLastPosition = position;
        if (mJudgeTimesPause) {
            return;
        }
        if (mTimeList != null) {
            for (TargetTimeBean bean : mTimeList) {
                boolean notify = false;
                switch (bean.getNotifyType()) {
                    case TYPE_DURING:
                        if (!bean.isLastDuring() && bean.isDuring(position)) {
                            notify = true;
                        }

                        break;
                    case TYPE_OUT:

                        if (bean.isLastDuring() && !bean.isDuring(position)) {
                            notify = true;
                        }

                        if (!bean.isLastDuring() && !bean.isLastPre() && !bean.isLastAfter() &&
                                !bean.isDuring(position)) {
                            //第一次判断
                            notify = true;
                        }
                        break;
                    case TYPE_OVER_TARGET:
                        if (!bean.isLastAfter() && !bean.isTargetPre(position)) {
                            notify = true;
                        }
                        break;
                    case TYPE_TARGET_TO_TIME:
                        int tmpLastPosition = bean.getLastTimePosition();

                        if (bean.isLastPre() && !bean.isTargetPre(position)) {
                            notify = true;
                            //兼容一下，如果上次和这次判断的当前播放时间差超过了2s，
                            // 就认为seek过了时间点(实际上正常只应该差500ms，此处做个容错)
                            // 两种情况都会通知
                            boolean isTo = tmpLastPosition > 0 && (position - tmpLastPosition)
                                    < REFRESH_TIME_INTERVAL * 4;
                            if (isTo) {
                                bean.setMatchType(MATCH_TYPE_TIME_TO);
                            }
                        }
                        break;

                    case TYPE_TARGET:
                    default:
                        if (bean.isLastPre() && !bean.isTargetPre(position)) {
                            notify = true;
                        }
                        break;
                }
                bean.setLastTimeState(position);
                if (notify) {
                    notifyPlayToTarget(bean);
                }
            }
        }
    }

    /**
     * @Method: dealNotifyFirstFrame
     * @Time: 2020/8/3 14:55
     * @Description: 处理第一帧
     * @Author: Mr.xw
     */
    public void dealNotifyFirstFrame() {
        if (hasFirstFrame || mHandler == null) {
            return;
        }
        hasFirstFrame = true;
        mHandler.removeMessages(MSG_PLAYER_FIRSTFRAME_PREPARE);
        onPlayerFirstFrame();
    }

    private void onPlayerFirstFrame() {
        if (mHandler == null) {
            return;
        }
        mHandler.removeMessages(MSG_PLAYER_TIMEOUT);
        if (playerListenerCallBack != null) {
            playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_FIRST_FRAME);
        }
    }

    /**
     * @Method: dealTimeRefreshed
     * @Time: 2020/8/3 15:01
     * @Description: 时间定时器
     * @Author: Mr.xw
     */
    public void dealTimeRefreshed() {
        if (mHandler == null) {
            return;
        }
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_REFRESH_TIME;
        mHandler.sendMessageDelayed(msg, REFRESH_TIME_INTERVAL);
    }

    /**
     * @Method:dealFirstFrameTimeout
     * @Time: 2020/8/3 15:13
     * @Description:处理第一帧超时
     * @Author: Mr.xw
     */
    private void dealFirstFrameTimeout() {
        notifyError(MEDIA_ERROR_FIRST_FRAME_TIMEOUT, (int) mFirstFrameTimeout);
    }

    private void notifyError(int what, int extra) {
        notifyError(what, extra, getCurrentPosition(), false);
    }

    private void notifyError(int what, int extra, int curPos, boolean processed) {
        release();
        mCurrentState = STATE_ERROR;
        if (playerListenerCallBack != null) {
            playerListenerCallBack.onError(what, extra);
        }
    }

    /**
     * @Method: setTimeoutMsg
     * @Time: 2020/8/3 16:01
     * @Description: 设置第一帧超时时间消息
     * @Author: Mr.xw
     */
    public void setTimeoutMsg(long timeout) {
        int playSettingValue = getTotalTimeout();
        if (playSettingValue > 0) {
            mFirstFrameTimeout = playSettingValue;
        } else {
            mFirstFrameTimeout = timeout;
        }
        if (mFirstFrameTimeout > 0) {
            mHandler.sendEmptyMessageDelayed(MSG_FIRST_FRAME_TIMEOUT, mFirstFrameTimeout);
        }
    }

    private boolean isNormalState() {
        return (mediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE
                && mCurrentState != STATE_PREPARING && mCurrentState != STATE_STOP &&
                mCurrentState != STATE_RESET && mCurrentState != STATE_RELEASE);
    }

    public void specialPlay(SurfaceView surfaceView) {
        setSurfaceHolder(surfaceView);
        surfaceView.getHolder().addCallback(mSurfaceCallback);
        if (playerListenerCallBack != null) {
            playerListenerCallBack.onEvent(PlayerConstants.EventType.EVENT_TYPE_SURFACE_CREATED);
        }
        if (surfaceView.getVisibility() != View.VISIBLE) {
            surfaceView.setVisibility(View.VISIBLE);
        }
    }

    public void rmPlayToTargetTime(int tag) {
        if (mTimeList == null) {
            return;
        }
        for (TargetTimeBean bean : mTimeList) {
            if (bean.getTag() == tag) {
                mTimeList.remove(bean);
                break;
            }
        }
    }

    public void rmPlayToTargetTime(int tag, int targetTime) {
        if (mTimeList == null) {
            return;
        }
        for (TargetTimeBean bean : mTimeList) {
            if (bean.getTag() == tag && bean.getTargetTime() == targetTime) {
                mTimeList.remove(bean);
                break;
            }
        }
    }

    public void addPlayToTargetTime(TargetTimeBean timeBean) {
        if (timeBean == null) {
            return;
        }
        if (mTimeList == null) {
            mTimeList = new CopyOnWriteArrayList<>();
        }
        mTimeList.add(timeBean);
    }


    public void rmPlayToTargetTime(TargetTimeBean timeBean) {
        if (mTimeList == null || timeBean == null) {
            return;
        }
        for (TargetTimeBean bean : mTimeList) {
            if (bean.equals(timeBean)) {
                mTimeList.remove(bean);
                break;
            }
        }
    }

    public void addPlayToTargetTime(int tag, int targetTime) {
        TargetTimeBean bean = new TargetTimeBean();
        bean.setNotifyType(TYPE_TARGET);
        bean.setTag(tag);
        bean.setTargetTime(targetTime);
        addPlayToTargetTime(bean);
    }

}
