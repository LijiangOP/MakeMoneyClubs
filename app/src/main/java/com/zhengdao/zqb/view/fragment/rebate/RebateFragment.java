package com.zhengdao.zqb.view.fragment.rebate;


import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RebateFragment extends MVPBaseFragment<RebateContract.View, RebatePresenter> implements RebateContract.View {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.fake_status_bar)
    View               mFakeStatusBar;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.rebate_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mFakeStatusBar.setBackgroundColor(calculateStatusColor(getResources().getColor(R.color.main), 0));//fragment中添加一个顶部占位View
        StatusBarUtil.setTransparentForImageViewInFragment(getActivity(), null);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.refresh();
                refreshlayout.finishRefresh();
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                mPresenter.getMore();
                refreshlayout.finishLoadmore();
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.initData();
            }
        });
        mPresenter.getSwitchState();
    }

    @Override
    public void onSwitchStateGet(DictionaryHttpEntity result) {
        try {
            if (result.code == Constant.HttpResult.SUCCEED) {
                if (result.types != null && result.types.size() > 0) {
                    String value = result.types.get(0).value;
                    if (!TextUtils.isEmpty(value)) {
                        mPresenter.setSwitchState(new Integer(value));
                    }
                }
            }
            mPresenter.initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void setAdapter(RecyclerView.Adapter adapter, boolean isHasNext) {
        if (adapter != null) {
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
    public void refreshAdapter(boolean isHasNext) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
        if (isHasNext) {
            mSmartRefreshLayout.resetNoMoreData();
        } else {
            mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
        }
    }

    @Override
    public void onGetAdvReward(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            LogUtils.i(TextUtils.isEmpty(httpResult.msg) ? "获取奖励成功" : httpResult.msg);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            LogUtils.e("登录超时");
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    @Override
    public void search(String value) {
        mPresenter.doSearch(value);
        hideSoftInput();
    }

    @Override
    public void onBaiduAdvClick() {
        //        mPresenter.getSeeAdvReward(1,1);
    }

    @Override
    public void onTencentAdvClick() {
        //        mPresenter.getSeeAdvReward(1,2);
    }

    @Override
    public void onAnZhiAdvClick() {
        //        mPresenter.getSeeAdvReward(1,3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_LOGIN) {
            if (data != null) {
                boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                if (booleanExtra && SettingUtils.isLogin(getActivity()))
                    mPresenter.initData();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }

}
