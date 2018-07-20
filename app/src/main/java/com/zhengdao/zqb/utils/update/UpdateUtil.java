package com.zhengdao.zqb.utils.update;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zhengdao.zqb.api.DownloadApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.UpdateInfoEntity;
import com.zhengdao.zqb.entity.Version;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.service.DownloadApkService;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import java.io.File;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhengdao.zqb.config.Constant.Download.APK_FILE_NAME;
import static com.zhengdao.zqb.utils.AppUtils.getVersionCode;

/**
 * @创建者 cairui
 * @创建时间 2017/2/16 15:27
 */
public class UpdateUtil {
    private Context        mContext;
    private UpdateListener mUpdateListener;
    private Subscription   mSubscribe;

    public UpdateUtil(Context context, UpdateListener listener) {
        this.mContext = context;
        this.mUpdateListener = listener;
    }

    public void unSubscripe() {
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
        }
    }

    public void checkUpdate() {
        mSubscribe = RetrofitManager.getInstance().create(DownloadApi.class)
                .getUpdateInfo(getVersionCode(mContext))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UpdateInfoEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mUpdateListener != null)
                            mUpdateListener.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(UpdateInfoEntity result) {
                        if (result.code == Constant.HttpResult.SUCCEED) {
                            if (mUpdateListener != null)
                                mUpdateListener.onSuccess(true, result);
                            if (result.version == null)
                                LogUtils.e(TextUtils.isEmpty(result.msg) ? "" : result.msg);
                        } else {
                            if (mUpdateListener != null)
                                mUpdateListener.onSuccess(false, null);
                        }
                    }
                });
    }

    public interface UpdateListener {
        void onError(String msg);

        void onSuccess(boolean needUpdate, UpdateInfoEntity entity);
    }

    public void update(Version bean, boolean mustUpdate) {
        File outputFile = new File(FileUtils.getDownloadPath(APK_FILE_NAME));
        if (outputFile.exists()) {
            boolean available = AppUtils.checkFile(outputFile, bean.mD5);
            if (available) {
                AppUtils.install(mContext, outputFile);
                if (mustUpdate) {
                    System.exit(0);
                }
                return;
            } else {
                outputFile.delete();
            }
        }
        ToastUtil.showToast(mContext, "正在下载，请稍后");
        Intent intent = new Intent(mContext, DownloadApkService.class);
        intent.putExtra(Constant.Download.URL, bean.downloadApp);
        intent.putExtra(Constant.Download.MD5, bean.mD5);
        intent.putExtra(Constant.Download.MUST_UPDATE, mustUpdate);
        mContext.startService(intent);
    }
}
