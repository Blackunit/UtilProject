package com.example.dx.utilproject.log;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.RequiresApi;

import com.example.dx.utilproject.file.FileUtil;

/**
 * 将日志输出到文件中
 * 这里默认使用的反射的方式获取的Context，最好在Application的onCreate方法中调用一次init(Context context)方法
 * Created by admin on 2017/11/7.
 */

public class FileLogUtil {
    private static String PATH_NAME="";
    private static FileUtil FILE_UTIL;
    private static Object syncObject=new Object();

    private static final int VERBOSE=1;
    private static final int DEBUG=2;
    private static final int INFO=3;
    private static final int WARN=4;
    private static final int ERROR=5;
    private static final int WTF=6;

    public static final int NOTHING=7;
    /**通过调整LEVEL的值，控制日志的输出*/
    private static final int LEVEL =VERBOSE;
    /**控制抛出的异常日志是否需要输出*/
    private static final boolean PRINT_THROWABLE=true;
    /**控制日期的输出*/
    private static final boolean PRINT_TIME=true;

    /**
     *
     * @param context
     */
    public static void init(Context context){
        PATH_NAME=context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getPath()+"/log.txt";
        FILE_UTIL=FileUtil.newInstance(PATH_NAME);
    }

    private static boolean printFileLog(String data, boolean logTime){
        if (FILE_UTIL == null) {
            synchronized (syncObject) {
                if (FILE_UTIL == null) {
                    init(getContextByReflect());
                }
            }
        }
        if ("".equals(PATH_NAME.trim())){
            throw new RuntimeException("请自定义Application,并在Application的onCreate()方法体中调用FileLogUtil.init()方法！");
        }
        return FILE_UTIL.saveAppend(data,logTime);
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

    public static void v(String tag,String msg){
        LogUtil.v(tag,msg);
        if (LEVEL <=VERBOSE){
            printFileLog("V:"+tag+":"+msg,PRINT_TIME);
        }
    }
    public static void v(String tag,String msg,Throwable tr){
        LogUtil.v(tag,msg,tr);
        if (PRINT_THROWABLE){
            printFileLog("V:"+tag+":"+msg+":e="+tr.toString(),PRINT_TIME);
        }
    }
    public static void d(String tag,String msg){
        LogUtil.d(tag,msg);
        if (LEVEL <=DEBUG){
            printFileLog("D:"+tag+":"+msg,PRINT_TIME);
        }
    }
    public static void d(String tag,String msg,Throwable tr){
        LogUtil.d(tag,msg,tr);
        if (PRINT_THROWABLE){
            printFileLog("D:"+tag+":"+msg+":e="+tr.toString(),PRINT_TIME);
        }
    }
    public static void i(String tag,String msg){
        LogUtil.i(tag,msg);
        if (LEVEL <=INFO){
            printFileLog("I:"+tag+":"+msg,PRINT_TIME);
        }
    }
    public static void i(String tag,String msg,Throwable tr){
        LogUtil.i(tag,msg,tr);
        if (PRINT_THROWABLE){
            printFileLog("I:"+tag+":"+msg+":e="+tr.toString(),PRINT_TIME);
        }
    }
    public static void w(String tag,String msg){
        LogUtil.w(tag,msg);
        if (LEVEL <=WARN){
            printFileLog("W:"+tag+":"+msg,PRINT_TIME);
        }
    }
    public static void w(String tag,String msg,Throwable tr){
        LogUtil.w(tag,msg,tr);
        if (PRINT_THROWABLE){
            printFileLog("W:"+tag+":"+msg+":e="+tr.toString(),PRINT_TIME);
        }
    }
    public static void w(String tag,Throwable tr){
        LogUtil.w(tag,tr);
        if (PRINT_THROWABLE){
            printFileLog("W:"+tag+":e="+tr.toString(),PRINT_TIME);
        }
    }
    public static void e(String tag,String msg){
        LogUtil.e(tag,msg);
        if (LEVEL <=ERROR){
            printFileLog("E:"+tag+":"+msg,PRINT_TIME);
        }
    }
    public static void e(String tag,String msg,Throwable tr){
        LogUtil.e(tag,msg,tr);
        if (PRINT_THROWABLE){
            printFileLog("E:"+tag+":"+msg+":e="+tr.toString(),PRINT_TIME);
        }
    }
    public static void wtf(String tag,String msg){
        LogUtil.wtf(tag,msg);
        if (LEVEL <=WARN){
            printFileLog("WTF:"+tag+":"+msg,PRINT_TIME);
        }
    }
    public static void wtf(String tag,String msg,Throwable tr){
        LogUtil.wtf(tag,msg,tr);
        if (PRINT_THROWABLE){
            printFileLog("WTF:"+tag+":"+msg+":e="+tr.toString(),PRINT_TIME);
        }
    }
    public static void wtf(String tag,Throwable tr){
        LogUtil.wtf(tag,tr);
        if (PRINT_THROWABLE){
            printFileLog("WTF:"+tag+":e="+tr.toString(),PRINT_TIME);
        }
    }
}
