package com.zhengdao.zqb.view.fragment.rewardticket;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.RewardTicketEntity;
import com.zhengdao.zqb.entity.RewardTicketHttpEntity;
import com.zhengdao.zqb.event.TicketActivateShareEvent;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.dailywechatshare.DailyWeChatShareActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.rewardticket.RewardTicketActivity;
import com.zhengdao.zqb.view.adapter.RewardTicketAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class RewardTicketFragment extends MVPBaseFragment<RewardTicketContract.View, RewardTicketPresenter> implements RewardTicketContract.View, RewardTicketAdapter.ItemCallBack {
    private static final int REQUESTCODE = 007;

    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private int mState       = -1;
    private int mCurrentPage = 1;
    private boolean                  mIsHasNext;
    private RewardTicketAdapter      mAdapter;
    private List<RewardTicketEntity> mData;
    private Disposable               mDisposable;

    public static RewardTicketFragment newInstance(int state) {
        RewardTicketFragment fragment = new RewardTicketFragment();
        Bundle args = new Bundle(2);
        args.putInt(Constant.Fragment.Param, state);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments())
            mState = getArguments().getInt(Constant.Fragment.Param);
    }

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.reward_ticket_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mPresenter.getData(mState, mCurrentPage);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mCurrentPage = 1;
                initData();
                refreshlayout.finishRefresh();
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (mIsHasNext) {
                    mCurrentPage++;
                    initData();
                }
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentPage = 1;
                initData();
            }
        });
        mDisposable = RxBus.getDefault().toObservable(TicketActivateShareEvent.class).subscribe(new Consumer<TicketActivateShareEvent>() {
            @Override
            public void accept(TicketActivateShareEvent ticketActivateShareEvent) throws Exception {
                mCurrentPage = 1;
                initData();
            }
        });
    }

    public void initData() {
        mPresenter.getData(mState, mCurrentPage);
    }


    @Override
    public void onItemClick(int position) {
        try {
            if (mData != null && mData.size() > position && mState == 0) {
                RewardTicketEntity rewardTicketEntity = mData.get(position);
                Intent intent = new Intent(getActivity(), DailyWeChatShareActivity.class);
                intent.putExtra(Constant.Activity.Type, 1);
                intent.putExtra(Constant.Activity.Data1, String.valueOf(new Double(rewardTicketEntity.quota).intValue()));
                intent.putExtra(Constant.Activity.Data2, rewardTicketEntity.id);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataGet(RewardTicketHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            RewardTicketActivity activity = (RewardTicketActivity) getActivity();
            activity.onGetData(httpResult.notUsedCount, httpResult.alreadyUsedCount, httpResult.expiredCount);
            ArrayList<RewardTicketEntity> list = httpResult.coupons.list;
            if (list == null || list.size() == 0) {
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
                return;
            }
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mIsHasNext = httpResult.coupons.hasNextPage;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            mData.addAll(list);
            if (mAdapter == null) {
                mAdapter = new RewardTicketAdapter(getActivity(), mData, this, mState);
                mRecycleView.setAdapter(mAdapter);
                mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecycleView.addItemDecoration(new SpacesItemDecoration(10));
            } else
                mAdapter.notifyDataSetChanged();
            if (mIsHasNext) {
                mSmartRefreshLayout.resetNoMoreData();
            } else {
                mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE:
                if (data != null) {
                    boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                    if (booleanExtra) {
                        mCurrentPage = 1;
                        initData();
                    }
                }
                break;
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
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null)
            mDisposable.dispose();
    }
}
