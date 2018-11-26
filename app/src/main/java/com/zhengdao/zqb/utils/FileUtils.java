package com.zhengdao.zqb.utils;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.zhengdao.zqb.application.ClientAppLike;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.zhengdao.zqb.application.ClientAppLike.APK_FILE_NAME;

public class FileUtils {
    protected static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 判断SD卡是否可用
     *
     * @return
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }


    /**
     * 获取文件大小
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            //            return size + "Byte";
            return "0K";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return
     */
    public static long getSDFreeSize() {
        if (isSDCardAvailable()) {
            StatFs statFs = new StatFs(getSDCardPath());

            long blockSize = statFs.getBlockSize();

            long freeBlocks = statFs.getAvailableBlocks();
            return freeBlocks * blockSize;
        }

        return 0;
    }

    /**
     * 获取SD卡的总容量
     *
     * @return
     */
    public static long getSDAllSize() {
        if (isSDCardAvailable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            return freeBlocks * availableBlocks;
        }
        return 0;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 拷贝文件，通过返回值判断是否拷贝成功
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        boolean isOK = false;
        if (!TextUtils.isEmpty(sourcePath) && !TextUtils.isEmpty(targetPath)) {
            File sourcefile = new File(sourcePath);
            File targetFile = new File(targetPath);
            if (!sourcefile.exists()) {
                return false;
            }
            if (sourcefile.isDirectory()) {
                isOK = copyDir(sourcefile, targetFile);
            } else if (sourcefile.isFile()) {
                if (!targetFile.exists()) {
                    createFile(targetPath);
                }
                FileOutputStream outputStream = null;
                FileInputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(sourcefile);
                    outputStream = new FileOutputStream(targetFile);
                    byte[] bs = new byte[1024];
                    int len;
                    while ((len = inputStream.read(bs)) != -1) {
                        outputStream.write(bs, 0, len);
                    }
                    isOK = true;
                } catch (Exception e) {
                    LogUtils.i(e.getLocalizedMessage());
                    LogUtils.i(e.getLocalizedMessage());
                    isOK = false;
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            LogUtils.i(e.getLocalizedMessage());
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            LogUtils.i(e.getLocalizedMessage());
                        }
                    }
                }
            }

            return isOK;
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
            try {
                file.delete();
            } catch (Exception e) {
                LogUtils.i(e.getLocalizedMessage());
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 统计文件夹文件的大小
     */
    public static long getSize(File file) {
        // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小
            if (!file.isFile()) {
                // 获取文件大小
                File[] fl = file.listFiles();
                long ss = 0;
                for (File f : fl)
                    ss += getSize(f);
                return ss;
            } else {
                long ss = (long) file.length();
                return ss; // 单位制bytes
            }
        } else {
            // System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0;
        }
    }

    /**
     * 把bytes转换成MB
     */
    public static String getTrafficStr(long total) {
        DecimalFormat format = new DecimalFormat("##0.0");
        if (total < 1024 * 1024) {
            return format.format(total / 1024f) + "KB";
        } else if (total < 1024 * 1024 * 1024) {
            return format.format(total / 1024f / 1024f) + "MB";
        } else if (total < 1024 * 1024 * 1024 * 1024) {
            return format.format(total / 1024f / 1024f / 1024f) + "GB";
        } else {
            return "统计错误";
        }
    }

    /**
     * 删除文件夹里面的所以文件
     */
    public static void deleteDir(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    files[i].delete();
                } else {
                    deleteDir(files[i]);
                }
            }
        }
    }

    /**
     * 剪切文件，将文件拷贝到目标目录，再将源文件删除
     *
     * @param sourcePath
     * @param targetPath
     */
    public static boolean cutFile(String sourcePath, String targetPath) {
        boolean isSuccessful = copyFile(sourcePath, targetPath);
        if (isSuccessful) {
            // 拷贝成功则删除源文件
            return deleteFile(sourcePath);
        }
        return false;
    }

    /**
     * 拷贝目录
     *
     * @param sourceFile
     * @param targetFile
     * @return
     */
    public static boolean copyDir(File sourceFile, File targetFile) {
        if (sourceFile == null || targetFile == null) {
            return false;
        }
        if (!sourceFile.exists()) {
            return false;
        }
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        // 获取目录下所有文件和文件夹的列表
        File[] files = sourceFile.listFiles();
        if (files == null || files.length < 1) {
            return false;
        }
        File file = null;
        StringBuffer buffer = new StringBuffer();
        boolean isSuccessful = false;
        // 遍历目录下的所有文件文件夹，分别处理
        for (int i = 0; i < files.length; i++) {
            file = files[i];
            buffer.setLength(0);
            buffer.append(targetFile.getAbsolutePath()).append(File.separator).append(file.getName());
            if (file.isFile()) {
                // 文件直接调用拷贝文件方法
                isSuccessful = copyFile(file.getAbsolutePath(), buffer.toString());
                if (!isSuccessful) {
                    return false;
                }
            } else if (file.isDirectory()) {
                // 目录再次调用拷贝目录方法
                copyDir(file, new File(buffer.toString()));
            }

        }
        return true;
    }

    /**
     * 剪切目录，先将目录拷贝完后再删除源目录
     *
     * @param sourceDir
     * @param targetDir
     * @return
     */
    public static boolean cutDir(String sourceDir, String targetDir) {
        File sourceFile = new File(sourceDir);
        File targetFile = new File(targetDir);
        boolean isCopySuccessful = copyDir(sourceFile, targetFile);
        if (isCopySuccessful) {
            return deleteDir(sourceDir);
        }
        return false;
    }

    /**
     * 删除目录
     *
     * @param dir
     * @return
     */
    public static boolean deleteDir(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            return false;
        }
        File[] files = file.listFiles();
        boolean isSuccessful = false;
        if (files.length == 0) {
            file.delete();
            return true;
        }
        // 对所有列表中的路径进行判断是文件还是文件夹
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                isSuccessful = deleteDir(files[i].getAbsolutePath());
            } else if (files[i].isFile()) {
                isSuccessful = deleteFile(files[i].getAbsolutePath());
            }

            if (!isSuccessful) {
                // 如果有删除失败的情况直接跳出循环
                break;
            }
        }
        if (isSuccessful) {
            file.delete();
        }
        return isSuccessful;
    }

    /**
     * 将流写入指定文件
     *
     * @param inputStream
     * @param file
     * @return
     */
    public static boolean stream2File(InputStream inputStream, File file) {
        boolean isSuccessful = true;
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        //		FileOutputStream fileOutputStream = null;
        try {
            if (!file.exists()) {
                File file2 = file.getParentFile();
                file2.mkdirs();
                file.createNewFile();
            }
            bis = new BufferedInputStream(inputStream);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            //			fileOutputStream = new FileOutputStream(file);
            byte[] bs = new byte[1024 * 8];
            int length = 0;
            while ((length = bis.read(bs)) != -1) {
                bos.write(bs, 0, length);
            }
        } catch (Exception e) {
            LogUtils.i(e.getLocalizedMessage());
            isSuccessful = false;
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                LogUtils.i(e.getLocalizedMessage());
            }
        }
        return isSuccessful;
    }

    public static String saveBitmap(Bitmap bm) {
        File file;
        if (isSDCardAvailable()) {
            file = Environment.getExternalStorageDirectory();
        } else {
            file = Environment.getDataDirectory();
        }
        file = new File(file.getPath());
        if (!file.isDirectory()) {
            file.delete();
            file.mkdirs();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        writeBitmap(file.getPath(), String.valueOf(System.currentTimeMillis()), bm);
        return new File(file, String.valueOf(System.currentTimeMillis())).getAbsolutePath();
    }

    public static void writeBitmap(String path, String name, Bitmap bitmap) {
        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        File _file = new File(path + name);
        if (_file.exists()) {
            _file.delete();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(_file);
            if (name != null && !"".equals(name)) {
                int index = name.lastIndexOf(".");
                if (index != -1 && (index + 1) < name.length()) {
                    String extension = name.substring(index + 1).toLowerCase();
                    if ("png".equals(extension)) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } else if ("jpg".equals(extension)
                            || "jpeg".equals(extension)) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建目录
     *
     * @param path
     */
    public static void createDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 修改文件读写权限
     *
     * @param fileAbsPath
     * @param mode
     */
    public static void chmodFile(String fileAbsPath, String mode) {
        String cmd = "chmod " + mode + " " + fileAbsPath;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            LogUtils.i(e.getLocalizedMessage());
        }
    }

    /**
     * 将object对象写入outFile文件
     *
     * @param outFile
     * @param object
     * @param context
     */
    public static void writeObject2File(String outFile, Object object, Context context) {
        ObjectOutputStream out = null;
        FileOutputStream outStream = null;
        try {
            File dir = context.getDir("cache", Context.MODE_PRIVATE);
            outStream = new FileOutputStream(new File(dir, outFile));
            out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            out.writeObject(object);
            out.flush();
        } catch (Exception e) {
            LogUtils.i(e.getLocalizedMessage());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    LogUtils.i(e.getLocalizedMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LogUtils.i(e.getLocalizedMessage());
                }
            }
        }
    }

    /**
     * 从outFile文件读取对象
     *
     * @param filePath
     * @param context
     * @return
     */
    public static Object readObjectFromPath(String filePath, Context context) {
        Object object = null;
        ObjectInputStream in = null;
        FileInputStream inputStream = null;
        try {
            File dir = context.getDir("cache", Context.MODE_PRIVATE);
            File f = new File(dir, filePath);
            if (f == null || !f.exists()) {
                return null;
            }
            inputStream = new FileInputStream(new File(dir, filePath));
            in = new ObjectInputStream(new BufferedInputStream(inputStream));
            object = in.readObject();
        } catch (Exception e) {
            LogUtils.i(e.getLocalizedMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LogUtils.i(e.getLocalizedMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogUtils.i(e.getLocalizedMessage());
                }
            }

        }
        return object;
    }

    /**
     * 读取指定路径下的文件内容
     *
     * @param path
     * @return 文件内容
     */
    public static String readFile(String path) {
        BufferedReader br = null;
        try {
            File myFile = new File(path);
            br = new BufferedReader(new FileReader(myFile));
            StringBuffer sb = new StringBuffer();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            LogUtils.i(e.getLocalizedMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LogUtils.i(e.getLocalizedMessage());
                }
            }
        }
        return null;
    }

    /**
     * 创建文件，并修改读写权限
     *
     * @param filePath
     * @param mode
     * @return
     */
    public static File createFile(String filePath, String mode) {
        File desFile = null;
        try {
            String desDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
            File dir = new File(desDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            chmodFile(dir.getAbsolutePath(), mode);
            desFile = new File(filePath);
            if (!desFile.exists()) {
                desFile.createNewFile();
            }
            chmodFile(desFile.getAbsolutePath(), mode);
        } catch (Exception e) {
            LogUtils.i(e.getLocalizedMessage());
        }
        return desFile;
    }

    /**
     * 根据指定路径，创建父目录及文件
     *
     * @param filePath
     * @return File 如果创建失败的话，返回null
     */
    public static File createFile(String filePath) {
        return createFile(filePath, "755");
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 获取外部存储路径
     *
     * @return
     */
    public static String getExternalStorageDirectoryPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取手机默认下载路径，并生成文件
     *
     * @param fileName 文件名
     * @return
     */
    public static String getDownloadPath(String fileName) {
        File file;
        if (isSDCardAvailable()) {
            file = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), fileName);
        } else {
            file = new File(Environment.getDownloadCacheDirectory(), fileName);
        }
        return file.getAbsolutePath();
    }

    /**
     * app更新Apk文件路径是否可用
     *
     * @return
     */

    public static boolean isDownloadFileValable() {
        String downLoadCacheDirectory = ClientAppLike.getInstance().getDownLoadCacheDirectory();
        File dir = new File(downLoadCacheDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File outputFile = new File(dir, APK_FILE_NAME);
        if (outputFile != null) {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static File getUpdataApkFilePath() {
        File outputFile = new File(ClientAppLike.getInstance().getDownLoadCacheDirectory(), APK_FILE_NAME);
        return outputFile;
    }

    /**
     * 获取手机默认下载路径，并生成文件(APK)
     *
     * @param fileName
     * @return
     */
    public static String getAppDownloadPath(String fileName) {
        File file;
        if (TextUtils.isEmpty(fileName))
            return "";
        if (isSDCardAvailable() && fileName != null) {
            if (fileName.contains(".apk"))
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            else
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName + ".apk");
        } else {
            if (fileName.contains(".apk"))
                file = new File(Environment.getDownloadCacheDirectory(), fileName);
            else
                file = new File(Environment.getDownloadCacheDirectory(), fileName + ".apk");
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取默认的图片下载地址，并生成文件
     *
     * @param fileName 文件名
     * @return
     */
    public static String getFilePath(String fileName) {
        File file;
        if (isSDCardAvailable()) {
            file = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES), fileName);
        } else {
            file = new File(Environment.getDownloadCacheDirectory(), fileName);
        }
        return file.getAbsolutePath();
    }

    /**
     * 检查创建目录
     *
     * @param path 路径
     * @return
     */
    public static boolean checkAndCreateDirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            int y = path.lastIndexOf('.');
            int x = path.lastIndexOf('/');
            if (y > 0 && y > x) {
                String parentDir = path.substring(0, x);
                return new File(parentDir).mkdirs();
            } else {
                return file.mkdirs();
            }
        }
        return true;
    }

    /**
     * 根据uri获取路径
     *
     * @param context
     * @param imageUri
     * @return
     */
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static void deleteZqbCacheImg() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zqb");
                circulationDeleteFile(dir);
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        }
    }

    public static void circulationDeleteFile(File file) throws Exception {
        if (file == null || !file.exists() || !file.isDirectory())
            return;
        for (File childfile : file.listFiles()) {
            if (childfile.isFile())
                childfile.delete(); // 删除所有文件
            else if (childfile.isDirectory())
                circulationDeleteFile(childfile); // 递规的方式删除文件夹
        }
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}

