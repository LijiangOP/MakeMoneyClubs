package com.zhengdao.zqb.view.activity.identitycertify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IdentityCertifyActivity extends MVPBaseActivity<IdentityCertifyContract.View, IdentityCertifyPresenter> implements IdentityCertifyContract.View {

    @BindView(R.id.et_real_name)
    EditText mEtRealName;
    @BindView(R.id.et_identity_num)
    EditText mEtIdentityNum;
    @BindView(R.id.et_alipay_account)
    EditText mEtAlipayAccount;
    @BindView(R.id.tv_commite)
    TextView mTvCommite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_certify);
        ButterKnife.bind(this);
        setTitle("实名认证");
        init();
    }

    private void init() {
        mTvCommite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commiteInfo();
            }
        });
    }

    private void commiteInfo() {
        String name = mEtRealName.getText().toString().trim();
        String sfz = mEtIdentityNum.getText().toString().trim();
        String alipay = mEtAlipayAccount.getText().toString().trim();
        JSONObject jsonObject = new JSONObject();
        try {
            if (!TextUtils.isEmpty(name))
                jsonObject.put("name", name);
            if (!TextUtils.isEmpty(sfz))
                jsonObject.put("sfz", sfz);
            if (!TextUtils.isEmpty(alipay))
                jsonObject.put("alipay", alipay);
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.showToast(this, "输入有误");
            return;
        }
        mPresenter.editUserInfo(jsonObject.toString());
    }

    @Override
    public void showEditResult(HttpResult httpResult) {

    }
}
