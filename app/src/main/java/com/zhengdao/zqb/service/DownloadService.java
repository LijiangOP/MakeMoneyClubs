package com.zhengdao.zqb.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HotRecommendEntity;
import com.zhengdao.zqb.event.DownLoadEvent;
import com.zhengdao.zqb.event.Download;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.update.DownloadAPI;
import com.zhengdao.zqb.utils.update.DownloadProgressListener;

import java.io.File;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/11 17:28
 */
public class DownloadService extends IntentService {

    private NotificationManager        notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private int                        mPosition;
    private HotRecommendEntity         mHotRecommendEntity;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notifycation_download)
                .setContentTitle("应用正在下载")
                .setAutoCancel(true);

        notificationManager.notify(0, notificationBuilder.build());
        mPosition = intent.getIntExtra(Constant.Download.POSITION, 0);
        mHotRecommendEntity = (HotRecommendEntity) intent.getSerializableExtra(Constant.Activity.Data);
        try {
            download();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void download() {
        if (mHotRecommendEntity == null)
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
        File outputFile = new File(FileUtils.getAppDownloadPath(mHotRecommendEntity.appName));
        if (outputFile != null && outputFile.exists()) {
            outputFile.delete();
        }
        new DownloadAPI(Constant.Url.BASEURL, listener).downloadAPK(mHotRecommendEntity.download, outputFile, new Subscriber() {
            @Override
            public void onCompleted() {
                downloadCompleted(0);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                downloadCompleted(1);
            }

            @Override
            public void onNext(Object o) {
                ToastUtil.showToast(DownloadService.this, "应用正在下载...");
            }
        });
    }

    private void downloadCompleted(int state) {
        try {
            RxBus.getDefault().post(new DownLoadEvent(mHotRecommendEntity.id, mPosition, state, mHotRecommendEntity.packageName));
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
                                File file = new File(FileUtils.getAppDownloadPath(mHotRecommendEntity.appName));
                                if (null != file && file.exists()) {
                                    AppUtils.install(ClientAppLike.getContext(), file, mHotRecommendEntity.packageName);
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
                File file = new File(FileUtils.getAppDownloadPath(mHotRecommendEntity.appName));
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
