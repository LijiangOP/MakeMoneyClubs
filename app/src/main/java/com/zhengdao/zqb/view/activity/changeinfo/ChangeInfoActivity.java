package com.zhengdao.zqb.view.activity.changeinfo;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeInfoActivity extends MVPBaseActivity<ChangeInfoContract.View, ChangeInfoPresenter> implements ChangeInfoContract.View {


    @BindView(R.id.iv_title_bar_back)
    ImageView      mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView       mTvTitleBarTitle;
    @BindView(R.id.tv_title_bar_right)
    TextView       mTvTitleBarRight;
    @BindView(R.id.et_input)
    EditText       mEtInput;
    @BindView(R.id.iv_clear)
    ImageView      mIvClear;
    private String mType;
    private String mValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeinfo);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mType = getIntent().getStringExtra(Constant.Activity.Type);
        if (!TextUtils.isEmpty(mType) && mType.equals("userName")) {
            mTvTitleBarTitle.setText("用户名");
            mEtInput.setHint("请输入英文加数字的用户名(只有一次修改机会)");
            mEtInput.setKeyListener(DigitsKeyListener.getInstance("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        } else if (!TextUtils.isEmpty(mType) && mType.equals("nickName")) {
            mEtInput.setHint("请输入昵称");
            mTvTitleBarTitle.setText("呢称");
        } else if (!TextUtils.isEmpty(mType) && mType.equals("qq")) {
            mEtInput.setHint("请输入QQ");
            mTvTitleBarTitle.setText("QQ");
            mEtInput.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
            mEtInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String trim = s.toString().trim();
                        if (!TextUtils.isEmpty(trim) && trim.length() > 12) {
                            mEtInput.setText(trim.substring(0, 12));
                            String value = mEtInput.getText().toString().trim();
                            mEtInput.setSelection(value.length());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        mIvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtInput.setText("");
            }
        });
        mIvTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeInfoActivity.this.finish();
            }
        });
        mTvTitleBarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValue = mEtInput.getText().toString().trim();
                if (!TextUtils.isEmpty(mValue)) {
                    doChangeUserInfo(mValue);
                } else {
                    ToastUtil.showToast(ChangeInfoActivity.this, "输入不能为空");
                }
            }

        });
    }

    private void doChangeUserInfo(String value) {
        try {
            JSONObject json = new JSONObject();
            if (!TextUtils.isEmpty(mType) && mType.equals("userName")) {
                json.put("userName", value);
            } else if (!TextUtils.isEmpty(mType) && mType.equals("nickName")) {
                json.put("nickName", value);
            } else if (!TextUtils.isEmpty(mType) && mType.equals("qq")) {
                json.put("qq", value);
            }
            mPresenter.ChangeUserInfo(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(mType) && mType.equals("userName")) {
                SettingUtils.setAccount(ChangeInfoActivity.this, mValue);
            }
            Intent intent = new Intent();
            intent.putExtra(Constant.Activity.FreedBack, mValue);
            setResult(RESULT_OK, intent);
            ChangeInfoActivity.this.finish();
            RxBus.getDefault().post(new UpDataUserInfoEvent());
            ToastUtil.showToast(ChangeInfoActivity.this, "修改成功!");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(ChangeInfoActivity.this, TextUtils.isEmpty(httpResult.msg) ? "修改失败!" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
