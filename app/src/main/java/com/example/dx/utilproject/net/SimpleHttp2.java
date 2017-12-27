package com.example.dx.utilproject.net;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.dx.utilproject.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleHttp2 {
    private static final String TAG = "SimpleHttp2";
    /**
     * 是否允许输出日志
     */
    private static final boolean PRINT_LOG = true;
    private static final String DEFAULT_CHARSET = "UTF-8";
    private int timeout =8000;
    private String charset= DEFAULT_CHARSET;
    /**
     * 需要引用compile 'com.google.code.gson:gson:2.8.2'
     */
    private Gson gson=new Gson();
    public abstract static class Listener<Result>{
        /**
         * 任务开始前的准备工作,该方法运行在UI线程中
         */
        public void onStart(){}
        /**
         * 调用者自己解析服务器返回的字符串
         * 该方法运行在子线程中
         * @param response 服务器返回的字符串
         * @return 调用者将解析好的字符串返回
         */
        public abstract Result onParse(String response);
        /**
         * 正常返回结果,该方法运行在UI线程中
         * @param result 用户自己解析的结果
         */
        public abstract void onSuccess(Result result);
        /**
         * 访问出错,该方法运行在UI线程中
         * @param error 网络访问出现问题,给出错误信息
         */
        public abstract void onError(String error);
    }
    public SimpleHttp2(){

    }

    public SimpleHttp2 setTimeout(int timeout) {
        if (timeout<=0){
            throw new IllegalArgumentException("timeout must >0 ms");
        }
        this.timeout =timeout;
        return this;
    }

    public SimpleHttp2 setCharset(@NonNull String charset) {
        this.charset = charset;
        return this;
    }

    private void logI(String msg) {
        if (PRINT_LOG) {
            Log.i(TAG, msg);
        }
    }
    private String encodeStr(String data, String charset) {
        String str = data;
        try {
            str = URLEncoder.encode(data, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }
    private String encodeParams(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(encodeStr(entry.getValue(), DEFAULT_CHARSET));
                sb.append("&");
            }
            //移除最后一个多余的"&"符号
            sb.deleteCharAt(sb.lastIndexOf("&"));
        }
        return sb.toString();
    }

    private String encodeUrl(String urlStr, @Nullable Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return urlStr;
        }
        String encodeUrl=urlStr+"?"+encodeParams(params);;
        logI("encodeUrl-->url=" + encodeUrl);
        return encodeUrl;
    }

    /**
     * Get请求
     * @param url 网络请求url
     * @param args 网络请求参数,这里会直接接在url后面
     * @param listener 网络访问监听器
     * @param <Result> 声明一个泛型,支持List<Result>的集合类型
     */
    public <Result> void get(@NonNull String url, @Nullable Map<String,String> args, @NonNull Listener<Result> listener){
        new HttpAsyncTask<Result>(encodeUrl(url,args),HttpAsyncTask.REQUEST_METHOD_GET,timeout,charset,listener).execute();
    }

    /**
     * Post请求
     * @param url 网络请求url
     * @param args 网络请求参数,这里会直接接在url后面
     * @param listener 网络访问监听器
     * @param <Result> 声明一个泛型,支持List<Result>的集合类型
     */
    public <Result> void post(@NonNull String url, @Nullable Map<String,String> args, @NonNull Listener<Result> listener){
        post(url,args,null,listener);
    }

    /**
     * Post请求
     * @param url 网络请求url
     * @param args 网络请求参数,这里会直接接在url后面
     * @param body 泛型body,支持List<Body>的集合类型
     * @param listener 网络访问监听器
     * @param <Body> 声明一个泛型,支持List<Body>的集合类型
     * @param <Result> 声明一个泛型,支持List<Result>的集合类型
     */
    public <Body,Result> void post(@NonNull String url, @Nullable Map<String,String> args, @Nullable Body body, @NonNull Listener<Result> listener){
        String requestBody=gson.toJson(body);
        new HttpAsyncTask<>(encodeUrl(url,args),
                HttpAsyncTask.REQUEST_METHOD_POST,
                timeout,
                charset,
                requestBody,
                listener).execute();
    }
}
