package com.zhengdao.zqb.view.activity.kindofwanted;

import android.text.TextUtils;

import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.KindOfWantedEntity;
import com.zhengdao.zqb.entity.KindOfWantedHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.KindOfWantedAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class KindOfWantedPresenter extends BasePresenterImpl<KindOfWantedContract.View> implements KindOfWantedContract.Presenter {

    private List<KindOfWantedEntity> mData;
    private int mCurrentPage = 1;
    private boolean             mIsHasNext;
    private KindOfWantedAdapter mAdapter;
    private int                 mType;

    @Override
    public void getData(int type, String flag) {
        mType = type;
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getWantedList(mCurrentPage, type, flag, token)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<KindOfWantedHttpEntity>() {
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
                    public void onNext(KindOfWantedHttpEntity httpResult) {
                        mView.hideProgress();
                        initListData(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void updateData(int type, String flag) {
        mCurrentPage = 1;
        getData(type, flag);
    }

    @Override
    public void getMoreData(int type, String flag) {
        if (mIsHasNext) {
            mCurrentPage++;
            getData(type, flag);
        }
    }

    private void initListData(KindOfWantedHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.pageTasks == null || httpResult.pageTasks.list == null || httpResult.pageTasks.list.size() == 0) {
                mView.noData();
                return;
            }
            mIsHasNext = httpResult.pageTasks.hasNextPage;
            List<KindOfWantedEntity> list = httpResult.pageTasks.list;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            mData.addAll(list);
            if (mAdapter == null) {
                mAdapter = new KindOfWantedAdapter(mView.getContext(), mData, mType);
                mView.showListView(mAdapter, mIsHasNext);
            } else {
                mAdapter.notifyDataSetChanged();
                mView.showViewState(mIsHasNext);
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            mView.ReLogin();
        } else
            mView.showErrorMessage(httpResult.msg);
    }
}
