package com.zhengdao.zqb.view.activity.rewardscheck;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.KindOfWantedEntity;
import com.zhengdao.zqb.entity.KindOfWantedHttpEntity;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.kindofwanted.KindOfWantedActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.KindOfWantedAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * <p>
 * 最近：       state 1 flag="";
 * 急需审核     state 1 flag="1";
 * 审核不通过   state=2;
 * 未提交      state=0;
 */
public class RewardsCheckActivity extends MVPBaseActivity<RewardsCheckContract.View, RewardsCheckPresenter> implements RewardsCheckContract.View, View.OnClickListener {

    @BindView(R.id.ll_un_check)
    LinearLayout       mLlUnCheck;
    @BindView(R.id.ll_un_pass)
    LinearLayout       mLlUnPass;
    @BindView(R.id.ll_un_submit)
    LinearLayout       mLlUnSubmit;
    @BindView(R.id.listView)
    ListView           mListView;
    @BindView(R.id.ll_passed)
    LinearLayout       mLlPassed;
    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private long mCurrentTimeMillis = 0;
    private ArrayList<Object>   mData;
    private KindOfWantedAdapter mAdapter;
    private Disposable          mUpDataDisposable;
    private int mCurrentpage = 1;
    private boolean mHasNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewardscheck);
        ButterKnife.bind(this);
        setTitle("悬赏审核");
        init();
    }

    private void init() {
        mLlUnCheck.setOnClickListener(this);
        mLlUnPass.setOnClickListener(this);
        mLlUnSubmit.setOnClickListener(this);
        mLlPassed.setOnClickListener(this);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mCurrentpage = 1;
                mPresenter.getData(mCurrentpage);
                refreshlayout.finishRefresh();
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (mHasNextPage) {
                    mCurrentpage++;
                    mPresenter.getData(mCurrentpage);
                }
                refreshlayout.finishLoadmore();
            }
        });
        mUpDataDisposable = RxBus.getDefault().toObservable(UpDataUserInfoEvent.class).subscribe(new Consumer<UpDataUserInfoEvent>() {
            @Override
            public void accept(UpDataUserInfoEvent upDataUserInfoEvent) throws Exception {
                mCurrentpage = 1;
                mPresenter.getData(mCurrentpage);
            }
        });
        mPresenter.getData(mCurrentpage);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_un_check:
                Intent unCheck = new Intent(RewardsCheckActivity.this, KindOfWantedActivity.class);
                unCheck.putExtra(Constant.Activity.Type, "unCheck");
                startActivity(unCheck);
                break;
            case R.id.ll_un_pass:
                Intent unPass = new Intent(RewardsCheckActivity.this, KindOfWantedActivity.class);
                unPass.putExtra(Constant.Activity.Type, "unPass");
                startActivity(unPass);
                break;
            case R.id.ll_un_submit:
                Intent unSubmit = new Intent(RewardsCheckActivity.this, KindOfWantedActivity.class);
                unSubmit.putExtra(Constant.Activity.Type, "unSubmit");
                startActivity(unSubmit);
                break;
            case R.id.ll_passed:
                Intent passed = new Intent(RewardsCheckActivity.this, KindOfWantedActivity.class);
                passed.putExtra(Constant.Activity.Type, "passed");
                startActivity(passed);
                break;
        }
    }

    @Override
    public void onGetDataResult(KindOfWantedHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult.pageTasks == null || httpResult.pageTasks.list == null) {
                mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
                return;
            }
            List<KindOfWantedEntity> list = httpResult.pageTasks.list;
            mHasNextPage = httpResult.pageTasks.hasNextPage;
            if (mData == null)
                mData = new ArrayList<>();
            if (mCurrentpage == 1)
                mData.clear();
            mData.addAll(list);
            if (mAdapter == null) {
                mAdapter = new KindOfWantedAdapter(this, mData, 1);
                mListView.setAdapter(mAdapter);
            } else
                mAdapter.notifyDataSetChanged();
            if (mHasNextPage) {
                mSmartRefreshLayout.resetNoMoreData();
            } else {
                mSmartRefreshLayout.finishLoadmoreWithNoMoreData();
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpDataDisposable != null)
            mUpDataDisposable.dispose();
    }
}
