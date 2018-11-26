package com.zhengdao.zqb.view.fragment.couponsdetail;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.CouponsAdapter;
import com.zhengdao.zqb.view.fragment.coupons.CouponsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponsdetailFragment extends MVPBaseFragment<CouponsdetailContract.View, CouponsdetailPresenter> implements CouponsdetailContract.View, CouponsFragment.CommunicationCallBack, View.OnClickListener {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.re_synthesize)
    TextView           mReSynthesize;
    @BindView(R.id.tv_hot)
    TextView           mTvHot;
    @BindView(R.id.iv_hot_arrow_up)
    ImageView          mIvHotArrowUp;
    @BindView(R.id.iv_hot_arrow_down)
    ImageView          mIvHotArrowDown;
    @BindView(R.id.re_price)
    RelativeLayout     mRePrice;
    @BindView(R.id.tv_award)
    TextView           mTvAward;
    @BindView(R.id.iv_reward_arrow_up)
    ImageView          mIvRewardArrowUp;
    @BindView(R.id.iv_reward_arrow_down)
    ImageView          mIvRewardArrowDown;
    @BindView(R.id.re_sale)
    RelativeLayout     mReSale;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;

    private int     mType;
    private boolean mIsUpFrist;
    private boolean mIsUpSecond;

    public static CouponsdetailFragment newInstance(int type) {
        CouponsdetailFragment fragment = new CouponsdetailFragment();
        Bundle args = new Bundle(2);
        args.putInt(Constant.Fragment.Param, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments())
            mType = getArguments().getInt(Constant.Fragment.Param);
    }

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.coupons_detail_fragment, null);
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
        mPresenter.initData(mType);
        initClickListener();
    }

    private void initClickListener() {
        mReSynthesize.setOnClickListener(this);
        mRePrice.setOnClickListener(this);
        mReSale.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.re_synthesize:
                changeArrow(0);
                break;
            case R.id.re_price:
                changeArrow(1);
                break;
            case R.id.re_sale:
                changeArrow(2);
                break;
        }
    }

    private void changeArrow(int i) {
        String sortName;
        String orderType;
        switch (i) {
            case 0:
                mPresenter.getDataWithSort(mType, "", "");
                mIsUpFrist = false;
                mIsUpSecond = false;
                mIvHotArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                mIvHotArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                mIvRewardArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                mIvRewardArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                break;
            case 1:
                if (mIsUpFrist) {
                    mIvHotArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                    mIvHotArrowDown.setImageResource(R.drawable.little_arror_press_down);
                    orderType = "desc";
                } else {
                    mIvHotArrowUp.setImageResource(R.drawable.little_arror_press_up);
                    mIvHotArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                    orderType = "asc";
                }
                mIvRewardArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                mIvRewardArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                mIsUpFrist = !mIsUpFrist;
                mIsUpSecond = false;
                sortName = "price";
                mPresenter.getDataWithSort(mType, sortName, orderType);
                break;
            case 2:
                mIvHotArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                mIvHotArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                if (mIsUpSecond) {
                    mIvRewardArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                    mIvRewardArrowDown.setImageResource(R.drawable.little_arror_press_down);
                    orderType = "desc";
                } else {
                    mIvRewardArrowUp.setImageResource(R.drawable.little_arror_press_up);
                    mIvRewardArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                    orderType = "asc";
                }
                mIsUpSecond = !mIsUpSecond;
                mIsUpFrist = false;
                sortName = "sales";
                mPresenter.getDataWithSort(mType, sortName, orderType);
                break;
        }
    }

    @Override
    public void getDataWithSearch(String value) {
        mPresenter.getDataWithSearch(mType, value);
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
    public void setAdapter(CouponsAdapter adapter, boolean isHasNext) {
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
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
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
    }
}
