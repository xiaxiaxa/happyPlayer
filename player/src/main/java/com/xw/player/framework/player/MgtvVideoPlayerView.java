package com.xw.player.framework.player;

import android.content.Context;

import android.os.Handler;
import android.os.Message;

import android.util.AttributeSet;
import android.view.KeyEvent;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xw.helper.utils.MLog;
import com.xw.player.framework.IMgtvVideoPlayer;
import com.xw.player.framework.IMgtvVideoPlayerView;
import com.xw.player.framework.bean.PlayInfo;
import com.xw.player.framework.ui.MgtvLoadingView;
import com.xw.player.framework.ui.MgtvProgressSeekBarView;

import java.util.Timer;
import java.util.TimerTask;

import static com.xw.player.framework.constants.PlayerConstants.HIDE_PROGRESS_DELAY_TIME;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.ACCELERATION_NUM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.DEFUALTNUM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.DELAY_INIT_PROGRESS_BAR;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.FIFTH_UNIFORM_SEEK_NUM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.FIRST_SEEK_UNIFORM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.FIRST_UNIFORM_SEEK_NUM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.FOURTH_UNIFORM_SEEK_NUM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.LEF_SEEK_BAR_ICON;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.MAX_SEEK_UNIFORM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.NONE_SEEK_BAR_ICON;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.NUM_UNIFORM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.RIGHT_SEEK_BAR_ICON;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.SECOND_UNIFORM_SEEK_NUM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.SEEK_LEFT_LIMIT;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.THIRD_UNIFORM_SEEK_NUM;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.TOTAL_TIME_FIRST;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.TOTAL_TIME_FOURTH;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.TOTAL_TIME_SECOND;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.TOTAL_TIME_THIRD;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.TOTAL_TIME_ZERO;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.UPDATE_TIME_TEXT;
import static com.xw.player.framework.utils.PlayUtils.msConvertToHms;


/**
 * @Description 实现播放器封装的基础View, 包含进度条、缓冲View、快进快退暂停图标等
 * @date: 2020/08/06/14:47
 * @author: Mr.xw
 */
public class MgtvVideoPlayerView extends FrameLayout implements IMgtvVideoPlayerView {

    private MgtvLoadingView mgtvLoadingView;
    private IMgtvVideoPlayer sMgtvVideoPlayer;
    private MgtvProgressSeekBarView mgtvProgressSeekBarView;
    private Context mContext;
    protected PlayInfo playInfo;

    private int accelerationTime;
    private int accelerationNum = ACCELERATION_NUM;
    private int defualtNum = DEFUALTNUM;

    private boolean isLongPress = false;
    private boolean isHide = false;
    private boolean isSeekAndToPlay = false;
    private boolean isLeftKeyEvent = false;
    private boolean needAddUnform = false;
    private boolean isPause = false;
    private Timer timer;
    private boolean isFirstInit = false;
    private static final String TAG = "MgtvVideoPlayerView";
    private int lastDelayTime;
    private TimerTask task;

    /**
     * @description 计时器刷新时间显示以及进度相关增量逻辑处理
     * @date: 2020/8/9 1:42
     * @author: Mr.xw
     */

    private Runnable hideProgressBarViewRunner = new Runnable() {

        @Override
        public void run() {
            if (getMgtvProgressSeekBarView() != null) {
                getMgtvProgressSeekBarView().hideSeekBarAndTextView();
            }
        }
    };

    private Runnable showProgressBarViewRunner = new Runnable() {

        @Override
        public void run() {
            if (getMgtvProgressSeekBarView() != null) {
                getMgtvProgressSeekBarView().showSeekBarAndTextView();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateSeekData();
            super.handleMessage(msg);
        }
    };

    public MgtvVideoPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public MgtvVideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MgtvVideoPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        setFirstInit(true);
    }

