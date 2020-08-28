package com.xw.player.core;

/**
*@Time: 2020/8/11 8:59
*@Description: 扩展能力，提供部分业务相关底层能力
*@Author: Mr.xw
*/
public class TargetTimeBean {

    public static final int MATCH_TYPE_TIME_BACK = 1;//通知类型：到目标时间之前
    public static final int MATCH_TYPE_TIME_TO = 2;//通知类型：到达目标时间
    public static final int MATCH_TYPE_TIME_OVER = 3;//通知类型：超过目标时间

    public static final int TYPE_TARGET = 1;//在某时间点自然到达通知
    public static final int TYPE_OUT = 2;//在某时间段之外通知
    public static final int TYPE_DURING = 3;//在某时间段之间通知
    public static final int TYPE_OVER_TARGET = 4;//在某时间点到达通知(开始时已超过时间点也通知)
    public static final int TYPE_TARGET_TO_TIME = 5;//自然播放到达某时间点通知到达；

    public static final int TIME_STATE_PRE = 1;//目标时间点之前
    public static final int TIME_STATE_DURING = 2;//目标时间段之间
    public static final int TIME_STATE_AFTER = 3;//目标时间段之后

    private int tag;//标识
    private int notifyType;//时间点类型
    private int targetTime;//播放目标时间点，ms
    private int startTime;//播放目标时间段起始点，ms
    private int endTime;//播放目标时间段终止点，ms
    private int lastTimeState;//之前的时间状态
    private int lastTimePosition;//之前的播放时间点
    private int matchType = MATCH_TYPE_TIME_TO;//通知类型

    public boolean isTargetPre(int position) {
        return position < targetTime;
    }

    public boolean isDuring(int position) {
        return position >= startTime && position <= endTime;
    }

    public boolean isLastPre() {
        return lastTimeState == TIME_STATE_PRE;
    }

    public boolean isLastAfter() {
        return lastTimeState == TIME_STATE_AFTER;
    }

    public boolean isLastDuring() {
        return lastTimeState == TIME_STATE_DURING;
    }


    public int getLastTimeState() {
        return lastTimeState;
    }

    public int getLastTimePosition() {
        return lastTimePosition;
    }

    public void setLastTimeState(int position) {
        lastTimePosition = position;
        switch (notifyType) {
            case TYPE_DURING:
            case TYPE_OUT:
                if (position < startTime) {
                    lastTimeState = TIME_STATE_PRE;
                } else if (position > endTime) {
                    lastTimeState = TIME_STATE_AFTER;
                } else {
                    lastTimeState = TIME_STATE_DURING;
                }
                break;
            case TYPE_OVER_TARGET:
            case TYPE_TARGET:
            case TYPE_TARGET_TO_TIME:
            default:
                if (position < targetTime) {
                    lastTimeState = TIME_STATE_PRE;
                } else {
                    lastTimeState = TIME_STATE_AFTER;
                }
                break;
        }
    }

    public int getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(int targetTime) {
        this.targetTime = targetTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getMatchType() {
        return matchType;
    }

    public void setMatchType(int matchType) {
        this.matchType = matchType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TargetTimeBean) {
            TargetTimeBean bean = (TargetTimeBean) obj;
            if (tag != bean.tag) {
                return false;
            }
            switch (notifyType) {
                case TYPE_DURING:
                case TYPE_OUT:
                    return bean.startTime == startTime
                            && bean.endTime == endTime;
                case TYPE_TARGET:
                case TYPE_OVER_TARGET:
                case TYPE_TARGET_TO_TIME:
                default:
                    return bean.targetTime == targetTime;
            }
        }
        return false;

    }

    @Override
    public String toString() {
        return "TargetTimeBean{" +
                "tag=" + tag +
                ", notifyType=" + notifyType +
                ", targetTime=" + targetTime +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", lastTimeState=" + lastTimeState +
                ", lastTimePosition=" + lastTimePosition +
                ", matchType=" + matchType +
                '}';
    }
}
