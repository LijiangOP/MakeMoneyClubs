package com.zhengdao.zqb.view.activity.about;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.UpdataProgressWindow;
import com.zhengdao.zqb.customview.UpdataWindow;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.UpdateInfoEntity;
import com.zhengdao.zqb.entity.Version;
import com.zhengdao.zqb.event.IsShowProgressEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.CacheUtil;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.update.UpdateUtil;
import com.zhengdao.zqb.view.activity.StatementActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AboutActivity extends MVPBaseActivity<AboutContract.View, AboutPresenter> implements AboutContract.View, View.OnClickListener, UpdateUtil.UpdateListener {

    private static final int ACTION_HANDLER = 007;
    @BindView(R.id.tv_version)
    TextView       mTvVersion;
    @BindView(R.id.ll_check_update)
    LinearLayout   mLlCheckUpdate;
    @BindView(R.id.ll_version_desc)
    LinearLayout   mLlVersionDesc;
    @BindView(R.id.ll_give_a_mark)
    LinearLayout   mLlGiveAMark;
    @BindView(R.id.tv_cache)
    TextView       mTvCache;
    @BindView(R.id.ll_clear_cache)
    LinearLayout   mLlClearCache;
    @BindView(R.id.ll_serve_protocol)
    LinearLayout   mLlServeProtocol;

    private long mCurrentTimeMillis = 0;
    private UpdateUtil           mUpdateUtil;
    private boolean              mMustUpdate;
    private UpdataWindow         mUpdataWindow;
    private Disposable           mIsShowProgressDisposable;
    private UpdataProgressWindow mUpdataProgressWindow;
    private int                  mContentView;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showUpdateProgressDialog();
            float obj = (float) msg.obj;
            if (mUpdataProgressWindow != null)
                mUpdataProgressWindow.setProgress(obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setTitle("关于");
        init();
        setOnclickListener();
    }

    private void init() {
        showCacheSize();
        mTvVersion.setText("当前版本：".concat(AppUtils.getVersionName(this)));
        mUpdateUtil = new UpdateUtil(this, this);
        mLlVersionDesc.setVisibility(View.GONE);

        mIsShowProgressDisposable = RxBus.getDefault().toObservable(IsShowProgressEvent.class).subscribe(new Consumer<IsShowProgressEvent>() {
            @Override
            public void accept(IsShowProgressEvent isShowProgressEvent) throws Exception {
                if (isShowProgressEvent.isShow) {
                    mHandler.sendMessage(mHandler.obtainMessage(ACTION_HANDLER, isShowProgressEvent.progress));
                } else if (mUpdataProgressWindow != null) {
                    mUpdataProgressWindow.dismiss();
                }
            }
        });
    }

    private void setOnclickListener() {
        mLlCheckUpdate.setOnClickListener(this);
        mLlVersionDesc.setOnClickListener(this);
        mLlGiveAMark.setOnClickListener(this);
        mLlClearCache.setOnClickListener(this);
        mLlServeProtocol.setOnClickListener(this);
    }

    private void showCacheSize() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String cacheSize = CacheUtil.getCacheSize();
                subscriber.onNext(cacheSize);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mTvCache.setText(s);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_check_update:
                showProgress();
                mUpdateUtil.checkUpdate();
                break;
            case R.id.ll_version_desc:
                ToastUtil.showToast(this, getResources().getString(R.string.unOpen));
                //                startActivity(new Intent(AboutActivity.this, VersionDescActivity.class));
                break;
            case R.id.ll_give_a_mark:
                doSkipToMarket();
                break;
            case R.id.ll_clear_cache:
                clearCache();
                break;
            case R.id.ll_serve_protocol:
                mPresenter.getData("DISCLAIMER");
                break;
        }
    }

    private void doSkipToMarket() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "您没有安装应用市场", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearCache() {
        mProgressDialog.setMessage("请稍后");
        showProgress();
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                CacheUtil.clearCache();
                subscriber.onNext("0K");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        hideProgress();
                        ToastUtil.showToast(AboutActivity.this, "清除缓存成功！");
                        mTvCache.setText(s);
                    }
                });
    }

    /**
     * 检测更新的回调
     *
     * @param msg
     */
    @Override
    public void onError(String msg) {
        hideProgress();
    }

    /**
     * 检测更新的回调
     *
     * @param needUpdate
     * @param entity
     */
    @Override
    public void onSuccess(boolean needUpdate, UpdateInfoEntity entity) {
        hideProgress();
        if (needUpdate) {
            if (entity != null && entity.version != null)
                showUpdateDialog(entity.version);
            else
                ToastUtil.showToast(AboutActivity.this, TextUtils.isEmpty(entity.msg) ? "已经是最新版本!" : entity.msg);
        }
    }

    private void showUpdateDialog(final Version bean) {
        int updateInstall = bean.updateInstall;
        mMustUpdate = updateInstall == 0 ? false : updateInstall == 1 ? true : false;
        if (mUpdataWindow == null) {
            mUpdataWindow = new UpdataWindow(AboutActivity.this);
            mUpdataWindow.setCanceledOnTouchOutside(false);
            mUpdataWindow.setCancelable(false);
        }
        mUpdataWindow.setPosition(0, -100);
        if (mMustUpdate) {
            mUpdataWindow.initContentView(bean.clientVersion, bean.cotent, true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUpdateUtil.update(bean, mMustUpdate);
                    mUpdataWindow.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUpdataWindow.dismiss();
                }
            });
        } else {
            mUpdataWindow.initContentView(bean.clientVersion, bean.cotent, false, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUpdataWindow.dismiss();
                    mUpdateUtil.update(bean, mMustUpdate);
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUpdataWindow.dismiss();
                }
            });
        }
        mUpdataWindow.show();
    }

    /**
     * 正在下载的进度显示框
     */
    private void showUpdateProgressDialog() {
        if (mUpdataProgressWindow == null) {
            mUpdataProgressWindow = new UpdataProgressWindow(this);
            mUpdataProgressWindow.setCanceledOnTouchOutside(false);
            mUpdataProgressWindow.setCancelable(false);
        }
        mUpdataProgressWindow.setPosition(0, -100);
        mUpdataProgressWindow.show();
    }


    @Override
    public void showView(DictionaryHttpEntity result, String key) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(key) && key.equals("DISCLAIMER")) {
                if (result.types != null && result.types.size() > 0) {
                    String value = result.types.get(0).value;
                    if (!TextUtils.isEmpty(value)) {
                        Intent intent = new Intent(AboutActivity.this, StatementActivity.class);
                        intent.putExtra(Constant.Activity.Data, value);
                        startActivity(intent);
                    }
                }
            }
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(AboutActivity.this, TextUtils.isEmpty(result.msg) ? "数据加载失败" : result.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgress();
        if (mUpdataWindow != null) {
            mUpdataWindow.dismiss();
            mUpdataWindow = null;
        }
        if (null != mIsShowProgressDisposable)
            mIsShowProgressDisposable.dispose();
    }
}
