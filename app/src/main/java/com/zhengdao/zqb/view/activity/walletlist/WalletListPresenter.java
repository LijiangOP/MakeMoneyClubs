package com.zhengdao.zqb.view.activity.walletlist;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WalletApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.BalanceDetailEntity;
import com.zhengdao.zqb.entity.BalanceEntity;
import com.zhengdao.zqb.entity.BalanceHttpEntity;
import com.zhengdao.zqb.entity.ChargeRecordEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.IntegralEntity;
import com.zhengdao.zqb.entity.IntegralHttpEntity;
import com.zhengdao.zqb.entity.RebPocketEntity;
import com.zhengdao.zqb.entity.WalletListEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.adapter.WalletListAdapter;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_BROKERAGE;
import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_CHARGERECORD;
import static com.zhengdao.zqb.config.Constant.Wallet.TYPE_INTEGRAL;

public class WalletListPresenter extends BasePresenterImpl<WalletListContract.View> implements WalletListContract.Presenter {

    private ArrayList<Object> mData;
    private WalletListAdapter mAdapter;
    private int mCurrentPage = 1;
    private boolean mIsHasNext;

    @Override
    public void getData(String date, String type) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        if (!TextUtils.isEmpty(type) && type.equals("rebPocket")) {//红包流水
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                    .getRebPocket(date)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HttpResult<RebPocketEntity>>() {
                        @Override
                        public void onCompleted() {
                            mView.hideProgress();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.showErrorMessage(e.getMessage());
                            mView.hideProgress();
                        }

                        @Override
                        public void onNext(HttpResult<RebPocketEntity> httpResult) {
                            mView.hideProgress();
                        }
                    });
            addSubscription(subscribe);
        } else if (!TextUtils.isEmpty(type) && type.equals("integral")) {//积分流水
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                    .getIntegral(date, userToken, mCurrentPage)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<IntegralHttpEntity<IntegralEntity>>() {
                        @Override
                        public void onCompleted() {
                            mView.hideProgress();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.showErrorMessage(e.getMessage());
                            mView.hideProgress();
                        }

                        @Override
                        public void onNext(IntegralHttpEntity<IntegralEntity> httpResult) {
                            mView.hideProgress();
                            buidDataTow(httpResult);
                        }
                    });
            addSubscription(subscribe);
        } else if (!TextUtils.isEmpty(type) && type.equals("brokerage")) { //推广佣金
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                    .getBrokerage(date, "flag", userToken, mCurrentPage)
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
                            mView.showErrorMessage(e.getMessage());
                            mView.hideProgress();
                        }

                        @Override
                        public void onNext(BalanceHttpEntity<BalanceEntity> httpResult) {
                            mView.hideProgress();
                            buidDataOne(httpResult, TYPE_BROKERAGE);
                        }
                    });
            addSubscription(subscribe);
        } else if (!TextUtils.isEmpty(type) && type.equals("charge_record")) {//充值记录
            Subscription subscribe = RetrofitManager.getInstance().noCache().create(WalletApi.class)
                    .getChargeRecord(date, userToken, mCurrentPage)
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            mView.showProgress();
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<HttpResult<ChargeRecordEntity>>() {
                        @Override
                        public void onCompleted() {
                            mView.hideProgress();
                        }

                        @Override
                        public void onError(Throwable e) {
                            mView.showErrorMessage(e.getMessage());
                            mView.hideProgress();
                        }

                        @Override
                        public void onNext(HttpResult<ChargeRecordEntity> httpResult) {
                            mView.hideProgress();
                            buidDataThere(httpResult);
                        }
                    });
            addSubscription(subscribe);
        }
    }

    @Override
    public void getMore(String date, String type) {
        if (mIsHasNext) {
            mCurrentPage++;
            getData(date, type);
        }
    }

    @Override
    public void updataData(String date, String type) {
        mCurrentPage = 1;
        getData(date, type);
    }

    /**
     * 推广佣金 ；吧值
     *
     * @param httpResult
     */
    private void buidDataOne(BalanceHttpEntity<BalanceEntity> httpResult, int type) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mView.showBalanceView(httpResult);
            if (httpResult.data == null || httpResult.data.list == null || httpResult.data.list.size() == 0) {
                mView.noData();
                return;
            }
            mIsHasNext = httpResult.data.hasNextPage;
            ArrayList<BalanceDetailEntity> result = httpResult.data.list;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            for (BalanceDetailEntity entity : result) {
                mData.add(new WalletListEntity(type, entity));
            }
            if (mAdapter == null) {
                mAdapter = new WalletListAdapter(mView.getContext(), mData);
                mView.showListView(mAdapter, mIsHasNext);
            } else {
                mAdapter.notifyDataSetChanged();
                mView.showViewState(mIsHasNext);
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            mView.ReLogin();
        } else {
            mView.showErrorMessage(httpResult.msg);
        }
    }

    /**
     * 积分
     *
     * @param httpResult
     */
    private void buidDataTow(IntegralHttpEntity<IntegralEntity> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.data == null || httpResult.data.list == null || httpResult.data.list.size() == 0) {
                mView.noData();
                return;
            }
            mView.showIntegralView(httpResult);
            mIsHasNext = httpResult.data.hasNextPage;
            ArrayList<IntegralEntity.IntegralDetailEntity> result = httpResult.data.list;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            for (IntegralEntity.IntegralDetailEntity entity : result) {
                mData.add(new WalletListEntity(TYPE_INTEGRAL, entity));
            }
            if (mAdapter == null)
                mAdapter = new WalletListAdapter(mView.getContext(), mData);
            mView.showListView(mAdapter, mIsHasNext);
        } else {
            ToastUtil.showToast(mView.getContext(), TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    /**
     * 充值记录
     *
     * @param httpResult
     */
    private void buidDataThere(HttpResult<ChargeRecordEntity> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.data == null || httpResult.data.list == null || httpResult.data.list.size() == 0) {
                mView.noData();
                return;
            }
            mIsHasNext = httpResult.data.hasNextPage;
            ArrayList<ChargeRecordEntity.ChargeRecordDetailEntity> result = httpResult.data.list;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            for (ChargeRecordEntity.ChargeRecordDetailEntity entity : result) {
                mData.add(new WalletListEntity(TYPE_CHARGERECORD, entity));
            }
            if (mAdapter == null)
                mAdapter = new WalletListAdapter(mView.getContext(), mData);
            mView.showListView(mAdapter, mIsHasNext);
        } else {
            ToastUtil.showToast(mView.getContext(), TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }
}
