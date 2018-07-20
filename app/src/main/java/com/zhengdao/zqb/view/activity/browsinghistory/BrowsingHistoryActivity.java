package com.zhengdao.zqb.view.activity.browsinghistory;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.CustomAlertDialog;
import com.zhengdao.zqb.customview.RuleHintDialog;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.ScanInfoDetailBean;
import com.zhengdao.zqb.entity.ScanInfoDetailEntity;
import com.zhengdao.zqb.entity.ScanInfoEntity;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.MyBrowsingHistoryAdapter;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BrowsingHistoryActivity extends MVPBaseActivity<BrowsingHistoryContract.View, BrowsingHistoryPresenter> implements BrowsingHistoryContract.View, View.OnClickListener, MyBrowsingHistoryAdapter.allCheckCallBack {

    private static final int REQUEST_CODE_FIRST = 01;
    @BindView(R.id.iv_back)
    ImageView          mIvBack;
    @BindView(R.id.tv_clear)
    TextView           mTvClear;
    @BindView(R.id.tv_edit)
    TextView           mTvEdit;
    @BindView(R.id.listView)
    ListView           mListView;
    @BindView(R.id.iv_checkbox)
    ImageView          mIvCheckbox;
    @BindView(R.id.tv_delete)
    TextView           mTvDelete;
    @BindView(R.id.ll_edit)
    LinearLayout       mLlEdit;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swipeRefreshLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    private int  mCurrentPage       = 1;
    private long mCurrentTimeMillis = 0;
    private RuleHintDialog                mRuleHintDialog;
    private MyBrowsingHistoryAdapter      mAdapter;
    private boolean                       mIsHasNext;
    private ArrayList<ScanInfoDetailBean> mData;
    private ArrayList<Integer>            mDeleteList;
    private boolean                       mIsShowCb;
    private boolean mIsChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browsing_history);
        ButterKnife.bind(this);
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
        mIvBack.setOnClickListener(this);
        mTvClear.setOnClickListener(this);
        mTvEdit.setOnClickListener(this);
        mTvDelete.setOnClickListener(this);
        mIvCheckbox.setOnClickListener(this);
    }

    private void initData() {
        mCurrentPage = 1;
        mPresenter.initData(mCurrentPage);
    }

    @Override
    public void onGetDataFinish(HttpResult<ScanInfoEntity> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.data == null || httpResult.data.list == null || httpResult.data.list.size() == 0) {
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                mSwipeRefreshLayout.finishLoadmoreWithNoMoreData();
                return;
            }
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mIsHasNext = httpResult.data.hasNextPage;
            ArrayList<ScanInfoDetailEntity> result = httpResult.data.list;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentPage == 1)
                mData.clear();
            for (ScanInfoDetailEntity entity : result) {
                mData.add(new ScanInfoDetailBean(mIsShowCb, entity));
            }
            if (mAdapter == null) {
                mAdapter = new MyBrowsingHistoryAdapter(this, mData, this);
                mSwipeRefreshLayout.finishRefresh();
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
    public void allChecked(boolean isAllCheck) {
        if (isAllCheck)
            mIvCheckbox.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
        else
            mIvCheckbox.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
        mIsChecked = isAllCheck;
    }

    @Override
    public void showErrorMessage(String msg) {
        if (mDeleteList != null)
            mDeleteList.clear();
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_back:
                BrowsingHistoryActivity.this.finish();
                break;
            case R.id.tv_clear:
                showDialog();
                break;
            case R.id.tv_edit:
                doEdit();
                break;
            case R.id.tv_delete:
                doDelete();
                break;
            case R.id.iv_checkbox:
                doSelected();
                break;
        }
    }

    private void doSelected() {
        if (mAdapter != null) {
            mAdapter.isAllChechked(!mIsChecked);
            changField(true);
            mAdapter.notifyDataSetChanged();
        }
        mIsChecked = !mIsChecked;
    }

    private void doDelete() {
        if (mDeleteList == null)
            mDeleteList = new ArrayList<>();
        Iterator<Integer> iterator = Constant.Data.BrowsingHistoryEditSet.iterator();
        LogUtils.i("删除前选中集合大小为" + Constant.Data.BrowsingHistoryEditSet.size());
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if (mData.size() > next) {
                mDeleteList.add(mData.get(next).entity.id);
            }
        }
        mPresenter.deleteHistory(mDeleteList);
        LogUtils.i("删除集合大小为" + mDeleteList.size());
    }

    private void showDialog() {
        if (mData == null || mData.size() == 0) {
            ToastUtil.showToast(BrowsingHistoryActivity.this, "暂无记录");
            return;
        }
        CustomAlertDialog alertDialog = new CustomAlertDialog(this, "是否清空记录", REQUEST_CODE_FIRST, new CustomAlertDialog.OnDialogButtonClickListener() {
            @Override
            public void onDialogButtonClick(int requestCode, boolean isPositive) {
                if (isPositive)
                    mPresenter.deleteAllHistory();
            }
        });
        alertDialog.show();
    }


    private void doEdit() {
        if (mAdapter == null)
            return;
        if (Constant.Data.BrowsingHistoryEditSet != null)
            Constant.Data.BrowsingHistoryEditSet.clear();
        if (mTvEdit.getText().toString().equals("编辑")) {
            mTvEdit.setText("取消");
            mLlEdit.setVisibility(View.VISIBLE);
            changField(true);
            mAdapter.showEdit(true);
            mAdapter.notifyDataSetChanged();
        } else {
            mIvCheckbox.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
            mIsChecked = false;
            mTvEdit.setText("编辑");
            mLlEdit.setVisibility(View.GONE);
            changField(false);
            mAdapter.showEdit(false);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void changField(boolean b) {
        mIsShowCb = b;
        if (mData != null && mData.size() > 0) {
            for (ScanInfoDetailBean bean : mData) {
                bean.isShowCb = b;
            }
        }
    }

    @Override
    public void onDeleteResult(HttpResult result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (Constant.Data.BrowsingHistoryEditSet != null)
                Constant.Data.BrowsingHistoryEditSet.clear();
            mIvCheckbox.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
            mIsChecked = false;
            ToastUtil.showToast(this, "删除成功");
            initData();
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else {
            ToastUtil.showToast(this, "删除失败");
        }
        if (mDeleteList != null)
            mDeleteList.clear();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Constant.Data.BrowsingHistoryEditSet != null)
            Constant.Data.BrowsingHistoryEditSet.clear();
        if (mPresenter != null)
            mPresenter.unsubcrible();
        if (mRuleHintDialog != null) {
            mRuleHintDialog.dismiss();
            mRuleHintDialog = null;
        }
    }
}
