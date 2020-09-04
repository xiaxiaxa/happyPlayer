package com.xw.happy.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.xw.happy.R;
import com.xw.helper.utils.MLog;
import com.xw.player.core.PlayerConstants;
import com.xw.player.framework.ListenerCallBack;
import com.xw.player.framework.bean.MgtvPlayerInfo;
import com.xw.player.framework.bean.PlayInfo;
import com.xw.player.framework.player.MgtvVideoPlayerImp;
import com.xw.player.framework.player.MgtvVideoPlayerView;
import com.xw.player.framework.ui.IViewListener;

/**
 * @Time: 2020/8/27 10:10
 * @Description: 自定义MediaPlayer
 * @Author: Mr.xw
 */
public class MyPlayerActivity extends Activity {
    MgtvVideoPlayerView mgtvPlayerTestView;
    private Context mContext;
    private SurfaceView surfaceView;
    private SurfaceView surfaceView1;
    private MgtvPlayerInfo mgtvPlayerInfo;
    private PlayInfo playInfo;
    private static String TAG = "MgtvPlayerActivity" ;
    private String videoUrlRtp = "rtp://239.76.245.115:1234";
    private String videoUrlRtp2 = "rtp://239.76.246.104:1234 ?ChannelFCCIP=61.150.161.42&ChannelFCCPort=8027";
//    private String videoUrlHls2= "http://10.255.30.137:8082/EDS/RedirectPlay/000000000000/vod/75c2a75766da48a692c8383b5d79926a/56cc3627501c469cb9a66022c83ca984?UserToken=00228754829678360681080810093254&UserName=6830016";
    /**
     *45分多钟的正常片*
     */
    private String videoUrlHls1 = "http://10.255.30.137:8082/EDS/RedirectPlay/000000000000/vod/f95e41c67ca2410c89b335ee5f5eecb8/3cc6fae6d07740aa8d57934e26cc2632?UserToken=123456789&UserName=6830018";
    /**
     * 1分多钟的短片*
     */
//    private String videoUrlHls = "http://10.255.30.137:8082/EDS/RedirectPlay/000000000000/vod/75c2a75766da48a692c8383b5d79926a/56cc3627501c469cb9a66022c83ca984?UserToken=00228754829678360681080810093254&UserName=6830016";
    /**
     * 2小时电影
     */
    private String videoUrlHls2 = "http://10.255.30.137:8082/EDS/RedirectPlay/000000000000/vod/5d9ed2bc2a69ae88169a4ee4bf286f55/5d9ed2bced7df138ff7b04a9ee35c222?UserToken=00228754829678360681080810093254&UserName=6830016";
    private String videoUrlHls;
    MgtvVideoPlayerImp player = new MgtvVideoPlayerImp();
    boolean isNotHide = false;
    Handler mHandler = new Handler();
    private IViewListener iViewListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.mgtv_player);
        surfaceView = findViewById(R.id.surface);
        surfaceView1 = findViewById(R.id.surface1);
        initIntent();
        MLog.d("initIntent=" + initIntent());
        player.init(mContext);
        mgtvPlayerTestView = findViewById(R.id.mgtv_player);
        mgtvPlayerTestView.init(player);
        iViewListener = new IViewListener() {
            @Override
            public void hideSeekBarAndTextView() {
                Log.d("Mr.xw=123=","=hideSeekBarAndTextView=" );
            }

            @Override
            public void showSeekBarAndTextView() {
                Log.d("Mr.xw=123=","=showSeekBarAndTextView=");
            }

            @Override
            public void showSeekTimeTextView() {
                Log.d("Mr.xw=123=","=showSeekTimeTextView=" );
            }

            @Override
            public void hideSeekTimeTextView() {
                Log.d("Mr.xw=123=","=hideSeekTimeTextView=" );
            }
        };
        mgtvPlayerTestView.getMgtvProgressSeekBarView().setViewListener(iViewListener);

//        mgtvPlayerTestView.showLoadingView();
        /**初始化播放器*/
        initMgtvPlayer();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                initMgtvPlayer();
//            }
//        }, 2000);
    }

    /**
     * 获取页面传递参数
     */
    private boolean initIntent() {
        videoUrlHls = getIntent().getStringExtra("url");
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化播放器准备开始播放
     */
    private void initMgtvPlayer() {

        /** 初始化播放info信息*/
        initPlayInfo();
        player.setListenerCallBack(listenerCallBack);
        /** 准备工作完成，开始正常播放流程*/
        Log.d("Mr.xw==" , "player.getCurrentState ==  "+ player.getCurrentState());
        player.openMediaPlayer(surfaceView,0,0,false);

    }

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

        }

        @Override
        public void onStart() {
            try {
                mgtvPlayerTestView.setTotalTime(player.getDuration());
                mgtvPlayerTestView.setSeekBarProgress(player.getCurrentPosition());
                mgtvPlayerTestView.setSeekBarMax(player.getDuration());
                mgtvPlayerTestView.naturShowSeekBar(3000);
            } catch (IllegalStateException e) {
                Log.d(TAG, "setDisplay e:" + e.getMessage());
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "setDisplay failed:", ex);
            } catch (SecurityException ex) {
                Log.d(TAG, "setDisplay failed:", ex);
            }
        }
    };


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
        playInfo.setUrl(videoUrlHls);

        /** 业务层自定义infoBean*/
        player.setMgtvPlayerInfo(mgtvPlayerInfo);
        player.setPlayerInfo(playInfo);
    }


    @Override
    public void onPause() {
        super.onPause();
        releaseData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseData();
    }

    private void releaseData() {
        if (isFinishing()) {
            if (player != null) {
                player.setListenerCallBack(null);
                player.pause();
                player.stop();
                player.reset();
                player.release();
            }
            if(mgtvPlayerTestView!=null){
                mgtvPlayerTestView.release();
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mgtvPlayerTestView.showSeekBarAndTextView(true);
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    mgtvPlayerTestView.playerBackKeyDown();
                    isNotHide = false;
                    finish();
                    return true;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mgtvPlayerTestView.playerDownKeyDown();
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1920,1080);
                    surfaceView.setLayoutParams(layoutParams);
                    player.switchPlayer(surfaceView,videoUrlHls2,false);
                    mgtvPlayerTestView.showSeekBarAndTextView(false);
                    /*                    *//**测试直播切台*//*
                    playInfo.setUrl(videoUrlRtp2);
                    player.stop();
                    player.release();
                    player.openMediaPlayer(surfaceView,true);*/

//                    *测试点播切台
/*                    playInfo.setUrl(videoUrlHls2);
                    player.pause();
                    player.stop();
                    player.reset();
                    player.openMediaPlayer(surfaceView,false);*/
                    return true;
                case KeyEvent.KEYCODE_DPAD_UP:
                    mgtvPlayerTestView.playerUpKeyDown();
                    RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(1920,1080);
                    surfaceView.setLayoutParams(layoutParams1);
//                    mgtvPlayerTestView.release();
                    player.release();
                    MgtvVideoPlayerImp player1 = new MgtvVideoPlayerImp();
                    mgtvPlayerTestView.init(player1);
                    mgtvPlayerTestView.showSeekBarAndTextView(false);
                    initMgtvPlayer();
                    return true;
                default:
                    return super.dispatchKeyEvent(event);
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {


        }
        return super.dispatchKeyEvent(event);
    }

}
