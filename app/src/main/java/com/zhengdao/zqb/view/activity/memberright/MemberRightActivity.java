package com.zhengdao.zqb.view.activity.memberright;


import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.view.activity.pay.PayActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberRightActivity extends MVPBaseActivity<MemberRightContract.View, MemberRightPresenter> implements MemberRightContract.View, View.OnClickListener {

    @BindView(R.id.iv_title_bar_back)
    ImageView mIvTitleBarBack;
    @BindView(R.id.tv_chuangtou)
    TextView  mTvChuangtou;
    @BindView(R.id.tv_chuangke)
    TextView  mTvChuangke;
    private long mCurrentTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_right);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mIvTitleBarBack.setOnClickListener(this);
        SpannableString chuangtou = new SpannableString("开通创投会员\n会员特惠¥9988 原价¥11800");
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);
        chuangtou.setSpan(sizeSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTvChuangtou.setText(chuangtou);
        mTvChuangtou.setOnClickListener(this);
        SpannableString chuangke = new SpannableString("开通创客会员\n会员特惠¥988 原价¥1200");
        chuangke.setSpan(sizeSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTvChuangke.setText(chuangke);
        mTvChuangke.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_bar_back:
                MemberRightActivity.this.finish();
                break;
            case R.id.tv_chuangtou:
                Intent chuangtou = new Intent(MemberRightActivity.this, PayActivity.class);
                chuangtou.putExtra(Constant.Activity.Price, 9988);
                startActivity(chuangtou);
                break;
            case R.id.tv_chuangke:
                Intent chuangke = new Intent(MemberRightActivity.this, PayActivity.class);
                chuangke.putExtra(Constant.Activity.Price, 988);
                startActivity(chuangke);
                break;
        }
    }
}
