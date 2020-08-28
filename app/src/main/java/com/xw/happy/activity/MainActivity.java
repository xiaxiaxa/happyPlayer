package com.xw.happy.activity;

import android.app.Activity;
import android.os.Bundle;

import com.xw.happy.R;
import com.xw.happy.happyApplication;
import com.xw.helper.utils.MLog;
import com.xw.helper.utils.MyApplication;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApplication myApplication = new MyApplication();
        happyApplication.init((MyApplication) getApplication());
        MLog.i("onCreate");
    }

}
