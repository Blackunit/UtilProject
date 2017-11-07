package com.example.dx.utilproject.net;

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 自己简单的对HttpURLConnection进行了封装
 * Created by DX on 2017/7/4.
 */

public class SimpleHttp {
    private static final boolean printLog = true;//是否允许输出日志
    private static final String defaultCharset = "UTF-8";
    private static final String TAG = "SimpleHttp";
    private int mConnTimeOut = 60000;
    private int mReadTimeOut = 2 * 60000;

    public static interface Listener {
        void onResponse(String response);
    }

    public SimpleHttp(int mConnTimeOut, int mReadTimeOut) {
        this.mConnTimeOut = mConnTimeOut;
        this.mReadTimeOut = mReadTimeOut;
    }

    public SimpleHttp() {
    }

    public SimpleHttp(int mConnTimeOut) {
        this.mConnTimeOut = mConnTimeOut;
    }

    private void logI(String msg) {
        if (printLog) {
            Log.i(TAG, msg);
        }
    }

    private void logE(String msg) {
        Log.e(TAG, msg);
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

    private String encodeStr(String data) {
        return encodeStr(data, defaultCharset);
    }

    private String encodeParams(Map<String, String> params) {
        String submit = "";
        StringBuilder sb = new StringBuilder();
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(encodeStr(entry.getValue()));
                sb.append("&");
            }
            sb.deleteCharAt(sb.lastIndexOf("&"));//移除最后一个多余的"&"符号
        }
        return submit;
    }

    private String encodeUrl(String urlStr, Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return urlStr;
        }
        logI("encodeUrl-->url=" + urlStr);
        return urlStr+"?"+encodeParams(params);
    }

    public String get(String urlStr, Map<String, String> params) {
        String url = encodeUrl(urlStr, params);
        return get(url);
    }

    private String get(final String urlStr) {
        String result = "";
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", defaultCharset);
            connection.setRequestProperty("contentType", defaultCharset);
            connection.setReadTimeout(mReadTimeOut);
            connection.setConnectTimeout(mConnTimeOut);
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(is, defaultCharset));
                while ((result = reader.readLine()) != null) {
                    sb.append(result);
                }
                result = sb.toString();
            } else {
                logE("get:url=" + urlStr);
                logE("get:error code=" + code);
            }
        }catch (IOException e) {
            logE("get:url=" + urlStr);
            logE(e.toString());
        } finally {
            if (connection != null)
                connection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public String post(String urlStr, String data) {
        return post(urlStr,null,data);
    }

    public String post(String url, Map<String, String> params, String data) {
        String urlStr=url;
        String result = "";
        urlStr = encodeUrl(urlStr, params);
        StringBuilder sb = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            URL url_ = new URL(urlStr);
            connection = (HttpURLConnection) url_.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(mConnTimeOut);
            connection.setRequestProperty("Content-Type", "application/json; charset=" + defaultCharset);//application/x-www-form-urlencoded
            if (data!=null) {
                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();
            }
            int code = connection.getResponseCode();
            if (code == 200) {
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, defaultCharset));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                result = sb.toString();
            } else {
                logE("post:url=" + urlStr);
                logE("post:error code=" + code);
            }
        }catch (IOException e) {
            logE("post:url=" + urlStr);
            logE(e.toString());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return result;
    }

    public void getAsync(final String urlStr, final Listener listener, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String response = get(urlStr);
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        });
    }

    public void getAsync(final String urlStr, final Listener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = get(urlStr);
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        }).start();
    }

    public void postAsync(final String urlStr, final Map<String, String> params, final String data, final Listener listener, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                String response = post(urlStr, params,data);
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        });
    }

    public void postAsync(final String urlStr, final Map<String, String> params, final String data, final Listener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = post(urlStr, params,data);
                if (listener != null) {
                    listener.onResponse(response);
                }
            }
        }).start();
    }
}
