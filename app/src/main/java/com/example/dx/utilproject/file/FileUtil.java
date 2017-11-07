package com.example.dx.utilproject.file;

import android.Manifest;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件读写操作的工具类
 * Created by DX on 2017/7/11.
 */
public class FileUtil {
    private static final String DEFAULT_PATH_NAME = Environment.getExternalStorageDirectory() + "/.system/data_info/app_info.txt";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static Object syncObj=new Object();
    private static final SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static FileUtil INSTANCE;

    private String pathname= DEFAULT_PATH_NAME;

    private FileUtil(String pathname) {
        this.pathname=pathname;
        if(!initDirectory(getPathByFile(pathname))||!initFile(pathname)){
            throw new RuntimeException("创建文件目录失败");
        }
    }

    //单例模式的实现,这里会使用默认的路径
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    })
    public static FileUtil getInstance() {
        //双检查实现单例模式
        if (INSTANCE==null){
            synchronized (syncObj){
                if (INSTANCE==null){
                    INSTANCE=new FileUtil(DEFAULT_PATH_NAME);
                }
            }
        }
        return INSTANCE;
    }
    //新建一个FileUtil对象
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
    })
    public static FileUtil newInstance(@NonNull String pathname){
        if (pathname.trim().equals("")){
            throw new IllegalArgumentException("pathname格式不对");
        }
        FileUtil fileUtil=new FileUtil(pathname);
        return fileUtil;
    }
    public void saveDataToFile(String data) {
        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(pathname);
            fs.write(data.getBytes(DEFAULT_CHARSET));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fs != null) {
                    fs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean saveAppend(String data, boolean logTime) {
        String dateString=DATE_FORMAT.format(new Date());
        if (logTime) {
            data =String.format("\r\n------%s------\r\n%s" ,dateString,data);
        }
        return saveDataAppend(data + "\r\n", pathname);
    }

    private boolean saveDataAppend(String data, String path) {
        OutputStreamWriter write = null;
        BufferedWriter out = null;
        try {
            // new FileOutputStream(fileName, true) 第二个参数表示追加写入
            write = new OutputStreamWriter(new FileOutputStream(
                    path, true), Charset.forName(DEFAULT_CHARSET));
//                    out = new BufferedWriter(write, BUFFER_SIZE);
            out = new BufferedWriter(write);
            out.write(data);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private static boolean initFile(String pathname) {
        File file = new File(pathname);
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            return true;
        }
        return false;
    }

    private static boolean initDirectory(String path) {
        try {
            File folder = new File(path);
            if (!folder.isDirectory()) {
                folder.delete();
            }
            if (!folder.exists()) {
                return folder.mkdirs();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private static String getPathByFile(String pathname){
        int flagIndex=pathname.lastIndexOf('/');
        return pathname.substring(0, flagIndex+1);
    }
}
