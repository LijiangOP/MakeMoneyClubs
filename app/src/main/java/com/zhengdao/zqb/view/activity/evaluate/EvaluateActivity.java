package com.zhengdao.zqb.view.activity.evaluate;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EvaluateActivity extends MVPBaseActivity<EvaluateContract.View, EvaluatePresenter> implements EvaluateContract.View, View.OnClickListener {

    @BindView(R.id.iv_shop_icon)
    ImageView mIvShopIcon;
    @BindView(R.id.iv_star_1)
    ImageView mIvStar1;
    @BindView(R.id.iv_star_2)
    ImageView mIvStar2;
    @BindView(R.id.iv_star_3)
    ImageView mIvStar3;
    @BindView(R.id.iv_star_4)
    ImageView mIvStar4;
    @BindView(R.id.iv_star_5)
    ImageView mIvStar5;
    @BindView(R.id.tv_star)
    TextView  mTvStar;
    @BindView(R.id.editText)
    EditText  mEditText;
    @BindView(R.id.cb_anonymity)
    CheckBox  mCbAnonymity;
    @BindView(R.id.tv_publish)
    TextView  mTvPublish;

    private int mStars;
    private int mWantedId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        ButterKnife.bind(this);
        setTitle("发布评价");
        init();
    }

    private void init() {
        mWantedId = getIntent().getIntExtra(Constant.Activity.Data, 0);
        Glide.with(this).load("").error(R.drawable.net_less_36).into(mIvShopIcon);
        initListener();
    }

    private void initListener() {
        mIvStar1.setOnClickListener(this);
        mIvStar2.setOnClickListener(this);
        mIvStar3.setOnClickListener(this);
        mIvStar4.setOnClickListener(this);
        mIvStar5.setOnClickListener(this);
        mTvPublish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_star_1:
                doStars(1);
                break;
            case R.id.iv_star_2:
                doStars(2);
                break;
            case R.id.iv_star_3:
                doStars(3);
                break;
            case R.id.iv_star_4:
                doStars(4);
                break;
            case R.id.iv_star_5:
                doStars(5);
                break;
            case R.id.tv_publish:
                doPublish();
                break;
        }
    }

    private void doStars(int position) {
        mStars = position;
        switch (position) {
            case 1:
                mIvStar1.setImageResource(R.drawable.btn_star1);
                mIvStar2.setImageResource(R.drawable.btn_star);
                mIvStar3.setImageResource(R.drawable.btn_star);
                mIvStar4.setImageResource(R.drawable.btn_star);
                mIvStar5.setImageResource(R.drawable.btn_star);
                mTvStar.setText("非常差");
                break;
            case 2:
                mIvStar1.setImageResource(R.drawable.btn_star1);
                mIvStar2.setImageResource(R.drawable.btn_star1);
                mIvStar3.setImageResource(R.drawable.btn_star);
                mIvStar4.setImageResource(R.drawable.btn_star);
                mIvStar5.setImageResource(R.drawable.btn_star);
                mTvStar.setText("差");
                break;
            case 3:
                mIvStar1.setImageResource(R.drawable.btn_star1);
                mIvStar2.setImageResource(R.drawable.btn_star1);
                mIvStar3.setImageResource(R.drawable.btn_star1);
                mIvStar4.setImageResource(R.drawable.btn_star);
                mIvStar5.setImageResource(R.drawable.btn_star);
                mTvStar.setText("一般");
                break;
            case 4:
                mIvStar1.setImageResource(R.drawable.btn_star1);
                mIvStar2.setImageResource(R.drawable.btn_star1);
                mIvStar3.setImageResource(R.drawable.btn_star1);
                mIvStar4.setImageResource(R.drawable.btn_star1);
                mIvStar5.setImageResource(R.drawable.btn_star);
                mTvStar.setText("满意");
                break;
            case 5:
                mIvStar1.setImageResource(R.drawable.btn_star1);
                mIvStar2.setImageResource(R.drawable.btn_star1);
                mIvStar3.setImageResource(R.drawable.btn_star1);
                mIvStar4.setImageResource(R.drawable.btn_star1);
                mIvStar5.setImageResource(R.drawable.btn_star1);
                mTvStar.setText("非常满意");
                break;
        }
    }

    private void doPublish() {
        String trim = mEditText.getText().toString().trim();
        if (TextUtils.isEmpty(trim) || trim.length() < 10) {
            ToastUtil.showToast(EvaluateActivity.this, "请输入不少于10个字的描述");
            return;
        }
        boolean checked = mCbAnonymity.isChecked();
        int stars = mStars;
        mPresenter.doPublish(mWantedId);
    }

    @Override
    public void onDoPublicResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, "操作成功");
        }else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "操作失败" : httpResult.msg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
