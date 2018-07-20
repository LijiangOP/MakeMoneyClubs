package com.zhengdao.zqb.view.activity.investinfoinput;

import android.text.TextUtils;

import com.zhengdao.zqb.api.RebateAPi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.InvestBean;
import com.zhengdao.zqb.entity.InvestRecordBean;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.adapter.InformationInputAdapter;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class InvestInfoInputPresenter extends BasePresenterImpl<InvestInfoInputContract.View> implements InvestInfoInputContract.Presenter, InformationInputAdapter.ItemCallBack {

    private int mCurrentPage = 1;
    private boolean                 mIsHasNext;
    private ArrayList<InvestBean>   mDatas;
    private InformationInputAdapter mAdapter;

    @Override
    public void getData() {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token)) {
            mView.ReLogin();
            return;
        }
        //        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
        //                .getUnPassInvestRecord(token, mCurrentPage)
        //                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
        //                    @Override
        //                    public void call() {
        //                        mView.showProgress();
        //                    }
        //                })
        //                .subscribeOn(AndroidSchedulers.mainThread())
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribe(new Subscriber<HttpResult<InvestRecordBean>>() {
        //                    @Override
        //                    public void onCompleted() {
        //                        mView.hideProgress();
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        mView.hideProgress();
        //                        mView.showErrorMessage(e.getMessage());
        //                    }
        //
        //                    @Override
        //                    public void onNext(HttpResult<InvestRecordBean> httpResult) {
        //                        mView.hideProgress();
        //                        initData(httpResult);
        //                    }
        //                });
        //        addSubscription(subscribe);
    }

    private void initData(HttpResult<InvestRecordBean> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mIsHasNext = httpResult.data.hasNextPage;
            ArrayList<InvestBean> result = httpResult.data.list;
            if (result == null || result.size() == 0) {
                mView.noData();
                return;
            }
            if (mDatas == null)
                mDatas = new ArrayList<>();
            if (mCurrentPage == 1)
                mDatas.clear();
            mDatas.addAll(result);
            if (mAdapter == null) {
                mAdapter = new InformationInputAdapter(mView.getContext(), mDatas, this);
                mView.setAdapter(mIsHasNext, mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
                mView.updateAdapter(mIsHasNext, null);
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            mView.ReLogin();
        } else {
            ToastUtil.showToast(mView.getContext(), TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void updataData() {
        mCurrentPage = 1;
        getData();
    }

    @Override
    public void getMoreData() {
        if (mIsHasNext) {
            mCurrentPage++;
            getData();
        }
    }

    @Override
    public void deleteItem(int id) {
        String token = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(token))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(RebateAPi.class)
                .deleteItem(token, id)
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult>() {
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
                    public void onNext(HttpResult httpResult) {
                        mView.hideProgress();
                        if (httpResult.code == Constant.HttpResult.SUCCEED) {
                            ToastUtil.showToast(mView.getContext(), "删除记录成功");
                            updataData();
                        } else {
                            ToastUtil.showToast(mView.getContext(), TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
                        }
                    }
                });
        addSubscription(subscribe);
    }
}
