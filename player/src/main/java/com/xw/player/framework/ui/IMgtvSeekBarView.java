package com.xw.player.framework.ui;

import android.graphics.drawable.Drawable;

/**
 * @Description 原生接口
 * @date: 2020/08/07/14:12
 * @author: Mr.xw
 */
interface IMgtvSeekBarView {

    /**
     * @Description: 设置seekBar颜色
     */
    void setSeekBarBackgroundColor(int color);

    /**
     * @Description: 隐藏seekBar
     */
    void hideSeekBarView();

    /**
     * @Description: 显示SeekBar
     */
    void showSeekBarView();

    /**
     * @Description: 去掉原生的圆标焦点
     */
    void setThumb();

    /**
     * @Description: 设置进度条样式图
     */
    void setSeekBarDrawable(Drawable drawable);

    /**
     * @Description: 设置进度条是否可见
     */
    void setVisibility(int visibility);
}
