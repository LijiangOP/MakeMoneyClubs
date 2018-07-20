package com.zhengdao.zqb.view.activity.chargephone;


import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.PhoneChargeDetailEntity;
import com.zhengdao.zqb.entity.PhoneChargeEntity;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RegUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.walletlist.WalletListActivity;
import com.zhengdao.zqb.view.adapter.PhoneChargeAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChargePhoneActivity extends MVPBaseActivity<ChargePhoneContract.View, ChargePhonePresenter> implements ChargePhoneContract.View, View.OnClickListener, PhoneChargeAdapter.onItemClickCallBack {


    @BindView(R.id.iv_title_bar_back)
    ImageView      mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView       mTvTitleBarTitle;
    @BindView(R.id.tv_title_bar_right)
    TextView       mTvTitleBarRight;
    @BindView(R.id.re_title_bar)
    RelativeLayout mReTitleBar;
    @BindView(R.id.et_number)
    EditText       mEtNumber;
    @BindView(R.id.iv_clear)
    ImageView      mIvClear;
    @BindView(R.id.recyclerView)
    RecyclerView   mRecyclerView;

    private long mCurrentTimeMillis = 0;
    private int  mSelectNumber      = 0; //充值数额
    private int                                mChargeType; //充值类型
    private PopupWindow                        mPopupWindow;
    private ArrayList<PhoneChargeDetailEntity> mData;
    private PhoneChargeAdapter                 mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargephone);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mTvTitleBarTitle.setText("手机充值");
        mTvTitleBarRight.setText("充值记录");
        mIvTitleBarBack.setOnClickListener(this);
        mTvTitleBarRight.setOnClickListener(this);
        mIvClear.setOnClickListener(this);
        // 模拟添加数据
        simulateData();
        //        mPresenter.getData();
    }

    private void simulateData() {
        if (mData == null)
            mData = new ArrayList<>();
        mData.add(new PhoneChargeDetailEntity(5, "55", "57"));
        mData.add(new PhoneChargeDetailEntity(5, "56", "57"));
        mData.add(new PhoneChargeDetailEntity(5, "57", "57"));
        mData.add(new PhoneChargeDetailEntity(5, "58", "57"));
        mData.add(new PhoneChargeDetailEntity(5, "59", "57"));
        mData.add(new PhoneChargeDetailEntity(5, "65", "57"));
        mData.add(new PhoneChargeDetailEntity(5, "75", "57"));
        mData.add(new PhoneChargeDetailEntity(5, "85", "57"));
        mData.add(new PhoneChargeDetailEntity(5, "95", "57"));
        if (mAdapter == null) {
            mAdapter = new PhoneChargeAdapter(this, R.layout.phone_charge_item, mData, this);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            mRecyclerView.addItemDecoration(new SpaceBusinessItemDecoration(10));
            mRecyclerView.setAdapter(mAdapter);
        } else
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetDataResult(PhoneChargeEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ArrayList<PhoneChargeDetailEntity> list = httpResult.list;
            if (httpResult.list.size() == 0)
                return;
            if (mData == null)
                mData = new ArrayList<>();
            mData.addAll(list);
            if (mAdapter == null) {
                mAdapter = new PhoneChargeAdapter(this, R.layout.phone_charge_item, mData, this);
                mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
                mRecyclerView.addItemDecoration(new SpaceBusinessItemDecoration(10));
                mRecyclerView.setAdapter(mAdapter);
            } else
                mAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToast(ChargePhoneActivity.this, TextUtils.isEmpty(httpResult.msg) ? "数据加载失败" : httpResult.msg);
        }
    }

    @Override
    public void onItemClick(int position) {
        hideSoftInput();
        if (TextUtils.isEmpty(mEtNumber.getText().toString().trim()))
            ToastUtil.showToast(this, "请输入手机号码");
        else if (!RegUtils.isPhoneNum(mEtNumber.getText().toString().trim())) {
            ToastUtil.showToast(this, "请输入正确的手机号码");
        } else {
            try {
                if (mData != null)
                    mSelectNumber = new Integer(mData.get(position).price);
                showChargeType();
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.iv_title_bar_back:
                ChargePhoneActivity.this.finish();
                break;
            case R.id.tv_title_bar_right:
                Intent brokerage = new Intent(ChargePhoneActivity.this, WalletListActivity.class);
                brokerage.putExtra(Constant.Activity.Type, "charge_record");
                startActivity(brokerage);
                break;
            case R.id.iv_clear:
                mEtNumber.setText("");
                break;
            case R.id.tv_confirm:
                if (mPopupWindow != null)
                    mPopupWindow.dismiss();
                ToastUtil.showToast(this, "暂未支持手机充值");
                break;
        }
    }

    private void showChargeType() {
        int layoutId = R.layout.popup_charge;
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        TextView number = contentView.findViewById(R.id.tv_number);
        TextView confirm = contentView.findViewById(R.id.tv_confirm);
        final CheckBox cbWeChatPay = contentView.findViewById(R.id.cb_wechat_pay);
        final CheckBox cbAliPay = contentView.findViewById(R.id.cb_alipay);
        number.setText(mSelectNumber + "元");
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
        mPopupWindow.showAtLocation(mReTitleBar, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        backgroundAlpha(0.5f);
    }


    @Override
    public void onChargeResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(ChargePhoneActivity.this, "充值成功");
        } else {
            ToastUtil.showToast(ChargePhoneActivity.this, TextUtils.isEmpty(httpResult.msg) ? "" : httpResult.msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPopupWindow != null)
            mPopupWindow.dismiss();
    }

    public class SpaceBusinessItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceBusinessItemDecoration(int space) {
            this.space = ViewUtils.dip2px(ChargePhoneActivity.this, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = 0;
            outRect.bottom = 0;
            if ((pos + 1) % 3 != 0) {
                outRect.right = space;
            } else {
                outRect.right = 0;
            }
            if (pos < 3) {
                outRect.top = 0;
            } else {
                outRect.top = 20;
            }
        }
    }
}
