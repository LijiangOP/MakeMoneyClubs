package com.zhengdao.zqb.view.activity.mybalance;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WalletApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.BalanceDetailEntity;
import com.zhengdao.zqb.entity.BalanceEntity;
import com.zhengdao.zqb.entity.BalanceHttpEntity;
import com.zhengdao.zqb.entity.NewBalanceEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.BalanceAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class MyBalancePresenter extends BasePresenterImpl<MyBalanceContract.View> implements MyBalanceContract.Presenter {

    private List<NewBalanceEntity> mData;
    private List<String>           mMonths;
    private int mCurrentPage = 1;
    private boolean        mIsHasNext;
    private BalanceAdapter mAdapter;
    private int            mCurrentType;

    @Override
    public void initData(int type) {
        mCurrentType = type;
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token)) {
            mView.ReLogin();
            return;
        }
        if (mCurrentPage == 1) {
            if (mMonths != null)
                mMonths.clear();
        }
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                .getBalance(mCurrentType, token, mCurrentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<BalanceHttpEntity<BalanceEntity>>() {
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
                    public void onNext(BalanceHttpEntity<BalanceEntity> httpEntity) {
                        mView.hideProgress();
                        initListData(httpEntity);
                    }
                });
        addSubscription(subscribe);
    }

    private void initListData(BalanceHttpEntity<BalanceEntity> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mIsHasNext = httpResult.data.hasNextPage;
            Double usableSum = httpResult.usableSum;
            ArrayList<BalanceDetailEntity> result = httpResult.data.list;
            if (result.size() == 0 || result == null) {
                mView.noData();
                return;
            }
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            addData(result);
            if (mAdapter == null) {
                mAdapter = new BalanceAdapter(mView.getContext(), mData);
                mView.setAdapter(mAdapter, mIsHasNext, usableSum);
            } else {
                mView.updateAdapter(mIsHasNext, usableSum);
                mAdapter.notifyDataSetChanged();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            mView.ReLogin();
        } else
            mView.showErrorMessage(httpResult.msg);
    }

    private void addData(ArrayList<BalanceDetailEntity> result) {
        if (mData != null && result.size() > 0) {
            if (mMonths == null)
                mMonths = new ArrayList<>();
            for (BalanceDetailEntity entity : result) {
                try {
                    String time = new SimpleDateFormat("yyyy-MM").format(entity.addTime);
                    if (!mMonths.contains(time)) {
                        mMonths.add(time);
                        String currentTime = new SimpleDateFormat("yyyy-MM").format(new Date());
                        if (!currentTime.equals(time))
                            mData.add(new NewBalanceEntity(0, time));
                    }
                    mData.add(new NewBalanceEntity(1, entity));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void updataData() {
        mCurrentPage = 1;
        initData(mCurrentType);
    }

    @Override
    public void getMoreData() {
        if (mIsHasNext) {
            mCurrentPage++;
            initData(mCurrentType);
        }
    }
}
