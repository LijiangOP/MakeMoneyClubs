package com.zhengdao.zqb.view.activity.mybalance;


import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.withdraw.WithDrawActivity;
import com.zhengdao.zqb.view.adapter.BalanceAdapter;
import com.zhengdao.zqb.view.adapter.BalanceTypeAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyBalanceActivity extends MVPBaseActivity<MyBalanceContract.View, MyBalancePresenter> implements MyBalanceContract.View, BalanceTypeAdapter.CallBack, View.OnClickListener {

    @BindView(R.id.re_title_bar)
    RelativeLayout     mReTitleBar;
    @BindView(R.id.tv_balance)
    TextView           mTvBalance;
    @BindView(R.id.tv_current_month)
    TextView           mTvCurrentMonth;
    @BindView(R.id.iv_select)
    ImageView          mIvSelect;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.tv_withdraw)
    TextView           mTvWithdraw;

    private PopupWindow       mPopupWindow;
    private ArrayList<String> mSelectData;
    private int    mBalanceType       = -1;
    private String mBalanceTypeString = "全部";
    private Double mUsableSum;
    private long mCurrentTimeMillis = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_balance);
        ButterKnife.bind(this);
        setTitle("我的钱包");
        init();
    }


    private void init() {
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
                mPresenter.initData(mBalanceType);
            }
        });
        mTvWithdraw.setOnClickListener(this);
        mTvCurrentMonth.setOnClickListener(this);
        mIvSelect.setOnClickListener(this);
        mPresenter.initData(mBalanceType);
    }

    private void doSelect() {
        View contentView = LayoutInflater.from(MyBalanceActivity.this).inflate(R.layout.popup_balance_type, null);
        RecyclerView recyclerView = contentView.findViewById(R.id.recycle_view);
        if (mSelectData == null) {
            mSelectData = new ArrayList<>();
            mSelectData.add("全部");
            mSelectData.add("悬赏收入");
            mSelectData.add("广告收入");
            mSelectData.add("理财返利");
            mSelectData.add("推荐奖励");
            mSelectData.add("提现");
        }
        BalanceTypeAdapter balanceTypeAdapter = new BalanceTypeAdapter(this, R.layout.balance_type_item, mSelectData, this, mBalanceTypeString);
        recyclerView.setAdapter(balanceTypeAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new SpaceBusinessItemDecoration(10));
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
            mPopupWindow.setFocusable(true);
        }
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        if (Build.VERSION.SDK_INT > 18)
            mPopupWindow.showAsDropDown(mReTitleBar, 0, 0, Gravity.TOP);
        else
            mPopupWindow.showAsDropDown(mReTitleBar, 0, 0);
        backgroundAlpha(0.7f);
    }

    @Override
    public void doItemClick(int position) {
        try {
            if (mSelectData != null && mSelectData.size() > (position)) {
                String value = mSelectData.get(position);
                LogUtils.i("value=" + value);
                mBalanceTypeString = value;
                if (value.equals("全部")) {
                    mBalanceType = -1;
                } else if (value.equals("悬赏收入")) {
                    mBalanceType = 3;
                } else if (value.equals("广告收入")) {
                    mBalanceType = 7;
                } else if (value.equals("理财返利")) {
                    mBalanceType = 8;
                } else if (value.equals("推荐奖励")) {
                    mBalanceType = 4;
                } else if (value.equals("提现")) {
                    mBalanceType = 5;
                }
                mPresenter.initData(mBalanceType);
            }
            if (mPopupWindow != null)
                mPopupWindow.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(this, "登录超时,请重新登录");
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void setAdapter(BalanceAdapter adapter, boolean isHasNext, Double balance) {
        mUsableSum = balance;
        mTvBalance.setText("" + (balance == null ? 0 : balance));
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (adapter != null) {
            mSwipeRefreshLayout.finishRefresh();
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mRecycleView.setAdapter(adapter);
            mRecycleView.setLayoutManager(new LinearLayoutManager(this));
            mRecycleView.addItemDecoration(new SpacesItemDecoration(10));
        }
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void updateAdapter(boolean isHasNext, Double balance) {
        mTvBalance.setText("" + (balance == null ? 0 : balance));
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSwipeRefreshLayout.resetNoMoreData();
        } else {
            mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_withdraw:
                Intent withDraw = new Intent(MyBalanceActivity.this, WithDrawActivity.class);
                withDraw.putExtra(Constant.Activity.Data1, mUsableSum);
                Utils.StartActivity(MyBalanceActivity.this, withDraw);
                break;
            case R.id.tv_current_month:
                try {
                    mRecycleView.scrollToPosition(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.iv_select:
                doSelect();
                break;
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = ViewUtils.dip2px(MyBalanceActivity.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
        if (mPopupWindow != null)
            mPopupWindow = null;
    }


    public class SpaceBusinessItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceBusinessItemDecoration(int space) {
            this.space = ViewUtils.dip2px(MyBalanceActivity.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = space;
            outRect.bottom = space;
            if ((pos + 1) % 3 == 0) {
                outRect.right = space;
            } else {
                outRect.right = 0;
            }
        }
    }
}
