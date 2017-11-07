package com.example.dx.utilproject.log;

import android.util.Log;

public class LogUtil {
    private static final int VERBOSE=1;
    private static final int DEBUG=2;
    private static final int INFO=3;
    private static final int WARN=4;
    private static final int ERROR=5;
    private static final int WTF=6;
    public static final int NOTHING=7;
    private static final int LEVEL =VERBOSE;//通过调整LEVEL的值，控制日志的输出
    private static final boolean PRINT_THROWABLE=true;//控制抛出的异常日志是否需要输出

    public static void v(String tag,String msg){
        if (LEVEL <=VERBOSE){
            Log.v(tag,msg);
        }
    }
    public static void v(String tag,String msg,Throwable tr){
        if (PRINT_THROWABLE){
            Log.v(tag,msg,tr);
        }
    }
    public static void d(String tag,String msg){
        if (LEVEL <=DEBUG){
            Log.d(tag,msg);
        }
    }
    public static void d(String tag,String msg,Throwable tr){
        if (PRINT_THROWABLE){
            Log.d(tag,msg,tr);
        }
    }
    public static void i(String tag,String msg){
        if (LEVEL <=INFO){
            Log.i(tag,msg);
        }
    }
    public static void i(String tag,String msg,Throwable tr){
        if (PRINT_THROWABLE){
            Log.i(tag,msg,tr);
        }
    }
    public static void w(String tag,String msg){
        if (LEVEL <=WARN){
            Log.w(tag,msg);
        }
    }
    public static void w(String tag,String msg,Throwable tr){
        if (PRINT_THROWABLE){
            Log.w(tag,msg,tr);
        }
    }
    public static void w(String tag,Throwable tr){
        if (PRINT_THROWABLE){
            Log.w(tag,tr);
        }
    }
    public static void e(String tag,String msg){
        if (LEVEL <=ERROR){
            Log.e(tag,msg);
        }
    }
    public static void e(String tag,String msg,Throwable tr){
        if (PRINT_THROWABLE){
            Log.e(tag,msg,tr);
        }
    }
    public static void wtf(String tag,String msg){
        if (LEVEL <=WARN){
            Log.wtf(tag,msg);
        }
    }
    public static void wtf(String tag,String msg,Throwable tr){
        if (PRINT_THROWABLE){
            Log.wtf(tag,msg,tr);
        }
    }
    public static void wtf(String tag,Throwable tr){
        if (PRINT_THROWABLE){
            Log.wtf(tag,tr);
        }
    }
}
