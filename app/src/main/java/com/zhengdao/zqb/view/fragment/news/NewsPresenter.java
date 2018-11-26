package com.zhengdao.zqb.view.fragment.news;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.zhengdao.zqb.api.HomeApi;
import com.zhengdao.zqb.api.UserApi;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.FindEntityHttpResult;
import com.zhengdao.zqb.entity.HotRecommendEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.NewsDetailEntity;
import com.zhengdao.zqb.entity.NewsHttpEntity;
import com.zhengdao.zqb.event.ApkInstallEvent;
import com.zhengdao.zqb.event.DownLoadEvent;
import com.zhengdao.zqb.manager.RetrofitManager;
import com.zhengdao.zqb.mvp.BasePresenterImpl;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxUtils;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.adapter.NewsAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class NewsPresenter extends BasePresenterImpl<NewsContract.View> implements NewsContract.Presenter {

    private List<HotRecommendEntity> mDownloadApp;
    private List<NewsDetailEntity>   mData;
    private int mCurrentPage = 1;
    private int mPageSize    = 10;//每页加载的新闻条数
    private boolean     mIsHasNext;
    private NewsAdapter mAdapter;
    private boolean isDownloadAppDataAdd = false;
    private LinkedList<String> mQueue;

    @Override
    public void initData(String type) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getNews(type, mCurrentPage)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewsHttpEntity>() {
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
                    public void onNext(NewsHttpEntity httpResult) {
                        mView.hideProgress();
                        initListData(httpResult);
                    }

                });
        addSubscription(subscribe);
        //获取APP下载数据
        if (mCurrentPage == 1) {//只在第一页展示;
            getDownloadAppData(1);
        }
    }

    private void initListData(NewsHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mIsHasNext = httpResult.news.hasNextPage;
            ArrayList<NewsDetailEntity> result = httpResult.news.list;
            if (result.size() == 0 || result == null) {
                if (mCurrentPage == 1)
                    mView.noData();
                return;
            }
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1) {
                mData.clear();
                isDownloadAppDataAdd = false;
            }
            addData(result);
            if (mAdapter == null) {
                mAdapter = new NewsAdapter((Activity) mView.getContext(), mData, mView);
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

    /**
     * 添加数据到集合
     *
     * @param result
     */
    private void addData(ArrayList<NewsDetailEntity> result) {
        if (null != mData) {
            mData.addAll(result);
            //添加app下载的数据
            if (mDownloadApp != null && mData.size() > 3 && !isDownloadAppDataAdd) {//只加一条
                mData.add(2, new NewsDetailEntity(9, mDownloadApp.get(0)));
                isDownloadAppDataAdd = true;
            }
            //添加百度广告(按顺序添加)
            if (mQueue == null)
                mQueue = new LinkedList<>();
            if (mQueue.size() == 0) {
                mQueue.offer(Constant.BaiDuAdv.PicAndText);
                mQueue.offer(Constant.BaiDuAdv.PicAndText1);
                mQueue.offer(Constant.BaiDuAdv.PicAndText2);
                mQueue.offer(Constant.BaiDuAdv.Text1);
            }
            //添加百度广告
            int position = mData.size() - 4;//每页第3位
            mData.add(position, new NewsDetailEntity(10, mQueue.poll()));
            //添加API广告
            position = mData.size() - 4;//每页第4位
            mData.add(position, new NewsDetailEntity(11));
            //添加安智广告
            position = mData.size() - 4;//每页第5位
            mData.add(position, new NewsDetailEntity(12));

            //添加百度广告
            position = mData.size() - 2;//每页第7位
            mData.add(position, new NewsDetailEntity(10, mQueue.poll()));
            //添加API广告
            position = mData.size() - 2;//每页第8位
            mData.add(position, new NewsDetailEntity(11));
            //添加安智广告
            position = mData.size() - 2;//每页第9位
            mData.add(position, new NewsDetailEntity(12));
        }
    }

    @Override
    public void updataData(String type) {
        mCurrentPage = 1;
        initData(type);
    }

    @Override
    public void getMoreData(String type) {
        if (mIsHasNext) {
            mCurrentPage++;
            initData(type);
        }
    }

    /**
     * 获取下载应用数据
     *
     * @param pageNo
     */
    @Override
    public void getDownloadAppData(int pageNo) {
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getFindInfo(pageNo, 1)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgress();
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FindEntityHttpResult>() {
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
                    public void onNext(FindEntityHttpResult httpResult) {
                        mView.hideProgress();
                        BuildDownloadAppData(httpResult);
                    }
                });
        addSubscription(subscribe);
    }

    /**
     * 处理添加下载应用数据
     *
     * @param httpResult
     */
    private void BuildDownloadAppData(FindEntityHttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED && httpResult.hotApp != null) {
            ArrayList<HotRecommendEntity> data = httpResult.hotApp.list;
            if (data != null && data.size() > 0) {
                HotRecommendEntity hotRecommendEntity = data.get(0);
                if (mDownloadApp == null)
                    mDownloadApp = new ArrayList<>();
                mDownloadApp.add(hotRecommendEntity);
                if (mData != null) {
                    if (mData.size() > 3 && !isDownloadAppDataAdd) {//新闻数据大于3，才添加
                        mData.add(2, new NewsDetailEntity(9, hotRecommendEntity));
                        isDownloadAppDataAdd = true;
                    }
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
                RxUtils.doTask(new RxUtils.Task<String>() {
                    @Override
                    public void doOnUIThread() {
                        if (isDownloadAppDataAdd && mDownloadApp != null && mDownloadApp.size() > 0) {
                            mData.set(2, new NewsDetailEntity(9, mDownloadApp.get(0)));
                            if (mAdapter != null)
                                mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void doOnIOThread() {
                        doDataSegement();//数据操作,细分状态
                    }
                });
            }
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
        }
    }

    /**
     * 解析app在手机的状态
     */
    private void doDataSegement() {
        if (mDownloadApp != null) {
            for (int i = 0; i < mDownloadApp.size(); i++) {
                HotRecommendEntity entity = mDownloadApp.get(i);
                if (entity != null) {
                    //case 1 已下载，显示安装
                    File outputFile = new File(FileUtils.getAppDownloadPath(entity.appName));
                    if (outputFile != null && outputFile.exists() && AppUtils.checkFile(outputFile, entity.mD5)) {
                        entity.setState(2);
                        mDownloadApp.set(i, entity);
                    }
                    //case 2 手机上是否安装该软件
                    if (AppUtils.isAvilible(mView.getContext(), entity.packageName)) {
                        entity.setState(3);
                        mDownloadApp.set(i, entity);
                    }
                }
            }
        }
    }

    /**
     * 刷新列表的app状态：下载
     *
     * @param downLoadEvent
     */
    public void refreshDownload(DownLoadEvent downLoadEvent) {
        if (downLoadEvent == null)
            return;
        int position = downLoadEvent.position;
        if (mData != null && mData.size() > position && mData.get(position).showType == 9) {
            NewsDetailEntity newsDetailEntity = mData.get(position);
            HotRecommendEntity appEntity = newsDetailEntity.appEntity;
            appEntity.setState(2);
            newsDetailEntity.setAppEntity(appEntity);
            mData.set(position, newsDetailEntity);
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 刷新列表的app状态：安装
     *
     * @param currentInstall
     * @param apkInstallEvent
     */
    public void refreshInstall(int currentInstall, ApkInstallEvent apkInstallEvent) {
        if (apkInstallEvent == null)
            return;
        if (mData != null && mData.size() > currentInstall && mData.get(currentInstall).showType == 9) {
            NewsDetailEntity newsDetailEntity = mData.get(currentInstall);
            HotRecommendEntity appEntity = newsDetailEntity.appEntity;
            appEntity.setState(3);
            newsDetailEntity.setAppEntity(appEntity);
            mData.set(currentInstall, newsDetailEntity);
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
            doGetReward(appEntity.id, appEntity.packageName);
        }
    }

    /**
     * 安装成功后，获取安装app奖励
     *
     * @param id
     * @param packageName
     */
    private void doGetReward(int id, String packageName) {
        TelephonyManager tm = (TelephonyManager) mView.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String IMEI = tm.getDeviceId();//设备唯一标识
        int anInt = Settings.System.getInt(mView.getContext().getContentResolver(), packageName, 0);//是否安装该应用的记录
        if (anInt == 0) {
            LogUtils.i("去领取奖励+IMEI=" + IMEI);
            getDownloadReward(TextUtils.isEmpty(IMEI) ? "" : IMEI, id, packageName);
        } else {
            LogUtils.i("不是第一次安装该应用/已经领取过奖励");
        }
    }

    /**
     * 获取应用安装后的奖励
     *
     * @param uniquenessId
     * @param id
     * @param packageName
     */
    @Override
    public void getDownloadReward(String uniquenessId, int id, final String packageName) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken))
            return;
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(HomeApi.class)
                .getDownloadReward(userToken, uniquenessId, id)
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
                        mView.onDownloadRewardGet(httpResult, packageName);
                    }
                });
        addSubscription(subscribe);
    }

    /**
     * 看广告奖励接口
     *
     * @param address
     * @param type
     */
    @Override
    public void getSeeAdvReward(int address, int type) {
        String userToken = SettingUtils.getUserToken(mView.getContext());
        if (TextUtils.isEmpty(userToken)) {
            //            mView.ReLogin();用户体验不好
            return;
        }
        Subscription subscribe = RetrofitManager.getInstance().noCache().create(UserApi.class)
                .getSeeAdvReward(userToken, address, type)
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
                        mView.onGetAdvReward(httpResult);
                    }
                });
        addSubscription(subscribe);
    }
}
