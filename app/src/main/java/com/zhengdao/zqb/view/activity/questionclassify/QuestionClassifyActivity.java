package com.zhengdao.zqb.view.activity.questionclassify;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.CustomHttpEntity;
import com.zhengdao.zqb.entity.DictionaryValue;
import com.zhengdao.zqb.entity.QuestionClassifyEntity;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.QuestionClassifyAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class QuestionClassifyActivity extends MVPBaseActivity<QuestionClassifyContract.View, QuestionClassifyPresenter> implements QuestionClassifyContract.View, QuestionClassifyAdapter.CallBack {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    private int mDataType;
    private ArrayList<QuestionClassifyEntity> mData = new ArrayList<>();
    private QuestionClassifyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classify);
        ButterKnife.bind(this);
        setTitle("客服中心");
        initData();
    }

    private void initData() {
        mDataType = getIntent().getIntExtra(Constant.Activity.Data, 0);
        mPresenter.getData();
        mData = new ArrayList<>();
        mAdapter = new QuestionClassifyAdapter(QuestionClassifyActivity.this, mData, QuestionClassifyActivity.this);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        switch (mDataType) {
            case 1:
                mRecycleView.scrollToPosition(0);
                break;
            case 2:
                mRecycleView.scrollToPosition(5);
                break;
            case 3:
                mRecycleView.scrollToPosition(9);
                break;
        }
    }

    @Override
    public void onItemChange(int position) {
        if (mData != null && mData.size() > position) {
            boolean isShowDesc = mData.get(position).isShowDesc;
            mData.get(position).setShowDesc(!isShowDesc);
        }
        if (mAdapter != null)
            mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onGetDataResult(CustomHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ArrayList<DictionaryValue> account = httpResult.account;
            if (account != null && account.size() > 0)
                mData.add(new QuestionClassifyEntity(1, "帐号相关", ""));
            if (account != null) {
                for (int i = 0; i < account.size(); i++) {
                    mData.add(new QuestionClassifyEntity(0, TextUtils.isEmpty(account.get(i).option2) ? "" : account.get(i).option2,
                            TextUtils.isEmpty(account.get(i).value) ? "" : account.get(i).value));
                }
            }
            ArrayList<DictionaryValue> user = httpResult.user;
            if (user != null && user.size() > 0)
                mData.add(new QuestionClassifyEntity(1, "用户相关", ""));
            if (user != null) {
                for (int i = 0; i < user.size(); i++) {
                    mData.add(new QuestionClassifyEntity(0, TextUtils.isEmpty(user.get(i).option2) ? "" : user.get(i).option2,
                            TextUtils.isEmpty(user.get(i).value) ? "" : user.get(i).value));
                }
            }
            ArrayList<DictionaryValue> reward = httpResult.reward;
            if (reward != null && reward.size() > 0)
                mData.add(new QuestionClassifyEntity(1, "悬赏主相关", ""));
            if (reward != null) {
                for (int i = 0; i < reward.size(); i++) {
                    mData.add(new QuestionClassifyEntity(0, TextUtils.isEmpty(reward.get(i).option2) ? "" : reward.get(i).option2,
                            TextUtils.isEmpty(reward.get(i).value) ? "" : reward.get(i).value));
                }
            }
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivityForResult(new Intent(this, LoginActivity.class), ACTION_LOGIN);
        } else if (httpResult.code == Constant.HttpResult.FAILD) {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_LOGIN:
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
    }
}
