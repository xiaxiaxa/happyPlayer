package com.xw.player.framework.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;


import androidx.annotation.DrawableRes;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @Description 工具类
 * @date: 2020/08/03/14:20
 * @author: Mr.xw
 */
public class PlayUtils {

    static final String DATE_TIME = "HH:mm:ss";
    static final String ZONE_TIME = "GMT+00:00";

    public static Drawable getDrawable(Context context, @DrawableRes int drawableId) {
        if (context == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(drawableId);
        } else {
            return context.getResources().getDrawable(drawableId);
        }
    }

    public static String msConvertToHms(long ms) {//毫秒转换为时分秒
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME);
        formatter.setTimeZone(TimeZone.getTimeZone(ZONE_TIME));
        return formatter.format(ms);
    }



}
