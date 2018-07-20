package com.zhengdao.zqb.view.activity.redpacketdetail;


import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.RedPacketAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class RedpacketDetailActivity extends MVPBaseActivity<RedpacketDetailContract.View, RedpacketDetailPresenter> implements RedpacketDetailContract.View {

    @BindView(R.id.iv_icon)
    CircleImageView mIvIcon;
    @BindView(R.id.tv_name)
    TextView        mTvName;
    @BindView(R.id.tv_desc)
    TextView        mTvDesc;
    @BindView(R.id.tv_number)
    TextView        mTvNumber;
    @BindView(R.id.lv_listview)
    ListView        mLvListview;
    @BindView(R.id.tv_detail)
    TextView        mTvDetail;
    @BindView(R.id.multiStateView)
    MultiStateView  mMultiStateView;

    private String           mName;
    private int              mGetRPNumber;
    private int              mTotalRPNumber;
    private int              mTotal;
    private int              mBalance;
    private ArrayList        mData;
    private RedPacketAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redpacket_detail);
        ButterKnife.bind(this);
        setTitle("红包详情");
        init();
    }

    private void init() {
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData();
            }
        });
        mPresenter.getData();
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onGetDataFinished(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mTvName.setText(TextUtils.isEmpty(mName) ? "" : mName + "发的红包");
            mTvDesc.setText("全网最高价，不信你就比，只为开单");
            SpannableString spannableString = new SpannableString("共" + mTotal + "元");
            spannableString.setSpan(new RelativeSizeSpan(2f), 1, String.valueOf(mTotal).length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvNumber.setText(spannableString);
            mTvDetail.setText("已领取" + mGetRPNumber + "/" + mTotalRPNumber + "个，还剩" + mBalance + "元未领");
            if (mData == null)
                mData = new ArrayList();
            mData.clear();
            // 添加数据
            if (mAdapter == null)
                mAdapter = new RedPacketAdapter(RedpacketDetailActivity.this, mData);
            mLvListview.setAdapter(mAdapter);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        }  else {
            ToastUtil.showToast(RedpacketDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }
}
