package com.example.dx.utilproject.handlers;

import android.app.Activity;
import android.os.BaseBundle;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * 自定义Handler
 * Created by admin on 2017/10/30.
 */

public class MyHandler<T extends Activity> extends Handler{
    private static final String TAG = "MyHandler";
    public static final String METHOD="method";
    private SoftReference<T> activity;
    public MyHandler(T activity){
        this.activity=new SoftReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        if (activity==null){
            Log.w(TAG,"activity is null");
            return;
        }
        Bundle bundle=msg.getData();
        if (bundle==null){
            Log.w(TAG,"bundle is null");
            return;
        }
        String methodName=bundle.getString(METHOD,"");
        if (methodName!=null&&!methodName.trim().equals("")) {
            try {
                // TODO: 2017/10/30 如果调用的方法需要传入数据怎么办?
                Method method=activity.get().getClass().getMethod(methodName);
                method.invoke(activity.get());
            } catch (Exception e) {
                Log.w(TAG, "handleMessage: ", e);
            }
        }
    }
}
