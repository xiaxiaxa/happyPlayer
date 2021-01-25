package com.xw.happy.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Toast;


import com.xw.happy.R;
import com.xw.happy.player.VideoListener;
import com.xw.happy.ui.PlayerView;
import com.xw.helper.utils.MLog;
import com.xw.player.core.PlayerConstants;
import com.xw.player.framework.ListenerCallBack;
import com.xw.player.framework.bean.MgtvPlayerInfo;
import com.xw.player.framework.bean.PlayInfo;
import com.xw.player.framework.player.MgtvVideoPlayerImp;
import com.xw.player.framework.player.MgtvVideoPlayerView;
import com.xw.player.framework.ui.IViewListener;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * @Time: 2020/8/27 10:10
 * @Description: 自定义IjkPlayer
 * @Author: Mr.xw
 */
public class IjkPlayerActivity extends Activity implements VideoListener {

    private static final String TAG = "IjkPlayerActivity" ;
    private PlayerView mMediaPlayer = null;
    private String videoUrlRtp = "rtp://239.76.245.115:1234";
    private String videoUrlHls = "http://10.255.30.137:8082/EDS/RedirectPlay/000000000000/vod/f95e41c67ca2410c89b335ee5f5eecb8/3cc6fae6d07740aa8d57934e26cc2632?UserToken=123456789&UserName=6830018";
    private String videoUrlHls1 ="http://10.255.30.137:8082/EDS/RedirectPlay/000000000000/vod/5e990b4c8aee957d47c1c002118dbcf9/5ec23aca2f20487083931172f5002cff?UserToken=123456789&UserName=CS005456387004%40VOD";
    MgtvVideoPlayerView mgtvPlayerTestView;
    MgtvVideoPlayerImp player = new MgtvVideoPlayerImp();
    private PlayInfo playInfo;
    Handler mHandler = new Handler();
    private IViewListener iViewListener;
    private SurfaceView surfaceView;
    private MgtvPlayerInfo mgtvPlayerInfo;
    private Context mContext;

    private ListenerCallBack listenerCallBack = new ListenerCallBack() {
        @Override
        public void onEvent(PlayerConstants.EventType type, Object... params) {

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

        @Override
        public void onPrepare() {
            player.setLoopPlay(true);
        }

        @Override
        public void onStart() {
            try {
                mgtvPlayerTestView.setTotalTime(player.getDuration());
                mgtvPlayerTestView.setSeekBarProgress(player.getCurrentPosition());
                mgtvPlayerTestView.setSeekBarMax(player.getDuration());
                mgtvPlayerTestView.natureShowSeekBar(3000);
            } catch (IllegalStateException e) {
                Log.d(TAG, "setDisplay e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "setDisplay failed:", ex);
            } catch (SecurityException ex) {
                Log.d(TAG, "setDisplay failed:", ex);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ijkplayer);
        mContext = this;
        mMediaPlayer = findViewById(R.id.ijk_player);
        surfaceView = findViewById(R.id.surface3);
        /**硬解码走播放器*/
        player.init(mContext);
        mgtvPlayerTestView = findViewById(R.id.mgtv_player1);
        mgtvPlayerTestView.init(player);
        iViewListener = new IViewListener() {
            @Override
            public void hideSeekBarAndTextView() {
                MLog.d("=hideSeekBarAndTextView=" );
            }

            @Override
            public void showSeekBarAndTextView() {
                MLog.d("=showSeekBarAndTextView=");
            }

            @Override
            public void showSeekTimeTextView() {
                MLog.d("=showSeekTimeTextView=" );
            }

            @Override
            public void hideSeekTimeTextView() {
                MLog.d("=hideSeekTimeTextView=" );
            }
        };
        mgtvPlayerTestView.getMgtvProgressSeekBarView().setViewListener(iViewListener);

//        mgtvPlayerTestView.showLoadingView();
        /**初始化播放器*/
        initMgtvPlayer();

    }

    /**
     * @method initPlayInfo
     * @description 初始化播放info信息
     * @date: 2020/7/26 21:04
     * @author: Mr.xw
     */
    private void initPlayInfo() {
        if (playInfo == null) {
            playInfo = new PlayInfo();
        }
        if (mgtvPlayerInfo == null) {
            mgtvPlayerInfo = new MgtvPlayerInfo();
        }
        /** 在播放信息info里面带播放串*/
        playInfo.setUrl(videoUrlHls1);

        /** 业务层自定义infoBean*/
        player.setMgtvPlayerInfo(mgtvPlayerInfo);
        player.setPlayerInfo(playInfo);
    }



    /**
     * 初始化播放器准备开始播放
     */
    private void initMgtvPlayer() {

        /** 初始化播放info信息*/
        initPlayInfo();
        player.setListenerCallBack(listenerCallBack);
        /** 准备工作完成，开始正常播放流程*/
        MLog.d( "player.getCurrentState ==  "+ player.getCurrentState());
        player.openMediaPlayer(surfaceView,0,0,false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initIJKPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseData();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseData();
    }

    private void releaseData() {
        if (isFinishing()) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

    /**
     * 初始化播放器
     */
    private void initIJKPlayer() {
        mMediaPlayer.setVideoListener(this);
        mMediaPlayer.setPath(videoUrlHls);
        /**false 软解，true:硬解 默认使用硬解码*/
        mMediaPlayer.setEnableMediaCodec(false);
        try {
            mMediaPlayer.load();
        } catch (IOException e) {
            Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mMediaPlayer.start();
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }
}
