package com.zhengdao.zqb.view.fragment.wanted;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
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
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.MyWantedAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WantedFragment extends MVPBaseFragment<WantedContract.View, WantedPresenter> implements WantedContract.View {

    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private int        mState;
    private Disposable mUpDataUserInfoDisposable;
    private CompositeDisposable mDisposables = new CompositeDisposable();

    public static WantedFragment newInstance(int state) {
        WantedFragment fragment = new WantedFragment();
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
        View view = View.inflate(getActivity(), R.layout.wanted_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.updataData();
                refreshlayout.finishRefresh();
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPresenter.getMoreData();
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.updataData(mState);
            }
        });
        mUpDataUserInfoDisposable = RxBus.getDefault().toObservable(UpDataUserInfoEvent.class).subscribe(new Consumer<UpDataUserInfoEvent>() {
            @Override
            public void accept(UpDataUserInfoEvent upDataUserInfoEvent) throws Exception {
                LogUtils.e("需要刷新该页面数据");
                mPresenter.updataData(mState);
            }
        });
        mDisposables.add(mUpDataUserInfoDisposable);
        mPresenter.getData(mState);
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    public void updataAdapter(MyWantedAdapter adapter, boolean isHasNext) {
        if (adapter != null) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mRecycleView.setAdapter(adapter);
            mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(getActivity().getResources().getDrawable(R.drawable.shape_recycleview_divider));
            mRecycleView.addItemDecoration(dividerItemDecoration);
        }
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void showContentState(boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void onRemindCheckResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(getActivity(), "提醒成功");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    public void onCancleMissionResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(getActivity(), "取消任务成功");
            mPresenter.getData(mState);
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposables != null)
            mDisposables.clear();
    }
}
