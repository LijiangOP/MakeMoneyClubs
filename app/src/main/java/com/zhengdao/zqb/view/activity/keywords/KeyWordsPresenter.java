package com.zhengdao.zqb.view.activity.keywords;

import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class KeyWordsPresenter extends BasePresenterImpl<KeyWordsContract.View> implements KeyWordsContract.Presenter {
    @Override
    public void getData(final String key) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getTypesByKey(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DictionaryHttpEntity>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onNext(DictionaryHttpEntity result) {
                        mView.showView(result, key);
                    }
                });
        addSubscription(subscribe);
    }
}
