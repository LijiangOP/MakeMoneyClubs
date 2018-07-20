package com.zhengdao.zqb.view.activity.message;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.event.ClearRedPointEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.MessageAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MessageActivity extends MVPBaseActivity<MessageContract.View, MessagePresenter> implements MessageContract.View {

    private static final int REQUESTCODE = 001;
    @BindView(R.id.listView)
    ListView           mListView;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        setTitle("消息");
        init();
    }

    private void init() {
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPresenter.updateData();
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
        //MultiStateView设置
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData();
            }
        });
        initMessageData();
    }

    private void initMessageData() {
        if (SettingUtils.isLogin(this)) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
            mPresenter.getData();
        } else {
            startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
        }
    }


    @Override
    public void updataAdapter(MessageAdapter adapter, int total, boolean isHasNext) {
        if (adapter != null) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mListView.setAdapter(adapter);
            RxBus.getDefault().post(new ClearRedPointEvent());
            SettingUtils.setMessageCount(this, total);
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
    public void showErrorMessage(String msg) {
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        super.showErrorMessage(msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE) {
            if (data != null) {
                boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                if (booleanExtra && SettingUtils.isLogin(this))
                    mPresenter.getData();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubcrible();
    }
}
