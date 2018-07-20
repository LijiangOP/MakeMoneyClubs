package com.zhengdao.zqb.view.activity.kindofwanteddetail;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kennyc.view.MultiStateView;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.ReuseListView;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.KindOfWantedDetailHttpEntity;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.setting.SettingActivity;
import com.zhengdao.zqb.view.activity.wantednotpassdetail.WantedNotPassDetailActivity;
import com.zhengdao.zqb.view.adapter.KindOfWantedDetailAdapter;
import com.zhengdao.zqb.view.adapter.KindOfWantedDetailTextAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.Disposable;

public class KindOfWantedDetailActivity extends MVPBaseActivity<KindOfWantedDetailContract.View, KindOfWantedDetailPresenter> implements KindOfWantedDetailContract.View, View.OnClickListener {


    private static final int ACTION_NOT_PASS = 001;
    @BindView(R.id.iv_user_icon)
    CircleImageView mIvUserIcon;
    @BindView(R.id.tv_nickname)
    TextView        mTvNickname;
    @BindView(R.id.tv_username)
    TextView        mTvUsername;
    @BindView(R.id.tv_attention)
    TextView        mTvAttention;
    @BindView(R.id.tv_user_nickname)
    TextView        mTvUserNickname;
    @BindView(R.id.recycle_view)
    RecyclerView    mRecycleView;
    @BindView(R.id.listView)
    ReuseListView   mListView;
    @BindView(R.id.tv_no_pass)
    TextView        mTvNoPass;
    @BindView(R.id.tv_passed)
    TextView        mTvPassed;
    @BindView(R.id.multiStateView)
    MultiStateView  mMultiStateView;
    @BindView(R.id.ll_user_info)
    LinearLayout    mLlUserInfo;
    @BindView(R.id.tv_usrinfo_tag)
    TextView        mTvUsrinfoTag;

    private long mCurrentTimeMillis = 0;
    private ArrayList                     mImgData;
    private ArrayList                     mTextData;
    private KindOfWantedDetailAdapter     mImgAdapter;
    private KindOfWantedDetailTextAdapter mTextAdapter;
    private int                           mWantedId;
    private int                           mUserId;
    private int                           mTaskId;
    private int                           mRwId;
    private int                           mType;
    private Disposable                    mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kindofwanted_detail);
        ButterKnife.bind(this);
        setTitle("审核详情");
        init();
        initClickListener();
    }

    private void init() {
        mWantedId = getIntent().getIntExtra(Constant.Activity.Data, 0);
        mType = getIntent().getIntExtra(Constant.Activity.Type, 0);
        mMultiStateView.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.getData(mWantedId);
            }
        });
        mPresenter.getData(mWantedId);
        if (mType != 1) {
            mTvPassed.setVisibility(View.GONE);
            mTvNoPass.setVisibility(View.GONE);
        }
    }

    private void initClickListener() {
        mLlUserInfo.setOnClickListener(this);
        mTvAttention.setOnClickListener(this);
        mTvPassed.setOnClickListener(this);
        mTvNoPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_user_info:
                Intent intent = new Intent(KindOfWantedDetailActivity.this, SettingActivity.class);
                intent.putExtra(Constant.Activity.Skip, "Skip_To_Certification");
                Utils.StartActivity(KindOfWantedDetailActivity.this, intent);
                break;
            case R.id.tv_attention:
                doAttention();
                break;
            case R.id.tv_passed:
                doPassed();
                break;
            case R.id.tv_no_pass:
                doNotPass();
                break;
        }
    }

    private void doAttention() {
        if (mTvAttention.getText().equals("关注"))
            mPresenter.AddAttention(mUserId);
    }

    private void doPassed() {
        mPresenter.ConfirmCheck("adopt", mTaskId, mRwId, "", mUserId);
    }

    private void doNotPass() {
        if (!SettingUtils.isLogin(KindOfWantedDetailActivity.this))
            startActivity(new Intent(this, LoginActivity.class));
        else {
            Intent intent = new Intent(KindOfWantedDetailActivity.this, WantedNotPassDetailActivity.class);
            intent.putExtra(Constant.Activity.Data, mTaskId);
            intent.putExtra(Constant.Activity.Data1, mRwId);
            intent.putExtra(Constant.Activity.Data2, mUserId);
            startActivityForResult(intent, ACTION_NOT_PASS);
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onGetDataResult(KindOfWantedDetailHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            showView(httpResult);
            mUserId = httpResult.task.user.id;
            mTaskId = httpResult.task.id;
            mRwId = httpResult.task.rwId;
            if (httpResult.task.taskPics != null && httpResult.task.taskPics.size() > 0) {
                if (mImgData == null)
                    mImgData = new ArrayList();
                mImgData.clear();
                mImgData.addAll(httpResult.task.taskPics);
                if (mImgAdapter == null) {
                    mImgAdapter = new KindOfWantedDetailAdapter(KindOfWantedDetailActivity.this, mImgData);
                    mRecycleView.setAdapter(mImgAdapter);
                    LinearLayoutManager ms = new LinearLayoutManager(this);
                    ms.setOrientation(LinearLayoutManager.HORIZONTAL);
                    mRecycleView.setLayoutManager(ms);
                } else
                    mImgAdapter.notifyDataSetChanged();
            }
            if (httpResult.task.taskUserInfos != null && httpResult.task.taskUserInfos.size() > 0) {
                if (mTextData == null)
                    mTextData = new ArrayList();
                mTextData.clear();
                mTextData.addAll(httpResult.task.taskUserInfos);
                if (mTextAdapter == null) {
                    mTextAdapter = new KindOfWantedDetailTextAdapter(KindOfWantedDetailActivity.this, mTextData);
                    mListView.setAdapter(mTextAdapter);
                } else
                    mTextAdapter.notifyDataSetChanged();
            } else
                mTvUsrinfoTag.setVisibility(View.GONE);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(KindOfWantedDetailActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onAddAttentionFinished(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, "添加关注成功");
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    public void onConfirmCheckFinished(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "操作成功" : httpResult.msg);
            RxBus.getDefault().post(new UpDataUserInfoEvent());
            this.finish();
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    private void showView(KindOfWantedDetailHttpEntity httpResult) {
        mTvNickname.setText(TextUtils.isEmpty(httpResult.task.user.nickName) ? "" : httpResult.task.user.nickName);
        mTvUsername.setText(TextUtils.isEmpty(httpResult.task.user.userName) ? "" : "用户名：" + httpResult.task.user.userName);
        mTvUserNickname.setText(TextUtils.isEmpty(httpResult.task.user.nickName) ? "" : httpResult.task.user.nickName);
        Glide.with(this).load(httpResult.task.user.avatar).error(R.drawable.net_less_140).into(mIvUserIcon);
        if (httpResult.task.value == 1) {
            mTvAttention.setText("已关注");
            mTvAttention.setBackgroundColor(getResources().getColor(R.color.white));
            mTvAttention.setTextColor(getResources().getColor(R.color.color_b3b3b3));
        } else {
            mTvAttention.setText("关注");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                mTvAttention.setBackground(getResources().getDrawable(R.drawable.shape_rect_five_red));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTION_NOT_PASS:
                KindOfWantedDetailActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }
    }
}
