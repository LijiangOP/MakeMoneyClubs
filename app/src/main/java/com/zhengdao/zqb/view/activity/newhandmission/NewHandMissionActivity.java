package com.zhengdao.zqb.view.activity.newhandmission;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.SignedWindow;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.NewBieHttpEntity;
import com.zhengdao.zqb.entity.TaskEntity;
import com.zhengdao.zqb.event.NewHandUpDataEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.dailysign.DailySignActivity;
import com.zhengdao.zqb.view.activity.dailywechatshare.DailyWeChatShareActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.NewHandMissionAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class NewHandMissionActivity extends MVPBaseActivity<NewHandMissionContract.View, NewHandMissionPresenter> implements NewHandMissionContract.View, NewHandMissionAdapter.CallBack {

    private static final int REQUESTCODE = 001;
    @BindView(R.id.tv_balance)
    TextView           mTvBalance;
    @BindView(R.id.multiStateView)
    MultiStateView     mMultiStateView;
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout mSwiperefreshlayout;
    @BindView(R.id.recycle_view)
    RecyclerView       mRecycleView;

    private NewHandMissionAdapter mAdapter;
    private List<TaskEntity>      mData;
    private Disposable            mUpDataDisposable;
    private Double                mUseableSum;
    private SignedWindow          mSignedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newhand_mission);
        ButterKnife.bind(this);
        setTitle("每日任务");
        init();
    }

    private void init() {
        mSwiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getData();
                mSwiperefreshlayout.setRefreshing(false);
            }
        });
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData();
            }
        });
        mUpDataDisposable = RxBus.getDefault().toObservable(NewHandUpDataEvent.class).subscribe(new Consumer<NewHandUpDataEvent>() {
            @Override
            public void accept(NewHandUpDataEvent newHandUpDataEvent) throws Exception {
                mPresenter.getData();
            }
        });
        mPresenter.getData();
    }

    @Override
    public void showView(NewBieHttpEntity httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                if (httpResult.newbieTasks == null || httpResult.newbieTasks.size() == 0) {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                    return;
                }
                mUseableSum = httpResult.sum;
                mTvBalance.setText("" + new DecimalFormat("#0.00").format(httpResult.sum));
                if (mData == null)
                    mData = new ArrayList<>();
                mData.clear();
                ArrayList<TaskEntity> objects1 = new ArrayList<>();
                ArrayList<TaskEntity> objects2 = new ArrayList<>();
                for (int i = 0; i < httpResult.newbieTasks.size(); i++) {
                    if (httpResult.newbieTasks.get(i).type == 4 || httpResult.newbieTasks.get(i).type == 5) {
                        objects1.add(new TaskEntity(1, httpResult.newbieTasks.get(i)));
                    } else {
                        objects2.add(new TaskEntity(1, httpResult.newbieTasks.get(i)));
                    }
                }
                if (objects1.size() > 0)
                    mData.add(new TaskEntity(0, "每日任务"));
                mData.addAll(objects1);
                if (objects2.size() > 0)
                    mData.add(new TaskEntity(0, "新手任务"));
                mData.addAll(objects2);
                mData.add(new TaskEntity(10));//广告
                if (mAdapter == null) {
                    mAdapter = new NewHandMissionAdapter(NewHandMissionActivity.this, mData, this);
                    mRecycleView.setAdapter(mAdapter);
                    mRecycleView.setLayoutManager(new LinearLayoutManager(this));
                } else
                    mAdapter.notifyDataSetChanged();
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(this, "登录超时,请重新登录");
                startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
            } else {
                ToastUtil.showToast(NewHandMissionActivity.this, TextUtils.isEmpty(httpResult.msg) ? "获取数据失败" : httpResult.msg);
                mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            }
        } catch (Exception ex) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
            LogUtils.e(ex.getMessage());
        }
    }

    @Override
    public void onSign(boolean state) {
        Intent intent = new Intent(this, DailySignActivity.class);
        startActivity(intent);
    }

    @Override
    public void onShare() {
        Intent intent = new Intent(this, DailyWeChatShareActivity.class);
        intent.putExtra(Constant.Activity.Data, mUseableSum);
        startActivity(intent);
    }

    @Override
    public void onBaiduAdvClick() {
        mPresenter.getSeeAdvReward(2, 1);
    }

    @Override
    public void onTencentAdvClick() {
        mPresenter.getSeeAdvReward(2, 2);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUESTCODE:
                if (data != null) {
                    boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                    if (booleanExtra)
                        mPresenter.getData();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpDataDisposable != null)
            mUpDataDisposable.dispose();
        if (mSignedDialog != null) {
            mSignedDialog.dismiss();
            mSignedDialog = null;
        }
    }
}
