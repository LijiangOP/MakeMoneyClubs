package com.zhengdao.zqb.view.activity.chargeaccount;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.HintDialog;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.pay.AliPay;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.walletlist.WalletListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChargeAccountActivity extends MVPBaseActivity<ChargeAccountContract.View, ChargeAccountPresenter> implements ChargeAccountContract.View, View.OnClickListener, AliPay.PayCallBackListener {
    @BindView(R.id.iv_title_bar_back)
    ImageView mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView  mTvTitleBarTitle;
    @BindView(R.id.tv_title_bar_right)
    TextView  mTvTitleBarRight;
    @BindView(R.id.et_number)
    EditText  mEtNumber;
    @BindView(R.id.tv_charge)
    TextView  mTvCharge;

    private long mCurrentTimeMillis = 0;
    private PopupWindow mPopupWindow;
    private String      mNumber;
    private int mChargeType = 1;//默认是支付宝
    private HintDialog mChargeHintDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargeaccount);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTvCharge.setOnClickListener(this);
        mIvTitleBarBack.setOnClickListener(this);
        mTvTitleBarRight.setOnClickListener(this);
        mTvTitleBarTitle.setText("账户充值");
        mTvTitleBarRight.setText("充值记录");
    }


    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_bar_back:
                ChargeAccountActivity.this.finish();
                break;
            case R.id.tv_title_bar_right:
                Intent intent = new Intent(ChargeAccountActivity.this, WalletListActivity.class);
                intent.putExtra(Constant.Activity.Type, "charge_record");
                startActivity(intent);
                break;
            case R.id.tv_charge:
                hideSoftInput();
                showChargeType();
                break;
            case R.id.tv_confirm:
                hideSoftInput();
                mPresenter.doCharge(mChargeType, mNumber);
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                break;
        }
    }

    private void showChargeType() {
        mChargeType = 1;
        mNumber = mEtNumber.getText().toString().trim();
        if (TextUtils.isEmpty(mNumber)) {
            ToastUtil.showToast(ChargeAccountActivity.this, "请输入充值金额");
            return;
        }
        int layoutId = R.layout.popup_charge;
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        TextView number = contentView.findViewById(R.id.tv_number);
        TextView confirm = contentView.findViewById(R.id.tv_confirm);
        final CheckBox cbWeChatPay = contentView.findViewById(R.id.cb_wechat_pay);
        final CheckBox cbAliPay = contentView.findViewById(R.id.cb_alipay);
        number.setText(mNumber + "元");
        cbWeChatPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbAliPay.setChecked(false);
                    mChargeType = 0;
                }
            }
        });
        cbAliPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbWeChatPay.setChecked(false);
                    mChargeType = 1;
                }
            }
        });
        confirm.setOnClickListener(this);
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.showAtLocation(mTvCharge, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        backgroundAlpha(0.5f);
    }

    @Override
    public void onChargeResult(HttpResult<String> httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(httpResult.data)) {
                AliPay aliPay = new AliPay(ChargeAccountActivity.this);
                aliPay.setPayCallBackListener(this);
                aliPay.pay(httpResult.data);
            }
        } else if (httpResult.code == Constant.HttpResult.CHARGE_NO_PERMIT) {
            showHintDialog();
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(ChargeAccountActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    private void showHintDialog() {
        mChargeHintDialog = new HintDialog(this);
        mChargeHintDialog.initView(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChargeHintDialog != null)
                    mChargeHintDialog.dismiss();
            }
        });
        mChargeHintDialog.setTitle(getString(R.string.charge_no_permit_hint_title));
        mChargeHintDialog.setMessage(getString(R.string.charge_no_permit_hint_content));
        mChargeHintDialog.show();
    }

    @Override
    public void onPayCallBack(int status, String resultStatus, String progress) {
        ToastUtil.showToast(ChargeAccountActivity.this, progress);
        if (status == 9000) {
            ToastUtil.showToast(this, "充值成功");
            setResult(RESULT_OK, new Intent());
            finish();
        } else if (status == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(this, "充值失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPopupWindow != null)
            mPopupWindow.dismiss();
        if (mChargeHintDialog != null) {
            mChargeHintDialog.dismiss();
            mChargeHintDialog = null;
        }
    }
}
