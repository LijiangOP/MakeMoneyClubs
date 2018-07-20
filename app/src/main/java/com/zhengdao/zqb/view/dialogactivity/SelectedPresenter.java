package com.zhengdao.zqb.view.dialogactivity;

import android.content.Context;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
import com.zhengdao.zqb.manager.RetrofitManager;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/29 16:01
 */
class SelectedPresenter {
    private Context               mContext;
    private ISelectedActivityView mView;
    private Subscription          mSubscribe;

    public SelectedPresenter(Context context, ISelectedActivityView iSelectedActivityView) {
        mContext = context;
        mView = iSelectedActivityView;
    }


    public void getData() {
        mSubscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getSelectedData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ScreenLoadEntity>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(ScreenLoadEntity result) {
                        mView.showView(result);
                    }
                });
    }

    public void onDestory() {
        if (mSubscribe != null)
            mSubscribe.unsubscribe();
    }
}
