package com.zhengdao.zqb.view.fragment.user;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anzhi.sdk.ad.main.AzBannerAd;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jaeger.library.StatusBarUtil;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.DailyWindow;
import com.zhengdao.zqb.customview.InvestCodeWindow;
import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.event.LogInEvent;
import com.zhengdao.zqb.event.LogOutEvent;
import com.zhengdao.zqb.event.RegistSuccessEvent;
import com.zhengdao.zqb.event.UpDataUserInfoEvent;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.ActivityUtils;
import com.zhengdao.zqb.utils.AdvertisementUtils;
import com.zhengdao.zqb.utils.DateDefUtils;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.SkipUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.activitycenter.ActivityCenterActivity;
import com.zhengdao.zqb.view.activity.advcenter.AdvCenterActivity;
import com.zhengdao.zqb.view.activity.bindalipay.BindAliPayActivity;
import com.zhengdao.zqb.view.activity.browsinghistory.BrowsingHistoryActivity;
import com.zhengdao.zqb.view.activity.customservice.CustomServiceActivity;
import com.zhengdao.zqb.view.activity.favorite.FavoriteActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.main.MainActivity;
import com.zhengdao.zqb.view.activity.mybalance.MyBalanceActivity;
import com.zhengdao.zqb.view.activity.mywanted.MyWantedActivity;
import com.zhengdao.zqb.view.activity.newhandmission.NewHandMissionActivity;
import com.zhengdao.zqb.view.activity.questionsurvery.QuestionSurveryActivty;
import com.zhengdao.zqb.view.activity.rebaterecords.RebateRecordsActivity;
import com.zhengdao.zqb.view.activity.rewardticket.RewardTicketActivity;
import com.zhengdao.zqb.view.activity.setting.SettingActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.activity.welfareget.WelfareGetActivity;
import com.zhengdao.zqb.view.activity.withdraw.WithDrawActivity;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.zhengdao.zqb.application.ClientAppLike.AppType;


public class UserFragment extends MVPBaseFragment<UserContract.View, UserPresenter> implements UserContract.View, View.OnClickListener, InvestCodeWindow.CallBack {
    private static final int ACTION_LOGIN         = 101;
    private static final int ACTION_LOGIN_RELOGIN = 102;
    private static final int BIND_ALIPAY          = 103;

