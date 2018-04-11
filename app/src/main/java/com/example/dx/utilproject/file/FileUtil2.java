package com.example.dx.utilproject.file;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.dx.utilproject.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 使用该类时，最好声明文件修改权限，并动态申请权限
 * android.permission.MOUNT_UNMOUNT_FILESYSTEMS
 * android.permission.WRITE_EXTERNAL_STORAGE
 * android.permission.READ_EXTERNAL_STORAGE
 *
 * 获取文件目录的方法，得到的目录有可能不存在，建议在该目录下进行读写操作时，先创建一下(调用FileUtil.mkdirs方法)
 */
public class FileUtil2 {
    private static final String TAG = "FileUtil2";
    /**
     * 默认字符编码
     */
    private static final String DEFAULT_CHARSET = "UTF-8";

    private static Context mContext = MyApplication.getContext();
    /**
     * 各种文件类型
     */
    public static class FileType {
        /**
         * 未知的文件类型
         */
        public static final int UNKNOWN = 0;
        /**
         * excel文件
         */
        public static final int EXCEL = 1;
        /**
         * word文件
         */
        public static final int WORD = 2;
        /**
         * ppt文件
         */
        public static final int PPT = 3;
        /**
         * pdf文件
         */
        public static final int PDF = 4;
        /**
         * txt文件
         */
        public static final int TXT = 5;
        /**
         * zip文件
         */
        public static final int ZIP = 6;
        /**
         * 图片文件
         */
        public static final int IMAGE = 7;
        /**
         * 音频文件
         */
        public static final int VOICE = 8;
        /**
         * 视频文件
         */
        public static final int VIDEO = 9;
    }

    /**
     * @return 获取设备的sd卡的根目录
     */
    public static File getExternalStoragePath(){
        return Environment.getExternalStorageDirectory();
    }

    /**
     * 获取sd卡下指定目录：
     * 例如：getExternalStoragePublicPath(Environment.DIRECTORY_DOWNLOADS)将会返回/sdcard/Download路径
     * getExternalStoragePublicPath("test")将会返回/sdcard/test路径
     * @param type {@link android.os.Environment#DIRECTORY_MUSIC}, {@link android.os.Environment#DIRECTORY_PODCASTS},
     *            {@link android.os.Environment#DIRECTORY_RINGTONES}, {@link android.os.Environment#DIRECTORY_ALARMS},
     *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS}, {@link android.os.Environment#DIRECTORY_PICTURES},
     *            {@link android.os.Environment#DIRECTORY_MOVIES}, {@link android.os.Environment#DIRECTORY_DOWNLOADS},
     *            {@link android.os.Environment#DIRECTORY_DCIM}, {@link android.os.Environment#DIRECTORY_DOCUMENTS}
     * @return
     */
    public static File getExternalStoragePublicPath(@Nullable String type){
        return Environment.getExternalStoragePublicDirectory(type);
    }

    /**
     * 获取sd卡下的缓存目录
     * @return 具体的路径/sdcard/Android/data/xxx.xxx.xxx/cache
     */
    public static File getExternalCachePath() {
        return mContext.getExternalCacheDir();
    }

    /**
     * 获取sd卡下的缓存目录，具体位于/sdcard/Android/data/xxx.xxx.xxx/files目录下；
     * 调用getSdcardFilesDir(Environment.DIRECTORY_PICTURES)将会返回/sdcard/Android/data/xxx.xxx.xxx/files/Pictures
     * 使用getSdcardFilesDir("test")将会返回/sdcard/Android/data/xxx.xxx.xxx/files/test
     * @param type {@link android.os.Environment#DIRECTORY_MUSIC},
     *            {@link android.os.Environment#DIRECTORY_PODCASTS},
     *            {@link android.os.Environment#DIRECTORY_RINGTONES},
     *            {@link android.os.Environment#DIRECTORY_ALARMS},
     *            {@link android.os.Environment#DIRECTORY_NOTIFICATIONS},
     *            {@link android.os.Environment#DIRECTORY_PICTURES},
     *            {@link android.os.Environment#DIRECTORY_MOVIES}.
     * @return 具体的路径
     */
    public static File getExternalFilesDir(@Nullable String type){
        return mContext.getExternalFilesDir(type);
    }

