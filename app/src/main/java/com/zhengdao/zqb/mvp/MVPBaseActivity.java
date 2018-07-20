package com.zhengdao.zqb.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.CustomProgressDialog;
import com.zhengdao.zqb.customview.SwipeBackActivity.AbsSwipeBackActivity;
import com.zhengdao.zqb.event.NetworkChangeEvent;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;

import java.lang.reflect.ParameterizedType;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class MVPBaseActivity<V extends BaseView, T extends BasePresenterImpl<V>> extends AbsSwipeBackActivity implements BaseView {
    public    T                    mPresenter;
    protected CustomProgressDialog mProgressDialog;
    protected boolean showProgressDialog = true;
    private   Disposable         mDisposable;
    protected InputMethodManager mImm;
    protected View               decorView;
    private   View               mView;
    private   TextView           mTvTitle;
    private   ImageView          mIvBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getInstance(this, 1);
        mPresenter.attachView((V) this);
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog(this);
            mProgressDialog.setMessage("正在加载");
        }
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        decorView = getWindow().getDecorView();
        mDisposable = RxBus.getDefault().toObservable(NetworkChangeEvent.class).subscribe(new Consumer<NetworkChangeEvent>() {
            @Override
            public void accept(NetworkChangeEvent networkChangeEvent) throws Exception {
                onNetworkChange(networkChangeEvent);
            }
        });
    }

    protected void setTitle(String title) {
        initTitle();
        hideSoftInput();
        mTvTitle.setText(title);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MVPBaseActivity.this.finish();
            }
        });
    }

    private void initTitle() {
        mView = findViewById(R.id.re_title_bar);
        if (mView == null)
            mView = LayoutInflater.from(this).inflate(R.layout.title_bar, null);
        mTvTitle = mView.findViewById(R.id.tv_title_bar_title);
        mIvBack = mView.findViewById(R.id.iv_title_bar_back);
    }

    public void hideSoftInput() {
        if (mImm != null && decorView != null) {
            mImm.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
    }

    public void toggleSoftInput() {
        if (mImm != null) {
            mImm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected void onNetworkChange(NetworkChangeEvent networkChangeEvent) {
        if (networkChangeEvent.isNetWorkAvailable) {
            //            ToastUtil.showToast(this,"当前网络可用");
        } else {
            ToastUtil.showToast(this, "当前网络不可用");
        }
        if (networkChangeEvent.isWifiConnected) {
            //            ToastUtil.showToast(this,"当前wifi连接");
        }
        if (networkChangeEvent.is3gConnected) {
            //            ToastUtil.showToast(this,"当前是移动网络连接");
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    public <T> T getInstance(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void backgroundAlpha(float bgAlpha) {
        if (bgAlpha > 1 || bgAlpha < 0)
            return;
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

    @Override
    public void showProgress() {
        if (showProgressDialog) {
            if (mProgressDialog != null) {
                mProgressDialog.show();
            }
        }
    }

    @Override
    public void hideProgress() {
        if (showProgressDialog) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        if (msg != null) {
            LogUtils.e("ErrorMsg:" + msg);
            ToastUtil.showToast(this, "请求出错，请稍后再试");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detachView();
        if (mPresenter != null)
            mPresenter.unsubcrible();
        if (mDisposable != null)
            mDisposable.dispose();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }

    }
}
