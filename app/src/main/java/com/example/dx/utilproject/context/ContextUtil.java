package com.example.dx.utilproject.context;

import android.app.Application;
import android.content.Context;

/**
 * 默认使用的反射的方式获取的Context，最好在Application的onCreate方法中调用一次init(Context context)方法
 * Created by admin on 2017/11/7.
 */

public class ContextUtil {
    private static Context mContext;
    private static Object syncObject=new Object();

    /**
     * 该方法最好在Application的onCreate方法中调用一次
     * @param context
     */
    public static void init(Context context){
        mContext=context.getApplicationContext();
    }
    public static Context getContext(){
        if (mContext == null) {
            synchronized (syncObject) {
                if (mContext == null) {
                    mContext = getContextByReflect();
                }
            }
        }
        return mContext;
    }
    private static Context getContextByReflect(){
        Application application=null;
        try {
            application = (Application) Class.forName("android.app.ActivityThread").getMethod("currentApplication").invoke(null, (Object[]) null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if (application==null){
            try {
                application = (Application) Class.forName("android.app.AppGlobals").getMethod("getInitialApplication").invoke(null, (Object[]) null);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (application==null){
            throw new RuntimeException("无法使用反射的方式获取Context,请尝试使用getInstance(Context context)方法!");
        }
        return application.getApplicationContext();
    }
}
