package com.zhengdao.zqb.view.activity.wantednotpassdetail;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.type;

public class WantedNotPassDetailActivity extends MVPBaseActivity<WantedNotPassDetailContract.View, WantedNotPassDetailPresenter> implements WantedNotPassDetailContract.View, View.OnClickListener {

    @BindView(R.id.et_input)
    EditText  mEtInput;
    @BindView(R.id.tv_descibe_count)
    TextView  mTvDescibeCount;
    @BindView(R.id.tv_commit)
    TextView  mTvCommit;
    @BindView(R.id.iv_type_do_not_find)
    ImageView mIvTypeDoNotFind;
    @BindView(R.id.iv_type_inconformity)
    ImageView mIvTypeInconformity;
    @BindView(R.id.iv_type_other)
    ImageView mIvTypeOther;

    private long mCurrentTimeMillis = 0;
    private int  mCbType            = 0;
    public int editStart;
    public int editEnd;
    public int MaxNum = 100;
    private int mTaskId;
    private int mRwId;
    private int mUserId;
    private boolean mIsSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wanted_not_pass_detail);
        ButterKnife.bind(this);
        setTitle("审核详情");
        init();
    }

    private void init() {
        mTaskId = getIntent().getIntExtra(Constant.Activity.Data, 0);
        mRwId = getIntent().getIntExtra(Constant.Activity.Data1, 0);
        mUserId = getIntent().getIntExtra(Constant.Activity.Data2, 0);
        mIvTypeDoNotFind.setOnClickListener(this);
        mIvTypeInconformity.setOnClickListener(this);
        mIvTypeOther.setOnClickListener(this);
        mTvCommit.setOnClickListener(this);
        mEtInput.addTextChangedListener(mTextWatcher);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = mEtInput.getSelectionStart();
            editEnd = mEtInput.getSelectionEnd();
            mEtInput.removeTextChangedListener(mTextWatcher);
            while (calculateLength(s.toString()) > MaxNum) {
                s.delete(editStart - 1, editEnd);
                editStart--;
                editEnd--;
            }
            mEtInput.addTextChangedListener(mTextWatcher);
            mTvDescibeCount.setText(String.valueOf(calculateLength(mEtInput.getText().toString())));
        }
    };

    public static long calculateLength(CharSequence cs) {
        double len = 0;
        for (int i = 0; i < cs.length(); i++) {
            int tmp = (int) cs.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 1;
            } else {
                len++;
            }
        }
        long round = Math.round(len);
        return new Long(round).intValue();
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_type_do_not_find:
                if (!mIsSelected) {
                    mCbType = 1;
                    mIvTypeDoNotFind.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvTypeInconformity.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIsSelected = true;
                } else {
                    mCbType = 0;
                    mIvTypeDoNotFind.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIsSelected = false;
                }
                break;
            case R.id.iv_type_inconformity:
                if (!mIsSelected) {
                    mCbType = 2;
                    mIvTypeDoNotFind.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeInconformity.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIsSelected = true;
                } else {
                    mCbType = 0;
                    mIvTypeInconformity.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIsSelected = false;
                }
                break;
            case R.id.iv_type_other:
                if (!mIsSelected) {
                    mCbType = 3;
                    mIvTypeDoNotFind.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeInconformity.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select));
                    mIsSelected = true;
                } else {
                    mCbType = 0;
                    mIvTypeOther.setImageDrawable(getResources().getDrawable(R.drawable.btn_select_r));
                    mIsSelected = false;
                }
                break;
            case R.id.tv_commit:
                doCommit();
                break;
        }
    }

    private void doCommit() {
        if (mCbType == 0) {
            ToastUtil.showToast(WantedNotPassDetailActivity.this, "请选择不通过的原因");
        } else {
            String trim = mEtInput.getText().toString().trim();
            switch (mCbType) {
                case 1:
                    trim = "未查询到有效订单，请核实后再次提交".concat(trim);
                    break;
                case 2:
                    trim = "查询到有效订单，但是不符合悬赏条件/限制".concat(trim);
                    break;
                case 3:
                    if (TextUtils.isEmpty(trim)){
                        ToastUtil.showToast(WantedNotPassDetailActivity.this,"请输入描述");
                        return;
                    }
                    break;
            }
            mPresenter.doCommit("noadopt", mTaskId, mRwId, TextUtils.isEmpty(trim) ? "" : trim, mUserId);
        }
    }

    @Override
    public void onConfirmCheckFinished(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(this, "操作成功");
            RxBus.getDefault().post(new UpDataUserInfoEvent());
            doFinish();
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    private void doFinish() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        WantedNotPassDetailActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
