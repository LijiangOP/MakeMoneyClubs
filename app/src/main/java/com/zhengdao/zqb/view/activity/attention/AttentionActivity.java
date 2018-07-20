package com.zhengdao.zqb.view.activity.attention;


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
import com.zhengdao.zqb.entity.AttentionEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.MyAttentionListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AttentionActivity extends MVPBaseActivity<AttentionContract.View, AttentionPresenter> implements AttentionContract.View, MyAttentionListAdapter.CancleAttentionCallBack {


    @BindView(R.id.listView)
    ListView           mListView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;
    private int mCurrentPage = 1;
    private boolean                                     mIsHasNext;
    private MyAttentionListAdapter                      mAdapter;
    private List<AttentionEntity.AttentionDetailEntity> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        ButterKnife.bind(this);
        setTitle("我的关注");
        init();
        initData();
    }

    private void init() {
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
                refreshlayout.finishRefresh();

            }
        });
        mSwipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mCurrentPage++;
                mPresenter.getMoreData(mIsHasNext, mCurrentPage);
                refreshlayout.finishLoadmore();
            }
        });

        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
    }

    private void initData() {
        mCurrentPage = 1;
        mPresenter.initData(mCurrentPage);
    }

    @Override
    public void onGetDataResult(AttentionEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.list == null || httpResult.list.list == null || httpResult.list.list.size() == 0) {
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
                return;
            }
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mIsHasNext = httpResult.list.hasNextPage;
            ArrayList<AttentionEntity.AttentionDetailEntity> result = httpResult.list.list;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            mData.addAll(result);
            if (mAdapter == null)
                mAdapter = new MyAttentionListAdapter(this, mData, this);
            if (mAdapter != null) {
                mSwipeRefreshLayout.finishRefresh();
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
                mListView.setAdapter(mAdapter);
            } else
                mAdapter.notifyDataSetChanged();
            if (mIsHasNext) {
                mSwipeRefreshLayout.resetNoMoreData();
            } else {
                mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void cancleAttention(int fid) {
        mPresenter.cancleAttention(fid);
    }

    @Override
    public void onCancleAttentionResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(AttentionActivity.this, "操作成功");
            initData();
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else {
            ToastUtil.showToast(AttentionActivity.this, TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }

}
