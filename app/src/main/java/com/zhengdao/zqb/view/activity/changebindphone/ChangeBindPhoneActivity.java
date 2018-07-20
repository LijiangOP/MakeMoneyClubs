package com.zhengdao.zqb.view.activity.changebindphone;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.view.activity.identityvertify.IdentityVertifyActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChangeBindPhoneActivity extends MVPBaseActivity<ChangeBindPhoneContract.View, ChangeBindPhonePresenter> implements ChangeBindPhoneContract.View, View.OnClickListener {

    @BindView(R.id.ll_can_get_code)
    LinearLayout mLlCanGetCode;
    @BindView(R.id.ll_can_not_get_code)
    LinearLayout mLlCanNotGetCode;
    @BindView(R.id.tv_hint)
    TextView     mTvHint;
    private long mCurrentTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changebindphone);
        ButterKnife.bind(this);
        setTitle("修改绑定手机");
        init();
    }

    private void init() {
        mLlCanGetCode.setOnClickListener(this);
        mLlCanNotGetCode.setOnClickListener(this);
        SpannableString spannableString = new SpannableString("*如无法自助修改，请联系客服协助。");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#fc3135"));
        spannableString.setSpan(colorSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTvHint.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_can_get_code:
                Intent canGetCode = new Intent(ChangeBindPhoneActivity.this, IdentityVertifyActivity.class);
                canGetCode.putExtra(Constant.Activity.Skip, "change_bind_phone");
                startActivity(canGetCode);
                break;
            case R.id.ll_can_not_get_code:
                Intent canNotGetCode = new Intent(ChangeBindPhoneActivity.this, IdentityVertifyActivity.class);
                canNotGetCode.putExtra(Constant.Activity.Skip, "change_bind_phone");
                canNotGetCode.putExtra(Constant.Activity.Type, 1);
                startActivity(canNotGetCode);
                break;
        }
    }
}