    /**
     * @Time: 2020/8/6 16:01
     * @Description: 获得传递的播放器对象，用于UI交互使用
     * @Author: Mr.xw
     */
    @Override
    public void init(IMgtvVideoPlayer mgtvVideoPlayer) {
        if (isFirstInit()) {
            if (mgtvVideoPlayer == null) {
                return;
            }
            setMgtvVideoPlayer(mgtvVideoPlayer);
            if (playInfo == null) {
                playInfo = new PlayInfo();
            }
            /**适配机顶盒得出延迟经验值，延迟50ms一次task，然后每隔100ms执行一次task*/
            startTimerAndTask();
            initView();

        } else {
            MLog.d("Error calling method about init");
        }
    }

    private void startTimerAndTask() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = UPDATE_TIME_TEXT;
                if (mHandler != null) {
                    mHandler.sendMessage(message);
                }
            }
        };
        timer.schedule(task, 50, 100);
    }

    protected void updateSeekData() {
        getSeekBarAcceleration();
        if (isSeekAndToPlay()) {
            if (isNeedAddUnform()) {
                if (isLongPress()) {
                    setSeekBarProgress(getSeekBarKeyEvent());
                }
            }
        } else {
            setSeekBarProgress(getCurrentPosition());
        }
        updateText();
    }

    public void setTotalTime(int time) {
        if (playInfo != null) {
            playInfo.setTotalTime(time);
        }
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().setTotalTimeText(msConvertToHms(time));
        }
    }

    /**
     * @description 这里我们对进度条增加加速度方法，每秒刷新一次
     * 长按的时候开始计时，非长按状态，重新计零；
     * @date: 2020/8/9 0:17
     * @author: Mr.xw
     */
    private int getSeekBarAcceleration() {
        if (isLongPress()) {
            accelerationTime++;
        } else {
            accelerationTime = 1;
        }
        return accelerationTime;
    }


    @Override
    public View.OnFocusChangeListener getOnFocusChangeListener() {
        return super.getOnFocusChangeListener();
    }

    public void updateText() {
        if (getMgtvProgressSeekBarView() == null) {
            return;
        }
        if (getMgtvVideoPlayer() != null) {
            long time = getCurrentPosition();
            getMgtvProgressSeekBarView().setTimeText(msConvertToHms(time));
        }
        if (!isPause()) {
            long seekTime = getMgtvProgressSeekBarView().getSeekBarProgress();
            getMgtvProgressSeekBarView().setSeekTimeText(msConvertToHms(seekTime));
        } else {
            long seekTimePos = getCurrentPosition();
            getMgtvProgressSeekBarView().setSeekTimeText(msConvertToHms(seekTimePos));
        }

    }

    /**
     * @description 获得短按的固定增量以及长按的加速度
     * @date: 2020/8/9 0:52
     * @author: Mr.xw
     */
    public int getSeekBarKeyEvent() {
        if (isLongPress()) {
            defualtNum++;
        } else {
            defualtNum = 1;
        }
        int leftTime = getSeekBarTime() - (defualtNum * getPlayerUniform());
        int rightTime = getSeekBarTime() + (defualtNum * getPlayerUniform());

        if (isLeftKeyEvent()) {
            if (leftTime <= SEEK_LEFT_LIMIT) {
                leftTime = SEEK_LEFT_LIMIT;
            }
            return leftTime;
        } else {
            if (rightTime >= getDuration()) {
                rightTime = getDuration();
            }
            return rightTime;
        }
    }

    /**
     * @Method: getPlayerUniform
     * @Time: 2020/8/10 11:26
     * @Description: 通过总时长的260次执行次数，算出基于等分的合适加速
     * Sn=n*a+n(n-1)d/2 初始值设定为a=30，n为划分的等份260,也就是从0到结束加速执行次数,Sn为总时长；
     * d = (2*Sn -a*n)/n*n-n (加速度)
     * @Author: Mr.xw
     */
    public int getPlayerUniform() {
        /**获取总时长*/
        int totalTime = playInfo.getTotalTime();
        int fixSeekUniform;
        int status = 0;
        /**
         *@Time: 2020/8/10 14:49
         *@Description: 这里我们根据经验值再进行细微调节，区分三种情况*
         * 1、影片长大于一次seek时长 暂定15s;15s<=totalTime<=5min fixSeekUniformFirst
         * 2、5min<totalTime<=15min fixSeekUniformSecond
         * 3、15min<totalTime<=45min fixSeekUniformThird
         * 4、45min<totalTime<=90min fixSeekUniformFourth
         * 5、90min<totalTime<=180min fixSeekUniformFifth
         *@Author: Mr.xw
         */
        fixSeekUniform = ((NUM_UNIFORM * totalTime - (NUM_UNIFORM * FIRST_SEEK_UNIFORM * MAX_SEEK_UNIFORM)))
                / (MAX_SEEK_UNIFORM * MAX_SEEK_UNIFORM);

        if (totalTime > TOTAL_TIME_ZERO && totalTime <= TOTAL_TIME_FIRST) {
            status = FIRST_UNIFORM_SEEK_NUM;
        }
        if (totalTime > TOTAL_TIME_FIRST && totalTime <= TOTAL_TIME_SECOND) {
            status = SECOND_UNIFORM_SEEK_NUM;
        }
        if (totalTime > TOTAL_TIME_SECOND && totalTime <= TOTAL_TIME_THIRD) {
            status = THIRD_UNIFORM_SEEK_NUM;
        }
        if (totalTime > TOTAL_TIME_THIRD && totalTime <= TOTAL_TIME_FOURTH) {
            status = FOURTH_UNIFORM_SEEK_NUM;
        }
        if (totalTime > TOTAL_TIME_FOURTH) {
            status = FIFTH_UNIFORM_SEEK_NUM;
        }

        switch (status) {
            case FIRST_UNIFORM_SEEK_NUM:
                fixSeekUniform = FIRST_UNIFORM_SEEK_NUM * fixSeekUniform;
                break;
            case SECOND_UNIFORM_SEEK_NUM:
                fixSeekUniform = SECOND_UNIFORM_SEEK_NUM * fixSeekUniform;
                break;
            case THIRD_UNIFORM_SEEK_NUM:
                fixSeekUniform = THIRD_UNIFORM_SEEK_NUM * fixSeekUniform;
                break;
            case FOURTH_UNIFORM_SEEK_NUM:
                fixSeekUniform = FOURTH_UNIFORM_SEEK_NUM * fixSeekUniform;
                break;
            case FIFTH_UNIFORM_SEEK_NUM:
                fixSeekUniform = FIFTH_UNIFORM_SEEK_NUM * fixSeekUniform;
                break;
            default:
                fixSeekUniform = SECOND_UNIFORM_SEEK_NUM * fixSeekUniform;

        }

        return fixSeekUniform;
    }

    /**
     * @description 加速度为一个自定义的初始默认值，并且只有长按才会触发加速度
     * @date: 2020/8/9 10:22
     * @author: Mr.xw
     */
    public int getSeekBarTime() {

        if (isLongPress()) {
            if (isLeftKeyEvent()) {
                return getSeekBarProgress() - getPlayerUniform() * getSeekBarAcceleration();
            } else {
                return getSeekBarProgress() + getPlayerUniform() * getSeekBarAcceleration();
            }
        } else {
            if (isLeftKeyEvent()) {
                return getSeekBarProgress() - accelerationNum;
            } else {
                return getSeekBarProgress() + accelerationNum;
            }
        }

    }

    /**
     * @Method: initView
     * @Time: 2020/8/6 15:45
     * @Description: 初始化播放器相关View, initView快于init，获取player相关状态的时候需要在init处理
     * @Author: Mr.xw
     */
    @Override
    public void initView() {
        initLoadingView();
        initProgressSeekBarView();
    }

    /** ===============================SeekBarView begin=================================*/
    /**
     * ==========================*****************************************======================
     */
    private void initProgressSeekBarView() {
        mgtvProgressSeekBarView = new MgtvProgressSeekBarView(mContext);
        setMgtvProgressSeekBarView(mgtvProgressSeekBarView);
        addView(mgtvProgressSeekBarView);
        mgtvProgressSeekBarView.initMgtvProgressSeekBarView();
        if (mHandler != null) {
            if (lastDelayTime > 0) {
                mHandler.postDelayed(hideProgressBarViewRunner, lastDelayTime);
            } else {
                mHandler.postDelayed(hideProgressBarViewRunner, DELAY_INIT_PROGRESS_BAR);
            }
        }
        getPlayerUniform();
    }

    /**
     * @Method: setProgressBarBackgroundColor
     * @Time: 2020/8/6 11:18
     * @Description: 设置SeekBar颜色
     * @Author: Mr.xw
     */
    @Override
    public void setSeekBarBackgroundColor(int color) {
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().setSeekBarBackgroundColor(color);
        }
    }

    /**
     * @Method: hideProgressBarView
     * @Time: 2020/8/6 11:23
     * @Description:隐藏SeekBar
     * @Author: Mr.xw
     */
    @Override
    public void hideSeekBarView() {
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().hideSeekBarView();
        }
    }

    /**
     * @Method: showProgressBarView
     * @Time: 2020/8/6 11:23
     * @Description:显示SeekBar
     * @Author: Mr.xw
     */
    @Override
    public void showSeekBarAndTextView(boolean showFlag) {
        if (getMgtvProgressSeekBarView() != null) {
            if (showFlag) {
                getMgtvProgressSeekBarView().setSeekBarTimeTextIsOrNotVisible(View.VISIBLE);
            } else {
                getMgtvProgressSeekBarView().setSeekBarTimeTextIsOrNotVisible(View.INVISIBLE);
            }
        }
    }

    /** ===============================SeekBarView end=================================*/
    /**
     * ==========================*****************************************======================*/


    /** ===============================LoadingView相关控制 begin=================================*/
    /**
     * ==========================*****************************************======================*/

    /**
     * 初始化LoadingView
     */
    @Override
    public void initLoadingView() {
        mgtvLoadingView = new MgtvLoadingView(mContext);
        addView(mgtvLoadingView);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mgtvLoadingView.getLayoutParams();
        mgtvLoadingView.setLayoutParams(params);
    }

    /**
     * @Method:showLoadingView
     * @Time: 2020/8/6 9:39
     * @Description: 即刻显示LoadingView
     * @Author: Mr.xw
     */
    @Override
    public void showLoadingView() {
        if (mgtvLoadingView != null) {
            mgtvLoadingView.showLoadingView();
        }
    }

    /**
     * @Method:hideLoadingView
     * @Time: 2020/8/6 9:40
     * @Description:隐藏LoadingView
     * @Author: Mr.xw
     */
    @Override
    public void hideLoadingView() {
        if (mgtvLoadingView != null) {
            mgtvLoadingView.hideLoadingView();
        }
    }

    /**
     * @Method:delayShowLoading
     * @Time: 2020/8/6 9:41
     * @Description:延迟显示LaodingView
     * @Author: Mr.xw
     */
    @Override
    public void delayShowLoading(int delayTime) {
        if (mgtvLoadingView != null) {
            mgtvLoadingView.delayShowLoading(delayTime);
        }
    }

    /**
     * @Method:setLoadingBackgroundColor
     * @Time: 2020/8/6 9:49
     * @Description:设置loadingView的背景色
     * @Author: Mr.xw
     */
    @Override
    public void setLoadingBackgroundColor(int color) {
        if (mgtvLoadingView != null) {
            mgtvLoadingView.setLoadingBackgroundColor(color);
        }
    }

    /**
     * @Method:setTitle
     * @Time: 2020/8/6 9:49
     * @Description:设置loading标题
     * @Author: Mr.xw
     */
    @Override
    public void setTitle(String title) {
        if (mgtvLoadingView != null) {
            mgtvLoadingView.setTitle(title);
        }
    }
    /** ===============================LoadingView相关控制 end=================================*/
    /**
     * ==========================*****************************************======================
     */


    public boolean playerBackKeyDown() {
        setSeekAndToPlay(false);
        setHide(false);
        return true;
    }

    public boolean playerCenterKeyDown() {
        setHide(true);
        setNeedAddUnform(false);
        setSeekTimeTextVisibility(INVISIBLE);
        defualtNum = DEFUALTNUM;
        if (getMgtvVideoPlayer().isPlaying()) {
            setPause(true);
        }
        if (getMgtvProgressSeekBarView() != null) {
            setSeekAndToPlay(false);
            if (getMgtvVideoPlayer() != null) {
                setSeekBarMax(getDuration());
                if (getMgtvVideoPlayer().isPlaying()) {
                    getMgtvProgressSeekBarView().playerCenterKeyDown(true);
                    getMgtvVideoPlayer().pause();
                } else {
                    getMgtvProgressSeekBarView().playerCenterKeyDown(false);
                    getMgtvVideoPlayer().start();
                }
            }

        }
        return true;
    }

    private void switchShortAndLongKeyEvent() {
        if (!isLongPress()) {
            setSeekBarProgress(getSeekBarKeyEvent());
        }
    }

    /**
     * @method dispatchKeyEvent
     * @description View按键分发处理加速度等
     * @date: 2020/9/5 9:22
     * @author: Mr.xw
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mHandler != null) {
                mHandler.removeCallbacks(showProgressBarViewRunner);
                mHandler.removeCallbacks(hideProgressBarViewRunner);
            }
            if (event.getRepeatCount() == 0) {
                event.startTracking();
                setLongPress(false);
            } else {
                setLongPress(true);
            }
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                case KeyEvent.KEYCODE_MEDIA_REWIND:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    playerLeftKeyDown();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    playerRightKeyDown();
                    return true;
                case KeyEvent.KEYCODE_BACK:
                    playerBackKeyDown();
                    return true;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    playerCenterKeyDown();
                    return true;
                default:
                    showSeekBarAndTextView(false);
                    setSeekAndToPlay(false);
                    setNeedAddUnform(false);
                    setPause(false);
                    return super.dispatchKeyEvent(event);
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            setSeekTimeTextVisibility(INVISIBLE);
            if (!isHide()) {
                setPlayIconView(NONE_SEEK_BAR_ICON);
            }
            setNeedAddUnform(false);
            if (isSeekAndToPlay()) {
                seekAndToPlay();
            }
            /**加速度倍速复位*/
            accelerationTime = 1;
            defualtNum = 1;
            if (mHandler != null) {
                mHandler.removeCallbacks(showProgressBarViewRunner);
                mHandler.removeCallbacks(hideProgressBarViewRunner);
                if (lastDelayTime > 0) {
                    mHandler.postDelayed(hideProgressBarViewRunner, lastDelayTime);
                } else {
                    mHandler.postDelayed(hideProgressBarViewRunner, HIDE_PROGRESS_DELAY_TIME);
                }
            }
            setLongPress(false);
            setSeekAndToPlay(false);
        }

        return super.dispatchKeyEvent(event);
    }

    public void seekAndToPlay() {
        if (getMgtvVideoPlayer() != null) {
            getMgtvVideoPlayer().seekTo(getSeekBarProgress());
            getMgtvVideoPlayer().start();
        }
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().setShowIconView(false);
        }
    }

    public boolean playerLeftKeyDown() {
        setSeekAndToPlay(true);
        setNeedAddUnform(true);
        setPause(false);
        setLeftKeyEvent(true);
        setHide(false);
        showSeekBarAndTextView(true);
        setPlayIconView(LEF_SEEK_BAR_ICON);
        switchShortAndLongKeyEvent();
        if (getMgtvVideoPlayer() != null) {
            setSeekBarMax(getDuration());
        }
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().playerLeftKeyDown();
        }
        return true;
    }

    public boolean playerRightKeyDown() {
        setPause(false);
        setSeekAndToPlay(true);
        setNeedAddUnform(true);
        setLeftKeyEvent(false);
        setHide(false);
        showSeekBarAndTextView(true);
        setPlayIconView(RIGHT_SEEK_BAR_ICON);
        switchShortAndLongKeyEvent();
        setSeekBarMax(getDuration());
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().playerRightKeyDown();
        }
        return true;
    }


    public boolean playerUpKeyDown() {
        return true;
    }

    public boolean playerDownKeyDown() {
        return true;
    }

    public void setSeekBarMax(int max) {

        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().setSeekBarMax(max);
        }
    }

    public int getSeekBarProgress() {

        if (getMgtvProgressSeekBarView() != null) {
            return getMgtvProgressSeekBarView().getSeekBarProgress();
        }
        return 0;

    }

    public void setSeekBarProgress(int progress) {
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().setSeekBarProgress(progress);
        }
    }

    public void setSeekTimeTextVisibility(int visibility) {
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().setSeekTimeTextVisibility(visibility);
        }
    }

    public void setSeekTimeText(String text) {
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().setSeekTimeText(text);
        }
    }


    public void setPlayIconView(int direction) {
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().setPlayIconView(direction);
        }
    }

    public void hidePlayIcon() {
        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().hidePlayIcon();
        }
    }


    /**
     * @Method:natureShowSeekBar
     * @Time: 2020/8/26 16:45
     * @Description: 提供给业务层自然显示进度条并且根据传入的时间隐藏进度条
     * @Author: Mr.xw
     */
    @Override
    public void natureShowSeekBar(int delayTime) {

        lastDelayTime = delayTime;

        if (getMgtvProgressSeekBarView() != null) {
            getMgtvProgressSeekBarView().showSeekBarAndTextView();
            /**seektime时间不显示*/
            getMgtvProgressSeekBarView().setSeekTimeTextVisibility(INVISIBLE);
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(showProgressBarViewRunner);
            mHandler.removeCallbacks(hideProgressBarViewRunner);
            mHandler.postDelayed(hideProgressBarViewRunner, delayTime);
        }

    }

    public void release() {
        setFirstInit(false);
        if (mgtvProgressSeekBarView != null) {
            mgtvProgressSeekBarView.release();
            mgtvProgressSeekBarView = null;
        }
        if (mgtvLoadingView != null) {
            mgtvLoadingView.setVisibility(GONE);
            mgtvLoadingView = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    public int getCurrentPosition() {
        if (getMgtvVideoPlayer() != null) {
            return getMgtvVideoPlayer().getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (getMgtvVideoPlayer() != null) {
            return getMgtvVideoPlayer().getDuration();
        }
        return 0;
    }

    public boolean isLeftKeyEvent() {
        return isLeftKeyEvent;
    }

    public void setLeftKeyEvent(boolean leftKeyEvent) {
        isLeftKeyEvent = leftKeyEvent;
    }

    public boolean isSeekAndToPlay() {
        return isSeekAndToPlay;
    }

    public void setSeekAndToPlay(boolean seekAndToPlay) {
        isSeekAndToPlay = seekAndToPlay;
    }

    public boolean isNeedAddUnform() {
        return needAddUnform;
    }

    public void setNeedAddUnform(boolean needAddUnform) {
        this.needAddUnform = needAddUnform;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isLongPress() {
        return isLongPress;
    }

    public void setLongPress(boolean longPress) {
        isLongPress = longPress;
    }

    private boolean isHide() {
        return isHide;
    }

    private void setHide(boolean notHide) {
        isHide = notHide;
    }


    public IMgtvVideoPlayer getMgtvVideoPlayer() {
        return sMgtvVideoPlayer;
    }

    public void setMgtvVideoPlayer(IMgtvVideoPlayer mgtvVideoPlayer) {
        this.sMgtvVideoPlayer = mgtvVideoPlayer;
    }

    public MgtvProgressSeekBarView getMgtvProgressSeekBarView() {
        return mgtvProgressSeekBarView;
    }

    public void setMgtvProgressSeekBarView(MgtvProgressSeekBarView mgtvProgressSeekBarView) {
        this.mgtvProgressSeekBarView = mgtvProgressSeekBarView;
    }

    private boolean isFirstInit() {
        return isFirstInit;
    }

    private void setFirstInit(boolean firstInit) {
        isFirstInit = firstInit;
    }
}

