package com.xw.player.framework.ui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xw.player.R;
import com.xw.player.core.StringUtils;

/**
 * @Description 加载View
 * @date: 2020/08/04/16:52
 * @author: Mr.xw
 */
public class MgtvLoadingView extends LinearLayout {

    private ViewGroup loadingGroup;
    private TextView mTextView;
    private Handler mHandler = new Handler();
    ProgressBar mProgressBar;

    public MgtvLoadingView(Context context) {
        super(context);
        init();
    }

    public MgtvLoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    public MgtvLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    void init() {
        loadingGroup = (ViewGroup) LayoutInflater.from(getContext()).inflate
                (R.layout.sdk_player_framework_loading_view, this, false);
        addView(loadingGroup);
        mTextView = findViewById(R.id.loading_text);
        mProgressBar = findViewById(R.id.loading_icon);
        setVisibility(GONE);
    }

    /**
     * 设置loadingView的背景色
     *
     * @param color
     */
    public void setLoadingBackgroundColor(int color) {
        loadingGroup.setBackgroundColor(color);
    }

    /**
     * 隐藏loading
     */
    public void hideLoadingView() {
        this.setVisibility(GONE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 延时显示loading（默认延时1s显示）
     */
    public void delayShowLoading(int delayTime) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(VISIBLE);
            }
        }, delayTime);
    }

    /**
     * 显示loading
     */
    public void showLoadingView() {
        Log.d("Step1==", "showLoadingView====");
        setVisibility(VISIBLE);
    }

    /**
     * 设置loading标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (mTextView == null || StringUtils.equalsNull(title)) {
            return;
        }
        mTextView.setText(title);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == INVISIBLE || visibility == GONE) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

}
