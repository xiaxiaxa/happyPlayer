package com.xw.player.framework.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;


/**
 * @Description SeekBar UI
 * @date: 2020/08/06/10:19
 * @author: Mr.xw
 * android:max用于设置进度条的最大值
 * android:progress用于指定进度条已完成的进度值
 * android:progressDrawable用于设置进度条轨道的绘制形式
 * android:max 用于设置水平进度条的最大进度值
 * android:style 用于为ProgressBar指定风格
 * <p>
 * android:style属性:
 * ?android:attr/progressBarStyleHorizontal 细水平长条进度条
 * ?android:attr/progressBarStyleLarge 大圆形进度条
 * ?android:attr/progressBarStyleSmall 小圆形进度条
 * @android:style/Widget.ProgressBar.Large 大跳跃、旋转画面的进度条
 * @android:style/Widget.ProgressBar.Small 小跳跃、旋转画面的进度条
 * @android:style/Widget.ProgressBar.Horizontal 粗水平长条进度条
 */
@SuppressLint("AppCompatCustomView")
public class MgtvSeekBarView extends SeekBar implements IMgtvSeekBarView {

    public MgtvSeekBarView(Context context) {
        super(context);
    }

    public MgtvSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MgtvSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        refreshDrawablesBounds();
        super.onDraw(canvas);
    }

    /**
     * @Method:refreshDrawablesBounds
     * @Time: 2020/8/6 10:29
     * @Description: 根据进度值, 调整各种Drawable的Bounds
     * @Author: Mr.xw
     */
    void refreshDrawablesBounds() {
        int w = getWidth();
        int h = getHeight();
        float scaleProgress = (float) getProgress() / getMax();
        int wProgress = (int) (scaleProgress * w);
        float scaleSecProgress = (float) getSecondaryProgress() / getMax();
        int wSecProgress = (int) (scaleSecProgress * w);
        Drawable drawable = getProgressDrawable();
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layer = (LayerDrawable) drawable;
            final int N = layer.getNumberOfLayers();
            for (int i = 0; i < N; i++) {
                Drawable d = layer.getDrawable(i);
                Rect rect = d.getBounds();
                /** 适配Drawable的left、right*/
                if (layer.getId(i) == android.R.id.progress) {
                    rect.right = wProgress - getPaddingRight();
                } else if (layer.getId(i) == android.R.id.secondaryProgress) {
                    rect.right = wSecProgress - getPaddingRight();
                } else if (layer.getId(i) == android.R.id.background) {
                    rect.right = w - getPaddingRight();
                }
                rect.left = -getPaddingLeft();
                d.setBounds(rect);
            }
        } else {
            drawable.setBounds(0, 0, w, h);
        }
    }

    @Override
    public synchronized void setMax(int max) {
        super.setMax(max);
        if (max <= 0) {
            setVisibility(View.GONE);
            return;
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }



    @Override
    public void setSeekBarBackgroundColor(int color) {
        setBackgroundColor(color);
    }

    @Override
    public void hideSeekBarView() {
        setVisibility(GONE);
    }

    @Override
    public void showSeekBarView() {
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void setThumb() {
        this.setThumb(null);
        this.setDuplicateParentStateEnabled(true);
    }

    @Override
    public void setSeekBarDrawable(Drawable drawable) {
        this.setProgressDrawable(drawable);
    }

    public void setOneKeyPos(int pos) {
        setKeyProgressIncrement(pos);
    }

}
