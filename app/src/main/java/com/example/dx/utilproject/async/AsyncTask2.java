package com.example.dx.utilproject.async;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dx
 * 使用{@link android.os.HandlerThread}封装的异步任务类
 */
public class AsyncTask2 {
    private static final int FLAG_ASYNC = 1;
    private static final int FLAG_MAIN = 2;

    private static AsyncTask2 asyncTask2 = new AsyncTask2();
    private Map<Task, Object> taskResultMap = new HashMap<>(16);
    private static HandlerThread handlerThread;
    private MainHandler mainHandler;
    private AsyncHandler asyncHandler;


    public interface Task<Result> {
        /**
         * 具体要执行的任务
         *
         * @return 任务执行完了后返回的数据
         */
        Result run();

        /**
         * 如果在任务run方法执行完之前调用了recycle方法,onComplete可能不会被执行
         *
         * @param result 任务完成后返回的对象
         */
        void onComplete(Result result);
    }

    private class AsyncHandler extends Handler {
        AsyncHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.obj == null) {
                return;
            }
            Task task = (Task) msg.obj;
            switch (msg.what) {
                case FLAG_MAIN:
                    Object result1 = taskResultMap.get(task);
                    task.onComplete(result1);
                    taskResultMap.remove(task);
                    break;
                case FLAG_ASYNC:
                    Object result2 = task.run();
                    taskResultMap.put(task, result2);

                    Message mainMsg = mainHandler.obtainMessage();
                    mainMsg.obj = task;
                    mainMsg.what = FLAG_ASYNC;
                    mainMsg.sendToTarget();
                    break;
                default:
            }

        }
    }

    private class MainHandler extends Handler {
        MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null || msg.obj == null) {
                return;
            }
            Task task = (Task) msg.obj;
            switch (msg.what) {
                case FLAG_MAIN:
                    Object result1 = task.run();
                    if (handlerThread == null || !handlerThread.isAlive()) {
                        return;
                    }
                    taskResultMap.put(task, result1);
                    Message mainMsg = asyncHandler.obtainMessage();
                    mainMsg.obj = task;
                    mainMsg.what = FLAG_MAIN;
                    mainMsg.sendToTarget();
                    break;
                case FLAG_ASYNC:
                    Object result2 = taskResultMap.get(task);
                    task.onComplete(result2);
                    taskResultMap.remove(task);
                    break;
                default:
            }
        }
    }

    public static AsyncTask2 getInstance() {
        if (handlerThread == null || !handlerThread.isAlive()) {
            asyncTask2 = new AsyncTask2();
        }
        return asyncTask2;
    }

    private AsyncTask2() {
        handlerThread = new HandlerThread("AsyncTask");
        handlerThread.start();
        asyncHandler = new AsyncHandler(handlerThread.getLooper());
        mainHandler = new MainHandler(Looper.getMainLooper());
    }

    /**
     * 在子线程中,执行一个任务
     *
     * @param task     具体的任务
     * @param <Result> 调用者定义的泛型,当任务执行完了以后,返回此泛型的对象
     */
    public <Result> void doInThread(@NonNull Task<Result> task) {
        Message msg = asyncHandler.obtainMessage();
        msg.obj = task;
        msg.what = FLAG_ASYNC;
        msg.sendToTarget();
    }

    /**
     * 在子线程中,执行一个任务,调用者不关心任务执行的完成情况
     *
     * @param runnable 具体的任务
     */
    public void doInThread(@NonNull Runnable runnable) {
        asyncHandler.post(runnable);
    }

    /**
     * 在主线程中,执行一个任务,用于跟新ui之类的操作,不建议做太耗时的操作
     *
     * @param task     具体的任务
     * @param <Result> 调用者定义的泛型,当任务执行完了以后,返回此泛型的对象
     */
    public <Result> void doInMain(@NonNull Task<Result> task) {
        Message msg = mainHandler.obtainMessage();
        msg.obj = task;
        msg.what = FLAG_MAIN;
        msg.sendToTarget();
    }

    /**
     * 在主线程中,执行一个任务,调用者不关心任务执行的完成情况
     *
     * @param runnable 具体的任务
     */
    public void doInMain(@NonNull Runnable runnable) {
        mainHandler.post(runnable);
    }

    /**
     * 手动回收AsyncTask类中的资源
     */
    public void recycle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //当消息队列中还有消息时,会等到消息消化完毕才退出
            handlerThread.quitSafely();
        }else {
            handlerThread.quit();
        }
        handlerThread = null;
        taskResultMap.clear();
    }
}
