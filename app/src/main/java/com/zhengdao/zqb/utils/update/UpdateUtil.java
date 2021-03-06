package com.zhengdao.zqb.utils.update;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zhengdao.zqb.api.DownloadApi;
import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.UpdateInfoEntity;
import com.zhengdao.zqb.entity.Version;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.service.DownloadApkService;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        //TODO 更新不同类型
        int type;
        switch (ClientAppLike.AppType) {
            case Constant.App.Wlgfl:
                type = 1;
                break;
            case Constant.App.Wlgfl_hy:
                type = 2;
                break;
            case Constant.App.Lczj:
                type = 3;
                break;
            default:
                type = 0;
                break;
        }
        mSubscribe = RetrofitManager.getInstance().create(DownloadApi.class)
                .getUpdateInfo(getVersionCode(mContext), type)
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
        if (FileUtils.isDownloadFileValable()){
            ToastUtil.showToast(ClientAppLike.getContext(), "正在下载，请稍后");
            Intent intent = new Intent(mContext, DownloadApkService.class);
            intent.putExtra(Constant.Download.URL, bean.downloadApp);
            intent.putExtra(Constant.Download.MD5, bean.mD5);
            intent.putExtra(Constant.Download.MUST_UPDATE, mustUpdate);
            mContext.startService(intent);
        }else {
            ToastUtil.showToast(ClientAppLike.getContext(), "无法创建下载文件，更新失败");
        }
    }
}
