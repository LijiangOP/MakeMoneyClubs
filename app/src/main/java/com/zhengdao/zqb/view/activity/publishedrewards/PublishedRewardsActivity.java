package com.zhengdao.zqb.view.activity.publishedrewards;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import com.zhengdao.zqb.event.WantedPublishEvent;
import com.zhengdao.zqb.event.WantedSaveEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.publish.PublishActivity;
import com.zhengdao.zqb.view.adapter.PublishedWantedAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PublishedRewardsActivity extends MVPBaseActivity<PublishedRewardsContract.View, PublishedRewardsPresenter> implements PublishedRewardsContract.View, View.OnClickListener {


    @BindView(R.id.iv_title_bar_back)
    ImageView          mIvTitleBarBack;
    @BindView(R.id.iv_add)
    ImageView          mIvAdd;
    @BindView(R.id.tabLayout)
    TabLayout          mTabLayout;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listview)
    ListView           mListview;

    private long mCurrentTimeMillis = 0;
    private int  mState             = Constant.Wanted.STATE_ALL;
    private Disposable mDisposable;
    private Disposable mWantedSaveDisposable;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publishedrewards);
        ButterKnife.bind(this);
        initView();
        init();
        initClickListener();
    }

    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.updataData(mState);
                refreshlayout.finishRefresh();
            }
        });
        mSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPresenter.getMore(mState);
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData(mState, 1);
            }
        });
        mDisposable = RxBus.getDefault().toObservable(WantedPublishEvent.class).subscribe(new Consumer<WantedPublishEvent>() {
            @Override
            public void accept(WantedPublishEvent wantedPublishEvent) throws Exception {
                mPresenter.getData(mState, 1);
            }
        });
        mWantedSaveDisposable = RxBus.getDefault().toObservable(WantedSaveEvent.class).subscribe(new Consumer<WantedSaveEvent>() {
            @Override
            public void accept(WantedSaveEvent wantedSaveEvent) throws Exception {
                mPresenter.getData(mState, 1);
            }
        });
        mDisposables.add(mDisposable);
        mDisposables.add(mWantedSaveDisposable);
        mIvAdd.setVisibility(View.GONE);//v1.2取消线上悬赏主发布悬赏功能
    }

    private void initClickListener() {
        mIvTitleBarBack.setOnClickListener(this);
        mIvAdd.setOnClickListener(this);
    }

    private void init() {
        mPresenter.getData(mState);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                if (tab.getPosition() == 0) {
                    mState = Constant.Wanted.STATE_ALL;
                } else if (tab.getPosition() == 1) {
                    mState = Constant.Wanted.STATE_SAVE;
                } else if (tab.getPosition() == 2) {
                    mState = Constant.Wanted.STATE_CHECKING;
                } else if (tab.getPosition() == 3) {
                    mState = Constant.Wanted.STATE_PUBLISHED;
                } else if (tab.getPosition() == 4) {
                    mState = Constant.Wanted.STATE_FINISHED;
                } else if (tab.getPosition() == 5) {
                    mState = Constant.Wanted.STATE_SOLDING_OUT;
                } else if (tab.getPosition() == 6) {
                    mState = Constant.Wanted.STATE_SOLDED_OUT;
                }
                mPresenter.getData(mState, 1);
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
    public void updataAdapter(PublishedWantedAdapter adapter, boolean isHasNext) {
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
    public void onCancleWantedResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "操作成功" : httpResult.msg);
            mPresenter.getData(mState, 1);
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_bar_back:
                PublishedRewardsActivity.this.finish();
                break;
            case R.id.iv_add:
                Utils.StartActivity(PublishedRewardsActivity.this, new Intent(PublishedRewardsActivity.this, PublishActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposables != null) {
            mDisposables.dispose();
            mDisposables.clear();
        }
    }
}
