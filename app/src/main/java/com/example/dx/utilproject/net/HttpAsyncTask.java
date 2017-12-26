package com.example.dx.utilproject.net;

import android.os.AsyncTask;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpAsyncTask<Result> extends AsyncTask<Void,String,Result> {
    private static final String TAG = "HttpAsyncTask";
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_GBK = "GBK";
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";
    private SimpleHttp2.Listener<Result> listener;
    private String urlParam;
    private int timeout=80000;
    private String charset= CHARSET_UTF8;
    private String requestMethod= REQUEST_METHOD_GET;
    private String requsetBody=null;

    /**
     * @param urlParam 请求的url,已经接上了参数
     * @param requestMethod 请求方法REQUEST_METHOD_GET/REQUEST_METHOD_POST
     * @param timeout 超时时间:ConnectTimeout=ReadTimeout=timeout/2
     * @param charset 内容编码:CHARSET_UTF8/CHARSET_GBK
     * @param listener 回调监听器
     */
    public HttpAsyncTask(String urlParam, String requestMethod, int timeout, String charset, SimpleHttp2.Listener<Result> listener){
        if (urlParam==null||"".equals(urlParam)){
            throw new IllegalArgumentException("urlParam不能为空");
        }else {
            this.urlParam = urlParam;
        }

        if (!REQUEST_METHOD_GET.equals(requestMethod)&&!REQUEST_METHOD_POST.equals(requestMethod)){
            throw new IllegalArgumentException("不支持"+requestMethod+"的请求方式");
        }else {
            this.requestMethod=requestMethod;
        }

        if (timeout<=1){
            logW("timeout 不能小于等于1ms,所以默认使用80000ms");
        }else {
            this.timeout=timeout;
        }

        if (listener==null){
            throw new IllegalArgumentException("listener不允许为null");
        }else {
            this.listener = listener;
        }

        if (charset==null||"".equals(charset)){
            throw new IllegalArgumentException("charset必须为CHARSET_UTF8或者CHARSET_GBK");
        }else {
            if (CHARSET_UTF8.equals(charset)||CHARSET_GBK.equals(charset)) {
                this.charset = charset;
            }else {
                throw new IllegalArgumentException("charset必须为CHARSET_UTF8或者CHARSET_GBK");
            }
        }
    }

    /**
     * @param urlParam 请求的url,已经接上了参数
     * @param requestMethod 请求方法REQUEST_METHOD_GET/REQUEST_METHOD_POST
     * @param timeout 超时时间:ConnectTimeout=ReadTimeout=timeout/2
     * @param charset 内容编码:CHARSET_UTF8/CHARSET_GBK
     * @param body 请求体,只有post方法才有效
     * @param listener 回调监听器
     */
    public HttpAsyncTask(String urlParam, String requestMethod, int timeout, String charset, String body, SimpleHttp2.Listener listener){
        this(urlParam,requestMethod,timeout,charset,listener);
        this.requsetBody=body;
    }

    @Override
    @MainThread
    protected void onPreExecute() {
        listener.onStart();
    }

    @Override
    @WorkerThread
    protected Result doInBackground(Void... params) {
        if (REQUEST_METHOD_GET.equals(requestMethod)) {
            return get(urlParam, new HttpRequestListener<Result>() {
                @Override
                public Result onSuccess(String result) {
                    return listener.onParse(result);
                }

                @Override
                public Result onError(String error) {
                    publishProgress(error);
                    return null;
                }
            });
        }else if (REQUEST_METHOD_POST.equals(requestMethod)){
            return post(urlParam, requsetBody, new HttpRequestListener<Result>() {
                @Override
                public Result onSuccess(String result) {
                    return listener.onParse(result);
                }

                @Override
                public Result onError(String error) {
                    publishProgress(error);
                    return null;
                }
            });
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        listener.onError(values[0]);
    }

    @Override
    @MainThread
    protected void onPostExecute(Result result) {
        if (result!=null) {
            listener.onSuccess(result);
        }
    }
    private void logW(String msg) {
        Log.w(TAG, msg);
    }
    private interface HttpRequestListener<Result>{
        Result onSuccess(String result);
        Result onError(String error);
    }
    private Result get(final String urlStr, HttpRequestListener<Result> listener) {
        String result = "";
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("contentType", charset);
            connection.setReadTimeout(timeout/2);
            connection.setConnectTimeout(timeout/2);
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(is, charset));
                while ((result = reader.readLine()) != null) {
                    sb.append(result);
                }
                result = sb.toString();
            } else {
                return listener.onError("get:url=" + urlStr+"\n,error code=" + code);
            }
        }catch (IOException e) {
            return listener.onError("get:url=" + urlStr+"\n,e=" + e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    listener.onError("get:url=" + urlStr+"\n,e=" + e.toString());
                }
            }
        }
        return listener.onSuccess(result);
    }
    private Result post(String urlStr, String body, HttpRequestListener<Result> listener) {
        String result = "";
        StringBuilder sb = new StringBuilder();
        HttpURLConnection connection = null;
        BufferedReader br=null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setReadTimeout(timeout/2);
            connection.setConnectTimeout(timeout/2);
            //application/x-www-form-urlencoded
            connection.setRequestProperty("Content-Type", "application/json; charset=" + charset);
            if (body!=null) {
                OutputStream os = connection.getOutputStream();
                os.write(body.getBytes());
                os.flush();
                os.close();
            }
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, charset));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                result = sb.toString();
            } else {
                return listener.onError("post:url=" + urlStr+"\n,error code=" + code);
            }
        }catch (IOException e) {
            return listener.onError("post:url=" + urlStr+"\n,e" + e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    listener.onError("post:url=" + urlStr+"\n,e=" + e.toString());
                }
            }
        }
        return listener.onSuccess(result);
    }
}
