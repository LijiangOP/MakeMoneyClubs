package com.zhengdao.zqb.view.activity.kindofwanted;


import android.content.Intent;
import android.os.Bundle;
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
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.KindOfWantedAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class KindOfWantedActivity extends MVPBaseActivity<KindOfWantedContract.View, KindOfWantedPresenter> implements KindOfWantedContract.View {

    @BindView(R.id.listView)
    ListView           mListView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;
    private int    mType = 1;
    private String mFlag = "";
    private Disposable mUpDataDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kindofwanted);
        ButterKnife.bind(this);
        init();
        initData();
    }

    private void init() {
        String stringType = getIntent().getStringExtra(Constant.Activity.Type);
        int skip = getIntent().getIntExtra(Constant.Activity.Skip, 0);
        if (!TextUtils.isEmpty(stringType) && stringType.equals("unCheck")) {
            if (skip == 1)
                setTitle(getResources().getString(R.string.wanted_un_pass));
            else
                setTitle(getResources().getString(R.string.wanted_un_check));
            mType = 1;
            mFlag = "1";
        } else if (!TextUtils.isEmpty(stringType) && stringType.equals("unPass")) {
            setTitle(getResources().getString(R.string.wanted_un_passed));
            mType = 2;
        } else if (!TextUtils.isEmpty(stringType) && stringType.equals("unSubmit")) {
            setTitle(getResources().getString(R.string.wanted_un_submit));
            mType = 0;
        } else if (!TextUtils.isEmpty(stringType) && stringType.equals("passed")) {
            setTitle(getResources().getString(R.string.wanted_passed));
            mType = 3;
        }
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.updateData(mType, mFlag);
                refreshlayout.finishRefresh();
            }
        });
        mSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPresenter.getMoreData(mType, mFlag);
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.updateData(mType, mFlag);
            }
        });
        mUpDataDisposable = RxBus.getDefault().toObservable(UpDataUserInfoEvent.class).subscribe(new Consumer<UpDataUserInfoEvent>() {
            @Override
            public void accept(UpDataUserInfoEvent upDataUserInfoEvent) throws Exception {
                mPresenter.updateData(mType, mFlag);
            }
        });

    }

    private void initData() {
        mPresenter.getData(mType, mFlag);
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(this, "登录超时,请重新登录");
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void showViewState(boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void showListView(KindOfWantedAdapter adapter, boolean isHasNext) {
        if (adapter != null) {
            mSwipeRefreshLayout.finishRefresh();
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mListView.setAdapter(adapter);
        }
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpDataDisposable != null)
            mUpDataDisposable.dispose();
    }
}
