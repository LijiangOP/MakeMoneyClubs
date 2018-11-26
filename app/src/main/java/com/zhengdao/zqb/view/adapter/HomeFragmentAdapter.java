package com.zhengdao.zqb.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.fynn.fluidlayout.FluidLayout;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.AutoLinefeedLayout;
import com.zhengdao.zqb.customview.MarqueeView;
import com.zhengdao.zqb.entity.HomeItemBean;
import com.zhengdao.zqb.entity.MenusBean;
import com.zhengdao.zqb.entity.ScreenLoadEntity;
import com.zhengdao.zqb.utils.ActivityUtils;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.SkipUtils;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.newhandmission.NewHandMissionActivity;
import com.zhengdao.zqb.view.activity.news.NewsActivity;
import com.zhengdao.zqb.view.activity.rankinglist.RankingListActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.activity.zeroearn.ZeroEarnActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.application.ClientAppLike.AppType;

/**
 * @Author lijiangop
 * @CreateTime 2017/12/28 18:05
 */
public class HomeFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER       = 0;
    private static final int TYPE_GOODS        = 2;
    private static final int TYPE_REWARD_EMPTY = 3;
    private static final int TYPE_EMPTY        = 4;
    private HeaderViewHolder   mHeaderViewHolder;
    private GoodsViewHolder    mGoodsViewHolder;
    private Activity           mContext;
    private List<HomeItemBean> mData;
    private CallBack           mCallBack;

    public interface CallBack {
        void onItemCallBack();

        void onSelectionClick(String order, String desc, int i);

        void doSelect();
    }

    public HomeFragmentAdapter(Activity activity, List<HomeItemBean> data, CallBack callBack) {
        this.mCallBack = callBack;
        this.mContext = activity;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_HEADER) {//banner至跑马灯的模块
            View header = View.inflate(mContext, R.layout.recyl_home_header, null);
            holder = new HeaderViewHolder(header);
        } else if (viewType == TYPE_GOODS) {//悬赏任务条目模块
            View licai = View.inflate(mContext, R.layout.recyl_home_goods, null);
            holder = new GoodsViewHolder(licai);
        } else if (viewType == TYPE_REWARD_EMPTY) {//没有悬赏任务了(仅登录状态下，查询到该用户全部悬赏都已完成)
            View rewardempty = LayoutInflater.from(mContext).inflate(R.layout.recyl_home_reward_empty, parent, false);
            holder = new RewardEmptyViewHolder(rewardempty);
        } else if (viewType == TYPE_EMPTY) {//没有悬赏任务(服务器没数据)
            View empty = LayoutInflater.from(mContext).inflate(R.layout.recyl_home_empty, parent, false);
            holder = new EmptyViewHolder(empty);
        }
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            final HomeItemBean itemBean = mData.get(position);
            if (itemBean == null)
                return;
            if (itemBean.type == TYPE_HEADER) {
                mHeaderViewHolder = (HeaderViewHolder) holder;
                //banner
                mHeaderViewHolder.mConvenientBanner.setPages(new CBViewHolderCreator<BannerImageHolderView>() {
                    @Override
                    public BannerImageHolderView createHolder() {
                        return new BannerImageHolderView(ImageView.ScaleType.FIT_XY);
                    }
                }, itemBean.bannerList)
                        .setPageIndicator(new int[]{R.drawable.img_circle2, R.drawable.img_circle1})
                        .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
                if (!mHeaderViewHolder.mConvenientBanner.isTurning())
                    mHeaderViewHolder.mConvenientBanner.startTurning(5000);
                mHeaderViewHolder.mConvenientBanner.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ActivityUtils.doSkip(mContext, itemBean.bannerList.get(position).type, itemBean.bannerList.get(position).url, itemBean.bannerList.get(position).id);
                    }
                });
                //menus
                addMenus(itemBean.menus, 5);
                //跑马灯
                if (!mHeaderViewHolder.mMarqueeViewHome.isFlipping())
                    mHeaderViewHolder.mMarqueeViewHome.startWithViews(itemBean.marqueeList);
                mHeaderViewHolder.mTvRank.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SettingUtils.isLogin(mContext)) {
                            mContext.startActivity(new Intent(mContext, RankingListActivity.class));
                        } else {
                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                        }
                    }
                });
                initSelection();
            } else if (itemBean.type == TYPE_GOODS) {//商品数据 type=2;
                mGoodsViewHolder = (GoodsViewHolder) holder;
                Glide.with(mContext).load(itemBean.picture).error(R.drawable.net_less_140).into(mGoodsViewHolder.mIvIcon);
                mGoodsViewHolder.mTvDescibe.setText(TextUtils.isEmpty(itemBean.name) ? "" : itemBean.name);
                mGoodsViewHolder.mFluidLayoutKeyWord.removeAllViews();
                if (!TextUtils.isEmpty(itemBean.keyword)) {
                    if (itemBean.keyword.contains(",")) {
                        String[] split = itemBean.keyword.split(",");
                        switch (split.length) {
                            case 1:
                                addKeyword(split[0]);
                                break;
                            case 2:
                                addKeyword(split[0]);
                                addKeyword(split[1]);
                                break;
                            case 3:
                                addKeyword(split[0]);
                                addKeyword(split[1]);
                                addKeyword(split[2]);
                                break;
                        }
                    } else {
                        addKeyword(itemBean.keyword);
                    }
                }
                mGoodsViewHolder.mFluidLayoutMoney.removeAllViews();
                addMoneyPart(itemBean);
                mGoodsViewHolder.mLlRecommend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        if (SettingUtils.isLogin(mContext)) {
                            intent = new Intent(mContext, HomeGoodsDetailActivity.class);
                            intent.putExtra(Constant.Activity.Data, itemBean.id);
                        } else {
                            intent = new Intent(mContext, LoginActivity.class);
                        }
                        mContext.startActivity(intent);
                    }
                });
            } else if (itemBean.type == TYPE_REWARD_EMPTY) {// 该用户已无可做悬赏任务(仅登录状态下)
                LogUtils.i("reward_reward_empty");
            } else if (itemBean.type == TYPE_EMPTY) {//无悬赏任务(后台无数据)
                LogUtils.i("reward_empty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    /**
     * 添加banner下导航按钮
     *
     * @param menus    按钮数据集合
     * @param maxCount 每行最大显示多少个
     * @throws Exception
     */
    private void addMenus(List<MenusBean> menus, int maxCount) throws Exception {
        if (menus == null || menus.size() < 0)
            return;
        mHeaderViewHolder.mAutoLayout.removeAllViews();
        int[] screenSize = DensityUtil.getScreenSize(mContext);
        int count = menus.size();
        for (MenusBean bean : menus) {
            if (AppType == Constant.App.Zqb) {
                if (bean.state == 1)//遍历是否有关闭的入口
                    count--;
            } else {
                if (bean.switchs == 1)//遍历是否有关闭的入口
                    count--;
            }
        }
        count = count >= maxCount ? maxCount : count;//每行最多maxCount
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(screenSize[0] / count, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 10;
        for (int i = 0; i < menus.size(); i++) {
            final MenusBean menusBean = menus.get(i);
            if (menusBean == null)
                continue;
            if (AppType == Constant.App.Zqb) {
                if (menusBean.state == 1)//跳过关闭的入口
                    continue;
            } else {
                if (menusBean.switchs == 1)//跳过关闭的入口
                    continue;
            }
            View inflate = View.inflate(mContext, R.layout.menus_tv, null);
            TextView textView = inflate.findViewById(R.id.textView);
            ImageView imageView = inflate.findViewById(R.id.imageView);
            textView.setText(TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
            inflate.setLayoutParams(layoutParams);
            inflate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (menusBean.type) {
                        case 0://H5类型
                            if (!TextUtils.isEmpty(menusBean.option2)) {
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra(Constant.WebView.TITLE, TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
                                intent.putExtra(Constant.WebView.URL, menusBean.option2);
                                mContext.startActivity(intent);
                            }
                            break;
                        case 1://0元赚
                            if (!TextUtils.isEmpty(menusBean.option2)) {// option2有值就跳转该H5
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra(Constant.WebView.TITLE, TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
                                intent.putExtra(Constant.WebView.URL, menusBean.option2);
                                mContext.startActivity(intent);
                            } else {
                                mContext.startActivity(new Intent(mContext, ZeroEarnActivity.class));
                            }
                            break;
                        case 2://娱乐悬赏
                            if (!TextUtils.isEmpty(menusBean.option2)) {// option2有值就跳转该H5
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra(Constant.WebView.TITLE, TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
                                intent.putExtra(Constant.WebView.URL, menusBean.option2);
                                mContext.startActivity(intent);
                            } else {
                                SkipUtils.SkipToXianWan(mContext);
                            }
                            break;
                        case 3://每日任务
                            if (!TextUtils.isEmpty(menusBean.option2)) {// option2有值就跳转该H5
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra(Constant.WebView.TITLE, TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
                                intent.putExtra(Constant.WebView.URL, menusBean.option2);
                                mContext.startActivity(intent);
                            } else {
                                Utils.StartActivity(mContext, new Intent(mContext, NewHandMissionActivity.class));
                            }
                            break;
                        case 4://问卷调查
                            if (!TextUtils.isEmpty(menusBean.option2)) {// option2有值就跳转该H5
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra(Constant.WebView.TITLE, TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
                                intent.putExtra(Constant.WebView.URL, menusBean.option2);
                                mContext.startActivity(intent);
                            } else {
                                if (mCallBack != null)
                                    mCallBack.onItemCallBack();
                            }
                            break;
                        case 5://浏览器打开
                            if (!TextUtils.isEmpty(menusBean.option2)) {// option2有值就跳转该H5
                                Uri uri = Uri.parse(menusBean.option2);
                                Intent four = new Intent(Intent.ACTION_VIEW, uri);
                                mContext.startActivity(four);
                            }
                            break;
                        case 6://新闻资讯
                            if (!TextUtils.isEmpty(menusBean.option2)) {// option2有值就跳转该H5
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra(Constant.WebView.TITLE, TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
                                intent.putExtra(Constant.WebView.URL, menusBean.option2);
                                mContext.startActivity(intent);
                            } else {
                                mContext.startActivity(new Intent(mContext, NewsActivity.class));
                            }
                            break;
                        case 7://有乐游戏
                            if (!TextUtils.isEmpty(menusBean.option2)) {// option2有值就跳转该H5
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra(Constant.WebView.TITLE, TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
                                intent.putExtra(Constant.WebView.URL, menusBean.option2);
                                mContext.startActivity(intent);
                            } else {
                                SkipUtils.SkipToYouLe(mContext);
                            }
                            break;
                        case 8://享玩游戏
                            if (!TextUtils.isEmpty(menusBean.option2)) {// option2有值就跳转该H5
                                Intent intent = new Intent(mContext, WebViewActivity.class);
                                intent.putExtra(Constant.WebView.TITLE, TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value);
                                intent.putExtra(Constant.WebView.URL, menusBean.option2);
                                mContext.startActivity(intent);
                            } else {
                                SkipUtils.SkipToXiangWan(mContext);
                            }
                            break;
                        case 9://H5类型(拼接Token)
                            if (!TextUtils.isEmpty(menusBean.option2))
                                SkipUtils.SkipToH5WithToken(TextUtils.isEmpty(menusBean.value) ? "" : menusBean.value, menusBean.option2, mContext);
                            break;
                    }
                }
            });
            Glide.with(mContext).load(menusBean.icon).error(R.drawable.icon_fiction).into(imageView);
            mHeaderViewHolder.mAutoLayout.addView(inflate);
        }
    }

    /**
     * 初始化筛选控件
     */
    private void initSelection() {
        mHeaderViewHolder.mReSynthesize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null)
                    mCallBack.onSelectionClick("order", "desc", -1);
            }
        });
        mHeaderViewHolder.mReHot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeArrow(1);
            }
        });
        mHeaderViewHolder.mReAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeArrow(2);
            }
        });
        mHeaderViewHolder.mReSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallBack != null)
                    mCallBack.doSelect();
            }
        });
    }

    private PopupWindow mPopupWindow;

    public void setRewardType(ArrayList<ScreenLoadEntity.ScreenLoadDetailEntity> rewardType, final WantedSelectedAdapter.ItemSelectedCallBack callBack, int wantedType) {
        if (rewardType != null && rewardType.size() > 0) {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.recycler_view, null);
            RecyclerView recyclerView = contentView.findViewById(R.id.recycler_view);
            WantedSelectedAdapter adapter = new WantedSelectedAdapter(mContext, R.layout.selected_item, rewardType, new WantedSelectedAdapter.ItemSelectedCallBack() {
                @Override
                public void wantedItemIsSelected(int id) {
                    callBack.wantedItemIsSelected(id);
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            }, wantedType);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
            recyclerView.addItemDecoration(new SpaceWantedItemDecoration(10));
            recyclerView.setAdapter(adapter);
            if (mPopupWindow == null) {
                mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setBackgroundDrawable(new ColorDrawable());
                mPopupWindow.setFocusable(true);
            }
            mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                }
            });
            if (Build.VERSION.SDK_INT > 18)
                mPopupWindow.showAsDropDown(mHeaderViewHolder.mLlHeard, 0, 0, Gravity.TOP);
            else
                mPopupWindow.showAsDropDown(mHeaderViewHolder.mLlHeard, 0, 0);
        }
    }

    public void dismissWindow() {
        if (mPopupWindow == null) {
            mPopupWindow.dismiss();
        }
    }

    private boolean mIsUpFrist  = false;
    private boolean mIsUpSecond = false;

    private void changeArrow(int i) {
        String sortName;
        String orderType;
        switch (i) {
            case 1:
                if (mIsUpFrist) {
                    mHeaderViewHolder.mIvHotArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                    mHeaderViewHolder.mIvHotArrowDown.setImageResource(R.drawable.little_arror_press_down);
                    orderType = "desc";
                } else {
                    mHeaderViewHolder.mIvHotArrowUp.setImageResource(R.drawable.little_arror_press_up);
                    mHeaderViewHolder.mIvHotArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                    orderType = "asc";
                }
                mHeaderViewHolder.mIvRewardArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                mHeaderViewHolder.mIvRewardArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                mIsUpFrist = !mIsUpFrist;
                mIsUpSecond = false;
                sortName = "joincount";
                if (mCallBack != null)
                    mCallBack.onSelectionClick(sortName, orderType, -1);
                break;
            case 2:
                mHeaderViewHolder.mIvHotArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                mHeaderViewHolder.mIvHotArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                if (mIsUpSecond) {
                    mHeaderViewHolder.mIvRewardArrowUp.setImageResource(R.drawable.little_arror_normal_up);
                    mHeaderViewHolder.mIvRewardArrowDown.setImageResource(R.drawable.little_arror_press_down);
                    orderType = "desc";
                } else {
                    mHeaderViewHolder.mIvRewardArrowUp.setImageResource(R.drawable.little_arror_press_up);
                    mHeaderViewHolder.mIvRewardArrowDown.setImageResource(R.drawable.little_arror_normal_down);
                    orderType = "asc";
                }
                mIsUpSecond = !mIsUpSecond;
                mIsUpFrist = false;
                sortName = "money";
                mCallBack.onSelectionClick(sortName, orderType, -1);
                break;
        }
    }

    /**
     * 添加关键词(悬赏条目)
     *
     * @param keyword
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addKeyword(String keyword) {
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 10, 25, 0);
        TextView textView = new TextView(mContext);
        textView.setText(keyword);
        textView.setTextSize(10);
        textView.setTextColor(mContext.getResources().getColor(R.color.color_6f717f));
        textView.setBackground(mContext.getResources().getDrawable(R.drawable.shape3));
        textView.setPadding(10, 3, 10, 3);
        mGoodsViewHolder.mFluidLayoutKeyWord.addView(textView, params);
    }

    /**
     * 添加价格显示(悬赏条目)
     *
     * @param itemBean
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void addMoneyPart(HomeItemBean itemBean) {
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 25, 0);
        params.gravity = Gravity.CENTER;
        TextView textView = new TextView(mContext);
        textView.setTextSize(12);
        textView.setTextColor(mContext.getResources().getColor(R.color.color_fc3135));
        String format = new DecimalFormat("#0.00").format(itemBean.money);
        SpannableString spannableString = new SpannableString("赚 ¥" + format);
        if (format.contains(".")) {
            int i = format.indexOf(".");
            spannableString.setSpan(new RelativeSizeSpan(1.5f), 3, i + 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else
            spannableString.setSpan(new RelativeSizeSpan(1.5f), 3, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        mGoodsViewHolder.mFluidLayoutMoney.addView(textView, params);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.convenientBanner)
        ConvenientBanner   mConvenientBanner;
        @BindView(R.id.auto_layout)
        AutoLinefeedLayout mAutoLayout;
        @BindView(R.id.iv_system_notice)
        ImageView          mIvSystemNotice;
        @BindView(R.id.marqueeView_home)
        MarqueeView        mMarqueeViewHome;
        @BindView(R.id.tv_rank)
        TextView           mTvRank;
        @BindView(R.id.ll_home_announce)
        LinearLayout       mLlHomeAnnounce;
        @BindView(R.id.re_synthesize)
        RelativeLayout     mReSynthesize;
        @BindView(R.id.tv_hot)
        TextView           mTvHot;
        @BindView(R.id.iv_hot_arrow_up)
        ImageView          mIvHotArrowUp;
        @BindView(R.id.iv_hot_arrow_down)
        ImageView          mIvHotArrowDown;
        @BindView(R.id.re_hot)
        RelativeLayout     mReHot;
        @BindView(R.id.tv_award)
        TextView           mTvAward;
        @BindView(R.id.iv_reward_arrow_up)
        ImageView          mIvRewardArrowUp;
        @BindView(R.id.iv_reward_arrow_down)
        ImageView          mIvRewardArrowDown;
        @BindView(R.id.re_award)
        RelativeLayout     mReAward;
        @BindView(R.id.tv_select)
        TextView           mTvSelect;
        @BindView(R.id.re_select)
        RelativeLayout     mReSelect;
        @BindView(R.id.ll_heard)
        LinearLayout       mLlHeard;


        public HeaderViewHolder(View header) {
            super(header);
            ButterKnife.bind(this, header);
        }
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView    mIvIcon;
        @BindView(R.id.tv_descibe)
        TextView     mTvDescibe;
        @BindView(R.id.fluid_layout_keyWord)
        FluidLayout  mFluidLayoutKeyWord;
        @BindView(R.id.fluid_layout_money)
        FluidLayout  mFluidLayoutMoney;
        @BindView(R.id.ll_recommend)
        LinearLayout mLlRecommend;

        public GoodsViewHolder(View goods) {
            super(goods);
            ButterKnife.bind(this, goods);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.re_item)
        RelativeLayout mReItem;

        public EmptyViewHolder(View empty) {
            super(empty);
            ButterKnife.bind(this, empty);
        }
    }

    public class RewardEmptyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.re_item)
        RelativeLayout mReItem;

        public RewardEmptyViewHolder(View empty) {
            super(empty);
            ButterKnife.bind(this, empty);
        }
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.right = space;
        }
    }

    public class SpaceWantedItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceWantedItemDecoration(int space) {
            this.space = ViewUtils.dip2px(mContext, space);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);
            outRect.left = 10;
            outRect.top = 20;
            outRect.bottom = 0;
            if ((pos + 1) % 3 != 0) {
                outRect.right = space;
            } else {
                outRect.right = 0;
            }
        }
    }

}
