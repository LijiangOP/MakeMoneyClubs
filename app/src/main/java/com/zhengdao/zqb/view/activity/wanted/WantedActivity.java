package com.zhengdao.zqb.view.activity.wanted;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.WantedAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class WantedActivity extends MVPBaseActivity<WantedContract.View, WantedPresenter> implements WantedContract.View {

    private static final int ALLSTATE    = -1;
    private static final int UN_COMMITED = 0;
    private static final int COMMITED    = 1;
    private static final int UN_DONE     = 2;
    private static final int DONE        = 3;

    @BindView(R.id.tabLayout)
    TabLayout          mTabLayout;
    @BindView(R.id.listView)
    ListView           mListview;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    private String     mSeleted;
    private int        mCurrentPostion;
    private Disposable mUpDataUserInfoDisposable;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);
        setTitle("我的悬赏");
        initData();
        init();
    }

    private void initData() {
        mSeleted = getIntent().getStringExtra(Constant.Activity.Select);
        if (!TextUtils.isEmpty(mSeleted) && mSeleted.equals("un_commited")) {
            mCurrentPostion = 1;
        } else if (!TextUtils.isEmpty(mSeleted) && mSeleted.equals("commited")) {
            mCurrentPostion = 2;
        } else if (!TextUtils.isEmpty(mSeleted) && mSeleted.equals("un_done")) {
            mCurrentPostion = 3;
        } else if (!TextUtils.isEmpty(mSeleted) && mSeleted.equals("done")) {
            mCurrentPostion = 4;
        } else {
            mCurrentPostion = 0;
        }
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.updataData();
                refreshlayout.finishRefresh();
            }
        });
        mSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPresenter.getMoreData();
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData(mCurrentPostion - 1, 1);
            }
        });
        mUpDataUserInfoDisposable = RxBus.getDefault().toObservable(UpDataUserInfoEvent.class).subscribe(new Consumer<UpDataUserInfoEvent>() {
            @Override
            public void accept(UpDataUserInfoEvent upDataUserInfoEvent) throws Exception {
                LogUtils.e("需要刷新该页面数据");
                mPresenter.getData(mCurrentPostion - 1, 1);
            }
        });
        mDisposables.add(mUpDataUserInfoDisposable);
        mTabLayout.getTabAt(mCurrentPostion).select();
    }

    private void init() {
        mPresenter.getData(mCurrentPostion - 1);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                if (tab.getPosition() == 0) {
                    mPresenter.getData(ALLSTATE, 1);
                } else if (tab.getPosition() == 1) {
                    mPresenter.getData(UN_COMMITED, 1);
                } else if (tab.getPosition() == 2) {
                    mPresenter.getData(COMMITED, 1);
                } else if (tab.getPosition() == 3) {
                    mPresenter.getData(UN_DONE, 1);
                } else if (tab.getPosition() == 4) {
                    mPresenter.getData(DONE, 1);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        ViewUtils.reflex(mTabLayout);
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    public void updataAdapter(WantedAdapter adapter, boolean isHasNext) {
        if (adapter != null) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mListview.setAdapter(adapter);
        }
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void showContentState(boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void onRemindCheckResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, "提醒成功");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    public void onCancleMissionResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, "取消任务成功");
            initData();
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposables != null)
            mDisposables.clear();
    }
}
