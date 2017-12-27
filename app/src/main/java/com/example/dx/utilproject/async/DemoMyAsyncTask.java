package com.example.dx.utilproject.async;

import android.util.Log;

import com.example.dx.utilproject.string.MD5;

import java.io.FileNotFoundException;

/**
 * Created by admin on 2017/12/27.
 */

public class DemoMyAsyncTask {
    private static final String TAG = "DemoMyAsyncTask";
    public void demo2(){
        //使用简单封装的AsyncTask
        new SimpleAsyncTask().preExecute(new SimpleAsyncTask.Call() {
            @Override
            public void call() {

            }
        }).backgroundExecute(new SimpleAsyncTask.Call() {
            @Override
            public void call() {

            }
        }).completeExecute(new SimpleAsyncTask.Call() {
            @Override
            public void call() {

            }
        }).execute();
    }
    public void demo(){
        /**
         * 三个泛型值分别表示:
         * 初始化时传入的数据;
         * 后台线程发送给主线程的消息;
         * 任务完成后发送给主线程的消息
         */
        final MyAsyncTask<String,Integer,Boolean> myTask=new MyAsyncTask<>();
        myTask.preExecute(new MyAsyncTask.Call() {
            /**
             *任务开始前,首先调用
             */
            @Override
            public void call() {
                Log.wtf(TAG,Thread.currentThread().getName()+":preExecute");
            }
        }).backgroundExecute(new MyAsyncTask.Arg2Fun<MyAsyncTask<String, Integer, Boolean>, String,Boolean>() {
            /**
             * 后台任务执行中
             * @param myAsyncTask 当前任务对象,方便取消当前任务
             * @param param 任务初始化传入的参数
             * @return 任务返回结果,这里用的是Boolean
             */
            @Override
            public Boolean call(MyAsyncTask<String,Integer,Boolean> myAsyncTask,String param) {
                Log.wtf(TAG,Thread.currentThread().getName()+":backgroundExecute:"+param);
                try {
                    String md5Value;
                    MD5 md5=new MD5();
                    md5Value=md5.getFileMd5("/sdcard/.system/busybox");
                    //这里的sendMessage的参数类型,注意与MyAsyncTask的第二个泛型一致
                    myAsyncTask.sendMessage(md5Value.length());
                    //模拟还需要一段时间才能执行任务
                    Thread.sleep(500);
                    md5Value=md5.getFileMd5("/sdcard/.system/phone_info.json");
                    Log.wtf(TAG,Thread.currentThread().getName()+":backgroundExecute:是否执行到这里了?");
                    return md5Value!=null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }).receiveMessage(new MyAsyncTask.ArgCall<Integer>() {
            /**
             * 后台任务发消息到主线程,这里发送的是Integer
             * @param i 后台线程发送的消息
             */
            @Override
            public void call(Integer i) {
                Log.wtf(TAG,Thread.currentThread().getName()+":receiveMessage:"+i);
                myTask.cancel(false);
            }
        }).completeExecute(new MyAsyncTask.ArgCall<Boolean>() {
            /**
             * 任务执行完成后
             * @param s 任务执行完成后,后台线程返回的结果
             */
            @Override
            public void call(Boolean s) {
                Log.wtf(TAG,Thread.currentThread().getName()+":completeExecute:"+s);
            }
            //execute方法参数传入的字符串数据
        }).execute("随便传入数据");
    }
}
