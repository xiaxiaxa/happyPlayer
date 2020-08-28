package com.xw.happy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xw.happy.R;
import com.xw.happy.happyApplication;
import com.xw.helper.utils.MLog;
import com.xw.helper.utils.MyApplication;

public class MainActivity extends Activity implements View.OnClickListener{

    private Button mHlsButton1;
    private Button mHlsButton2;
    private Button mHlsButton3;
    private Button mHlsButton4;
    private Context mContext;
    /**
     * 1分多钟的短片*
     */
    private String videoUrlHls1 = "http://10.255.30.137:8082/EDS/RedirectPlay/000000000000/vod/75c2a75766da48a692c8383b5d79926a/56cc3627501c469cb9a66022c83ca984?UserToken=00228754829678360681080810093254&UserName=6830016";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        happyApplication.init((MyApplication) getApplication());
        mContext = happyApplication.getInstance().getApplicationContext();
        MLog.i("onCreate");
        mHlsButton1 = findViewById(R.id.mgtv_btn1);
        mHlsButton2 = findViewById(R.id.mgtv_btn2);
        mHlsButton3 = findViewById(R.id.mgtv_btn3);
        mHlsButton4 = findViewById(R.id.mgtv_btn4);
        mHlsButton1.setOnClickListener(this);
        mHlsButton2.setOnClickListener(this);
        mHlsButton3.setOnClickListener(this);
        mHlsButton4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
/*        if (v == mHlsButton1) {
            Intent intent = new Intent(mContext, MgtvPlayerActivity.class);
            intent.putExtra("url", videoUrlHls1);
            startActivity(intent);
        }
        if (v == mHlsButton2) {
            Intent intent = new Intent(mContext, MgtvPlayerActivity.class);
            intent.putExtra("url", videoUrlHls2);
            startActivity(intent);
        }
        if (v == mHlsButton3) {
            Intent intent = new Intent(mContext, MgtvPlayerActivity.class);
            intent.putExtra("url", videoUrlHls3);
            startActivity(intent);
        }*/
        if (v == mHlsButton4) {
            Intent intent = new Intent(mContext, MyPlayerActivity.class);
            intent.putExtra("url", videoUrlHls1);
            startActivity(intent);
        }
    }
}
