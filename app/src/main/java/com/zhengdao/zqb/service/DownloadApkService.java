package com.zhengdao.zqb.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.event.Download;
import com.zhengdao.zqb.event.IsShowProgressEvent;
import com.zhengdao.zqb.receiver.AppUpdateNotificationReceiver;
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

import static com.zhengdao.zqb.config.Constant.Download.MUST_UPDATE;

/**
 * @创建者 cairui
 * @创建时间 2017/2/16 15:57
 */
public class DownloadApkService extends IntentService {
    private static final String TAG = "DownloadApkService";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager        notificationManager;
    private String                     mMd5;
    private boolean                    mMustUpdate;

    public DownloadApkService() {
        super("DownloadApkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notifycation_download)
                .setContentTitle(getString(R.string.app_name).concat("更新"))
                .setContentText("正在更新")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        String apkUrl = intent.getStringExtra(Constant.Download.URL);
        mMd5 = intent.getStringExtra(Constant.Download.MD5);
        mMustUpdate = intent.getBooleanExtra(MUST_UPDATE, false);
        try {
            download(apkUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void download(String apkUrl) throws Exception {
        DownloadProgressListener listener = new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                Download download = new Download();
                download.setTotalFileSize(contentLength);
                download.setCurrentFileSize(bytesRead);
                int progress = (int) ((bytesRead * 100) / contentLength);
                download.setProgress(progress);
                sendNotification(download);
                RxBus.getDefault().post(new IsShowProgressEvent(true, (float) progress));
            }
        };
        File updataApkFilePath = FileUtils.getUpdataApkFilePath();
        if (!updataApkFilePath.exists()) {
            ToastUtil.showToast(ClientAppLike.getContext(), "无法创建下载文件，更新失败");
            return;
        }
        new DownloadAPI(Constant.Url.BASEURL, listener).downloadAPK(apkUrl, updataApkFilePath, new Subscriber() {
            @Override
            public void onCompleted() {
                if (AppUtils.checkFile(FileUtils.getUpdataApkFilePath(), mMd5))
                    downloadCompleted();
                else
                    downloadError();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                downloadError();
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    private void downloadCompleted() {
        //发送关闭下载进度显示框的事件
        RxBus.getDefault().post(new IsShowProgressEvent(false));

        //下载完成后的点击事件
        int id = (int) (System.currentTimeMillis() / 1000);
        Intent clickIntent = new Intent(this, AppUpdateNotificationReceiver.class); //点击通知之后要发送的广播
        PendingIntent broadcast = PendingIntent.getBroadcast(this, id, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //更新notification
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("应用下载完成");
        notificationBuilder.setContentIntent(broadcast);
        notificationManager.notify(0, notificationBuilder.build());
        ToastUtil.showToast(ClientAppLike.getContext(), "应用下载成功！");
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        AppUtils.install(ClientAppLike.getContext(), FileUtils.getUpdataApkFilePath());
                        if (mMustUpdate) {
                            System.exit(0);
                        }
                    }
                });
    }

    private void downloadError() {
        ToastUtil.showToast(ClientAppLike.getContext(), "应用下载失败,请重新下载");
        notificationManager.cancel(0);
        RxBus.getDefault().post(new IsShowProgressEvent(false));
    }

    /**
     * 更新进度
     *
     * @param download
     */
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