    @BindView(R.id.ib_home_service)
    ImageView          mIbHomeService;
    @BindView(R.id.ib_home_setting)
    ImageView          mIbHomeSetting;
    @BindView(R.id.iv_user_icon)
    CircleImageView    mIvUserIcon;
    @BindView(R.id.tv_user_name)
    TextView           mTvUserName;
    @BindView(R.id.tv_balance)
    TextView           mTvBalance;
    @BindView(R.id.tv_withdraw)
    TextView           mTvWithdraw;
    @BindView(R.id.tv_today_income)
    TextView           mTvTodayIncome;
    @BindView(R.id.tv_total_income)
    TextView           mTvTotalIncome;
    @BindView(R.id.tv_total_withdraw)
    TextView           mTvTotalWithdraw;
    @BindView(R.id.fl_container_top)
    FrameLayout        mFlContainerTop;
    @BindView(R.id.ll_module_welfare)
    LinearLayout       mLlModuleWelfare;
    @BindView(R.id.ll_module_invited_code)
    LinearLayout       mLlModuleInvitedCode;
    @BindView(R.id.ll_module_task_center)
    LinearLayout       mLlModuleTaskCenter;
    @BindView(R.id.ll_module_adv_center)
    LinearLayout       mLlModuleAdvCenter;
    @BindView(R.id.ll_module_rebate_records)
    LinearLayout       mLlModuleRebateRecords;
    @BindView(R.id.ll_module_invited_friends)
    LinearLayout       mLlModuleInvitedFriends;
    @BindView(R.id.tv_my_wallet)
    TextView           mTvMyWallet;
    @BindView(R.id.ll_module_my_wallet)
    LinearLayout       mLlModuleMyWallet;
    @BindView(R.id.ll_module_ticket)
    LinearLayout       mLlModuleTicket;
    @BindView(R.id.ll_module_favourite)
    LinearLayout       mLlModuleFavourite;
    @BindView(R.id.ll_module_browsing_history)
    LinearLayout       mLlModuleBrowsingHistory;
    @BindView(R.id.ll_module_activity_center)
    LinearLayout       mLlModuleActivityCenter;
    @BindView(R.id.fl_container_bottom)
    FrameLayout        mFlContainerBottom;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.ll_module_daily_mission)
    LinearLayout       mLlModuleDailyMission;
    @BindView(R.id.ll_module_fiction)
    LinearLayout       mLlModuleFiction;

    private Disposable mLogOutDisposable;
    private Disposable mLogInDisposable;
    private Disposable mRegistSuccessDisposable;
    private Disposable mUpDataUserInfoDisposable;
    private CompositeDisposable mDisposables       = new CompositeDisposable();
    private long                mCurrentTimeMillis = 0;
    private String      mNickname;
    private String      mAliPayAccount;
    private Double      mUsableSum;
    private int         mWelfareState;
    private DailyWindow mDailyWindow;
    private long mCurrentAskDataTime = 0;
    private InvestCodeWindow mInvestCodeWindow;
    private AzBannerAd       mAzBannerAd;

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.user_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForImageViewInFragment(getActivity(), null);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.getUserData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        reGistEvent();
        initClickListener();
        initAdv();//增加广告
        showDailyWindow();//展示每日弹窗
        if (!SettingUtils.isLogin(getActivity()))
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), ACTION_LOGIN);
    }

    private void initAdv() {
        try {
            if (AppType == Constant.App.Wlgfl) {
                //安智广告
                //                AdvertisementUtils.AnZhiAdv.addAnZhiNativeAdv(getActivity(), mFlContainerBottom, new AdvertisementUtils.onItemClick() {
                //                    @Override
                //                    public void onAdvClick() {
                //                        mPresenter.getSeeAdvReward(3, 3);
                //                    }
                //                });
                AdvertisementUtils.TencentAdv.getTencentBannerAdvWithCallBack(getActivity(), Constant.TencentAdv.advTenCent_ADV_BANNER_ID, mFlContainerTop, false, new AdvertisementUtils.onItemClick() {
                    @Override
                    public void onAdvClick() {
                        //                        mPresenter.getSeeAdvReward(3, 2);
                    }
                });
            } else {
                //安智广告
                //                AdvertisementUtils.AnZhiAdv.addAnZhiNativeAdv(getActivity(), mFlContainerBottom, new AdvertisementUtils.onItemClick() {
                //                    @Override
                //                    public void onAdvClick() {
                //                        mPresenter.getSeeAdvReward(3, 3);
                //                    }
                //                });
                //百度广告
                AdvertisementUtils.BaiDuAdv.addAdvNoDefCloseWithCallBack(getActivity(), Constant.BaiDuAdv.UserCenterBottom, mFlContainerTop, new AdvertisementUtils.onItemClick() {
                    @Override
                    public void onAdvClick() {
                        //                        mPresenter.getSeeAdvReward(3, 1);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPresenter.getAdvReplace(Constant.AdvPosition.PositionsMine);
        mLlModuleInvitedCode.setVisibility(View.GONE);//7.30 隐藏输入邀请码
        mLlModuleAdvCenter.setVisibility(View.GONE);//7.30 隐藏广告中心
        mLlModuleDailyMission.setVisibility(View.GONE);//10.23 隐藏每日任务
        mLlModuleFiction.setVisibility(View.GONE);//10.23
    }

    private void reGistEvent() {
        mLogOutDisposable = RxBus.getDefault().toObservable(LogOutEvent.class).subscribe(new Consumer<LogOutEvent>() {
            @Override
            public void accept(LogOutEvent logOutEvent) throws Exception {
                LogUtils.e("退出登录");
            }
        });
        mLogInDisposable = RxBus.getDefault().toObservable(LogInEvent.class).subscribe(new Consumer<LogInEvent>() {
            @Override
            public void accept(LogInEvent logInEvent) throws Exception {
                LogUtils.e("登录成功");
                mPresenter.getUserData();
            }
        });
        mRegistSuccessDisposable = RxBus.getDefault().toObservable(RegistSuccessEvent.class).subscribe(new Consumer<RegistSuccessEvent>() {
            @Override
            public void accept(RegistSuccessEvent registSuccessEvent) throws Exception {
                LogUtils.e("注册成功");
                mPresenter.getUserData();
            }
        });
        mUpDataUserInfoDisposable = RxBus.getDefault().toObservable(UpDataUserInfoEvent.class).subscribe(new Consumer<UpDataUserInfoEvent>() {
            @Override
            public void accept(UpDataUserInfoEvent upDataUserInfoEvent) throws Exception {
                LogUtils.e("需要刷新该页面数据");
                mPresenter.getUserData();
            }
        });
        mDisposables.add(mLogOutDisposable);
        mDisposables.add(mLogInDisposable);
        mDisposables.add(mRegistSuccessDisposable);
        mDisposables.add(mUpDataUserInfoDisposable);
    }

    private void initClickListener() {
        mIbHomeService.setOnClickListener(this);
        mIbHomeSetting.setOnClickListener(this);
        mIvUserIcon.setOnClickListener(this);
        mTvBalance.setOnClickListener(this);
        mTvWithdraw.setOnClickListener(this);

        mLlModuleWelfare.setOnClickListener(this);
        mLlModuleDailyMission.setOnClickListener(this);
        mLlModuleFiction.setOnClickListener(this);
        mLlModuleInvitedCode.setOnClickListener(this);

        mLlModuleTaskCenter.setOnClickListener(this);
        mLlModuleAdvCenter.setOnClickListener(this);
        mLlModuleRebateRecords.setOnClickListener(this);
        mLlModuleInvitedFriends.setOnClickListener(this);

        mLlModuleMyWallet.setOnClickListener(this);
        mLlModuleTicket.setOnClickListener(this);

        mLlModuleFavourite.setOnClickListener(this);
        mLlModuleBrowsingHistory.setOnClickListener(this);
        mLlModuleActivityCenter.setOnClickListener(this);
    }

    public void showDailyWindow() {
        try {
            if (DateDefUtils.isAfter(getActivity())) {
                if (mDailyWindow == null)
                    mDailyWindow = new DailyWindow(getActivity());
                mDailyWindow.setPosition(0, -100);
                mDailyWindow.initContentView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent_three;
                        if (SettingUtils.isLogin(getActivity())) {
                            intent_three = new Intent(getActivity(), WebViewActivity.class);
                            intent_three.putExtra(Constant.WebView.TITLE, "邀请有礼");
                            intent_three.putExtra(Constant.WebView.URL, Constant.IP.Invited + "/?token=" + SettingUtils.getUserToken(getActivity()));
                        } else {
                            intent_three = new Intent(getActivity(), LoginActivity.class);
                        }
                        Utils.StartActivity(getActivity(), intent_three);
                        mDailyWindow.dismiss();
                    }
                });
                mDailyWindow.setCanceledOnTouchOutside(true);
                mDailyWindow.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && !SettingUtils.isLogin(getActivity()))
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), ACTION_LOGIN);
        else
            mPresenter.getUserData();
    }

    @Override
    public void onResume() {
        super.onResume();
        long value = System.currentTimeMillis();
        if (value - mCurrentAskDataTime > 3 * 60 * 1000 || mCurrentAskDataTime == 0) {//3分钟间隔 自动请求数据
            if (SettingUtils.isLogin(getActivity()))
                mPresenter.getUserData();
        }
        mCurrentAskDataTime = System.currentTimeMillis();
    }

    @Override
    public void ReLogin() {
        ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
        startActivityForResult(new Intent(getActivity(), LoginActivity.class), ACTION_LOGIN_RELOGIN);
    }

    @Override
    public void showView(UserHomeBean httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            showFragmentView(httpResult);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else if (httpResult.code == Constant.HttpResult.FAILD) {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
            showView(new UserHomeBean());
        }
    }

    @Override
    public void onGetAdvReplace(final AdvertisementHttpEntity httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                if (httpResult.advert != null) {
                    String imgPath = httpResult.advert.imgPath;
                    final ImageView imageView = new ImageView(getActivity());
                    if (!TextUtils.isEmpty(imgPath))
                        Glide.with(this).load(imgPath).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource != null) {
                                    int[] screenSize = DensityUtil.getScreenSize(getActivity());
                                    if (screenSize[0] != 0) {
                                        imageView.setImageBitmap(resource);
                                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                                        layoutParams.width = screenSize[0];
                                        layoutParams.height = screenSize[0] / 5;
                                        imageView.setLayoutParams(layoutParams);
                                        mFlContainerTop.addView(imageView);
                                        mFlContainerBottom.addView(imageView);
                                    }
                                }
                            }
                        });
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityUtils.doSkip(getActivity(), httpResult.advert.type, httpResult.advert.url, httpResult.advert.id);
                        }
                    });
                }
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else if (httpResult.code == Constant.HttpResult.FAILD) {
                LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetInvitedReward(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "获取奖励成功" : httpResult.msg);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onGetAdvReward(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            LogUtils.i(TextUtils.isEmpty(httpResult.msg) ? "获取奖励成功" : httpResult.msg);
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            LogUtils.e("登录超时");
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onSurveyLinkGet(SurveyHttpResult httpResult) {
        if (httpResult == null) {
            ToastUtil.showToast(getActivity(), "请求出错");
            return;
        }
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            Intent welfare = new Intent(getActivity(), WelfareGetActivity.class);
            welfare.putExtra(Constant.Activity.Data, mWelfareState);
            Utils.StartActivity(getActivity(), welfare);
        } else if (httpResult.code == Constant.HttpResult.FAILD) {
            Intent survery = new Intent(getActivity(), QuestionSurveryActivty.class);
            survery.putExtra(Constant.Activity.Skip, "welfare");
            Utils.StartActivity(getActivity(), survery);
            ToastUtil.showToast(getActivity(), "请完善用户信息");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    /**
     * @param bean
     */
    public void showFragmentView(UserHomeBean bean) {
        if (bean != null) {
            //常量
            mNickname = bean.user.nickName;
            mAliPayAccount = bean.userInfo.zfb;
            mUsableSum = bean.account.usableSum;
            mWelfareState = bean.user.welfare;
            //控件显示
            Glide.with(getActivity()).load(bean.user.avatar).error(R.drawable.default_icon).into(mIvUserIcon);

            mTvUserName.setText(TextUtils.isEmpty(bean.user.nickName) ? "" : bean.user.nickName);

            SpannableString spannableString = new SpannableString("帐户余额: " + (mUsableSum == null ? 0 : new DecimalFormat("#0.00").format(mUsableSum)));
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 5, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main)), 5, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvBalance.setText(spannableString);

            SpannableString spannableTodayIncome = new SpannableString("今日收入: " + (bean.toDayProfit == null ? 0 : new DecimalFormat("#0.00").format(bean.toDayProfit)));
            spannableTodayIncome.setSpan(new StyleSpan(Typeface.BOLD), 5, spannableTodayIncome.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableTodayIncome.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main)), 5, spannableTodayIncome.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvTodayIncome.setText(spannableTodayIncome);

            double total = bean.account.takenAmount == null ? mUsableSum : (bean.account.takenAmount + mUsableSum);
            SettingUtils.setTotalIncome(getActivity(), new Double(total).floatValue());
            SpannableString spannableTotalIncome = new SpannableString("收入总额: " + ((bean.account.takenAmount == null ? mUsableSum : new DecimalFormat("#0.00").format(bean.account.takenAmount + mUsableSum))));
            spannableTotalIncome.setSpan(new StyleSpan(Typeface.BOLD), 5, spannableTotalIncome.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableTotalIncome.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main)), 5, spannableTotalIncome.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvTotalIncome.setText(spannableTotalIncome);

            SpannableString spannableTotalWithdraw = new SpannableString("提现总额: " + (bean.account.takenAmount == null ? 0 : new DecimalFormat("#0.00").format(bean.account.takenAmount)));
            spannableTotalWithdraw.setSpan(new StyleSpan(Typeface.BOLD), 5, spannableTotalWithdraw.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableTotalWithdraw.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main)), 5, spannableTotalWithdraw.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvTotalWithdraw.setText(spannableTotalWithdraw);

            mTvMyWallet.setText(mUsableSum + "元");

            //保存数据
            SettingUtils.SaveAfterGetUSerData(getActivity(), bean);
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ib_home_setting:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.ib_home_service:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), CustomServiceActivity.class));
                break;
            case R.id.iv_user_icon:
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra(Constant.Activity.Skip, "Skip_To_Certification");
                Utils.StartActivity(getActivity(), intent);
                break;
            case R.id.tv_balance:
                Intent balance = new Intent(getActivity(), MyBalanceActivity.class);
                Utils.StartActivity(getActivity(), balance);
                break;
            case R.id.tv_withdraw:
                if (TextUtils.isEmpty(mAliPayAccount) && !TextUtils.isEmpty(mNickname)) {
                    ToastUtil.showToast(getActivity(), "请绑定支付宝");
                    startActivityForResult(new Intent(getActivity(), BindAliPayActivity.class), BIND_ALIPAY);
                    return;
                }
                Intent withDraw = new Intent(getActivity(), WithDrawActivity.class);
                withDraw.putExtra(Constant.Activity.Data, mAliPayAccount);
                withDraw.putExtra(Constant.Activity.Data1, mUsableSum);
                Utils.StartActivity(getActivity(), withDraw);
                break;
            case R.id.ll_module_welfare:
                mPresenter.getSurveyLink();
                break;
            case R.id.ll_module_daily_mission:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), NewHandMissionActivity.class));
                break;
            case R.id.ll_module_fiction:
                Intent fiction = new Intent(getActivity(), WebViewActivity.class);
                fiction.putExtra(Constant.WebView.TITLE, "小说阅读");
                fiction.putExtra(Constant.WebView.URL, Constant.IP.Fiction);
                getActivity().startActivity(fiction);
                break;
            case R.id.ll_module_invited_code:
                showInvestCodeWindow();
                break;
            case R.id.ll_module_task_center:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), MyWantedActivity.class));
                break;
            case R.id.ll_module_adv_center:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), AdvCenterActivity.class));
                break;
            case R.id.ll_module_rebate_records:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), RebateRecordsActivity.class));
                break;
            case R.id.ll_module_invited_friends:
                SkipUtils.SkipToShouTu(getActivity());
                break;
            case R.id.ll_module_my_wallet:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), MyBalanceActivity.class));
                break;
            case R.id.ll_module_ticket:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), RewardTicketActivity.class));
                break;
            case R.id.ll_module_favourite:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), FavoriteActivity.class));
                break;
            case R.id.ll_module_browsing_history:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), BrowsingHistoryActivity.class));
                break;
            case R.id.ll_module_activity_center:
                Utils.StartActivity(getActivity(), new Intent(getActivity(), ActivityCenterActivity.class));
                break;
        }
    }

    public void showInvestCodeWindow() {
        try {
            if (mInvestCodeWindow == null)
                mInvestCodeWindow = new InvestCodeWindow(getActivity(), this);
            mInvestCodeWindow.setPosition(0, 10);
            mInvestCodeWindow.setCanceledOnTouchOutside(true);
            mInvestCodeWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTake(String investCode) {
        if (!TextUtils.isEmpty(investCode)) {
            hideSoftInput();
            mPresenter.getInvitedReward(investCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case BIND_ALIPAY:
                    mPresenter.getUserData();
                    break;
                case ACTION_LOGIN:
                    if (data != null) {
                        boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                        if (booleanExtra) {
                            if (SettingUtils.isLogin(getActivity()))
                                mPresenter.getUserData();
                        } else {
                            MainActivity activity = (MainActivity) getActivity();
                            activity.restoreToPreTab();
                            if (mDailyWindow != null)
                                mDailyWindow.dismiss();
                            if (mInvestCodeWindow != null)
                                mInvestCodeWindow.dismiss();
                        }
                    }
                    break;
                case ACTION_LOGIN_RELOGIN:
                    if (data != null) {
                        boolean booleanExtra = data.getBooleanExtra(Constant.Activity.Data, false);
                        if (booleanExtra) {
                            if (SettingUtils.isLogin(getActivity()))
                                mPresenter.getUserData();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.unsubcrible();
        if (mDisposables != null) {
            mDisposables.dispose();
            mDisposables.clear();
        }
        if (mDailyWindow != null) {
            mDailyWindow.dismiss();
            mDailyWindow = null;
        }
        if (mInvestCodeWindow != null) {
            mInvestCodeWindow.dismiss();
            mInvestCodeWindow = null;
        }
        if (mAzBannerAd != null)
            mAzBannerAd.onDestroy();
    }
}