    /**
     * @return 返回这个目录/sdcard/Android/obb/xxx.xxx.xxx
     */
    public static File getExternalObbPath(){
        return mContext.getObbDir();
    }
    /**
     * 获取应用的缓存目录
     * @return 返回这个目录/data/data/xxx.xxx.xxx/cache
     */
    public static File getCachePath() {
        return mContext.getCacheDir();
    }

    /**
     * @return 返回这个目录/data/data/xxx.xxx.xxx/files
     */
    public static File getFilesPath(){
        return mContext.getFilesDir();
    }
    /**
     * 获取app的存储目录，具体位于/data/data/xxx.xxx.xxx/app_name
     * 例如getAppPath("test",Context.MODE_PRIVATE)将会返回/data/data/xxx.xxx.xxx/app_test目录
     * @param name app_name文件夹名
     * @param mode 操作模式，参考getSharedPreferences(String name, int mode)的第二个参数
     * @return 返回/data/data/xxx.xxx.xxx/app_name路径
     */
    public static File getAppPath(String name, int mode){
        return mContext.getDir(name,mode);
    }

    /**
     * 如果不存在指定目录，就进行创建
     * @param pathname 具体文件目录
     * @return 对应目录的文件对象
     * @throws IOException
     */
    public static File mkdirs(String pathname) throws IOException {
        File file = new File(pathname);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.isFile()) {
            throw new IOException("当前路径下存在一个同名的文件");
        }
        return file;
    }

    /**
     * 根据文件路径创建目录，
     * 例如mkdirsFilePath("/sdcard/test/test2/test3.txt")将会创建/sdcard/test/test2"目录
     * @param pathname 文件路径
     * @return 是否创建成功
     */
    public static boolean mkdirsFilePath(String pathname){
        pathname=pathname.replaceAll("\\*", "/");
        if (pathname.indexOf('/') >= 0) {
            String filePath =pathname.substring(0, pathname.lastIndexOf('/'));
            try {
                mkdirs(filePath);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 创建具体的文件
     * @param pathname 文件的路径
     * @return 创建完成的文件
     */
    public static File createFile(String pathname) {
        //先创建路径
        mkdirsFilePath(pathname);

        File file = new File(pathname);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 删除文件
     * @param pathname 具体文件目录
     */
    public static void deleteFile(String pathname){
        File file=new File(pathname);
        file.deleteOnExit();
    }

    /**
     * 判断当前是否位于主线程中
     * @return
     */
    private static boolean isMainThread(){
        return Looper.getMainLooper().getThread().getId()== Thread.currentThread().getId();
    }
    private static void checkThread(String methodName){
        if(isMainThread()){
            logW(methodName+"方法最好放在子线程中运行");
        }
    }

    /**
     * 将数据写入文件中，使用覆盖写入的方式
     * @param data 要存入的数据
     * @param pathname 文件路径
     * @return 是否写入成功
     */
    public static boolean write(String data, String pathname){
        //默认为覆盖写入
        return write(data,pathname,false);
    }
    /**
     * 将数据写入文件中
     *
     * @param data     要存入的数据
     * @param pathname 文件路径
     * @param append 表示是否追加写入
     * @return 是否写入成功
     */
    public static boolean write(String data, String pathname, boolean append) {
        checkThread("write");

        //先创建路径
        mkdirsFilePath(pathname);

        OutputStreamWriter writer = null;
        BufferedWriter out = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(pathname,append), Charset.forName(DEFAULT_CHARSET));
            out = new BufferedWriter(writer);
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

    /**
     * 从指定路径中读取文件中的数据
     *
     * @param pathname 文件路径
     * @return 文件中的数据,如果文件不存在，或者出现异常，将返回""
     */
    public static String read(String pathname) {
        checkThread("read");
        File file = new File(pathname);
        if (!file.exists()) {
            return "";
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(DEFAULT_CHARSET)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * 返回文件大小，单位是字节，如果文件不存在则返回0
     *
     * @param pathname 文件路径
     * @return 单位为字节，除以1024后，单位为KB
     */
    public static long size(String pathname) {
        File file = new File(pathname);
        return file.length();
    }

    /**
     * 将字节大小转换成可以显示的文本内容例如3.2M
     * @param size 文件大小，单位是字节
     * @return 文本内容例如3.2M
     */
    public static String sizeToString(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;

        if (size >= gb) {
            return String.format(Locale.getDefault(), "%.1f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
        } else {
            return String.format(Locale.getDefault(), "%d B", size);
        }
    }

    /**
     * 获取文件的扩展名,以小写的方式返回
     * 例如原来的文件是xxx.PNG，该方法会返回.png
     *
     * @param fileName 文件名
     * @return 文件扩展名
     */
    private static String getFileExtension(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        int index = fileName.lastIndexOf('.');
        if (index < 0) {
            return "";
        }
        return fileName.substring(index).toLowerCase();
    }

    /**
     * 匹配文件类型
     *
     * @param fileName 文件名，可以包含文件路径，但是文件夹名最好不要包含'.'号
     * @return 返回文件类型, 目前支持这几种文件类型 {@link FileType}
     */
    public static int fileType(String fileName) {
        //未知类型
        int fileType = FileType.UNKNOWN;
        String typeStr = getFileExtension(fileName);
        if (TextUtils.isEmpty(typeStr)) {
            return fileType;
        }
        if (".xlsx".equalsIgnoreCase(typeStr) || ".xls".equalsIgnoreCase(typeStr)) {
            //表格
            fileType = FileType.EXCEL;
        } else if (".docx".equalsIgnoreCase(typeStr) || ".doc".equalsIgnoreCase(typeStr)) {
            //word
            fileType = FileType.WORD;
        } else if (".pptx".equalsIgnoreCase(typeStr) || ".ppt".equalsIgnoreCase(typeStr)) {
            //ppt
            fileType = FileType.PPT;
        } else if (".pdf".equalsIgnoreCase(typeStr)) {
            //pdf
            fileType = FileType.PDF;
        } else if (".txt".equalsIgnoreCase(typeStr)) {
            //text
            fileType = FileType.TXT;
        } else if (".zip".equalsIgnoreCase(typeStr) || ".rar".equalsIgnoreCase(typeStr)) {
            //zip
            fileType = FileType.ZIP;
        } else if (".jpg".equalsIgnoreCase(typeStr) || ".jpeg".equalsIgnoreCase(typeStr) || ".png".equalsIgnoreCase(typeStr) || ".gif".equalsIgnoreCase(typeStr)) {
            //图片
            fileType = FileType.IMAGE;
        } else if (".mp3".equalsIgnoreCase(typeStr) || ".wma".equalsIgnoreCase(typeStr) || ".wav".equalsIgnoreCase(typeStr) || ".rm".equalsIgnoreCase(typeStr)) {
            //音频
            fileType = FileType.VOICE;
        } else if (".mp4".equalsIgnoreCase(typeStr) || ".avi".equalsIgnoreCase(typeStr)) {
            //视频
            fileType = FileType.VIDEO;
        }
        return fileType;
    }

    /**
     * 由于是文件操作,执行时间可能比较久,建议在非主线程中运行
     *
     * @param pathname 文件路径
     * @return 文件的md5，如果中间出现异常，则返回""
     */
    public static String md5(String pathname) {
        checkThread("md5");
        File file = new File(pathname);
        if (!file.exists()) {
            return "";
        }
        FileInputStream fis=null;
        try {
            fis = new FileInputStream(file);
            MappedByteBuffer byteBuffer = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bigInteger = new BigInteger(1, md5.digest());
            return bigInteger.toString(16);
        } catch (NoSuchAlgorithmException | IOException e) {
            logW("md5方法执行错误,返回\"\"");
        } finally {
            try {
                if (fis!=null) {
                    fis.close();
                }
            } catch (IOException e) {
                logW("getFileMd5方法执行错误,返回\"\"");
            }
        }
        return "";
    }

    /**
     * 建议在子线程中调用
     * zip算法压缩文件，被压缩的可以是文件目录(注意吧不要出现outputPath位于inputPath之中的情况)
     * 错误示范:zipCompress("/sdcard/test/","/sdcard/test/out.zip")
     * @param inputPath  被压缩的文件路径
     * @param outputPath 被压缩后的zip文件目录，文件后缀名最好以.zip结尾
     */
    public static boolean zip(String inputPath, String outputPath) {
        return zip(inputPath, outputPath, true);
    }

    /**
     * zip算法压缩文件，被压缩的可以是文件目录(注意吧不要出现outputPath位于inputPath之中的情况)
     * 错误示范:zipCompress("/sdcard/test/","/sdcard/test/out.zip",true)
     *
     * @param inputPath           被压缩的文件路径
     * @param outputZipPathName       被压缩后的zip文件目录，文件后缀名最好以.zip结尾
     * @param keepEmptyDictionary 是否保留空文件夹
     */
    public static boolean zip(String inputPath, String outputZipPathName, boolean keepEmptyDictionary) {
        checkThread("zip");

        //先创建对应的zip路径
        mkdirsFilePath(outputZipPathName);

        if (!".zip".equalsIgnoreCase(getFileExtension(outputZipPathName))) {
            logW("zipCompress方法的参数outputPath最好以.zip结尾");
        }
        ZipOutputStream zipOut = null;
        BufferedOutputStream bos = null;
        try {
            File inputFile = new File(inputPath);
            zipOut = new ZipOutputStream(new FileOutputStream(outputZipPathName));
            bos = new BufferedOutputStream(zipOut);
            zipCompressRecursive(zipOut, inputFile, inputFile.getName(), keepEmptyDictionary);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 递归的方式遍历某个文件夹下所有的文件，并进行压缩
     *
     * @param out                 zip输出流
     * @param inputFile           待压缩输入文件
     * @param base                文件路径名
     * @param keepEmptyDictionary 是否保留空文件夹
     * @throws IOException 文件流操作异常
     */
    private static void zipCompressRecursive(ZipOutputStream out, File inputFile, String base, boolean keepEmptyDictionary) throws IOException {
        if (inputFile.isDirectory()) {
            File[] files = inputFile.listFiles();
            if (files.length == 0) {
                if (keepEmptyDictionary) {
                    out.putNextEntry(new ZipEntry(base + "/"));
                    out.closeEntry();
                }
            } else {
                for (int i = 0; i < files.length; i++) {
                    zipCompressRecursive(out, files[i], base + "/" + files[i].getName(), keepEmptyDictionary);
                }
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
            int count;
            byte[] buff = new byte[1024];
            while ((count = bis.read(buff)) != -1) {
                out.write(buff, 0, count);
            }
            bis.close();
            out.closeEntry();
        }
    }

    /**
     * zip文件解压缩
     *
     * @param inputZipPath 待解压的.zip文件
     * @param outputPath   解压后的路径
     */
    public static boolean unzip(String inputZipPath, String outputPath) {
        checkThread("unzip");

        File inputFile = new File(inputZipPath);
        if (!inputFile.exists()) {
            logW("unzip方法找不到zip文件，path="+inputZipPath);
            return false;
        }
        //创建输出路径
        try {
            mkdirs(outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //遍历zip文件，并解压
            ZipFile zipFile = new ZipFile(inputFile);
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                InputStream zipEntryIS = zipFile.getInputStream(zipEntry);
                String zipEntryName = zipEntry.getName();
                String outPath = (outputPath + "/" + zipEntryName).replaceAll("\\*", "/");
                if (outPath.indexOf('/') >=0) {
                    File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                }
                if (new File(outPath).isDirectory()) {
                    continue;
                }
                OutputStream os = new FileOutputStream(outPath);
                byte[] buff = new byte[1024];
                int len;
                while ((len = zipEntryIS.read(buff)) != -1) {
                    os.write(buff, 0, len);
                }
                zipEntryIS.close();
                os.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 用于输出当前类的警告信息
     *
     * @param log 待输出的警告信息
     */
    private static void logW(String log) {
        Log.w(TAG, log);
    }

}