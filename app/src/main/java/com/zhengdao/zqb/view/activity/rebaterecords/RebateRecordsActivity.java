package com.zhengdao.zqb.view.activity.rebaterecords;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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
import com.zhengdao.zqb.event.RefreshInVestListEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.investinfodetailinput.InvestInfoDetailInputActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.RebateRecordsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class RebateRecordsActivity extends MVPBaseActivity<RebateRecordsContract.View, RebateRecordsPresenter> implements RebateRecordsContract.View {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.iv_title_bar_back)
    ImageView          mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView           mTvTitleBarTitle;
    @BindView(R.id.iv_inputed)
    ImageView          mIvInputed;
    @BindView(R.id.re_title_bar)
    RelativeLayout     mReTitleBar;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    private Disposable mDisposable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebate_records);
        ButterKnife.bind(this);
        setTitle("返利记录");
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
                mPresenter.initData();
            }
        });
        mDisposable = RxBus.getDefault().toObservable(RefreshInVestListEvent.class).subscribe(new Consumer<RefreshInVestListEvent>() {
            @Override
            public void accept(RefreshInVestListEvent refreshInVestListEvent) throws Exception {
                mPresenter.updataData();
            }
        });
        mPresenter.initData();
        mIvInputed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.StartActivity(RebateRecordsActivity.this, new Intent(RebateRecordsActivity.this, InvestInfoDetailInputActivity.class));
            }
        });
    }


    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(this, "登录超时,请重新登录");
        startActivityForResult(new Intent(this, LoginActivity.class), ACTION_LOGIN);
    }

    @Override
    public void setAdapter(RebateRecordsAdapter adapter, boolean isHasNext) {
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
    public void updateAdapter(boolean isHasNext) {
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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = ViewUtils.dip2px(RebateRecordsActivity.this, space);
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
                if (booleanExtra && SettingUtils.isLogin(this))
                    mPresenter.initData();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
        if (mDisposable != null)
            mDisposable.dispose();
    }
}
