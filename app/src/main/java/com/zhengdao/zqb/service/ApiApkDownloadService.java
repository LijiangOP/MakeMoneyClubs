package com.zhengdao.zqb.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.event.Download;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.BaiDuAPiAdvUtils;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.JinChengUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.update.DownloadAPI;
import com.zhengdao.zqb.utils.update.DownloadProgressListener;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/5 15:04
 */
public class ApiApkDownloadService extends IntentService {

    private NotificationManager        notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private String                     mUrl;//下载路径
    private ArrayList<String>          mD_rpt;//开始下载的上报
    private ArrayList<String>          mDC_rpt;//完成下载的上报
    private String                     mReplace;//要替换掉上报数据中的"SZST_CLID"的数据
    private int                        mType;//2 金橙广告

    public ApiApkDownloadService() {
        super("ApiApkDownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notifycation_download)
                .setContentTitle("应用正在下载")
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());
        mUrl = intent.getStringExtra(Constant.Activity.Data);
        mD_rpt = intent.getStringArrayListExtra(Constant.Activity.Data1);
        mDC_rpt = intent.getStringArrayListExtra(Constant.Activity.Data2);
        mReplace = intent.getStringExtra(Constant.Activity.Replace);
        mType = intent.getIntExtra(Constant.Activity.Type, 0);
        try {
            download();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void download() {
        if (TextUtils.isEmpty(mUrl))
            return;
        //进度条
        DownloadProgressListener listener = new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                Download download = new Download();
                download.setTotalFileSize(contentLength);
                download.setCurrentFileSize(bytesRead);
                int progress = (int) ((bytesRead * 100) / contentLength);
                download.setProgress(progress);
                sendNotification(download);
            }
        };
        final int i = mUrl.hashCode();
        File outputFile = new File(FileUtils.getAppDownloadPath("" + i));
        if (outputFile != null && outputFile.exists()) {
            outputFile.delete();
        }
        new DownloadAPI(Constant.Url.BASEURL, listener).downloadAPK(mUrl, outputFile, new Subscriber() {
            @Override
            public void onCompleted() {
                downloadCompleted(0, "" + i);
                if (mDC_rpt != null && mDC_rpt.size() > 0) {
                    for (String value : mDC_rpt) {
                        if (!TextUtils.isEmpty(value)) {
                            if (mType == 2) {
                                JinChengUtils.ReportAdv(ApiApkDownloadService.this, value);
                            } else {
                                if (value.contains("SZST_CLID") && !TextUtils.isEmpty(mReplace))
                                    value = value.replace("SZST_CLID", mReplace);
                                BaiDuAPiAdvUtils.ReportAdv(value);
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                downloadCompleted(1, "" + i);
            }

            @Override
            public void onNext(Object o) {
                ToastUtil.showToast(ApiApkDownloadService.this, "应用正在下载...");
                if (mD_rpt != null && mD_rpt.size() > 0) {
                    for (String value : mD_rpt) {
                        if (!TextUtils.isEmpty(value)) {
                            if (mType == 2) {
                                JinChengUtils.ReportAdv(ApiApkDownloadService.this, value);
                            } else {
                                if (value.contains("SZST_CLID") && !TextUtils.isEmpty(mReplace))
                                    value = value.replace("SZST_CLID", mReplace);
                                BaiDuAPiAdvUtils.ReportAdv(value);
                            }
                        }
                    }
                }
            }
        });
    }

    private void downloadCompleted(int state, final String name) {
        try {
            if (state == 0) {
                notificationManager.cancel(0);
                notificationBuilder.setProgress(0, 0, false);
                notificationBuilder.setContentText("应用下载完成");
                notificationManager.notify(0, notificationBuilder.build());
                ToastUtil.showToast(this, "应用下载完成");
                Observable.timer(100, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long aLong) {
                                File file = new File(FileUtils.getAppDownloadPath(name));
                                String packageNameByFile = AppUtils.getPackageNameByFile(ApiApkDownloadService.this, file);
                                if (null != file && file.exists()) {
                                    AppUtils.install(ClientAppLike.getContext(), file, packageNameByFile);
                                } else {
                                    ToastUtil.showToast(ClientAppLike.getContext(), "应用校验失败！请重新下载！");
                                }
                            }
                        });
            } else {
                notificationManager.cancel(0);
                notificationBuilder.setProgress(0, 0, false);
                notificationBuilder.setContentText("应用下载出错");
                notificationManager.notify(0, notificationBuilder.build());
                ToastUtil.showToast(this, "应用下载出错,请重新下载");
                File file = new File(FileUtils.getAppDownloadPath(name));
                if (file != null && file.exists())
                    file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(Download download) {
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(
                FileUtils.getFormatSize(download.getCurrentFileSize()) + "/" +
                        FileUtils.getFormatSize(download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }
}
