package com.example.dx.utilproject.file;

import java.io.File;

/**
 * FileUtil的扩展性不强，需要重新设计
 * Created by admin on 2017/11/10.
 */

public abstract class BaseFile {
    long limitTime;//超时时间
    String path;//路径
    String encrypt;//编码格式
    File file;
    //注意判断文件大小，文件太大的话，手动抛出异常
    public abstract String read();
    public abstract boolean write(String data);
    //附加写入
    public abstract boolean writeAppend();
    public long size(){
        if (file==null||!file.exists()){
            return 0;
        }
        return file.length();
    }

    public File getFile() {
        return file;
    }

    public static long sizeOf(String path){
        return 0;
    }
    public static File createFile(String path){
        return null;
    }
    public static File move(String srcPath,String dstPath){
        return null;
    }
    public static boolean copy(String srcPath,String dstPath){
        return true;
    }
    public static boolean remove(String path){
        return true;
    }
    public static File rename(String path,String newName){
        return null;
    }
    public static File compress(String srcPath, String dstPath){
        return null;
    }
    public static File decompress(String srcPath, String dstPath){
        return null;
    }
}
