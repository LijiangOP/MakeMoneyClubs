package com.zhengdao.zqb.view.fragment.home;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.kennyc.view.MultiStateView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.ActivityWindow;
import com.zhengdao.zqb.customview.DragFloatButton;
import com.zhengdao.zqb.customview.ExitDialog;
import com.zhengdao.zqb.customview.RewardWindow;
import com.zhengdao.zqb.customview.UpdataProgressWindow;
import com.zhengdao.zqb.customview.UpdataWindow;
import com.zhengdao.zqb.entity.AnnouncementBean;
import com.zhengdao.zqb.entity.BannerBean;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.DictionaryValue;
import com.zhengdao.zqb.entity.EarnEntity;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HomeInfo;
import com.zhengdao.zqb.entity.HomeItemBean;
import com.zhengdao.zqb.entity.HomeItemEntity;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.entity.UpdateInfoEntity;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.entity.Version;
import com.zhengdao.zqb.entity.WelfareHttpData;
import com.zhengdao.zqb.event.ClearRedPointEvent;
import com.zhengdao.zqb.event.IsShowProgressEvent;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.DateDefUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.MyDateUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.utils.update.UpdateUtil;
import com.zhengdao.zqb.view.activity.customservice.CustomServiceActivity;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.message.MessageActivity;
import com.zhengdao.zqb.view.activity.questionsurvery.QuestionSurveryActivty;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.activity.welfareget.WelfareGetActivity;
import com.zhengdao.zqb.view.adapter.HomeFragmentAdapter;
import com.zhengdao.zqb.view.adapter.WantedSelectedAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.badgeview.BGABadgeLinearLayout;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends MVPBaseFragment<HomeContract.View, HomePresenter> implements HomeContract.View, View.OnClickListener, UpdateUtil.UpdateListener, HomeFragmentAdapter.CallBack {

    private static final int GET_WELFARE    = 007;
    private static final int ACTION_UPDATE  = 006;
    private static final int ACTION_MESSAGE = 005;
    @BindView(R.id.rv_home)
    RecyclerView         mRvHome;
    @BindView(R.id.msv_home_fragment)
    MultiStateView       mMsvHomeFragment;
    @BindView(R.id.swiperefreshlayout)
    SmartRefreshLayout   mSwiperefreshlayout;
    @BindView(R.id.fake_status_bar)
    View                 mFakeStatusBar;
    @BindView(R.id.bga_home_message)
    BGABadgeLinearLayout mBgaHomeMessage;
    @BindView(R.id.ib_home_message)
    ImageButton          mIbHomeMessage;
    @BindView(R.id.tv_title)
    TextView             mTvTitle;
    @BindView(R.id.ib_home_service)
    ImageButton          mIbHomeService;
    @BindView(R.id.rl_home_title)
    RelativeLayout       mRlHomeTitle;
    @BindView(R.id.drag_button)
    DragFloatButton      mDragButton;

    private UpdateUtil              mUpdateUtil;
    private boolean                 mMustUpdate;
    private int                     mBannerHeight;
    private ArrayList<HomeItemBean> mData;
    private HomeFragmentAdapter     mAdapter;
    private long    mCurrentAskDataTime = 0;
    private boolean mIsHasNext          = false;
    private RewardWindow         mRewardDialog;//完成悬赏后,弹出的获取奖励弹框
    private UpdataWindow         mUpdataWindow;//APP更新提示弹窗
    private ActivityWindow       mActivityDialog;//活动弹框(领红包)
    private UpdataProgressWindow mUpdataProgressWindow;//APP更新进度弹窗
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Disposable mClearRedPointDisposable;
    private Disposable mIsShowProgressDisposable;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_UPDATE:
                    showUpdateProgressDialog();
                    float obj = (float) msg.obj;
                    if (mUpdataProgressWindow != null)
                        mUpdataProgressWindow.setProgress(obj);
                    break;
                case ACTION_MESSAGE:
                    mBgaHomeMessage.hiddenBadge();
                    break;
            }
        }
    };
    private HomeItemBean mItemHeadBean;

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.home_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForImageViewInFragment(getActivity(), null);//设置banner中图片渲染至系统状态栏
        mBannerHeight = getActivity().getResources().getDimensionPixelOffset(R.dimen.home_banner_height);
        mSwiperefreshlayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initHomeData();
                refreshlayout.finishRefresh();
            }
        });
        mSwiperefreshlayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (mIsHasNext) {
                    mPresenter.getMoreData();
                }
                refreshlayout.finishLoadmore();
            }
        });
        mMsvHomeFragment.getView(MultiStateView.VIEW_STATE_ERROR).findViewById(R.id.btn_error_reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initHomeData();
            }
        });
        mClearRedPointDisposable = RxBus.getDefault().toObservable(ClearRedPointEvent.class).subscribe(new Consumer<ClearRedPointEvent>() {
            @Override
            public void accept(ClearRedPointEvent clearRedPointEvent) throws Exception {
                mHandler.sendMessage(mHandler.obtainMessage(ACTION_MESSAGE));
            }
        });
        mIsShowProgressDisposable = RxBus.getDefault().toObservable(IsShowProgressEvent.class).subscribe(new Consumer<IsShowProgressEvent>() {
            @Override
            public void accept(IsShowProgressEvent isShowProgressEvent) throws Exception {
                if (isShowProgressEvent.isShow) {
                    mHandler.sendMessage(mHandler.obtainMessage(ACTION_UPDATE, isShowProgressEvent.progress));
                } else if (mUpdataProgressWindow != null) {
                    mUpdataProgressWindow.dismiss();
                }
            }
        });
        mDisposables.add(mClearRedPointDisposable);
        mDisposables.add(mIsShowProgressDisposable);
        mIbHomeMessage.setOnClickListener(this);
        mIbHomeService.setOnClickListener(this);
        mRvHome.addOnScrollListener(mOnScrollListener);
        //软件更新模块
        mUpdateUtil = new UpdateUtil(getContext(), this);
        mUpdateUtil.checkUpdate();
        //活动弹窗
        if (SettingUtils.isFristInstall(getActivity()))//第一次安装才能领取
            mPresenter.getWelfareData();
        //获取用户个人数据(主要是使用用户的任务奖励数据)
        if (SettingUtils.isLogin(getActivity()))
            mPresenter.getUserData();
        //今天还未点击,获取支付宝红包
        if (DateDefUtils.isCanShowRedPacketToDay(getActivity()))
            mPresenter.getAliPayRedPacket("ZFB_REWARD");
        //登录,未领取任务,获取推荐悬赏
        boolean value = SettingUtils.isLogin(getActivity()) && DateDefUtils.isCanShowRecommendRewardToDay(getActivity()) && SettingUtils.getReceiveCount(getActivity()) == 0;
        if (value || !SettingUtils.isLogin(getActivity()))
            mPresenter.getRecommendReward();
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int verticalOffset = mRvHome.computeVerticalScrollOffset();
            setColor(verticalOffset);
        }
    };

    /**
     * 根据RecycleView高度,显示顶部控件
     *
     * @param dy RecycleView当前高度
     */
    private void setColor(int dy) {
        try {
            dy = Math.abs(dy);
            if (dy <= 0) {
                mTvTitle.setAlpha(0);
                mFakeStatusBar.setBackgroundColor(Color.argb(0, 243, 62, 63));
                mRlHomeTitle.setBackgroundColor(Color.argb(0, 243, 62, 63));
            } else if (dy > 0 && dy <= mBannerHeight) {
                float scale = (float) dy / mBannerHeight;
                float alpha = (255 * scale);
                mFakeStatusBar.setBackgroundColor(Color.argb((int) alpha, 255, 49, 53));
                mRlHomeTitle.setBackgroundColor(Color.argb((int) alpha, 255, 49, 53));
                mTvTitle.setAlpha(alpha);
            } else {
                mTvTitle.setAlpha(1);
                mFakeStatusBar.setBackgroundColor(Color.argb(255, 255, 49, 53));
                mRlHomeTitle.setBackgroundColor(Color.argb(255, 255, 49, 53));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHomeData() {
        mIsHasNext = true;//翻页
        mPresenter.initData();//获取首页数据
        mPresenter.getMessageCount();//获取消息计数
    }

    @Override
    public void onResume() {
        super.onResume();
        long value = System.currentTimeMillis();
        if (value - mCurrentAskDataTime > 3 * 60 * 1000 || mCurrentAskDataTime == 0)//3分钟间隔请求数据
            initHomeData();
        mCurrentAskDataTime = System.currentTimeMillis();
    }

    /**
     * 首页的数据返回处理
     *
     * @param result
     */
    @Override
    public void buildData(HomeInfo result) {
        hideProgress();
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result == null) {
                mMsvHomeFragment.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                mSwiperefreshlayout.finishLoadmoreWithNoMoreData();
                return;
            }
            if (mData == null)
                mData = new ArrayList<>();
            mData.clear();
            mIsHasNext = result.newsRewards.hasNextPage;
            //头部条目
            mItemHeadBean = new HomeItemBean();
            mItemHeadBean.menus = result.menus;
            mItemHeadBean.type = 0;
            //banner
            List<BannerBean> bannerList = result.adverts;
            List<AnnouncementBean> notice = result.platformMsg;
            if (bannerList != null) {
                mItemHeadBean.bannerList = bannerList;
            }
            //marqueen view
            ArrayList<TextView> views = buildmarqueeList(notice);
            mItemHeadBean.marqueeList = views;
            mItemHeadBean.notice = notice;
            //imageview
            mItemHeadBean.invitationBanner = result.invitationBanner;
            mData.add(mItemHeadBean);
            //商品模块
            if (result.newsRewards != null) {
                List<HomeItemEntity> list = result.newsRewards.list;
                if (list != null && list.size() > 0) {
                    for (HomeItemEntity entity : list) {
                        HomeItemBean itemlicaiBean = new HomeItemBean();
                        itemlicaiBean.type = 2;
                        itemlicaiBean.goodsType = entity.type;
                        itemlicaiBean.createTime = entity.createTime;
                        itemlicaiBean.id = entity.id;
                        itemlicaiBean.joincount = entity.joincount;
                        itemlicaiBean.isOwn = entity.isOwn;
                        itemlicaiBean.keyword = entity.keyword;
                        itemlicaiBean.money = entity.money;
                        itemlicaiBean.picture = entity.picture;
                        itemlicaiBean.title = entity.title;
                        itemlicaiBean.name = entity.name;
                        itemlicaiBean.discount = entity.discount;
                        itemlicaiBean.lowerTime = entity.lowerTime;
                        mData.add(itemlicaiBean);
                    }
                } else if (!TextUtils.isEmpty(SettingUtils.getUserToken(getActivity()))) {//登录状态下,悬赏已经全部完成,未完成任务无
                    HomeItemBean homeItemBean = new HomeItemBean();
                    homeItemBean.type = 3;
                    mData.add(homeItemBean);
                } else {//未登录状态,悬赏任务无
                    HomeItemBean homeItemBean = new HomeItemBean();
                    homeItemBean.type = 4;
                    mData.add(homeItemBean);
                }
            }
            mMsvHomeFragment.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if (mAdapter == null) {
                mAdapter = new HomeFragmentAdapter(getActivity(), mData, this);
                mRvHome.setAdapter(mAdapter);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return mAdapter.getItemViewType(position) == 2 ? 1 : 2;
                    }
                });
                mRvHome.setLayoutManager(gridLayoutManager);
            } else
                mAdapter.notifyDataSetChanged();
            //1.3.2版本漏掉的
            if (mIsHasNext) {
                mSwiperefreshlayout.resetNoMoreData();
            } else {
                mSwiperefreshlayout.finishLoadmoreWithNoMoreData();
            }
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else
            mMsvHomeFragment.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    /**
     * 下拉加载下一页的数据返回处理
     *
     * @param result
     */
    @Override
    public void onGetMoreData(EarnEntity result) {
        hideProgress();
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.rewards != null) {
                mIsHasNext = result.rewards.hasNextPage;
                if (result.rewards.list != null && result.rewards.list.size() > 0) {
                    ArrayList<HomeItemEntity> list = result.rewards.list;
                    for (HomeItemEntity entity : list) {
                        HomeItemBean itemlicaiBean = new HomeItemBean();
                        itemlicaiBean.type = 2;
                        itemlicaiBean.goodsType = entity.type;
                        itemlicaiBean.createTime = entity.createTime;
                        itemlicaiBean.id = entity.id;
                        itemlicaiBean.joincount = entity.joincount;
                        itemlicaiBean.isOwn = entity.isOwn;
                        itemlicaiBean.keyword = entity.keyword;
                        itemlicaiBean.money = entity.money;
                        itemlicaiBean.picture = entity.picture;
                        itemlicaiBean.title = entity.title;
                        itemlicaiBean.name = entity.name;
                        itemlicaiBean.discount = entity.discount;
                        mData.add(itemlicaiBean);
                    }
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
            } else
                mIsHasNext = false;
            if (mIsHasNext) {
                mSwiperefreshlayout.resetNoMoreData();
            } else {
                mSwiperefreshlayout.finishLoadmoreWithNoMoreData();
            }
        }
    }

    /**
     * 刷新商品条目
     *
     * @param result
     */
    @Override
    public void onRefreshZeroEarn(EarnEntity result) {
        hideProgress();
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result == null) {
                mMsvHomeFragment.setViewState(MultiStateView.VIEW_STATE_EMPTY);
                mSwiperefreshlayout.finishLoadmoreWithNoMoreData();
                return;
            }
            if (mData != null)
                mData.clear();
            if (mItemHeadBean != null)
                mData.add(mItemHeadBean);
            if (result.rewards != null) {
                mIsHasNext = result.rewards.hasNextPage;
                if (result.rewards.list != null && result.rewards.list.size() > 0) {
                    ArrayList<HomeItemEntity> list = result.rewards.list;
                    for (HomeItemEntity entity : list) {
                        HomeItemBean itemlicaiBean = new HomeItemBean();
                        itemlicaiBean.type = 2;
                        itemlicaiBean.goodsType = entity.type;
                        itemlicaiBean.createTime = entity.createTime;
                        itemlicaiBean.id = entity.id;
                        itemlicaiBean.joincount = entity.joincount;
                        itemlicaiBean.isOwn = entity.isOwn;
                        itemlicaiBean.keyword = entity.keyword;
                        itemlicaiBean.money = entity.money;
                        itemlicaiBean.picture = entity.picture;
                        itemlicaiBean.name = entity.name;
                        itemlicaiBean.title = entity.title;
                        itemlicaiBean.discount = entity.discount;
                        mData.add(itemlicaiBean);
                    }
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                }
            } else {//未登录状态,悬赏任务无
                HomeItemBean homeItemBean = new HomeItemBean();
                homeItemBean.type = 4;
                mData.add(homeItemBean);
            }
            mMsvHomeFragment.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
            if (mIsHasNext) {
                mSwiperefreshlayout.resetNoMoreData();
            } else {
                mSwiperefreshlayout.finishLoadmoreWithNoMoreData();
            }
        }
    }

    /**
     * 创建跑马灯数据
     *
     * @param bannerList
     * @return
     */
    private ArrayList<TextView> buildmarqueeList(List<AnnouncementBean> bannerList) {
        ArrayList<TextView> views = new ArrayList<>();
        for (AnnouncementBean bean : bannerList) {
            //TextView textView = new TextView(mContext); 这样会报already has parent的错误
            try {
                TextView textView = (TextView) View.inflate(getActivity(), R.layout.marquee_tv, null);
                long agoTime = bean.agoTime;
                String min = MyDateUtils.HomeformatTime(agoTime);
                String format = new DecimalFormat("#0.00").format(bean.inAmount == null ? 0 : bean.inAmount);
                String nickName = TextUtils.isEmpty(bean.nickName) ? "" : bean.nickName;
                nickName = nickName.length() > 3 ? nickName.substring(0, 3) + "..." : nickName;
                SpannableString spannableString = new SpannableString(nickName + " " + min + "前获得赏金" + format + "元");
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_00b9fd)), 0, nickName.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.main)), spannableString.length() - format.length() - 1, spannableString.length() - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
                textView.setTextSize(12);
                textView.setSingleLine(true);
                textView.setGravity(Gravity.CENTER_VERTICAL);
                textView.setTextColor(getActivity().getResources().getColor(R.color.color_424242));
                views.add(textView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return views;
    }

    /**
     * 新人福利弹窗
     *
     * @param httpResult
     */
    @Override
    public void showWelfareWindow(WelfareHttpData httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (TextUtils.isEmpty(httpResult.popup))
                return;
            if (mActivityDialog == null)
                mActivityDialog = new ActivityWindow(getActivity());
            mActivityDialog.setPosition(0, -100);
            mActivityDialog.initContentView(httpResult.popup, new ActivityWindow.ImgReadyCallBack() {
                @Override
                public void onImgReady() {
                    mActivityDialog.show();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SettingUtils.isLogin(getActivity())) {
                        startActivity(new Intent(getActivity(), WelfareGetActivity.class));
                        mActivityDialog.dismiss();
                    } else {
                        startActivityForResult(new Intent(getActivity(), LoginActivity.class), GET_WELFARE);
                    }
                }
            });
        } else
            LogUtils.e("请求出错");
    }

    /**
     * 完成悬赏任务后奖励弹窗
     *
     * @param httpResult
     */
    @Override
    public void showRewardWindow(UserHomeBean httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            int rewardMessageCount = SettingUtils.getRewardMessageCount(getActivity());
            int total = 0;
            if (httpResult.taskCount != null && httpResult.taskCount.size() > 0) {
                for (UserHomeBean.TaskCount entity : httpResult.taskCount) {
                    switch (entity.state) {
                        case 3:
                            total = entity.count;
                            break;
                    }
                }
            }
            if (total > rewardMessageCount)
                showMoney();
            if (total != 0)
                SettingUtils.setRewardMessageCount(getActivity(), total);
        } else
            LogUtils.e("请求出错");
    }

    private void showMoney() {
        if (mRewardDialog == null)
            mRewardDialog = new RewardWindow(getActivity());
        mRewardDialog.setPosition(0, -100);
        mRewardDialog.initContentView();
        mRewardDialog.setCanceledOnTouchOutside(true);
        mRewardDialog.show();
    }

    /**
     * 获取消息数目的数据返回处理
     *
     * @param httpResult
     */
    @Override
    public void onMessageCountGet(MessageEntity httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                int total = httpResult.message.total;
                int messageCount = SettingUtils.getMessageCount(getActivity());
                if (total > messageCount) {
                    mBgaHomeMessage.showCirclePointBadge();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 支护宝红包接口
     *
     * @param result
     * @param key
     */
    @Override
    public void onDictionaryDataGet(DictionaryHttpEntity result, String key) {
        try {
            if (result.code == Constant.HttpResult.SUCCEED) {
                if (result.types != null && result.types.size() > 0) {
                    final DictionaryValue dictionaryValue = result.types.get(0);
                    if (dictionaryValue != null && dictionaryValue.state == 0) {
                        mDragButton.init(getActivity());
                        mDragButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    if (dictionaryValue != null && !TextUtils.isEmpty(dictionaryValue.value)) {
                                        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                                        SettingUtils.setAliPayLastShowDate(getActivity(), dayOfYear);
                                        Uri uri;
                                        if (checkAliPayIsInstall(getActivity())) {
                                            uri = Uri.parse(dictionaryValue.value);
                                        } else {
                                            uri = Uri.parse("https://mobile.alipay.com/index.htm");
                                        }
                                        Intent four = new Intent(Intent.ACTION_VIEW, uri);
                                        getActivity().startActivity(four);
                                        mDragButton.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        mDragButton.setVisibility(View.VISIBLE);
                    } else
                        mDragButton.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkAliPayIsInstall(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 推荐悬赏
     *
     * @param httpResult
     */
    @Override
    public void onGetReCommandResult(final GoodsCommandHttpEntity httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            final ExitDialog exitDialog = new ExitDialog(getActivity());
            SpannableString spannableString = new SpannableString("推荐一个优质悬赏给您");
            exitDialog.init("温馨提示", spannableString, "马上赚钱", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                    SettingUtils.setRecommendRewardLastShowDate(getActivity(), dayOfYear);
                    Intent intent = new Intent(getActivity(), HomeGoodsDetailActivity.class);
                    intent.putExtra(Constant.Activity.Data, httpResult.reward.id);
                    Utils.StartActivity(getActivity(), intent);
                    exitDialog.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
                    SettingUtils.setRecommendRewardLastShowDate(getActivity(), dayOfYear);
                    exitDialog.dismiss();
                }
            });
            exitDialog.setGoods(httpResult);
            exitDialog.show();
        }
    }

    @Override
    public void showErrorMessage(String msg) {
        super.showErrorMessage(msg);
        mMsvHomeFragment.setViewState(MultiStateView.VIEW_STATE_ERROR);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_home_message:
                getActivity().startActivity(new Intent(getActivity(), MessageActivity.class));
                break;
            case R.id.ib_home_service:
                getActivity().startActivity(new Intent(getActivity(), CustomServiceActivity.class));
                break;
        }
    }

    /**
     * 软件更新的回调(失败)
     *
     * @param msg
     */
    @Override
    public void onError(String msg) {
        LogUtils.e(TextUtils.isEmpty(msg) ? "返回空" : msg);
    }

    /**
     * 软件更新的回调(成功)
     *
     * @param needUpdate
     * @param entity
     */
    @Override
    public void onSuccess(boolean needUpdate, UpdateInfoEntity entity) {
        if (needUpdate) {
            if (entity != null && entity.version != null)
                showUpdateDialog(entity.version);
        }
    }

    /**
     * 更新内容提示弹窗
     *
     * @param bean
     */
    public void showUpdateDialog(final Version bean) {
        try {
            int updateInstall = bean.updateInstall;
            mMustUpdate = updateInstall == 0 ? false : updateInstall == 1 ? true : false;
            String insideVersion = "" + bean.insideVersion;
            if (insideVersion != null) {
                if (insideVersion.equals(SettingUtils.getIgnoreVersion(getContext())))
                    return;
            }
            if (mUpdataWindow == null) {
                mUpdataWindow = new UpdataWindow(getActivity());
                mUpdataWindow.setCanceledOnTouchOutside(false);
                mUpdataWindow.setCancelable(false);
            }
            mUpdataWindow.setPosition(0, -100);
            if (mMustUpdate) {
                mUpdataWindow.initContentView(bean.clientVersion, bean.cotent, true, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUpdataWindow.dismiss();
                        mUpdateUtil.update(bean, mMustUpdate);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUpdataWindow.dismiss();
                        getActivity().finish();
                    }
                });
            } else {
                mUpdataWindow.initContentView(bean.clientVersion, bean.cotent, false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUpdataWindow.dismiss();
                        mUpdateUtil.update(bean, mMustUpdate);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SettingUtils.setIgnoreVersion(getContext(), "" + bean.insideVersion);
                        ToastUtil.showToastWithGravity(getContext(), "已忽略当前版本更新！\n你可以到 设置-->关于-->版本更新 手动更新", Gravity.CENTER);
                        mUpdataWindow.dismiss();
                    }
                });
            }
            mUpdataWindow.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * APP正在下载更新的进度显示框
     */
    private void showUpdateProgressDialog() {
        if (mUpdataProgressWindow == null) {
            mUpdataProgressWindow = new UpdataProgressWindow(getActivity());
            mUpdataProgressWindow.setCanceledOnTouchOutside(false);
            mUpdataProgressWindow.setCancelable(false);
        }
        mUpdataProgressWindow.setPosition(0, -100);
        mUpdataProgressWindow.show();
    }

    /**
     * 问卷调查点击回调
     */
    @Override
    public void onItemCallBack() {
        if (!SettingUtils.isLogin(getActivity()))
            startActivity(new Intent(getActivity(), LoginActivity.class));
        else
            mPresenter.getSurveyLink();
    }

    @Override
    public void onSelectionClick(String order, String desc, int i) {
        mPresenter.getDataWithBaseSearch(order, desc, i);
    }

    @Override
    public void doSelect() {
        mPresenter.getSelectTypeData();
    }


    /**
     * 获取问卷调查页面链接的数据返回处理
     *
     * @param httpResult
     */
    @Override
    public void onSurveyLinkGet(SurveyHttpResult httpResult) {
        if (httpResult == null) {
            ToastUtil.showToast(getActivity(), "请求出错");
            return;
        }
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(httpResult.url)) {
                Intent survey = new Intent(getActivity(), WebViewActivity.class);
                survey.putExtra(Constant.WebView.TITLE, getString(R.string.home_survey));
                survey.putExtra(Constant.WebView.URL, httpResult.url);
                startActivity(survey);
            }
        } else if (httpResult.code == Constant.HttpResult.FAILD) {
            Intent survery = new Intent(getActivity(), QuestionSurveryActivty.class);
            survery.putExtra(Constant.Activity.Skip, "survey");
            Utils.StartActivity(getActivity(), survery);
            ToastUtil.showToast(getActivity(), "请完善用户信息");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    private int mCategory = -1;

    /**
     * 筛选条件的数据获取返回
     *
     * @param result
     */
    @Override
    public void showSelectTypeView(ScreenLoadEntity result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (result.rewardType != null && mAdapter != null) {
                mAdapter.setRewardType(result.rewardType, new WantedSelectedAdapter.ItemSelectedCallBack() {
                    @Override
                    public void wantedItemIsSelected(int id) {
                        mCategory = id;
                        mPresenter.getDataWithFilter(-1, mCategory);
                    }
                }, mCategory);
            }
        } else {
            LogUtils.e(TextUtils.isEmpty(result.msg) ? "数据加载失败" : result.msg);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GET_WELFARE:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        startActivity(new Intent(getActivity(), WelfareGetActivity.class));
                    }
                }
                break;
        }
    }


    /**
     * RecycleView的子条目装饰
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = ViewUtils.dip2px(getActivity(), space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int itemViewType = parent.getAdapter().getItemViewType(position);
            if (itemViewType == 2) {
                if (position % 2 == 0) {
                    outRect.bottom = space;
                } else {
                    outRect.right = space;
                    outRect.bottom = space;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUpdateUtil != null)
            mUpdateUtil.unSubscripe();
        if (mActivityDialog != null) {
            mActivityDialog.dismiss();
            mActivityDialog = null;
        }
        if (mRewardDialog != null) {
            mRewardDialog.dismiss();
            mRewardDialog = null;
        }
        if (mUpdataWindow != null) {
            mUpdataWindow.dismiss();
            mUpdataWindow = null;
        }
        if (mDisposables != null) {
            mDisposables.dispose();
            mDisposables.clear();
        }
    }
}
