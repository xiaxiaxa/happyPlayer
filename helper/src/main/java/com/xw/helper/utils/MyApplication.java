package com.xw.helper.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * @Time: 2020/8/27 10:36
 * @Description: 自定义Application
 * @Author: Mr.xw
 */
public class MyApplication extends Application {

    private static MyApplication myApplication;
    private static Context mContext = null;
    public static MyApplication getInstance() {
        return myApplication;
    }
    public MyApplication(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initActivityLifecycleCallbacks();

    }

    /**
     * @Time: 2020/8/27 10:42
     * @Description: 在application里监听所有activity生命周期的回调
     * @Author: Mr.xw
     */
    private void initActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityPreCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                MLog.i("onActivityPreCreated");
            }

            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                MLog.i("onActivityCreated");
            }

            @Override
            public void onActivityPostCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                MLog.i("onActivityPostCreated");
            }

            @Override
            public void onActivityPreStarted(@NonNull Activity activity) {
                MLog.i("onActivityPreStarted");
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                MLog.i("onActivityStarted");
            }

            @Override
            public void onActivityPostStarted(@NonNull Activity activity) {
                MLog.i("onActivityPostStarted");
            }

            @Override
            public void onActivityPreResumed(@NonNull Activity activity) {
                MLog.i("onActivityPreResumed");
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                MLog.i("onActivityResumed");
            }

            @Override
            public void onActivityPostResumed(@NonNull Activity activity) {
                MLog.i("onActivityPostResumed");
            }

            @Override
            public void onActivityPrePaused(@NonNull Activity activity) {
                MLog.i("onActivityPrePaused");
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                MLog.i("onActivityPaused");
            }

            @Override
            public void onActivityPostPaused(@NonNull Activity activity) {
                MLog.i("onActivityPostPaused");
            }

            @Override
            public void onActivityPreStopped(@NonNull Activity activity) {
                MLog.i("onActivityPreStopped");
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                MLog.i("onActivityStopped");
            }

            @Override
            public void onActivityPostStopped(@NonNull Activity activity) {
                MLog.i("onActivityPostStopped");
            }

            @Override
            public void onActivityPreSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                MLog.i("onActivityPreSaveInstanceState");
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                MLog.i("onActivitySaveInstanceState");
            }

            @Override
            public void onActivityPostSaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                MLog.i("onActivityPostSaveInstanceState");
            }

            @Override
            public void onActivityPreDestroyed(@NonNull Activity activity) {
                MLog.i("onActivityPreDestroyed");
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                MLog.i("onActivityDestroyed");
            }

            @Override
            public void onActivityPostDestroyed(@NonNull Activity activity) {
                MLog.i("onActivityPostDestroyed");
            }
        });
    }


    public static void init(MyApplication application) {
        if (application == null) {
            throw new NullPointerException("Can not use null initialized application context");
        }
        mContext = application.getApplicationContext();
        myApplication = application;
    }

}