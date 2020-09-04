package com.xw.player.framework.constants;

/**
 * @Description 常量池
 * @date: 2020/08/03/11:59
 * @author: Mr.xw
 */
public class PlayerConstants {

    public static final int HIDE_PROGRESS_DELAY_TIME = 3000;
    public static final String NULL_TEXT = "";
    /**
     * @Time: 2020/8/3 11:03
     * @Description: 进度条View相关常量
     * @Author: Mr.xw
     */
    public static class ProgressConstants {

    }

    /**
     * @description LoadingView相关常量
     * @date: 2020/8/5 22:19
     * @author: Mr.xw
     */
    public static class LoadingConstants {
    }

    /**
     * @Time: 2020/8/6 10:06
     * @Description:SeekBarView 相关常量
     * @Author: Mr.xw
     */
    public static class SeekBarConstants {

        public static final int LEF_SEEK_BAR_ICON = 1;
        public static final int RIGHT_SEEK_BAR_ICON = 2;
        public static final int NONE_SEEK_BAR_ICON = 3;
        public static final int UPDATE_TIME_TEXT = 1;
        public static final int SEEK_LEFT_LIMIT = 0;
        /**
         * 单次seek固定时长，默认+1s
         */
        public static final int ACCELERATION_NUM = 8000;
        public static final int SEEKBAR_DELAY = 2000;
        public static final int DEFUALTNUM = 1;
        /**
         * 自定义第一次seek加速初始值A1 30
         */
        public static final int FIRST_SEEK_UNIFORM = 30;
        /**
         * 自定义经验值，从片头到片尾最大加速执行次数260
         */
        public static final int MAX_SEEK_UNIFORM = 260;
        public static final int NUM_UNIFORM = 2;
        public static final int SEEK_THOUSAND = 1000;

        public static final int DELAY_INIT_PROGRESS_BAR = 3000;

        /**
         * @Time: 2020/8/10 16:12
         * 1、影片长大于一次seek时长 暂定15s;15s<=totalTime<=5min fixSeekUniformFirst
         * 2、5min<totalTime<=15min fixSeekUniformSecond
         * 3、15min<totalTime<=45min fixSeekUniformThird
         * 4、45min<totalTime<=90min fixSeekUniformFourth
         * 5、90min<totalTime<=180min fixSeekUniformFifth
         * @Author: Mr.xw
         */
        public static final int FIRST_UNIFORM_SEEK_NUM = 1;
        public static final int SECOND_UNIFORM_SEEK_NUM = 2;
        public static final int THIRD_UNIFORM_SEEK_NUM = 4;
        public static final int FOURTH_UNIFORM_SEEK_NUM = 8;
        public static final int FIFTH_UNIFORM_SEEK_NUM = 16;

        public static final int TOTAL_TIME_ZERO = 300;
        public static final int TOTAL_TIME_FIRST = 900;
        public static final int TOTAL_TIME_SECOND = 2700;
        public static final int TOTAL_TIME_THIRD = 5400;
        public static final int TOTAL_TIME_FOURTH = 10800;
        public static final float MOVE_FLOAT = 1.0f;
    }
}
