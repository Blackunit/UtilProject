package com.example.dx.utilproject;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.dx.utilproject.command.Command;
import com.example.dx.utilproject.database.DatabaseManager;
import com.example.dx.utilproject.file.FileUtil;
import com.example.dx.utilproject.handlers.MyHandler;
import com.example.dx.utilproject.log.FileLogUtil;
import com.example.dx.utilproject.log.LogUtil;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MyHandler<MainActivity> handler=new MyHandler<MainActivity>(this);

    private SoftReference<String> softReference=new SoftReference<String>(new String("softReference"));
    private WeakReference<String> weakReference=new WeakReference<String>(new String("weakReference"));
    private PhantomReference<String> phantomReference=new PhantomReference<String>("phantomReference",new ReferenceQueue<String>());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getWindow().setContentView(LayoutInflater.from(this).inflate(R.layout.activity_main,null));//效果同setContentView();

        findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileLogUtil.e(TAG,"FileLogUtil test");
            }
        });
    }
    public void test(){
        Log.wtf("MainActivity","test method is called!");
    }

    @Override
    protected void onDestroy() {
        DatabaseManager.getInstance().close();
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    private Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
