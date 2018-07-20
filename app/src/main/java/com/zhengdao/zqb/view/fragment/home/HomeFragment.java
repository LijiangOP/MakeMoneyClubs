package com.zhengdao.zqb.view.fragment.home;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
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
import com.zhengdao.zqb.customview.RewardWindow;
import com.zhengdao.zqb.customview.UpdataProgressWindow;
import com.zhengdao.zqb.customview.UpdataWindow;
import com.zhengdao.zqb.entity.AnnouncementBean;
import com.zhengdao.zqb.entity.BannerBean;
import com.zhengdao.zqb.entity.EarnEntity;
import com.zhengdao.zqb.entity.HomeInfo;
import com.zhengdao.zqb.entity.HomeItemBean;
import com.zhengdao.zqb.entity.HomeItemEntity;
import com.zhengdao.zqb.entity.MessageEntity;
import com.zhengdao.zqb.entity.SurveyHttpResult;
import com.zhengdao.zqb.entity.UpdateInfoEntity;
import com.zhengdao.zqb.entity.UserHomeBean;
import com.zhengdao.zqb.entity.Version;
import com.zhengdao.zqb.entity.WelfareHttpData;
import com.zhengdao.zqb.event.ClearRedPointEvent;
import com.zhengdao.zqb.event.IsShowProgressEvent;
import com.zhengdao.zqb.mvp.MVPBaseFragment;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.MyDateUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.utils.update.UpdateUtil;
import com.zhengdao.zqb.view.activity.customservice.CustomServiceActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.message.MessageActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.activity.welfareget.WelfareGetActivity;
import com.zhengdao.zqb.view.adapter.HomeFragmentAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
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

    private UpdateUtil              mUpdateUtil;
    private boolean                 mMustUpdate;
    private int                     mBannerHeight;
    private ArrayList<HomeItemBean> mData;
    private HomeFragmentAdapter     mAdapter;
    private long    mCurrentAskDataTime = 0;
    private int     mCurrentPage        = 1;
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

    @Override
    protected View getFragmentView() {
        View view = View.inflate(getActivity(), R.layout.home_fragment, null);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setTransparentForImageViewInFragment(getActivity(), null);
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
                    mCurrentPage++;
                    mPresenter.getMore(mCurrentPage);
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
        if (SettingUtils.isFristInstall(getActivity())) {
            mPresenter.getWelfareData();
            SettingUtils.setFristInstall(getActivity(), false);
        }
        if (SettingUtils.isLogin(getActivity()))
            mPresenter.getUserData();
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

    private void setColor(int dy) {//当前高度
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
        mCurrentPage = 1;//翻页
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

    @Override
    public void buildData(HomeInfo result) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            hideProgress();
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
            HomeItemBean itemHeadBean = new HomeItemBean();
            String game_state = TextUtils.isEmpty(result.GAME_STATE) ? "" : result.GAME_STATE;
            itemHeadBean.isShowGame = game_state.equals("0") ? true : false;
            itemHeadBean.type = 0;
            //banner
            List<BannerBean> bannerList = result.adverts;
            List<AnnouncementBean> notice = result.platformMsg;
            if (bannerList != null) {
                itemHeadBean.bannerList = bannerList;
            }
            //marqueen view
            ArrayList<TextView> views = buildmarqueeList(notice);
            itemHeadBean.marqueeList = views;
            itemHeadBean.notice = notice;
            //imageview
            itemHeadBean.invitationBanner = result.invitationBanner;
            mData.add(itemHeadBean);
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
                        itemlicaiBean.discount = entity.discount;
                        itemlicaiBean.lowerTime = entity.lowerTime;
                        mData.add(itemlicaiBean);
                    }
                }
            }
            mMsvHomeFragment.setViewState(MultiStateView.VIEW_STATE_CONTENT);
            if (mAdapter == null) {
                mAdapter = new HomeFragmentAdapter(getActivity(), mData, this);
                mRvHome.setAdapter(mAdapter);
                mRvHome.setLayoutManager(new LinearLayoutManager(getActivity()));
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

    @Override
    public void onGetMoreData(EarnEntity result) {
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
                SpannableString spannableString = new SpannableString(nickName + " " + min + "前完成任务获得赏金" + format + "元");
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
     * 问卷点差点击回调
     */
    @Override
    public void onItemCallBack() {
        if (!SettingUtils.isLogin(getActivity()))
            startActivity(new Intent(getActivity(), LoginActivity.class));
        else
            mPresenter.getSurveyLink();
    }

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
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(getActivity(), "登录超时,请重新登录");
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            ToastUtil.showToast(getActivity(), TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

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
