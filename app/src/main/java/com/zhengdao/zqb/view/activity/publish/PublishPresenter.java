package com.zhengdao.zqb.view.activity.publish;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.UpLoadApi;
import com.zhengdao.zqb.api.WantedApi;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.HomeWantedDetailEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.SettingUtils;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class PublishPresenter extends BasePresenterImpl<PublishContract.View> implements PublishContract.Presenter {

    @Override
    public void getWantedData(int id) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getMyWantedDetail(userToken, id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HomeWantedDetailEntity>() {
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
                    public void onNext(HomeWantedDetailEntity httpResult) {
                        mView.hideProgress();
                        mView.onGetDataFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getData(final String key) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .getTypesByKey(key)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DictionaryHttpEntity>() {
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
                    public void onNext(DictionaryHttpEntity result) {
                        mView.hideProgress();
                        mView.showView(result, key);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void doPublish(int payType, String json) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(WantedApi.class)
                .saveAndSubmitReward(SettingUtils.getUserToken(mView.getContext()), payType, json)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
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
                        mView.onSaveOrPublishResult(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void uploadImages(RequestBody type, Map<String, RequestBody> file) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UpLoadApi.class)
                .uploadImages(type, file)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<ArrayList<String>>>() {
                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.onImgUploadError();
                    }

                    @Override
                    public void onNext(HttpResult<ArrayList<String>> result) {
                        mView.hideProgress();
                        mView.onImgUploadResult(result);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void uploadIconImages(RequestBody type, Map<String, RequestBody> file) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UpLoadApi.class)
                .uploadImages(type, file)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.io()).doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<ArrayList<String>>>() {
                    @Override
                    public void onCompleted() {
                        mView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.hideProgress();
                        mView.onIconImgUploadError();
                    }

                    @Override
                    public void onNext(HttpResult<ArrayList<String>> result) {
                        mView.hideProgress();
                        mView.onIconImgUploadResult(result);
                    }
                });
        addSubscription(subscribe);
    }
}
