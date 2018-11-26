package com.zhengdao.zqb.view.fragment.couponsdetail;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.Coupons;
import com.zhengdao.zqb.entity.CouponsEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.view.adapter.CouponsAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class CouponsdetailPresenter extends BasePresenterImpl<CouponsdetailContract.View> implements CouponsdetailContract.Presenter, CouponsAdapter.ItemSearchCallBack {

    private List<CouponsEntity> mData;
    private int mCurrentPage = 1;
    private boolean        mIsHasNext;
    private CouponsAdapter mAdapter;
    private String mSortName  = "";
    private String mSortOrder = "";
    private String mGoodsName = "";

    @Override
    public void initData(int type) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getCouponsData(mSortName, mSortOrder, mGoodsName, type, mCurrentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<Coupons>>() {
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
                    public void onNext(HttpResult<Coupons> httpResult) {
                        mView.hideProgress();
                        initListData(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    private void initListData(HttpResult<Coupons> httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                mIsHasNext = httpResult.data.hasNextPage;
                ArrayList<CouponsEntity> result = httpResult.data.list;
                if ((result.size() == 0 || result == null) && mCurrentPage == 1) {
                    mView.noData();
                    return;
                }
                //集合和头部
                if (mData == null)
                    mData = new ArrayList<>();
                if (mCurrentPage == 1) {
                    mData.clear();
                }
                //商品
                mData.addAll(result);
                if (mAdapter == null) {
                    mAdapter = new CouponsAdapter(mView.getContext(), mData, this);
                    mView.setAdapter(mAdapter, mIsHasNext);
                } else {
                    mView.updateAdapter(mIsHasNext);
                    mAdapter.notifyDataSetChanged();
                }
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                mView.ReLogin();
            } else
                mView.showErrorMessage(httpResult.msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updataData(int type) {
        mCurrentPage = 1;
        initData(type);
    }

    @Override
    public void getMoreData(int type) {
        if (mIsHasNext) {
            mCurrentPage++;
            initData(type);
        }
    }

    @Override
    public void getDataWithSearch(int type, String value) {
        mCurrentPage = 1;
        mGoodsName = value;
        initData(type);
    }

    @Override
    public void getDataWithSort(int type, String sortName, String orderType) {
        mCurrentPage = 1;
        mSortName = sortName;
        mSortOrder = orderType;
        initData(type);
    }

    @Override
    public void search(String value) {
        LogUtils.i(TextUtils.isEmpty(value) ? "" : value);
    }
}
