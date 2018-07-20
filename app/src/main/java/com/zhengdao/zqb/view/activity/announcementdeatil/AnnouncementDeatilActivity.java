package com.zhengdao.zqb.view.activity.announcementdeatil;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.AnnouncementDetailBean;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AnnouncementDeatilActivity extends MVPBaseActivity<AnnouncementDeatilContract.View, AnnouncementDeatilPresenter> implements AnnouncementDeatilContract.View {

    private static final int REQUEST_CODE = 101;
    @BindView(R.id.tv_anm_detail_title)
    TextView       mTvAnmDetailTitle;
    @BindView(R.id.tv_anm_detail_content)
    TextView       mTvAnmDetailContent;
    @BindView(R.id.tv_anm_detail_time)
    TextView       mTvAnmDetailTime;
    @BindView(R.id.multiStateView)
    MultiStateView mMultiStateView;
    private int mId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_deatil);
        ButterKnife.bind(this);
        setTitle("公告详情");
        initData();
    }

    private void initData() {
        mId = getIntent().getIntExtra(Constant.Activity.Data, 0);
        LogUtils.i("mId=" + mId);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData(mId);
            }
        });
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY);
        mPresenter.getData(mId);
    }

    @Override
    public void showView(HttpResult<AnnouncementDetailBean> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            mTvAnmDetailContent.setText("\u3000\u3000" + Html.fromHtml(httpResult.data.content));
            mTvAnmDetailTime.setText(httpResult.data.updateSTime);
            mTvAnmDetailTitle.setText(httpResult.data.title);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivityForResult(new Intent(this, LoginActivity.class), REQUEST_CODE);
        } else {
            ToastUtil.showToast(this, httpResult.msg);
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            mPresenter.getData(mId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
    }
}
