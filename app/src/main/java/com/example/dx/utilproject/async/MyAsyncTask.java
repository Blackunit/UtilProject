package com.example.dx.utilproject.async;

import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

/**
 * 异步任务,链式调用类(这个类是不是滥用泛型了?)
 * @param <Params> 异步任务需要传入的参数类型
 * @param <Message> 异步任务执行途中,向主线程发送的通知消息类型
 * @param <Result> 异步任务执行完成后,返回给主线程
 */
public class MyAsyncTask<Params, Message, Result> extends AsyncTask<Params, Message,Result> {
    private Call preCall=null;
    private Arg2Fun<MyAsyncTask<Params, Message, Result>,Params,Result> backgroundFun=null;
    private ArgCall<Message> receiveCall=null;
    private ArgCall<Result> completeFun =null;

    public static interface Call{
        void call();
    }
    public static interface ArgCall<Arg>{
        void call(Arg arg);
    }

    public static interface Arg2Fun<Arg1,Arg2,Result>{
        Result call(Arg1 arg1, Arg2 arg2);
    }

    @MainThread
    public MyAsyncTask<Params, Message, Result> preExecute(Call call){
        this.preCall=call;
        return this;
    }
    @WorkerThread
    public MyAsyncTask<Params, Message, Result> backgroundExecute(Arg2Fun<MyAsyncTask<Params, Message, Result>,Params,Result> function){
        this.backgroundFun =function;
        return this;
    }
    @MainThread
    public MyAsyncTask<Params, Message, Result> receiveMessage(ArgCall<Message> call){
        this.receiveCall=call;
        return this;
    }
    @MainThread
    public MyAsyncTask<Params, Message, Result> completeExecute(ArgCall<Result> call){
        this.completeFun =call;
        return this;
    }

    @Override
    protected void onPreExecute() {
        if (preCall!=null){
            preCall.call();
        }
    }

    @Override
    protected Result doInBackground(Params... params) {
        if (backgroundFun !=null){
            return backgroundFun.call(this,params[0]);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Message... messages) {
        if (receiveCall!=null){
            receiveCall.call(messages[0]);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if (completeFun !=null){
            completeFun.call(result);
        }
    }
    @WorkerThread
    public void sendMessage(Message... msgs){
        publishProgress(msgs);
    }
}
