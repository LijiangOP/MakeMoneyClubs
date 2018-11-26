package com.zhengdao.zqb.view.activity.homegoodsdetail;

import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.UpLoadApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.DictionaryEntity;
import com.zhengdao.zqb.entity.HomeWantedDetailEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.LessTimeHttpResult;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class HomeGoodsDetailPresenter extends BasePresenterImpl<HomeGoodsDetailContract.View> implements HomeGoodsDetailContract.Presenter {

    @Override
    public void getData(int id) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getWantedDetail(userToken, id)
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
    public void getCheckTime(int mode) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getCheckTime(mode)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<DictionaryEntity>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(DictionaryEntity httpResult) {
                        mView.onGetCheckTimeFinish(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    /**
     * 加关注
     *
     * @param id
     */
    @Override
    public void AddAttention(int id) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .AddACF(SettingUtils.getUserToken(mView.getContext()), id, Constant.HttpResult.Attention)
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
                        mView.onAddAttentionFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void CancleAttention(int id) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .CancleACF(SettingUtils.getUserToken(mView.getContext()), id)
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
                        mView.onCancleAttentionFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    /**
     * 加收藏
     *
     * @param id
     */
    @Override
    public void doCollect(int id) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .AddCollect(SettingUtils.getUserToken(mView.getContext()), id, Constant.HttpResult.Collect)
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
                        mView.onAddCollectFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void getWanted(int id) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .GetWanted(SettingUtils.getUserToken(mView.getContext()), id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LessTimeHttpResult>() {
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
                    public void onNext(LessTimeHttpResult httpResult) {
                        mView.hideProgress();
                        mView.onGetWantedFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    @Override
    public void uploadImages(RequestBody type, Map<String, RequestBody> file) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UpLoadApi.class)
                .uploadImages(type, file)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
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
    public void CommitWanted(ArrayList<String> uploadImages, ArrayList<String> jsons, String imei, int Id) {
        String taskInfo = "";
        if (jsons != null && jsons.size() > 0)
            taskInfo = jsons.toString();
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .CommitWantedQVU(uploadImages, taskInfo, SettingUtils.getUserToken(mView.getContext()), imei, Id)
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
                        LogUtils.e(e.getMessage());
                    }


                    @Override
                    public void onNext(HttpResult httpResult) {
                        mView.hideProgress();
                        mView.onCommitWantedFinished(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
