package com.xw.player.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.core.content.SharedPreferencesCompat;

import com.xw.helper.utils.MLog;
import com.xw.helper.utils.MyApplication;

import java.lang.reflect.Method;



/**
 * @Time: 2020/8/3 10:18
 * @Description: Lib-CorePlayer 工具类
 * @Author: Mr.xw
 */
public class PlayerUtil {

    private static final String DEFAULT_SP_NAME = "player";
    private static final String SP_DIRECTORY = "com_xw_helper_player_sharedPreference";
    private static final String METHOD_GET_PLAY_PARAMS = "getPlaybackParams";
    private static final boolean isLive = true;
    private static String playMode = "";
    private static boolean sNeedSplitDistinguish;
    public static final int VALUE_NULL = -1;
    private static String sTotalTimeout;
    private static final String KEY_COREPLAYER_TOTALTIMEOUT = "coreplayer_TotalTimeout";

    /**
     * 判断api是否支持倍速播
     *
     * @return
     */
    public static boolean hasSpeedMethod() {
        Method getPlaybackParams = null;
        try {
            getPlaybackParams = MediaPlayer.class.getDeclaredMethod(METHOD_GET_PLAY_PARAMS);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return getPlaybackParams != null;
    }

    /**
     * 初始化相关配置参数
     *
     * @param needSplitDistinguish
     */
    public static void init(boolean needSplitDistinguish) {
        sNeedSplitDistinguish = needSplitDistinguish;
    }

    /**
     * @method distinguishModel
     * @description 使能直播拼串
     * @author: Mr.xw
     */
    public static boolean distinguishModel() {
        return sNeedSplitDistinguish;
    }

    /**
     * @method getPlayMode
     * @description 获取正确的mode
     * @author: Mr.xw
     */
    @SuppressLint("WrongConstant")
    public static String getPlayMode() {
        final String WINDOW_MODE_720 = "&windowmode=3";
        final String WINDOW_MODE_1080 = "&windowmode=7";
        final String WINDOW_MODE_2160 = "&windowmode=9";
        final int SCREEN_HIGHT_720 = 720;
        final int SCREEN_HIGHT_1080 = 1080;
        final int SCREEN_HIGHT_2160 = 2160;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Context context = MyApplication.getInstance().getApplicationContext();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay()
                .getMetrics(displayMetrics);
        int mScreenHight = displayMetrics.heightPixels;
        if (distinguishModel()) {
            switch (mScreenHight) {
                case SCREEN_HIGHT_720:
                    playMode = WINDOW_MODE_720;
                    break;

                case SCREEN_HIGHT_2160:
                    playMode = WINDOW_MODE_2160;
                    break;

                case SCREEN_HIGHT_1080:
                default:
                    playMode = WINDOW_MODE_1080;
                    break;
            }
            return playMode;
        } else {
            //默认返回1080
            return WINDOW_MODE_1080;
        }
    }

    /**
     * @method getVideoSize
     * @description 获取小屏窗口视频播放的x, y, w, h等
     * @author: Mr.xw
     */
    public static String getPlaySize(SurfaceView surfaceView, int x, int y) {
        String concatenateUrl = "";

        if (distinguishModel()) {
            concatenateUrl = "&window_x=" + x
                    + "&window_y=" + y
                    + "&window_w=" + surfaceView.getWidth()
                    + "&window_h=" + surfaceView.getHeight()
                    + getPlayMode();
        }
        return concatenateUrl;
    }

    /**
     * 获取总超时时间,>0有效
     */
    public static int getTotalTimeout() {
        return getIntValue(sTotalTimeout, KEY_COREPLAYER_TOTALTIMEOUT);
    }

    private static int getIntValue(String value, String key) {
        if (value == null) {
            value = getString(null, key, String.valueOf(VALUE_NULL));
        }
        MLog.i( "--->getIntValue value:" + value + ",key:" + key);
        return parseInt(value, VALUE_NULL);
    }

    public static String getString(String fileName, String key,
                                   String defaultValue) {
        if (StringUtils.equalsNull(fileName)) {
            fileName = DEFAULT_SP_NAME;
        }
        return MyApplication.getInstance().getApplicationContext()
                .getSharedPreferences(getFilePath(fileName),
                        Context.MODE_PRIVATE)
                .getString(key, defaultValue);
    }

    private static void setStringValue(String key, String value) {
        MLog.i("--->setStringValue value:" + value + ",key:" + key);
        put(null, key, value);
    }
    private static String getFilePath(String fileName) {
        return SP_DIRECTORY + fileName;
    }

    public static int parseInt(String val, int defaultInt) {
        if (StringUtils.equalsNull(val)) {
            return defaultInt;
        } else {
            try {
                return Integer.parseInt(val);
            } catch (Exception var3) {
                return defaultInt;
            }
        }
    }

    /**
     * 设置总超时时间,>0有效
     */
    public static void setTotalTimeout(String totalTimeout) {
        if (StringUtils.equalsNull(totalTimeout)) {
            return;
        }
        sTotalTimeout = totalTimeout;
        setStringValue(KEY_COREPLAYER_TOTALTIMEOUT, totalTimeout);
    }

    /**
     * 存放基本数据类型到sharedPreference中
     * @param fileName
     *            内容存放的xml文件名
     * @param key
     *            存放内容的key值
     * @param value
     *            存放value值
     */
    public static void put(String fileName, String key, Object value) {
        if (StringUtils.equalsNull(fileName)) {
            fileName = DEFAULT_SP_NAME;
        }
        SharedPreferences sharedPreferences = MyApplication.getInstance()
                .getApplicationContext().getSharedPreferences(
                        getFilePath(fileName), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }
}
