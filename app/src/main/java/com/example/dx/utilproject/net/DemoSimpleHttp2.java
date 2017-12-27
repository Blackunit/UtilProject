package com.example.dx.utilproject.net;

import android.util.Log;

import com.example.dx.utilproject.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/12/27.
 */

public class DemoSimpleHttp2 {
    private static final String TAG = "DemoSimpleHttp2";
    /**
     * SimpleHttp2的简单使用demo,模拟网络访问
     */
    public void demo() {
        String url = "http://192.168.121.70:8080/IntellijServlet/test";
        Map<String, String> params = new HashMap<>();
        params.put("name", "DX");
        params.put("age", "24");
        List<User> users = new ArrayList<>();
        users.add(new User("DX2", 25));
        users.add(new User("中文", 20));
        new SimpleHttp2().setTimeout(20000).post(url, params, users, new SimpleHttp2.Listener<List<User>>() {
            /**
             * onStart方法可不用重写,如果需要在http访问之前在ui线程做点事情,可以在这个方法中进行
             */
            @Override
            public void onStart() {
                Log.wtf(TAG, Thread.currentThread().getName() + "-->onStart");
            }

            /**
             * 这里需要自己写解析算法,这里由于反回的是json数据,直接使用Gson解析
             * @param response 服务器返回的字符串
             * @return 返回解析好的数据,方便AsyncTask将解析好的数据发送到主线程
             */
            @Override
            public List<User> onParse(String response) {
                Log.wtf(TAG, Thread.currentThread().getName() + "-->response=" + response);
                return new Gson().fromJson(response, new TypeToken<List<User>>() {
                }.getType());
            }

            /**
             * 网络访问成功后,接收子线程解析好的数据
             * @param users 子线程解析好的数据
             */
            @Override
            public void onSuccess(List<User> users) {
                Log.wtf(TAG, Thread.currentThread().getName() + "-->onSuccess=" + users.toString());
            }

            /**
             * 网络出现错误时的回调
             * @param error 网络访问出现问题,给出错误信息
             */
            @Override
            public void onError(String error) {
                Log.wtf(TAG, Thread.currentThread().getName() + "-->onError=" + error);
            }
        });
    }
}
