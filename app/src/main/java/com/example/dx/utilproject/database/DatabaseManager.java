package com.example.dx.utilproject.database;

import android.app.Application;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by admin on 2017/10/23.
 */

public class DatabaseManager {
    private static final String TABLE_NAME=DatabaseHelper.TABLE_NAME;
    private static DatabaseHelper helper=null;
    private static DatabaseManager manager=null;
    private static Object syncObj=new Object();
    private static SQLiteDatabase db=null;
    private static Context context=null;
    private DatabaseManager(Context context){
        DatabaseManager.context=context.getApplicationContext();
    }
    public static DatabaseManager getInstance(Context context){//使用双检查机制实现的单例模式(兼顾线程安全和性能)
        if (manager==null) {
            synchronized (syncObj) {
                if (manager==null) {
                    manager = new DatabaseManager(context.getApplicationContext());
                    helper = new DatabaseHelper(DatabaseManager.context, "Test.db", null, DatabaseHelper.version);
                    db=helper.getWritableDatabase();
                }
            }
        }
        return manager;
    }
    public static DatabaseManager getInstance(){
        return getInstance(getContextByReflect());
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
    public void close(){
        helper=null;
        manager=null;
        Object syncObj=null;
        context=null;
        if (db!=null){
            db.close();
            db=null;
        }
    }
    public boolean insert(String name,String age){
        String sql=String.format("insert into %s (name,age)values(?,?)",TABLE_NAME);
        try {
            db.execSQL(sql,new String[]{name,age});
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
