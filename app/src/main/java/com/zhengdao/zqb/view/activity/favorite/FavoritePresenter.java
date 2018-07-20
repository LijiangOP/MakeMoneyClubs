package com.zhengdao.zqb.view.activity.favorite;

import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ScanInfoDetailBean;
import com.zhengdao.zqb.entity.ScanInfoDetailEntity;
import com.zhengdao.zqb.entity.ScanInfoEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.MyBrowsingHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class FavoritePresenter extends BasePresenterImpl<FavoriteContract.View> implements FavoriteContract.Presenter {

    private List<ScanInfoDetailBean> mData;
    private int mCurrentPage = 1;
    private boolean                  mIsHasNext;
    private MyBrowsingHistoryAdapter mAdapter;

    @Override
    public void initData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getScanInfo(SettingUtils.getUserToken(mView.getContext()), "collection", mCurrentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<ScanInfoEntity>>() {
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
                    public void onNext(HttpResult<ScanInfoEntity> httpResult) {
                        mView.hideProgress();
                        initListData(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    private void initListData(HttpResult<ScanInfoEntity> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.data == null || httpResult.data.list == null || httpResult.data.list.size() == 0) {
                mView.noData();
                return;
            }
            mIsHasNext = httpResult.data.hasNextPage;
            ArrayList<ScanInfoDetailEntity> result = httpResult.data.list;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            for (ScanInfoDetailEntity entity : result) {
                mData.add(new ScanInfoDetailBean(false, entity));
            }
            if (mAdapter == null) {
                mAdapter = new MyBrowsingHistoryAdapter(mView.getContext(), mData);
                mView.showListView(mAdapter, mIsHasNext);
            } else{
                mView.showViewState(mIsHasNext);
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
