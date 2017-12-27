package com.example.dx.utilproject.async;

/**
 * 简单对MyAsyncTask类包装了一下,干掉这些泛型
 * Created by admin on 2017/12/27.
 */

public class SimpleAsyncTask {
    private MyAsyncTask<Void,Void,Void> task=new MyAsyncTask<>();
    public static interface Call{
        void call();
    }
    public SimpleAsyncTask preExecute(final Call call){
        task.preExecute(new MyAsyncTask.Call() {
            @Override
            public void call() {
                call.call();
            }
        });
        return this;
    }
    public SimpleAsyncTask backgroundExecute(final Call call){
        task.backgroundExecute(new MyAsyncTask.Arg2Fun<MyAsyncTask<Void, Void, Void>, Void, Void>() {
            @Override
            public Void call(MyAsyncTask<Void, Void, Void> voidVoidVoidMyAsyncTask, Void aVoid) {
                call.call();
                return null;
            }
        });
        return this;
    }
    public SimpleAsyncTask completeExecute(final Call call){
        task.completeExecute(new MyAsyncTask.ArgCall<Void>() {
            @Override
            public void call(Void aVoid) {
                call.call();
            }
        });
        return this;
    }
    public void execute(){
        task.execute();
    }
    public void cancel(boolean mayInterruptIfRunning){
        task.cancel(mayInterruptIfRunning);
    }
}
