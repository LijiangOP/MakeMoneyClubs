package com.zhengdao.zqb.view.activity.rebaterecords;

import android.text.TextUtils;

import com.zhengdao.zqb.api.RebateAPi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.InvestBean;
import com.zhengdao.zqb.entity.InvestRecordsHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.RebateRecordsAdapter;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class RebateRecordsPresenter extends BasePresenterImpl<RebateRecordsContract.View> implements RebateRecordsContract.Presenter {

    private List<InvestBean> mData;
    private int mCurrentPage = 1;
    private boolean              mIsHasNext;
    private RebateRecordsAdapter mAdapter;

    @Override
    public void initData() {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token)) {
            mView.ReLogin();
            return;
        }
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(RebateAPi.class)
                .getInvestRecord(token, mCurrentPage)
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InvestRecordsHttpEntity>() {
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
                    public void onNext(InvestRecordsHttpEntity httpResult) {
                        mView.hideProgress();
                        initData(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    private void initData(InvestRecordsHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mIsHasNext = httpResult.wangzhuanList.hasNextPage;
            ArrayList<InvestBean> result = httpResult.wangzhuanList.list;
            if (result.size() == 0 || result == null) {
                mView.noData();
                return;
            }
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            mData.addAll(result);
            if (mAdapter == null) {
                mAdapter = new RebateRecordsAdapter(mView.getContext(), mData);
                mView.setAdapter(mAdapter, mIsHasNext);
            } else {
                mView.updateAdapter(mIsHasNext);
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
