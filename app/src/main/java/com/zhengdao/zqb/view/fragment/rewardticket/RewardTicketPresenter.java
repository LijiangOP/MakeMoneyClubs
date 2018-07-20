package com.zhengdao.zqb.view.fragment.rewardticket;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WalletApi;
import com.zhengdao.zqb.entity.RewardTicketHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class RewardTicketPresenter extends BasePresenterImpl<RewardTicketContract.View> implements RewardTicketContract.Presenter {

    @Override
    public void getData(int type, int currentPage) {
        try {
            String userToken = SettingUtils.getUserToken(mView.getContext());
            if (TextUtils.isEmpty(userToken))
                return;
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                    .getTicketData(userToken, type, currentPage).subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<RewardTicketHttpEntity>() {
                        @Override
                        public void onCompleted() {
                            mView.hideProgress();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.hideProgress();
                            mView.showErrorMessage(e.getMessage());
                        }

                        @Override
                        public void onNext(RewardTicketHttpEntity rewardTicketHttpEntity) {
                            mView.hideProgress();
                            mView.onDataGet(rewardTicketHttpEntity);
                        }
                    });
            addSubscription(subscribe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
