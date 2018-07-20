package com.zhengdao.zqb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/28 09:58
 */
public class ImageUtils {

    /**
     * 存放拍摄图片的文件夹
     */
    private static final String FILES_NAME = "/MyPhoto";
    /**
     * 获取的时间格式
     */
    public static final  String TIME_STYLE = "yyyyMMddHHmmss";
    /**
     * 图片种类
     */
    public static final  String IMAGE_TYPE = ".png";

    // 防止实例化
    private ImageUtils() {

    }

    /**
     * 获取手机可存储路径
     *
     * @param context 上下文
     * @return 手机可存储路径
     */
    private static String getPhoneRootPath(Context context) {
        // 是否有SD卡
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                || !Environment.isExternalStorageRemovable()) {
            // 获取SD卡根目录
            return context.getExternalCacheDir().getPath();
        } else {
            // 获取apk包下的缓存路径
            return context.getCacheDir().getPath();
        }
    }

    /**
     * 使用当前系统时间作为上传图片的名称
     *
     * @return 存储的根路径+图片名称
     */
    public static String getPhotoFileName(Context context) {
        File file = new File(getPhoneRootPath(context) + FILES_NAME);
        // 判断文件是否已经存在，不存在则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 设置图片文件名称
        SimpleDateFormat format = new SimpleDateFormat(TIME_STYLE, Locale.getDefault());
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        String photoName = "/" + time + IMAGE_TYPE;
        return file + photoName;
    }


    /**
     * 保存Bitmap图片在SD卡中
     * 如果没有SD卡则存在手机中
     *
     * @param mbitmap 需要保存的Bitmap图片
     * @return 保存成功时返回图片的路径，失败时返回null
     */
    public static String savePhotoToSD(Bitmap mbitmap, Context context) {
        FileOutputStream outStream = null;
        String fileName = getPhotoFileName(context);
        try {
            outStream = new FileOutputStream(fileName);
            // 把数据写入文件，100表示不压缩   PNG 格式很大，无法压缩，使用JPG的
            mbitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (outStream != null) {
                    // 记得要关闭流！
                    outStream.close();
                }
                if (mbitmap != null) {
                    mbitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 把原图按1/10的比例压缩
     *
     * @param path 原图的路径
     * @return 压缩后的图片
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 10; // 图片的大小设置为原来的十分之一
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }

    /**
     * 获取图片的宽高比例
     *
     * @param path
     * @return
     */
    public static int[] getImageScale(String path) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            BitmapFactory.decodeFile(path, options);
            int outHeight = options.outHeight;
            int outWidth = options.outWidth;
            return new int[]{outWidth, outHeight};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{};
    }

    /**
     * 获取图片的宽高比例
     *
     * @param bitmap
     * @return
     */
    public static int[] getImageScale(Bitmap bitmap) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            return new int[]{width, height};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{};
    }

    /**
     * 处理旋转后的图片
     *
     * @param originpath 原图路径
     * @param context    上下文
     * @return 返回修复完毕后的图片路径
     */
    public static String amendRotatePhoto(String originpath, Context context) {

        // 取得图片旋转角度
        int angle = readPictureDegree(originpath);

        // 把原图压缩后得到Bitmap对象
        Bitmap bmp = getCompressPhoto(originpath);
        ;

        // 修复图片被旋转的角度
        Bitmap bitmap = rotaingImageView(angle, bmp);

        // 保存修复后的图片并返回保存后的图片路径
        return savePhotoToSD(bitmap, context);
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }


    public static String compressImage(String filePath, String targetPath, int quality) {
        //gif图不压缩
        if (filePath.contains(".gif")) {
            return filePath;
        }
        Bitmap bm = getSmallBitmap(filePath);//获取一定尺寸的图片
        int degree = readPictureDegree(filePath);//获取相片拍摄角度
        if (degree != 0) {//旋转照片角度，防止头像横着显示
            bm = rotateBitmap(bm, degree);
        }
        File outputFile = new File(targetPath);
        FileOutputStream out = null;
        try {
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
            } else {
                outputFile.delete();
            }
            out = new FileOutputStream(outputFile);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (Exception e) {
            LogUtils.e(e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputFile.getPath();
    }

    /**
     * 根据路径获得图片信息并按比例压缩，返回bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只解析图片边沿，获取宽高
        BitmapFactory.decodeFile(filePath, options);
        // 计算缩放比
        options.inSampleSize = calculateInSampleSize(options, 720, 1280);//400X800
        // 完整解析图片返回bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 获取照片角度
     *
     * @param path
     * @return
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            if (isJpgFile(new File(path))) {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转照片
     *
     * @param bitmap
     * @param degress
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }

    /**
     * 计算压缩尺寸
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 处理图片
     *
     * @param bm        所要转换的bitmap
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @return 指定宽高的bitmap
     */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    /**
     * @param path    原图路径
     * @param offsetX 截取开始点在X轴偏移量
     * @param offsetY 截取开始点在Y轴偏移量
     * @param targetW 截取多宽（像素）
     * @param targetH 截取多高（像素）
     * @return
     */
    public static Bitmap matrixScale(String path, int offsetX, int offsetY, int targetW, int targetH) {
        // 构建原始位图
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        // 获取原始宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算宽高缩放比例，targetW，targetH即期待缩放完成后位图的宽高
        float scaleW = (float) targetW / width;
        float scaleH = (float) targetH / height;
        // 将缩放比例放进矩阵
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        // 这个方法作用非常多，详细解释一下各个参数的意义！
        // bitmap：原始位图
        // 第二到第五个参数，即截取原图哪一部分构建新位图，
        // offsetX和offsetY代表在X轴和Y轴上的像素偏移量，即从哪个位置开始截取
        // width和height代表截取多少个像素，但是要注意，offsetX+width应该小于等于原图的宽度
        // offsetY+height小于等于原图高度，要不然会报错，因为截到原图外面去了
        // 像下面这样填写，就代表截取整个原图，
        // Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
        // 如果填写100,100,200,200，就代表
        // 从原图左上角往右和下各偏移100像素，然后往后和往下各截取200构建新位图
        // matrix：缩放矩阵
        // 最后一个参数表示如果矩阵里面还存放了过滤条件，是否按条件过滤（如果matrix里面只放了平移数据），最后一个参数设置成什么都不会生效
        bitmap = Bitmap.createBitmap(bitmap, offsetX, offsetY, width, height, matrix, false);
        return bitmap;
    }

    /**
     * @param path    图片路径
     * @param quality 质量 0-100,100表示原图
     * @return
     */
    public static Bitmap losslessScale(String path, int quality) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        Log.e("哈哈", "原始大小：" + baos.toByteArray().length);
        // 因为质量压缩不是可以无限缩小的，所以一张高质量的图片，再怎么压缩，
        // 最终size可能还是大于你指定的size，造成异常
        // 所以不建议循环压缩，而是指定quality，进行一次压缩就可以了
        //        while (baos.toByteArray().length / 1024 > maxSize) {
        //            quality -= 10;
        //            baos.reset();
        //            bitmap.compress(CompressFormat.JPEG, quality, baos);
        //            Log.e("哈哈","过程中大小为："
        //                    + baos.toByteArray().length);
        //        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        Log.e("哈哈", "最终大小" + baos.toByteArray().length);
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                baos.toByteArray(), 0, baos.toByteArray().length);
        return compressedBitmap;
    }

    /**
     * 压缩图片
     *
     * @param cropFile
     * @param fileName
     * @return
     */
    public static File compressImage(Context context, File cropFile, String fileName) {
        File targetFile = null;
        try {
            if (FileUtils.isSDCardAvailable()) {
                String sdCardPath = FileUtils.getSDCardPath();
                File fileFolder = new File(sdCardPath + File.separator + "zqb");
                if (!fileFolder.exists())
                    fileFolder.mkdirs();
                targetFile = new File(fileFolder.getAbsolutePath(), fileName);
                if (!targetFile.exists())
                    targetFile.createNewFile();
            } else {
                targetFile = new File(Environment.getDownloadCacheDirectory(), fileName);
                if (!targetFile.exists())
                    targetFile.createNewFile();
            }
            ImageUtils.compressImage(cropFile.getAbsolutePath(), targetFile.getAbsolutePath(), 50);// 0-100 100为不压缩
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    public static boolean isGifFile(File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            int[] flags = new int[5];
            flags[0] = inputStream.read();
            flags[1] = inputStream.read();
            flags[2] = inputStream.read();
            flags[3] = inputStream.read();
            inputStream.skip(inputStream.available() - 1);
            flags[4] = inputStream.read();
            inputStream.close();
            return flags[0] == 71 && flags[1] == 73 && flags[2] == 70 && flags[3] == 56 && flags[4] == 0x3B;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return false;
    }

    public static boolean isJpgFile(File file) {
        try {
            FileInputStream bin = new FileInputStream(file);
            int b[] = new int[4];
            b[0] = bin.read();
            b[1] = bin.read();
            bin.skip(bin.available() - 2);
            b[2] = bin.read();
            b[3] = bin.read();
            bin.close();
            return b[0] == 255 && b[1] == 216 && b[2] == 255 && b[3] == 217;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
