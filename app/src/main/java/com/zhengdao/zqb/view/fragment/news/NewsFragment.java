package com.zhengdao.zqb.view.fragment.news;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.SwipeBackActivity.HotRecommendWindow;
import com.zhengdao.zqb.entity.HotRecommendEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.ApkInstallEvent;
import com.zhengdao.zqb.event.DownLoadEvent;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.AppUtils;
import com.zhengdao.zqb.utils.BaiDuAPiAdvUtils;
import com.zhengdao.zqb.utils.DownloadUtils;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.NewsAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class NewsFragment extends MVPBaseFragment<NewsContract.View, NewsPresenter> implements NewsContract.View {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private String              mType;
    private HotRecommendWindow  mDailyWindow;
    private Disposable          mInstallDisposable;
    private Disposable          mDownloadDisposable;
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private int                 mCurrentInstall;
    private ArrayList<String>   mI_rpt;
    private String              mReplaceValue;
    private String              mFileName;

    public static NewsFragment newInstance(String type) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle(2);
        args.putString(Constant.Fragment.Param, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments())
            mType = getArguments().getString(Constant.Fragment.Param);
    }

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.news_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.updataData(mType);
                refreshlayout.finishRefresh();
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPresenter.getMoreData(mType);
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.initData(mType);
            }
        });
        mDownloadDisposable = RxBus.getDefault().toObservable(DownLoadEvent.class).subscribe(new Consumer<DownLoadEvent>() {
            @Override
            public void accept(DownLoadEvent downLoadEvent) throws Exception {
                mPresenter.refreshDownload(downLoadEvent);
            }
        });
        mInstallDisposable = RxBus.getDefault().toObservable(ApkInstallEvent.class).subscribe(new Consumer<ApkInstallEvent>() {
            @Override
            public void accept(ApkInstallEvent apkInstallEvent) throws Exception {
                if (mI_rpt != null && mI_rpt.size() > 0 && TextUtils.isEmpty(mFileName)) {
                    mPresenter.refreshInstall(mCurrentInstall, apkInstallEvent);
                } else {
                    getAPIadv(apkInstallEvent);
                }
            }
        });
        mDisposables.add(mDownloadDisposable);
        mDisposables.add(mInstallDisposable);
        mPresenter.updataData(mType);
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
        startActivityForResult(new Intent(getActivity(), LoginActivity.class), ACTION_LOGIN);
    }

    @Override
    public void setAdapter(NewsAdapter adapter, boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (adapter != null) {
            mSmartRefreshLayout.finishRefresh();
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mRecycleView.setAdapter(adapter);
            mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void updateAdapter(boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void onDownloadRewardGet(HttpResult httpResult, String packageName) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                showRwardGetWindow(httpResult);
                Settings.System.putInt(getActivity().getContentResolver(), packageName, 1);//一个手机再次安装该应用，不会领取奖励
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else if (httpResult.code == Constant.HttpResult.FAILD) {
                LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRwardGetWindow(HttpResult httpResult) {
        try {
            if (mDailyWindow == null)
                mDailyWindow = new HotRecommendWindow(getActivity());
            mDailyWindow.setPosition(0, -100);
            mDailyWindow.initContentView(httpResult.money + "");
            mDailyWindow.setCanceledOnTouchOutside(true);
            mDailyWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onButtonClick(int position, HotRecommendEntity entity) {
        DownloadUtils.downLoadApk(getActivity(), entity, position);
    }

    @Override
    public void onInstallClick(int position, HotRecommendEntity entity) {
        mCurrentInstall = position;
        AppUtils.install(getActivity(), new File(FileUtils.getAppDownloadPath(entity.appName)), entity.packageName);
    }

    @Override
    public void onBaiduAdvClick() {
        mPresenter.getSeeAdvReward(2, 1);
    }

    @Override
    public void onTencentAdvClick() {
        mPresenter.getSeeAdvReward(2, 2);
    }

    @Override
    public void onApiDownLoadStart(ArrayList<String> i_rpt, String clickid, String fileName) {
        mI_rpt = i_rpt;
        mFileName = fileName;
        mReplaceValue = clickid;
    }

    /**
     * 接收到广播 上报广告apk安装成功
     *
     * @param apkInstallEvent
     */
    private void getAPIadv(ApkInstallEvent apkInstallEvent) {
        File file = new File(FileUtils.getAppDownloadPath(mFileName));
        String packageNameByFile = AppUtils.getPackageNameByFile(getActivity(), file);
        if (!TextUtils.isEmpty(apkInstallEvent.uri) && !TextUtils.isEmpty(packageNameByFile) && packageNameByFile.equals(apkInstallEvent.uri)) {
            Log.i("Banana", "packageName=" + packageNameByFile);
            if (mI_rpt != null && mI_rpt.size() > 0) {
                for (String value : mI_rpt) {
                    if (!TextUtils.isEmpty(value)) {
                        if (value.contains("SZST_CLID") && !TextUtils.isEmpty(mReplaceValue))
                            value = value.replace("SZST_CLID", mReplaceValue);
                        BaiDuAPiAdvUtils.ReportAdv(value);
                    }
                }
            }
        }
    }

    @Override
    public void onGetAdvReward(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            LogUtils.i(TextUtils.isEmpty(httpResult.msg) ? "获取奖励成功" : httpResult.msg);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            LogUtils.e("登录超时");
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = ViewUtils.dip2px(getActivity(), space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_LOGIN) {
            if (data != null) {
                boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                if (booleanExtra && SettingUtils.isLogin(getActivity()))
                    mPresenter.initData(mType);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
        if (mDailyWindow != null) {
            mDailyWindow.dismiss();
            mDailyWindow = null;
        }
        if (mDisposables != null) {
            mDisposables.dispose();
            mDisposables.clear();
        }
    }
}
