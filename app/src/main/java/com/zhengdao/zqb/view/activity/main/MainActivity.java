package com.zhengdao.zqb.view.activity.main;


import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ExitApplication;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.FragmentTabHost;
import com.zhengdao.zqb.customview.SwipeBackActivity.GoodsCommandDialog;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;
import com.zhengdao.zqb.entity.TabBean;
import com.zhengdao.zqb.event.ForceBindPhoneEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.activity.bindnewphone.BindNewPhoneActivity;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.licaidetail.LicaiDetailActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.fragment.coupons.CouponsFragment;
import com.zhengdao.zqb.view.fragment.focus.FocusFragment;
import com.zhengdao.zqb.view.fragment.home.HomeFragment;
import com.zhengdao.zqb.view.fragment.rebate.RebateFragment;
import com.zhengdao.zqb.view.fragment.user.UserFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.badgeview.BGABadgeLinearLayout;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends MVPBaseActivity<MainContract.View, MainPresenter> implements MainContract.View, TabHost.OnTabChangeListener {

    @BindView(R.id.main_tabHost)
    FragmentTabHost mMainTabHost;

    private long mExitTime;
    private List<TabBean> mTabs = new ArrayList<>();
    private int                preTab;
    private int                currentTab;
    private GoodsCommandDialog mCommandDialog;
    private Disposable         mBindPhoneDisposable;
    private List<BGABadgeLinearLayout> mBGABadgeLinearLayouts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExitApplication.getInstance().addActivity(this);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        initTabHost();
        GangUpInvite(this);
        init();
    }

    private void initTabHost() {
        TabBean home = new TabBean(HomeFragment.class, R.string.home, R.drawable.selector_home_tab);
        TabBean rebate = new TabBean(RebateFragment.class, R.string.rebate, R.drawable.selector_earn_tab);
        TabBean Find = new TabBean(FocusFragment.class, R.string.news, R.drawable.selector_hot_tab);
        TabBean ticket = new TabBean(CouponsFragment.class, R.string.coupons, R.drawable.selector_coupons_tab);
        TabBean user = new TabBean(UserFragment.class, R.string.mine, R.drawable.selector_user_tab);
        mTabs.add(home);
        mTabs.add(rebate);
        mTabs.add(Find);
        mTabs.add(ticket);
        mTabs.add(user);
        mMainTabHost.setup(this, getSupportFragmentManager(), R.id.main_realContent);
        for (TabBean tab : mTabs) {
            TabHost.TabSpec tabSpec = mMainTabHost.newTabSpec(getString(tab.title));
            tabSpec.setIndicator(buildIndicator(tab));
            mMainTabHost.addTab(tabSpec, tab.fragment, null);
        }
        mMainTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mMainTabHost.setOnTabChangedListener(this);
        mMainTabHost.setCurrentTab(0);
    }

    private View buildIndicator(TabBean tab) {
        View view = View.inflate(this, R.layout.tab_indicator, null);
        BGABadgeLinearLayout bgaBadgeLinearLayout = view.findViewById(R.id.tab_indicator_root);
        ImageView bgaBadgeImageView = view.findViewById(R.id.tab_indicator_icon);
        TextView textView = view.findViewById(R.id.tab_indicator_txt);
        bgaBadgeImageView.setBackgroundResource(tab.icon);
        textView.setText(getString(tab.title));
        mBGABadgeLinearLayouts.add(bgaBadgeLinearLayout);
        return view;
    }

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

    @Override
    public void onBackPressed() {
        if (mMainTabHost.getCurrentTab() != 0) {
            mMainTabHost.setCurrentTab(0);
            return;
        }
        if (System.currentTimeMillis() - mExitTime <= 2000) {
            ExitApplication.getInstance().exit();
        } else {
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onTabChanged(String s) {
        preTab = currentTab;
        currentTab = mMainTabHost.getCurrentTab();
    }

    public void restoreToPreTab() {
        if (!SettingUtils.isLogin(this)) {
            if (preTab == mTabs.size() - 1)
                preTab = 0;
        }
        mMainTabHost.setCurrentTab(preTab);
    }

    public void setCurrentPosition(int index) {
        mMainTabHost.setCurrentTab(index);
    }

    @Override
    public void showErrorMessage(String msg) {
        LogUtils.e(TextUtils.isEmpty(msg) ? "" : msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void GangUpInvite(final Context context) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
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
