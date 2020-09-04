package com.xw.player.framework.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xw.helper.utils.MLog;
import com.xw.player.R;

import static com.xw.player.framework.constants.PlayerConstants.NULL_TEXT;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.LEF_SEEK_BAR_ICON;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.MOVE_FLOAT;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.NONE_SEEK_BAR_ICON;
import static com.xw.player.framework.constants.PlayerConstants.SeekBarConstants.RIGHT_SEEK_BAR_ICON;
import static com.xw.player.framework.utils.PlayUtils.getDrawable;


/**
 * @Description 自定义播放器ProgressSeekBar
 * @date: 2020/08/07/13:56
 * @author: Mr.xw
 */
public class MgtvProgressSeekBarView extends FrameLayout implements IMgtvSeekBarView {

    private MgtvSeekBarView mgtvSeekBarView;
    private ViewGroup viewGroup;
    private ImageView playIconView;
    private TextView timeText;
    private TextView totalTimeText;
    private TextView seekTimeText;
    private Context mContext;
    private boolean showIconView;
    private IViewListener iViewListener;

    public MgtvProgressSeekBarView(Context context) {
        this(context, null);
    }

    public MgtvProgressSeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MgtvProgressSeekBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
    }

    public void initMgtvProgressSeekBarView() {
        viewGroup = (ViewGroup) LayoutInflater.from(getContext()).inflate
                (R.layout.sdk_player_framework_progress_bar_view, this, false);
        addView(viewGroup);
        initSeekBar();
        initPlayIconView();
        initTimeText();
    }

    public boolean playerLeftKeyDown() {
        playIconView.setImageResource(R.drawable.sdk_player_framework_play_status_backward);
        return true;
    }

    public boolean isShowIconView() {
        return showIconView;
    }

    public void setShowIconView(boolean isShow) {
        this.showIconView = isShow;
    }

    public boolean playerCenterKeyDown(boolean isShow) {
        setShowIconView(isShow);
        if (isShow) {
            playIconView.setImageResource(R.drawable.sdk_player_framework_play_status_pause);
        } else {
            playIconView.setImageResource(R.color.sdk_player_framework_transparent);
        }
        return true;
    }

    public boolean playerRightKeyDown() {
        playIconView.setImageResource(R.drawable.sdk_player_framework_status_forward);
        return true;
    }


    public void setSeekBarMax(int max) {
        mgtvSeekBarView.setMax(max);
    }


    public boolean playerUpKeyDown() {

        return true;
    }

    public boolean playerDownKeyDown() {

        return true;
    }

    /**
     * @method setSeekBarChangeListener
     * @description SeekBarChangeListener事件
     * @date: 2020/8/8 19:39
     * @author: Mr.xw
     */
    public void setSeekBarChangeListener() {

        mgtvSeekBarView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * @Time: 2020/8/7 15:03
     * @Description: MgtvProgressSeekBarView里面包含了 mgtvSeekBarView跟快进、快退等图标
     * @Author: Mr.xw
     */
    void initSeekBar() {
        mgtvSeekBarView = findViewById(R.id.sdk_player_framework_seek_bar);
        setThumb();
        setSeekBarDrawable(getDrawable(getContext(), R.drawable.sdk_player_framework_seek_bar_style));
        setSeekBarBackgroundColor(R.color.sdk_player_framework_progress_focus_bg_color);
//        showSeekBarView();
        setOneKeyPos(20);
        setSeekBarChangeListener();
        mgtvSeekBarView.setVisibility(INVISIBLE);
    }

    void initPlayIconView() {
        playIconView = findViewById(R.id.sdk_player_framework_play_state);
        playIconView.setImageResource(R.color.sdk_player_framework_transparent);
    }

    void initTimeText() {
        timeText = findViewById(R.id.sdk_player_framework_current_play_time);
        totalTimeText = findViewById(R.id.sdk_player_framework_total_play_time);
        seekTimeText = findViewById(R.id.sdk_player_framework_seek_play_time);
        timeText.setVisibility(INVISIBLE);
        totalTimeText.setVisibility(INVISIBLE);
        seekTimeText.setVisibility(INVISIBLE);
    }

    public void setTimeText(String text) {
        if (timeText != null) {
            timeText.setText(text);
        }
    }

    public void setSeekTimeText(String text) {
        if (seekTimeText != null) {
            seekTimeText.setText(text);
        }
    }

    public void setTotalTimeText(String text) {
        if (totalTimeText != null) {
            totalTimeText.setText(text);
        }
    }


    @Override
    public void setSeekBarBackgroundColor(int color) {
        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.setSeekBarBackgroundColor(color);
        }
    }

    @Override
    public void hideSeekBarView() {
        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.hideSeekBarView();
        }
    }


    @Override
    public void showSeekBarView() {
        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.showSeekBarView();
        }
    }

    @Override
    public void setThumb() {
        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.setThumb();
        }
    }

    @Override
    public void setSeekBarDrawable(Drawable drawable) {
        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.setSeekBarDrawable(drawable);
        }
    }

    public int getSeekBarProgress() {
        if (mgtvSeekBarView != null) {
            return mgtvSeekBarView.getProgress();
        }
        return 0;
    }

    public void setSeekBarProgress(int progress) {
        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.setProgress(progress);
            int movePos = (int) (progress * MOVE_FLOAT / mgtvSeekBarView.getMax() * mgtvSeekBarView.getWidth());
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) seekTimeText.getLayoutParams();
            params.leftMargin = movePos;
            seekTimeText.setLayoutParams(params);
        }
    }


    public int getSeekBarMax() {
        if (mgtvSeekBarView != null) {
            return mgtvSeekBarView.getMax();
        }
        return 0;
    }

    public void setSeekBarTimeText(CharSequence text) {
        timeText.setText(text);
    }

    public void setSeekBarTotalTimeText(CharSequence text) {
        totalTimeText.setText(text);
    }

    public void setSeekBarTimeTextIsOrNotVisible(int visibility) {

        if (iViewListener != null) {
            if (visibility == VISIBLE) {
                iViewListener.showSeekBarAndTextView();
            }
            if (visibility == INVISIBLE) {
                iViewListener.hideSeekBarAndTextView();
            }
        }

        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.setVisibility(visibility);
        }

        if (timeText != null) {
            timeText.setVisibility(visibility);
        }
        if (totalTimeText != null) {
            totalTimeText.setVisibility(visibility);
        }

        if (seekTimeText != null) {
            seekTimeText.setVisibility(visibility);
        }
    }

    public void setSeekTimeTextVisibility(int visibility) {

        if (iViewListener != null) {
            if (visibility == VISIBLE) {
                iViewListener.showSeekTimeTextView();
            }
            if (visibility == INVISIBLE) {
                iViewListener.hideSeekTimeTextView();
            }
        }

        if (seekTimeText != null) {
            seekTimeText.setVisibility(visibility);
        }
    }

    public void hideSeekBarAndTextView() {
        setSeekBarTimeTextIsOrNotVisible(INVISIBLE);
    }

    public void showSeekBarAndTextView() {
        setSeekBarTimeTextIsOrNotVisible(VISIBLE);
    }

    public int getSeekBarWidth() {
        if (mgtvSeekBarView != null) {
            return mgtvSeekBarView.getWidth();
        }
        return 0;
    }

    public void setPlayIconView(int direction) {

        if (playIconView == null) {
            return;
        }
        if (direction == LEF_SEEK_BAR_ICON) {
            playIconView.setImageResource(R.drawable.sdk_player_framework_play_status_backward);
        } else if (direction == RIGHT_SEEK_BAR_ICON) {
            playIconView.setImageResource(R.drawable.sdk_player_framework_status_forward);
        } else if (direction == NONE_SEEK_BAR_ICON) {
            playIconView.setImageResource(R.color.sdk_player_framework_transparent);
        }
    }

    public void hidePlayIcon() {
        if (playIconView != null) {
            playIconView.setImageResource(R.color.sdk_player_framework_transparent);
        }
    }

    public void setOneKeyPos(int pos) {
        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.setOneKeyPos(pos);
        }
    }

    public IViewListener getViewListener() {
        return iViewListener;
    }

    public void setViewListener(IViewListener iViewListener) {
        this.iViewListener = iViewListener;
    }

    public ImageView getPlayIconView() {
        return playIconView;
    }

    public void setPlayIconView(ImageView playIconView) {
        this.playIconView = playIconView;
    }

    public TextView getTimeText() {
        return timeText;
    }

    public void setTimeText(TextView timeText) {
        this.timeText = timeText;
    }

    public TextView getTotalTimeText() {
        return totalTimeText;
    }

    public void setTotalTimeText(TextView totalTimeText) {
        this.totalTimeText = totalTimeText;
    }

    public TextView getSeekTimeText() {
        return seekTimeText;
    }

    public void setSeekTimeText(TextView seekTimeText) {
        this.seekTimeText = seekTimeText;
    }

    public MgtvSeekBarView getMgtvSeekBarView() {
        return mgtvSeekBarView;
    }


    public void release() {
        if (mgtvSeekBarView != null) {
            mgtvSeekBarView.setProgress(0);
            mgtvSeekBarView.setVisibility(GONE);
            mgtvSeekBarView = null;
        }
        if (timeText != null) {
            timeText.setText(NULL_TEXT);
            timeText = null;
        }
        if (totalTimeText != null) {
            totalTimeText.setText(NULL_TEXT);
            totalTimeText = null;
        }
        if (seekTimeText != null) {
            seekTimeText.setText(NULL_TEXT);
            seekTimeText = null;
        }
    }

}
