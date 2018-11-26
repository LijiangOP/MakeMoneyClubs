package com.zhengdao.zqb.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.zhengdao.zqb.customview.CustomProgressDialog;
import com.zhengdao.zqb.event.RegistSuccessEvent;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.main.MainActivity;

import java.lang.reflect.ParameterizedType;

import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.content.Context.INPUT_METHOD_SERVICE;

public abstract class MVPBaseFragment<V extends BaseView, T extends BasePresenterImpl<V>> extends Fragment implements BaseView {
    public    T                    mPresenter;
    protected boolean              showProgressDialog = true;
    protected CustomProgressDialog mProgressDialog;
    protected Unbinder             mUnbinder;
    private   View                 mRootView;
    private   Disposable           mDisposable;
    protected boolean              mFristShow         = true;
    private   View                 decorView;
    private   InputMethodManager   mImm;
    //因为一般不会调用setUserVisibleHint()方法 所以 默认使isVisible为true
    protected boolean              isVisible          = true;
    private   boolean              isPrepared;
    private   boolean              isFirst            = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            lazyLoad();
        } else {
            isVisible = false;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = getInstance(this, 1);
        mPresenter.attachView((V) this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog(getContext());
            mProgressDialog.setMessage("正在加载");
            mProgressDialog.setCancelable(true);
        }
        if (mRootView == null)
            mRootView = getFragmentView();
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null)
            parent.removeView(mRootView);//防止发生已经有parent的错误
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        decorView = getActivity().getWindow().getDecorView();
        mImm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        isPrepared = true;
        lazyLoad();
    }

    private void lazyLoad() {
        //拦截处理
        boolean isFirst = this.isFirst;
        if (!isPrepared || !isVisible || !this.isFirst) {
            return;
        }
        //在fragment启动时只调用一次
        registListentr();
        initView();
        this.isFirst = false;
    }

    private void registListentr() {
        mDisposable = RxBus.getDefault().toObservable(RegistSuccessEvent.class).subscribe(new Consumer<RegistSuccessEvent>() {
            @Override
            public void accept(RegistSuccessEvent registSuccessEvent) throws Exception {
                MainActivity activity = (MainActivity) getActivity();
                activity.setCurrentPosition(0);
            }
        });
    }

    ;

    protected abstract View getFragmentView();

    protected abstract void initView();

    @Override
    public Context getContext() {
        return super.getContext();
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
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    public static int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    public void hideSoftInput() {
        if (mImm != null && decorView != null) {
            mImm.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
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
            ToastUtil.showToast(getActivity(), "请求出错，请稍后再试");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.detachView();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
        if (mUnbinder != null)
            mUnbinder.unbind();
        if (mRootView != null)
            mRootView = null;
    }

}
