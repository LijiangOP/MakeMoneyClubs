package com.zhengdao.zqb.view.activity.investinfoinput;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.investinfodetailinput.InvestInfoDetailInputActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.InformationInputAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class InvestInfoInputActivity extends MVPBaseActivity<InvestInfoInputContract.View, InvestInfoInputPresenter> implements InvestInfoInputContract.View {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.iv_title_bar_back)
    ImageView          mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView           mTvTitleBarTitle;
    @BindView(R.id.iv_inputed)
    ImageView          mIvInputed;

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_information_input);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitleBarTitle.setText("信息录入");
        mIvTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InvestInfoInputActivity.this.finish();
            }
        });
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
                mPresenter.updataData();
            }
        });
        mIvInputed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InvestInfoInputActivity.this, InvestInfoDetailInputActivity.class));
            }
        });
        mDisposable = RxBus.getDefault().toObservable(RefreshInVestListEvent.class).subscribe(new Consumer<RefreshInVestListEvent>() {
            @Override
            public void accept(RefreshInVestListEvent refreshInVestListEvent) throws Exception {
                mPresenter.updataData();
            }
        });
        mPresenter.updataData();
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void noData() {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(this, "登录超时,请重新登录");
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void setAdapter(boolean isHasNext, InformationInputAdapter adapter) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (adapter != null) {
            mRecycleView.setAdapter(adapter);
            mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        }
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void updateAdapter(boolean isHasNext, InformationInputAdapter adapter) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void deleteItem(int position) {
        LogUtils.i("position" + position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_LOGIN) {
            if (data != null) {
                boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                if (booleanExtra && SettingUtils.isLogin(this))
                    mPresenter.getData();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
        if (mDisposable != null)
            mDisposable.dispose();
    }
}
