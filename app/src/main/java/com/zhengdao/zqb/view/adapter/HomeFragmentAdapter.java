package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.fynn.fluidlayout.FluidLayout;
import com.xianwan.sdklibrary.util.XWUtils;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.MarqueeView;
import com.zhengdao.zqb.entity.HomeItemBean;
import com.zhengdao.zqb.utils.ActivityUtils;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.activity.newhandmission.NewHandMissionActivity;
import com.zhengdao.zqb.view.activity.webview.WebViewActivity;
import com.zhengdao.zqb.view.activity.zeroearn.ZeroEarnActivity;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;

/**
 * @Author lijiangop
 * @CreateTime 2017/12/28 18:05
 */
public class HomeFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_GOODS  = 2;
    private HeaderViewHolder   mHeaderViewHolder;
    private GoodsViewHolder    mGoodsViewHolder;
    private Context            mContext;
    private List<HomeItemBean> mData;
    private CallBack           mCallBack;

    public interface CallBack {
        void onItemCallBack();
    }

    public HomeFragmentAdapter(Context context, List<HomeItemBean> data, CallBack callBack) {
        this.mCallBack = callBack;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_HEADER) {
            View header = View.inflate(mContext, R.layout.recyl_home_header, null);
            holder = new HeaderViewHolder(header);
        } else if (viewType == TYPE_GOODS) {
            View licai = View.inflate(mContext, R.layout.recyl_home_goods, null);
            holder = new GoodsViewHolder(licai);
        }
        return holder;
    }

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
                //linearlayout
                mHeaderViewHolder.mTvGame.setOnClickListener(this);
                mHeaderViewHolder.mTvEarnMoney.setOnClickListener(this);
                mHeaderViewHolder.mTvFiction.setOnClickListener(this);
                mHeaderViewHolder.mTvSurvey.setOnClickListener(this);
                mHeaderViewHolder.mTvMission.setOnClickListener(this);
                if (itemBean.isShowGame)
                    mHeaderViewHolder.mTvGame.setVisibility(View.VISIBLE);
                else
                    mHeaderViewHolder.mTvGame.setVisibility(View.GONE);
                //marqueenview
                if (!mHeaderViewHolder.mMarqueeViewHome.isFlipping())
                    mHeaderViewHolder.mMarqueeViewHome.startWithViews(itemBean.marqueeList);
                mHeaderViewHolder.mTvSystemMore.setOnClickListener(this);
            } else if (itemBean.type == TYPE_GOODS) {//商品数据 type=2;
                mGoodsViewHolder = (GoodsViewHolder) holder;
                Glide.with(mContext).load(itemBean.picture).error(R.drawable.net_less_140).into(mGoodsViewHolder.mIvIcon);
                mGoodsViewHolder.mTvDescibe.setText(TextUtils.isEmpty(itemBean.title) ? "" : itemBean.title);
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
                if (itemBean.isOwn == 1)
                    mGoodsViewHolder.mTvSelfSupport.setVisibility(View.VISIBLE);
                else
                    mGoodsViewHolder.mTvSelfSupport.setVisibility(View.GONE);
                if (itemBean.lowerTime > 0 && itemBean.lowerTime < 86400000) {
                    mGoodsViewHolder.mCountDownView.setVisibility(View.VISIBLE);
                    mGoodsViewHolder.mCountDownView.start(itemBean.lowerTime);
                    mGoodsViewHolder.mTvFinishTag.setVisibility(View.VISIBLE);
                } else {
                    mGoodsViewHolder.mCountDownView.setVisibility(View.GONE);
                    mGoodsViewHolder.mTvFinishTag.setVisibility(View.GONE);
                }
                if (itemBean.isOwn == 1)
                    mGoodsViewHolder.mTvSelfSupport.setVisibility(View.VISIBLE);
                else
                    mGoodsViewHolder.mTvSelfSupport.setVisibility(View.GONE);
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

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.tv_game:
                    //闲玩游戏
                    if (SettingUtils.isLogin(mContext)) {
                        String userID = SettingUtils.getUserID(mContext);
                        if (TextUtils.isEmpty(userID)) {
                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                        } else {
                            XWUtils.getInstance(mContext).init(Constant.XianWan.Appid, Constant.XianWan.AppSecret, userID);
                            XWUtils.getInstance(mContext).setTitleBGColorString("#fc3135");
                            XWUtils.getInstance(mContext).setTitle(mContext.getString(R.string.home_entertainment));
                            XWUtils.getInstance(mContext).jumpToAd();
                        }
                    } else {
                        mContext.startActivity(new Intent(mContext, LoginActivity.class));
                    }
                    break;
                case R.id.tv_earn_money:
                    mContext.startActivity(new Intent(mContext, ZeroEarnActivity.class));
                    break;
                case R.id.tv_fiction:
                    //小说
                    Intent fiction = new Intent(mContext, WebViewActivity.class);
                    fiction.putExtra(Constant.WebView.TITLE, "小说阅读");
                    fiction.putExtra(Constant.WebView.URL, Constant.IP.Fiction);
                    mContext.startActivity(fiction);
                    break;
                case R.id.tv_survey:
                    if (mCallBack != null)
                        mCallBack.onItemCallBack();
                    break;
                case R.id.tv_mission:
                    Utils.StartActivity(mContext, new Intent(mContext, NewHandMissionActivity.class));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        SpannableString spannableString = new SpannableString("赚 ¥" + new DecimalFormat("#0.00").format(itemBean.money));
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 3, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        mGoodsViewHolder.mFluidLayoutMoney.addView(textView, params);
        if (!TextUtils.isEmpty(itemBean.discount)) {
            TextView textView1 = new TextView(mContext);
            textView1.setTextSize(9);
            textView1.setTextColor(mContext.getResources().getColor(R.color.white));
            textView1.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_five_redsolid));
            textView1.setPadding(10, 2, 10, 2);
            textView1.setText(itemBean.discount);
            mGoodsViewHolder.mFluidLayoutMoney.addView(textView1, params);
        }
        if (itemBean.goodsType == 1) {
            TextView textView2 = new TextView(mContext);
            textView2.setTextSize(9);
            textView2.setTextColor(mContext.getResources().getColor(R.color.color_00b9fd));
            textView2.setBackground(mContext.getResources().getDrawable(R.drawable.shape_rect_home_item_news));
            textView2.setPadding(10, 2, 10, 2);
            textView2.setText("新品");
            textView2.setGravity(Gravity.CENTER_VERTICAL);
            mGoodsViewHolder.mFluidLayoutMoney.addView(textView2, params);
        }
    }


    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.convenientBanner)
        ConvenientBanner mConvenientBanner;
        @BindView(R.id.tv_game)
        TextView         mTvGame;
        @BindView(R.id.tv_earn_money)
        TextView         mTvEarnMoney;
        @BindView(R.id.tv_fiction)
        TextView         mTvFiction;
        @BindView(R.id.tv_survey)
        TextView         mTvSurvey;
        @BindView(R.id.tv_mission)
        TextView         mTvMission;
        @BindView(R.id.linear_layout)
        LinearLayout     mLinearLayout;
        @BindView(R.id.iv_system_notice)
        ImageView        mIvSystemNotice;
        @BindView(R.id.marqueeView_home)
        MarqueeView      mMarqueeViewHome;
        @BindView(R.id.tv_system_more)
        TextView         mTvSystemMore;
        @BindView(R.id.ll_home_announce)
        LinearLayout     mLlHomeAnnounce;

        public HeaderViewHolder(View header) {
            super(header);
            ButterKnife.bind(this, header);
        }
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView     mIvIcon;
        @BindView(R.id.tv_descibe)
        TextView      mTvDescibe;
        @BindView(R.id.fluid_layout_keyWord)
        FluidLayout   mFluidLayoutKeyWord;
        @BindView(R.id.tv_self_support)
        TextView      mTvSelfSupport;
        @BindView(R.id.fluid_layout_money)
        FluidLayout   mFluidLayoutMoney;
        @BindView(R.id.countDownView)
        CountdownView mCountDownView;
        @BindView(R.id.tv_finish_tag)
        TextView      mTvFinishTag;
        @BindView(R.id.ll_recommend)
        LinearLayout  mLlRecommend;

        public GoodsViewHolder(View goods) {
            super(goods);
            ButterKnife.bind(this, goods);
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

}
