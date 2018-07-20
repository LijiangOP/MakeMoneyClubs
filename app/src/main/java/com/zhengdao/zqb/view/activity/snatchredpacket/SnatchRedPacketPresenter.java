package com.zhengdao.zqb.view.activity.snatchredpacket;

import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.SnatchRedPackeEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.SnatchRedPacketAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class SnatchRedPacketPresenter extends BasePresenterImpl<SnatchRedPacketContract.View> implements SnatchRedPacketContract.Presenter {

    private List<SnatchRedPackeEntity.SnatchRedPackeDetailEntity> mData;
    private int mCurrentPage = 1;
    private boolean                mIsHasNext;
    private SnatchRedPacketAdapter mAdapter;

    @Override
    public void initData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getSnatchRedPacketData(SettingUtils.getUserToken(mView.getContext()), mCurrentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<SnatchRedPackeEntity>>() {
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
                    public void onNext(HttpResult<SnatchRedPackeEntity> userHomeBeanHttpResult) {
                        mView.hideProgress();
                        initListData(userHomeBeanHttpResult);
                    }
                });
        addSubscription(subscribe);
    }

    private void initListData(HttpResult<SnatchRedPackeEntity> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mIsHasNext = httpResult.data.isHasNext;
            ArrayList<SnatchRedPackeEntity.SnatchRedPackeDetailEntity> result = httpResult.data.result;
            if (result != null) {
                if (result.size() == 0) {
                    mView.noData();
                    return;
                }
                if (mData == null)
                    mData = new ArrayList<>();
                if (mCurrentPage == 1)
                    mData.clear();
                mData.addAll(result);
                if (mAdapter == null) {
                    mAdapter = new SnatchRedPacketAdapter(mView.getContext(), mData);
                    mView.showListView(mAdapter, mIsHasNext);
                }else {
                    mView.showViewState(mIsHasNext);
                }
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
