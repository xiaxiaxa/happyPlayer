package com.xw.player.framework;


import com.xw.player.framework.player.MgtvVideoPlayerImp;

/**
 * @Description 播放器实例化
 * @date: 2020/08/06/17:45
 * @author: Mr.xw
 */
public class MgtvInstantPlayer {

    private static MgtvInstantPlayer sMgtvInstantPlayer;

    private MgtvInstantPlayer() {
    }

    public static MgtvInstantPlayer getInstance() {
        if (sMgtvInstantPlayer == null) {
            sMgtvInstantPlayer = new MgtvInstantPlayer();
        }
        return sMgtvInstantPlayer;
    }

    /**
     * 创建单独的播放器，只包含播放器
     * 调用举例：
     * IMgtvVideoPlayer player = MgtvInstantPlayer.getInstance().createPlayer();
     * player.init(mContext);
     * initPlayInfo();
     * initPlayerListener();
     * initSurfaceView(true);
     * player.firstInitMediaPlayer(surfaceView, playInfo.url, mgtvPlayerInfo);
     */
    public IMgtvVideoPlayer createPlayer() {
        return new MgtvVideoPlayerImp();
    }

}
