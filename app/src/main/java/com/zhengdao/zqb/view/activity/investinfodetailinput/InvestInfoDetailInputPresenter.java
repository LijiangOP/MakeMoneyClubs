package com.zhengdao.zqb.view.activity.investinfodetailinput;

import android.text.TextUtils;

import com.zhengdao.zqb.api.RebateAPi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.PlatformHttpEntity;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import java.util.ArrayList;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class InvestInfoDetailInputPresenter extends BasePresenterImpl<InvestInfoDetailInputContract.View> implements InvestInfoDetailInputContract.Presenter {

    @Override
    public void getData() {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(RebateAPi.class)
                .getPlatformList()
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<PlatformHttpEntity>() {
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
                    public void onNext(PlatformHttpEntity httpEntity) {
                        mView.hideProgress();
                        mView.showPlatformData(httpEntity);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void addData(ArrayList<String> editData) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(RebateAPi.class)
                .addInvestItem(SettingUtils.getUserToken(mView.getContext()), editData.get(0), editData.get(1), editData.get(2), editData.get(3), editData.get(4), editData.get(5), editData.get(6))
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
                            mView.SuccessAdd();
                            ToastUtil.showToast(mView.getContext(), "添加成功");
                        } else
                            ToastUtil.showToast(mView.getContext(), TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
                    }
                });
        addSubscription(subscribe);
    }
}
