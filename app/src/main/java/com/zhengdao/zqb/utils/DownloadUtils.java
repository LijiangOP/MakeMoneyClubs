package com.zhengdao.zqb.utils;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HotRecommendEntity;
import com.zhengdao.zqb.event.DownLoadEvent;
import com.zhengdao.zqb.service.ApiApkDownloadService;
import com.zhengdao.zqb.service.DownloadService;

import java.io.File;
import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/14 09:30
 */
public class DownloadUtils {

    /**
     * 下载APK
     *
     * @param context
     * @param entity
     */
    public static void downLoadApk(Context context, HotRecommendEntity entity, int position) {
        try {
            File outputFile = new File(FileUtils.getAppDownloadPath(entity.appName));
            if (outputFile != null && outputFile.exists() && AppUtils.checkFile(outputFile, entity.mD5)) {
                AppUtils.install(ClientAppLike.getContext(), outputFile, entity.packageName);
                RxBus.getDefault().post(new DownLoadEvent(entity.id, position, 0, entity.packageName));
            } else {
                Intent intent = new Intent(context, DownloadService.class);
                intent.putExtra(Constant.Activity.Data, entity);
                intent.putExtra(Constant.Download.POSITION, position);
                context.startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制服务
     *
     * @param context
     * @param conn
     * @param entity
     */
    public static void downLoadApk(Context context, ServiceConnection conn, HotRecommendEntity entity, int position) {
        File outputFile = new File(FileUtils.getDownloadPath(entity.appName));
        if (outputFile.exists()) {
            outputFile.delete();
        }
        ToastUtil.showToast(context, "正在下载...");
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(Constant.Download.ID, position);
        intent.putExtra(Constant.Download.MD5, entity.mD5);
        intent.putExtra(Constant.Download.URL, entity.download);
        intent.putExtra(Constant.Download.FILE_NAME, entity.appName);
        intent.putExtra(Constant.Download.PACKAGE_NAME, entity.packageName);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 下载APK(只有一个下载链接)
     *
     * @param context
     * @param url     下载url
     * @param d_rpt   开始下载上报数据
     * @param dc_rpt  下载完成上报数据
     * @param clickId 需要替换掉上报数据中的"SZST_CLID"的数据
     */
    public static void downLoadApk(Context context, String url, ArrayList<String> d_rpt, ArrayList<String> dc_rpt, String clickId) {
        try {
            Intent intent = new Intent(context, ApiApkDownloadService.class);
            intent.putExtra(Constant.Activity.Data, url);
            intent.putStringArrayListExtra(Constant.Activity.Data1, d_rpt);
            intent.putStringArrayListExtra(Constant.Activity.Data2, dc_rpt);
            intent.putExtra(Constant.Activity.Replace, clickId);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载APK  金橙广告
     *
     * @param context
     * @param url     下载url
     * @param d_rpt   开始下载上报数据
     * @param dc_rpt  下载完成上报数据
     */
    public static void downLoadApk(Context context, String url, ArrayList<String> d_rpt, ArrayList<String> dc_rpt) {
        try {
            Intent intent = new Intent(context, ApiApkDownloadService.class);
            intent.putExtra(Constant.Activity.Data, url);
            intent.putStringArrayListExtra(Constant.Activity.Data1, d_rpt);
            intent.putStringArrayListExtra(Constant.Activity.Data2, dc_rpt);
            intent.putExtra(Constant.Activity.Type, 2);
            context.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
