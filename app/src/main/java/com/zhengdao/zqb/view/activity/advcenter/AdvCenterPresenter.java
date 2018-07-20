package com.zhengdao.zqb.view.activity.advcenter;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WalletApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.AdvHttpEntity;
import com.zhengdao.zqb.entity.AdvRewardEntity;
import com.zhengdao.zqb.entity.WalletListEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.WalletListAdapter;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_ADVERTISEMENT;

public class AdvCenterPresenter extends BasePresenterImpl<AdvCenterContract.View> implements AdvCenterContract.Presenter {

    private int mCurrentPage = 1;
    private boolean                     mIsHasNext;
    private ArrayList<WalletListEntity> mData;
    private WalletListAdapter           mAdapter;

    @Override
    public void initData() {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken)) {
            mView.ReLogin();
            return;
        }
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                .getAdvData(userToken, mCurrentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AdvHttpEntity>() {
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
                    public void onNext(AdvHttpEntity httpEntity) {
                        mView.hideProgress();
                        initListData(httpEntity);
                    }
                });
        addSubscription(subscribe);
    }

    private void initListData(AdvHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mIsHasNext = httpResult.advertRecords.hasNextPage;
            ArrayList<AdvRewardEntity> result = httpResult.advertRecords.list;
            if (result.size() == 0 || result == null) {
                mView.noData();
                return;
            }
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            for (AdvRewardEntity entity : result) {
                mData.add(new WalletListEntity(TYPE_ADVERTISEMENT, entity));
            }
            if (mAdapter == null) {
                mAdapter = new WalletListAdapter(mView.getContext(), mData);
                mView.setAdapter(mAdapter, mIsHasNext,httpResult.advertAmount);
            } else {
                mView.updateAdapter(mIsHasNext,httpResult.advertAmount);
                mAdapter.notifyDataSetChanged();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            mView.ReLogin();
        } else
            mView.showErrorMessage(httpResult.msg);
    }

    @Override
    public void updataData() {
        mCurrentPage = 1;
        initData();
    }

    @Override
    public void getMoreData() {
        if (mIsHasNext) {
            mCurrentPage++;
            initData();
        }
    }
}
