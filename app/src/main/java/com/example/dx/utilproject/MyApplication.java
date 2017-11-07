package com.example.dx.utilproject;

import android.app.Application;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.dx.utilproject.log.FileLogUtil;
import com.example.dx.utilproject.log.LogUtil;

/**
 * Created by admin on 2017/11/7.
 */

public class MyApplication extends Application{
    private static final String TAG = "MyApplication";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
//        FileLogUtil.init(this);
        LogUtil.wtf(TAG,"MyApplication onCreate invoke");
    }
}
