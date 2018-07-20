package com.zhengdao.zqb.view.activity.customservice;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.SwipeBackActivity.CommonDialog;
import com.zhengdao.zqb.entity.CustomHttpEntity;
import com.zhengdao.zqb.entity.DictionaryValue;
import com.zhengdao.zqb.entity.QuestionClassifyEntity;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.advicefeedback.AdviceFeedbackActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.questionclassify.QuestionClassifyActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.adapter.QuestionClassifyAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CustomServiceActivity extends MVPBaseActivity<CustomServiceContract.View, CustomServicePresenter> implements CustomServiceContract.View, View.OnClickListener, QuestionClassifyAdapter.CallBack {

    private static final int ACTION_LOGIN = 001;
    @BindView(R.id.tv_wx)
    TextView       mTvWx;
    @BindView(R.id.tv_qq)
    TextView       mTvQq;
    @BindView(R.id.tv_business)
    TextView       mTvBusiness;
    @BindView(R.id.tv_guess_you_like)
    TextView       mTvGuessYouLike;
    @BindView(R.id.recycle_view)
    RecyclerView   mRecycleView;
    @BindView(R.id.tv_account)
    TextView       mTvAccount;
    @BindView(R.id.tv_user)
    TextView       mTvUser;
    @BindView(R.id.tv_rewardOwner)
    TextView       mTvRewardOwner;
    @BindView(R.id.tv_advice_freedBack)
    TextView       mTvAdviceFreedBack;

    private long mCurrentTimeMillis = 0;
    private CommonDialog mCommonQQDialog;
    private CommonDialog mCommonWechatDialog;
    private ArrayList<QuestionClassifyEntity> mData = new ArrayList<>();
    ;
    private QuestionClassifyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        ButterKnife.bind(this);
        setTitle("客服中心");
        init();
        initClickListener();
    }

    private void init() {
        mPresenter.getData();
        mAdapter = new QuestionClassifyAdapter(CustomServiceActivity.this, mData, CustomServiceActivity.this);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initClickListener() {
        mTvWx.setOnClickListener(this);
        mTvQq.setOnClickListener(this);
        mTvBusiness.setOnClickListener(this);
        mTvAccount.setOnClickListener(this);
        mTvUser.setOnClickListener(this);
        mTvRewardOwner.setOnClickListener(this);
        mTvAdviceFreedBack.setOnClickListener(this);
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
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_wx:
                doGoToWechat();
                break;
            case R.id.tv_qq:
                doGoToQq();
                break;
            case R.id.tv_business:
                doGoToBusiness();
                break;
            case R.id.tv_account:
                doGoToQuestionClassify(1);
                break;
            case R.id.tv_user:
                doGoToQuestionClassify(2);
                break;
            case R.id.tv_rewardOwner:
                doGoToQuestionClassify(3);
                break;
            case R.id.tv_advice_freedBack:
                doGoToFreedBack();
                break;
        }
    }

    private void isShowDesc(ImageView ivModuleFinishTask, TextView tvModuleFinishTask) {
        String trim = tvModuleFinishTask.getText().toString().trim();
        if (TextUtils.isEmpty(trim))
            return;
        if (ivModuleFinishTask.getRotation() == 0) {
            tvModuleFinishTask.setVisibility(View.VISIBLE);
            ivModuleFinishTask.setRotation(180);
        } else {
            tvModuleFinishTask.setVisibility(View.GONE);
            ivModuleFinishTask.setRotation(0);
        }
    }

    private void doGoToQuestionClassify(int type) {
        Intent intent = new Intent(this, QuestionClassifyActivity.class);
        intent.putExtra(Constant.Activity.Data, type);
        startActivity(intent);
    }

    private void doGoToWechat() {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", "zqb88cn");
        cm.setPrimaryClip(mClipData);
        if (mCommonWechatDialog == null)
            mCommonWechatDialog = new CommonDialog(this);
        mCommonWechatDialog.initView(getString(R.string.consult_wechat), "", "打开微信", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonWechatDialog != null)
                    mCommonWechatDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                    startActivity(intent);
                    if (mCommonWechatDialog != null)
                        mCommonWechatDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(CustomServiceActivity.this, "请检查是否安装微信");
                }
            }
        });
        mCommonWechatDialog.show();
    }

    private void doGoToQq() {
        if (mCommonQQDialog == null)
            mCommonQQDialog = new CommonDialog(this);
        mCommonQQDialog.initView(getString(R.string.consult_qq), "", "打开QQ", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonQQDialog != null)
                    mCommonQQDialog.dismiss();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=2660841136";//uin是发送过去的qq号码
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    if (mCommonQQDialog != null)
                        mCommonQQDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showToast(CustomServiceActivity.this, "请检查是否安装QQ");
                }
            }
        });
        mCommonQQDialog.show();
    }

    private void doGoToBusiness() {
        //商务合作
        Intent issue = new Intent(this, WebViewActivity.class);
        issue.putExtra(Constant.WebView.TITLE, "商务合作");
        issue.putExtra(Constant.WebView.URL, Constant.Url.BASEURL + "/zhaoshang");
        startActivity(issue);
    }

    private void doGoToFreedBack() {
        startActivity(new Intent(this, AdviceFeedbackActivity.class));
    }

    @Override
    public void onGetDataResult(CustomHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ArrayList<DictionaryValue> like = httpResult.like;
            if (like != null && like.size() > 0) {
                mTvGuessYouLike.setVisibility(View.VISIBLE);
            } else {
                mTvGuessYouLike.setVisibility(View.GONE);
            }
            if (like != null) {
                for (int i = 0; i < like.size(); i++) {
                    mData.add(0, new QuestionClassifyEntity(0, TextUtils.isEmpty(like.get(i).option2) ? "" : like.get(i).option2,
                            TextUtils.isEmpty(like.get(i).value) ? "" : like.get(i).value));
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
