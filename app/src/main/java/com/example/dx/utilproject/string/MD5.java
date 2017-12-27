package com.example.dx.utilproject.string;

import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final String TAG = "MD5";
    private void logW(String log){
        Log.w(TAG,log);
    }

    /**
     * 由于是文件操作,执行时间可能比较久,建议在非主线程中运行
     * @param pathname 文件路径
     * @return md5
     * @throws FileNotFoundException 参数pathname不合法,或者文件未找到
     */
    @WorkerThread
    public String getFileMd5(String pathname)throws FileNotFoundException{
        if (pathname==null||pathname.length()==0){
            throw new FileNotFoundException("pathname参数为null,或者pathname.length()=0");
        }
        return getFileMd5(new File(pathname));
    }
    /**
     * 由于是文件操作,执行时间可能比较久,建议在非主线程中运行
     * @param file 文件
     * @return md5
     * @throws FileNotFoundException 参数pathname不合法,或者文件未找到
     */
    @WorkerThread
    public String getFileMd5(File file) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException("file参数为null,或者file don't exits!");
        }
        FileInputStream fis = new FileInputStream(file);
        try {
            MappedByteBuffer byteBuffer = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bigInteger = new BigInteger(1, md5.digest());
            return bigInteger.toString(16);
        } catch (NoSuchAlgorithmException | IOException e) {
            logW("md5方法执行错误,返回\"\"");
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                logW("getFileMd5方法执行错误,返回\"\"");
            }
        }
        return "";
    }
    public String md5(String key){
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            BigInteger bigInteger=new BigInteger(1,mDigest.digest());
            return bigInteger.toString(16);
        } catch (NoSuchAlgorithmException e) {
            logW("md5方法执行错误,返回\"\"");
        }
        return "";
    }
    @Deprecated
    public String getMD5(String key) {
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            return bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            logW("getMD5方法执行错误,返回\"\"");
        }
        return "";
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
