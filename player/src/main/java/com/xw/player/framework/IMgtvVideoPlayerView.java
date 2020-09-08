package com.xw.player.framework;


import android.view.KeyEvent;

import com.xw.player.framework.ui.MgtvProgressSeekBarView;


/**
 * @Description UI控制类接口, 上抛给业务层用
 * @date: 2020/08/06/14:49
 * @author: Mr.xw
 */
public interface IMgtvVideoPlayerView {

    void initView();

    void init(IMgtvVideoPlayer mgtvVideoPlayer);

    /**
     * @Description: 及时显示LoadingView
     */
    void showLoadingView();

    /**
     * @Description: 隐藏LoadingView
     */
    void hideLoadingView();

    /**
     * @Description: 设置loadingView的背景色
     */
    void setLoadingBackgroundColor(int color);

    /**
     * @Description: 延迟显示LaodingView
     */
    void delayShowLoading(int delayTime);

    /**
     * @Description: 设置loading标题
     */
    void setTitle(String title);

    /**
     * 初始化LoadingView
     */
    void initLoadingView();

    /**
     * 显示SeekBar
     */
    void showSeekBarAndTextView(boolean showFlag);

    /**
     * 隐藏SeekBar
     */
    void hideSeekBarView();

    /**
     * 设置SeekBar颜色
     */
    void setSeekBarBackgroundColor(int color);

    void setTotalTime(int time);

    void updateText();

    int getPlayerUniform();

    int getSeekBarTime();

    void setMgtvProgressSeekBarView(MgtvProgressSeekBarView mgtvProgressSeekBarView);


    boolean playerBackKeyDown();

    boolean playerCenterKeyDown();

    boolean dispatchKeyEvent(KeyEvent event);

    void seekAndToPlay();

    boolean playerLeftKeyDown();

    boolean playerRightKeyDown();

    boolean playerUpKeyDown();

    boolean playerDownKeyDown();

    void setSeekBarMax(int max);

    int getSeekBarProgress();

    void setSeekBarProgress(int progress);

    void setSeekTimeTextVisibility(int visibility);

    void setSeekTimeText(String text);

    void setPlayIconView(int direction);

    void hidePlayIcon();

    void natureShowSeekBar(int delayTime);

}
