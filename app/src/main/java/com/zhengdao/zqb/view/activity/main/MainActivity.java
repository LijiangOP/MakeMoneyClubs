package com.zhengdao.zqb.view.activity.main;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ExitApplication;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.ExitDialog;
import com.zhengdao.zqb.customview.NewHandIntroduceDialog;
import com.zhengdao.zqb.customview.SwipeBackActivity.GoodsCommandDialog;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;
import com.zhengdao.zqb.event.ForceBindPhoneEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.DateDefUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.bindnewphone.BindNewPhoneActivity;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.licaidetail.LicaiDetailActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.fragment.coupons.CouponsFragment;
import com.zhengdao.zqb.view.fragment.daily.DailyFragment;
import com.zhengdao.zqb.view.fragment.home.HomeFragment;
import com.zhengdao.zqb.view.fragment.rebate.RebateFragment;
import com.zhengdao.zqb.view.fragment.user.UserFragment;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View, View.OnClickListener {

    private HomeFragment    mHomeFragment;
    private RebateFragment  mRebateFragment;
    private DailyFragment   mDailyFragment;
    private CouponsFragment mCouponsFragment;
    private UserFragment    mUserFragment;

    private FragmentManager     mFragmentManager;
    private FragmentTransaction mTransaction;

    private RelativeLayout mHomeTab;
    private RelativeLayout mEarnTab;
    private RelativeLayout mDailyTab;
    private RelativeLayout mCouponsTab;
    private RelativeLayout mUserTab;

    private TextView mTextUser;
    private TextView mTextHome;
    private TextView mTextEarn;
    private TextView mTextDaily;
    private TextView mTextCoupons;

    private ImageView mImageHome;
    private ImageView mImageEarn;
    private ImageView mImageDaily;
    private ImageView mImageCoupons;
    private ImageView mImageUser;

    private long mExitTime;
    private int  preTab;
    private int currentTab = 0;
    private GoodsCommandDialog mCommandDialog;
    private Disposable         mBindPhoneDisposable;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showNewHandIntroduce();
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExitApplication.getInstance().addActivity(this);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        init();
        initTab(savedInstanceState);
        GangUpInvite();
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    private void initTab(Bundle savedInstanceState) {
        mHomeTab = (RelativeLayout) findViewById(R.id.home_tab);
        mEarnTab = (RelativeLayout) findViewById(R.id.earn_tab);
        mDailyTab = (RelativeLayout) findViewById(R.id.daily_tab);
        mCouponsTab = (RelativeLayout) findViewById(R.id.coupons_tab);
        mUserTab = (RelativeLayout) findViewById(R.id.user_tab);

        mTextHome = (TextView) findViewById(R.id.text_home);
        mTextEarn = (TextView) findViewById(R.id.text_earn);
        mTextDaily = (TextView) findViewById(R.id.text_daily);
        mTextCoupons = (TextView) findViewById(R.id.text_coupons);
        mTextUser = (TextView) findViewById(R.id.text_user);

        mImageHome = (ImageView) findViewById(R.id.image_home);
        mImageEarn = (ImageView) findViewById(R.id.image_earn);
        mImageDaily = (ImageView) findViewById(R.id.image_daily);
        mImageCoupons = (ImageView) findViewById(R.id.image_coupons);
        mImageUser = (ImageView) findViewById(R.id.image_user);

        mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();

        mHomeFragment = (HomeFragment) mFragmentManager.findFragmentByTag("mHomeFragment");
        mRebateFragment = (RebateFragment) mFragmentManager.findFragmentByTag("mRebateFragment");
        mDailyFragment = (DailyFragment) mFragmentManager.findFragmentByTag("mDailyFragment");
        mCouponsFragment = (CouponsFragment) mFragmentManager.findFragmentByTag("mCouponsFragment");
        mUserFragment = (UserFragment) mFragmentManager.findFragmentByTag("mUserFragment");

        mHomeTab.setOnClickListener(this);
        mEarnTab.setOnClickListener(this);
        mDailyTab.setOnClickListener(this);
        mCouponsTab.setOnClickListener(this);
        mUserTab.setOnClickListener(this);

        onClick(mHomeTab);
    }

    @Override
    public void onClick(View v) {
        preTab = currentTab;
        switch (v.getId()) {
            case R.id.home_tab:
                currentTab = 0;
                switchButton(currentTab);
                break;
            case R.id.earn_tab:
                currentTab = 1;
                switchButton(currentTab);
                break;
            case R.id.daily_tab:
                currentTab = 2;
                switchButton(currentTab);
                break;
            case R.id.coupons_tab:
                currentTab = 3;
                switchButton(currentTab);
                break;
            case R.id.user_tab:
                currentTab = 4;
                switchButton(currentTab);
                break;
        }
        onCheckedChanged(v.getId());
    }

    public void switchButton(int position) {
        switch (position) {
            case 0:
                mTextHome.setSelected(true);
                mImageHome.setSelected(true);
                mTextEarn.setSelected(false);
                mImageEarn.setSelected(false);
                mTextDaily.setSelected(false);
                mImageDaily.setSelected(false);
                mTextCoupons.setSelected(false);
                mImageCoupons.setSelected(false);
                mTextUser.setSelected(false);
                mImageUser.setSelected(false);
                break;
            case 1:
                mTextHome.setSelected(false);
                mImageHome.setSelected(false);
                mTextEarn.setSelected(true);
                mImageEarn.setSelected(true);
                mTextDaily.setSelected(false);
                mImageDaily.setSelected(false);
                mTextCoupons.setSelected(false);
                mImageCoupons.setSelected(false);
                mTextUser.setSelected(false);
                mImageUser.setSelected(false);
                break;
            case 2:
                mTextHome.setSelected(false);
                mImageHome.setSelected(false);
                mTextEarn.setSelected(false);
                mImageEarn.setSelected(false);
                mTextDaily.setSelected(true);
                mImageDaily.setSelected(true);
                mTextCoupons.setSelected(false);
                mImageCoupons.setSelected(false);
                mTextUser.setSelected(false);
                mImageUser.setSelected(false);

                break;
            case 3:
                mTextHome.setSelected(false);
                mImageHome.setSelected(false);
                mTextEarn.setSelected(false);
                mImageEarn.setSelected(false);
                mTextDaily.setSelected(false);
                mImageDaily.setSelected(false);
                mTextCoupons.setSelected(true);
                mImageCoupons.setSelected(true);
                mTextUser.setSelected(false);
                mImageUser.setSelected(false);
                break;
            case 4:
                mTextHome.setSelected(false);
                mImageHome.setSelected(false);
                mTextEarn.setSelected(false);
                mImageEarn.setSelected(false);
                mTextDaily.setSelected(false);
                mImageDaily.setSelected(false);
                mTextCoupons.setSelected(false);
                mImageCoupons.setSelected(false);
                mTextUser.setSelected(true);
                mImageUser.setSelected(true);
                break;
        }
    }


    public void onCheckedChanged(int checkedId) {
        mTransaction = mFragmentManager.beginTransaction();
        if (mHomeFragment != null) {
            mTransaction.hide(mHomeFragment);
        }
        if (mRebateFragment != null) {
            mTransaction.hide(mRebateFragment);
        }
        if (mDailyFragment != null) {
            mTransaction.hide(mDailyFragment);
        }
        if (mCouponsFragment != null) {
            mTransaction.hide(mCouponsFragment);
        }
        if (mUserFragment != null) {
            mTransaction.hide(mUserFragment);
        }
        if (checkedId == R.id.home_tab) {
            if (mHomeFragment == null) {
                mHomeFragment = new HomeFragment();
                mTransaction.add(R.id.main_realContent, mHomeFragment, "mHomeFragment");
            } else {
                mTransaction.show(mHomeFragment);
            }
        } else if (checkedId == R.id.earn_tab) {
            if (mRebateFragment == null) {
                mRebateFragment = new RebateFragment();
                mTransaction.add(R.id.main_realContent, mRebateFragment, "mRebateFragment");
            } else {
                mTransaction.show(mRebateFragment);
            }
        } else if (checkedId == R.id.daily_tab) {
            if (mDailyFragment == null) {
                mDailyFragment = new DailyFragment();
                mTransaction.add(R.id.main_realContent, mDailyFragment, "mDailyFragment");
            } else {
                mTransaction.show(mDailyFragment);
            }
        } else if (checkedId == R.id.coupons_tab) {
            if (mCouponsFragment == null) {
                mCouponsFragment = new CouponsFragment();
                mTransaction.add(R.id.main_realContent, mCouponsFragment, "mCouponsFragment");
            } else {
                mTransaction.show(mCouponsFragment);
            }
        } else if (checkedId == R.id.user_tab) {
            if (mUserFragment == null) {
                mUserFragment = new UserFragment();
                mTransaction.add(R.id.main_realContent, mUserFragment, "mUserFragment");
            } else {
                mTransaction.show(mUserFragment);
            }
        }
        mTransaction.commit();
    }


    /**
     * RxBus注册 绑定手机号事件（三方登陆后必须绑定手机号的逻辑）
     */
    private void init() {
        mBindPhoneDisposable = RxBus.getDefault().toObservable(ForceBindPhoneEvent.class).subscribe(new Consumer<ForceBindPhoneEvent>() {
            @Override
            public void accept(ForceBindPhoneEvent forceBindPhoneEvent) throws Exception {
                Intent intent = new Intent(MainActivity.this, BindNewPhoneActivity.class);
                intent.putExtra(Constant.Activity.Data, forceBindPhoneEvent.type);
                startActivity(intent);
            }
        });
    }


    public void restoreToPreTab() {
        if (!SettingUtils.isLogin(this) && preTab == 4) {
            preTab = 0;
        }
        setCurrentPosition(preTab);
    }

    public void setCurrentPosition(int index) {
        switch (index) {
            case 0:
                onClick(mHomeTab);
                break;
            case 1:
                onClick(mEarnTab);
                break;
            case 2:
                onClick(mDailyTab);
                break;
            case 3:
                onClick(mCouponsTab);
                break;
            case 4:
                onClick(mUserTab);
                break;
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        LogUtils.e(TextUtils.isEmpty(msg) ? "" : msg);
    }

    /**
     * 商品口令的逻辑实现
     */
    public void GangUpInvite() {
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            //无数据时直接返回
            if (!clipboard.hasPrimaryClip()) {
                return;
            }
            //如果是文本信息
            if (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                ClipData cdText = clipboard.getPrimaryClip();
                ClipData.Item item = cdText.getItemAt(0);
                //此处是TEXT文本信息
                if (item.getText() != null) {
                    String str = item.getText().toString();
                    if (!TextUtils.isEmpty(str) && str.contains("￥")) {
                        String[] split = str.split("￥");
                        if (split.length > 2) {
                            String s = split[1];
                            if (s.contains("#")) {
                                String[] value = s.split("#");
                                if (value.length > 1) {
                                    Constant.Data.InvitedCodeList.add(value[0]);
                                    LogUtils.e("口令 邀请码" + value[0]);
                                }
                                if (value.length > 1 && isNumeric(value[1]))
                                    mPresenter.getZeroEarnGoodsCommand(new Integer(value[1]).intValue());
                                else if (value.length > 1 && value[1].contains("fl")) {
                                    String replace = value[1].replace("fl", "");
                                    mPresenter.getRebateGoodsCommand(new Integer(replace).intValue());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    private void showNewHandIntroduce() {
        if (SettingUtils.isFristInstall(MainActivity.this)) {
            SettingUtils.setFristInstall(MainActivity.this, false);
            NewHandIntroduceDialog dialog = new NewHandIntroduceDialog(MainActivity.this);
            dialog.setVisibility(1, View.VISIBLE);
            dialog.show();
            SettingUtils.setIntroduceLastShowDate(MainActivity.this, Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        } else if (DateDefUtils.isCanShowIntroduceToDay(MainActivity.this)) {
            NewHandIntroduceDialog dialog = new NewHandIntroduceDialog(MainActivity.this);
            dialog.setVisibility(0, View.VISIBLE);
            dialog.show();
            SettingUtils.setIntroduceLastShowDate(MainActivity.this, Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        }
    }

    /**
     * 0元赚商品类型 口令数据获取
     *
     * @param httpResult
     */
    @Override
    public void onGetZeroEarnGoodsCommandResult(final GoodsCommandHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (mCommandDialog == null)
                mCommandDialog = new GoodsCommandDialog(MainActivity.this);
            mCommandDialog.setPosition(0, -100);
            mCommandDialog.initContentView(httpResult, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SettingUtils.isLogin(MainActivity.this)) {
                        Intent intent = new Intent(MainActivity.this, HomeGoodsDetailActivity.class);
                        intent.putExtra(Constant.Activity.Data, httpResult.reward.id);
                        startActivity(intent);
                        mCommandDialog.dismiss();
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText("Label", ""));
                }
            });
            mCommandDialog.show();
        } else
            LogUtils.e("请求出错");
    }

    /**
     * 返利商品类型 口令数据获取
     *
     * @param result
     */
    @Override
    public void onGetRebateGoodsCommandResult(final HttpLiCaiDetailEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (mCommandDialog == null)
                mCommandDialog = new GoodsCommandDialog(MainActivity.this);
            mCommandDialog.setPosition(0, -100);
            mCommandDialog.initContentView(result, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SettingUtils.isLogin(MainActivity.this)) {
                        Intent intent = new Intent(MainActivity.this, LicaiDetailActivity.class);
                        intent.putExtra(Constant.Activity.Data, result.wangzhuan.id);
                        startActivity(intent);
                        mCommandDialog.dismiss();
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText("Label", ""));
                }
            });
            mCommandDialog.show();
        } else
            LogUtils.e("请求出错");
    }

    @Override
    public void onGetExitCommandResult(GoodsCommandHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            showExitDialog(httpResult);
        } else {
            ExitApplication.getInstance().exit();//请求失败就退出
        }
    }

    @Override
    public void onGetExitCommandError() {
        ExitApplication.getInstance().exit();//请求失败就退出
    }

    private void showExitDialog(final GoodsCommandHttpEntity httpResult) {
        float totalIncome = SettingUtils.getTotalIncome(MainActivity.this);
        ExitDialog exitDialog = new ExitDialog(MainActivity.this);
        SpannableString spannableString = new SpannableString("您已成功赚取" + totalIncome + "元，继续加油赚钱吧!");
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#fc3135")), 6, 6 + String.valueOf(totalIncome).length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        exitDialog.init("确定退出" + getString(R.string.app_name) + "?", spannableString, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HomeGoodsDetailActivity.class);
                intent.putExtra(Constant.Activity.Data, httpResult.reward.id);
                Utils.StartActivity(MainActivity.this, intent);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitApplication.getInstance().exit();
            }
        });
        exitDialog.setGoods(httpResult);
        exitDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (currentTab != 0) {
            onClick(mHomeTab);
            return;
        }
        if (SettingUtils.isLogin(MainActivity.this)) {
            mPresenter.getOutReward();
        } else {
            if (System.currentTimeMillis() - mExitTime <= 2000) {
                ExitApplication.getInstance().exit();
            } else {
                Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            }
            mExitTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCommandDialog != null) {
            mCommandDialog.dismiss();
            mCommandDialog = null;
        }
        if (mBindPhoneDisposable != null)
            mBindPhoneDisposable.dispose();
    }

}
