package com.zhengdao.zqb.view.activity.withdraw;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.entity.WithDrawSelectEntity;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.WithDrawSelectedAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WithDrawActivity extends MVPBaseActivity<WithDrawContract.View, WithDrawPresenter> implements WithDrawContract.View, View.OnClickListener, WithDrawSelectedAdapter.ItemSelectedCallBack {

    private static final int   BIND_ALIPAY  = 002;
    private static final int   ACTION_LOGIN = 001;
    private static       Toast toast        = null;
    @BindView(R.id.tv_aliPay_account)
    TextView     mTvAliPayAccount;
    @BindView(R.id.tv_change)
    TextView     mTvChange;
    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.tv_balance)
    TextView     mTvBalance;
    @BindView(R.id.tv_confirm)
    TextView     mTvConfirm;

    private long mCurrentTimeMillis = 0;
    private String mAccount;//账户
    private Double mUsableSum;//可用余额
    private Integer mWithDrawNum = 0;//提现金额
    private WithDrawSelectedAdapter    mAdapter;
    private List<WithDrawSelectEntity> mData;
    private boolean mIsFristWithDraw = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        ButterKnife.bind(this);
        setTitle("提现");
        initData();
        initListener();
        mPresenter.getUserData();
    }

    private void initData() {
        mAccount = getIntent().getStringExtra(Constant.Activity.Data);
        if (TextUtils.isEmpty(mAccount))
            mAccount = SettingUtils.getAlipayAccount(this);
        mTvAliPayAccount.setText(TextUtils.isEmpty(mAccount) ? "" : mAccount);
        mUsableSum = getIntent().getDoubleExtra(Constant.Activity.Data1, 0);
        mTvBalance.setText("¥" + new DecimalFormat("#0.00").format(mUsableSum));
    }

    private void initListener() {
        mTvChange.setOnClickListener(this);
        mTvConfirm.setOnClickListener(this);
        mData = new ArrayList<>();
        mData.add(new WithDrawSelectEntity(1, false));
        mData.add(new WithDrawSelectEntity(5, false));
        mData.add(new WithDrawSelectEntity(10, mUsableSum < 10 ? false : true));
        mData.add(new WithDrawSelectEntity(50, mUsableSum < 50 ? false : true));
        mData.add(new WithDrawSelectEntity(100, mUsableSum < 100 ? false : true));
        mData.add(new WithDrawSelectEntity(500, mUsableSum < 500 ? false : true));
        mAdapter = new WithDrawSelectedAdapter(this, R.layout.withdraw_selected_item, mData, this);
        mRecycleView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecycleView.addItemDecoration(new SpaceBusinessItemDecoration(13));
        mRecycleView.setAdapter(mAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_change:
                ToastUtil.showToast(WithDrawActivity.this, "修改");
                break;
            case R.id.tv_confirm:
                doWithDraw();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void doWithDraw() {
        try {
            hideSoftInput();
            if (TextUtils.isEmpty(mAccount)) {
                showToastWithGravityinDef(this, "请绑定支付宝账号", Gravity.CENTER);
                return;
            }
            if (mWithDrawNum < 1) {
                showToastWithGravityinDef(this, "请选择提现金额", Gravity.CENTER);
                return;
            }
            mPresenter.doWithDraw("1", "" + mWithDrawNum);
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onWithDrawResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            showToastWithGravityinDef(WithDrawActivity.this, TextUtils.isEmpty(httpResult.msg) ? "提现成功" : httpResult.msg, Gravity.CENTER);
            double value = new Double(mUsableSum) - new Double(mWithDrawNum);
            String format = new DecimalFormat("#0.00").format(value);
            mTvBalance.setText("¥" + format);
            RxBus.getDefault().post(new UpDataUserInfoEvent());
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录", Gravity.CENTER);
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(WithDrawActivity.this, TextUtils.isEmpty(httpResult.msg) ? "提现失败" : httpResult.msg, Gravity.CENTER);
        }
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(WithDrawActivity.this, "登录超时,请重新登录");
        startActivityForResult(new Intent(WithDrawActivity.this, LoginActivity.class), ACTION_LOGIN);
    }

    @Override
    public void showView(UserHomeBean httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                if (!TextUtils.isEmpty(httpResult.userInfo.zfb))
                    mAccount = httpResult.userInfo.zfb;
                mUsableSum = httpResult.account.usableSum;
                mTvBalance.setText("¥" + (mUsableSum == null ? 0 : new DecimalFormat("#0.00").format(mUsableSum)));
                if (httpResult.withdraw > 0) {
                    mIsFristWithDraw = false;
                    if (mData != null && mData.size() > 0) {
                        mData.set(0, new WithDrawSelectEntity(1, mUsableSum < 1 ? false : true));
                        mData.set(1, new WithDrawSelectEntity(5, mUsableSum < 5 ? false : true));
                    }
                }
                mData.set(2, new WithDrawSelectEntity(10, mUsableSum < 10 ? false : true));
                mData.set(3, new WithDrawSelectEntity(50, mUsableSum < 50 ? false : true));
                mData.set(4, new WithDrawSelectEntity(100, mUsableSum < 100 ? false : true));
                mData.set(5, new WithDrawSelectEntity(500, mUsableSum < 500 ? false : true));
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(WithDrawActivity.this, "登录超时,请重新登录");
                startActivity(new Intent(WithDrawActivity.this, LoginActivity.class));
            } else if (httpResult.code == Constant.HttpResult.FAILD) {
                ToastUtil.showToast(WithDrawActivity.this, TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
                showView(new UserHomeBean());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case BIND_ALIPAY:
                    LogUtils.i("绑定成功，返回");
                    break;
                case ACTION_LOGIN:
                    if (data != null) {
                        boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                        if (booleanExtra && SettingUtils.isLogin(WithDrawActivity.this))
                            mPresenter.getUserData();
                    }
                    break;
            }
        }
    }

    @Override
    public void onItemSelected(Integer value) {
        try {
            mWithDrawNum = value;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onItemClick(boolean type, Integer value) {
        if (!type) {//灰色按钮
            if (mIsFristWithDraw) {//首次提现
                if (value == 1 || value == 5)
                    showToastWithGravityinDef(this, "首次提现最低10元起", Gravity.CENTER);
                else if (mUsableSum < value)
                    showToastWithGravityinDef(this, "当前可提现余额不足", Gravity.CENTER);
            } else {//非首次提现
                if (mUsableSum < value)
                    showToastWithGravityinDef(this, "当前可提现余额不足", Gravity.CENTER);
            }
        }
    }

    public class SpaceBusinessItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceBusinessItemDecoration(int space) {
            this.space = ViewUtils.dip2px(WithDrawActivity.this, space);
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
            outRect.top = space;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void showToastWithGravityinDef(Context context, String str, int gravity) {
        if (toast == null)
            toast = Toast.makeText(context.getApplicationContext(), str, Toast.LENGTH_SHORT);
        else
            toast.setText(str);
        TextView textView = (TextView) toast.getView().findViewById(android.R.id.message);
        if (textView != null)
            textView.setTextColor(Color.WHITE);
        View view = toast.getView();
        view.setPadding(50, 20, 50, 20);
        view.setBackground(context.getResources().getDrawable(R.drawable.shape_rect_five_black));
        toast.setGravity(gravity, 0, 0);
        toast.setView(view);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
