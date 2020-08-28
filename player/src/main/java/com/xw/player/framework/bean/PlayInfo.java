package com.xw.player.framework.bean;

/**
*@Time: 2020/8/10 11:03
*@Description: 提供给业务层使用的
*@Author: Mr.xw
*/
public class PlayInfo {
    private String url;//播放的url
    private int beginDot = -1;//片头打点的位置，单位s.
    private int continueSeconds = -1;//续播位置,单位s.
    private int mCurrentTime;
    private int totalTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBeginDot() {
        return beginDot;
    }

    public void setBeginDot(int beginDot) {
        this.beginDot = beginDot;
    }

    public int getContinueSeconds() {
        return continueSeconds;
    }

    public void setContinueSeconds(int continueSeconds) {
        this.continueSeconds = continueSeconds;
    }


    public int getmCurrentTime() {
        return mCurrentTime;
    }

    public void setmCurrentTime(int mCurrentTime) {
        this.mCurrentTime = mCurrentTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "PlayInfo{" +
                "url='" + url + '\'' +
                ", beginDot=" + beginDot +
                ", continueSeconds=" + continueSeconds +
                '}';
    }
}
