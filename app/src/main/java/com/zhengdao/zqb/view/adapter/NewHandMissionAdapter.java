package com.zhengdao.zqb.view.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.TaskEntity;
import com.zhengdao.zqb.utils.AdvertisementUtils;
import com.zhengdao.zqb.utils.SkipUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.advertisementlist.AdvertisementListActivity;
import com.zhengdao.zqb.view.activity.bindalipay.BindAliPayActivity;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;
import com.zhengdao.zqb.view.activity.marketcommentdetail.MarketCommentDetailActivity;
import com.zhengdao.zqb.view.activity.register.RegisterActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.application.ClientAppLike.AppType;
import static com.zhengdao.zqb.utils.AdvertisementUtils.TencentAdv.getAdInfo;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/30 15:42
 */
public class NewHandMissionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_REGIST        = 0;
    private static final int TYPE_BINDALIPAY    = 1;
    private static final int TYPE_MARKETCOMMENT = 2;
    private static final int TYPE_RECOMMEND     = 3;
    private static final int TYPE_SHARE         = 4;
    private static final int TYPE_SIGN          = 5;

    private static final int TYPE_WANTED_REWARD    = 6;
    private static final int TYPE_GAME_REWARD      = 7;
    private static final int TYPE_SHOUTU_REWARD    = 8;
    private static final int TYPE_WENJUAN_REWARD   = 9;
    private static final int TYPE_GUANGGAO         = 10;
    private static final int TYPE_XINSHOU_FULI     = 11;
    private static final int TYPE_ALIPAY_REDPACKET = 12;

    private CallBack         mCallBack;
    private Context          mContext;
    private List<TaskEntity> mData;
    private HeaderViewHolder mHeaderViewHolder;
    private ItemViewHolder   mItemViewHolder;
    private BaiduAdvHolder   mBaiduViewHolder;

    public interface CallBack {
        void onItemClick(int type, int state);

        void onBaiduAdvClick();

        void onTencentAdvClick();
    }

    public NewHandMissionAdapter(Context context, List<TaskEntity> data, CallBack callBack) {
        this.mCallBack = callBack;
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if (viewType == 0) {//标题栏模块
            View title = View.inflate(mContext, R.layout.new_hand_mission_1, null);
            holder = new HeaderViewHolder(title);
        } else if (viewType == 10) {//广告模块
            return new BaiduAdvHolder(View.inflate(mContext, R.layout.item_news_adv, null));
        } else {//内容模块
            View item = View.inflate(mContext, R.layout.item_newhand_mission, null);
            holder = new ItemViewHolder(item);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final TaskEntity taskEntity = mData.get(position);
        if (taskEntity.type == 0) {
            mHeaderViewHolder = (HeaderViewHolder) holder;
            mHeaderViewHolder.mTitle.setText(TextUtils.isEmpty(taskEntity.title) ? "" : taskEntity.title);
        } else if (taskEntity.type == 10) {
            mBaiduViewHolder = (BaiduAdvHolder) holder;
            mBaiduViewHolder.mFlContainer.removeAllViews();
            initAdv();
        } else {
            mItemViewHolder = (ItemViewHolder) holder;
            mItemViewHolder.mTvTask.setText(TextUtils.isEmpty(taskEntity.entity.title) ? "" : taskEntity.entity.title);
            String describe = TextUtils.isEmpty(taskEntity.entity.describe) ? "" : taskEntity.entity.describe;
            Double integral = taskEntity.entity.integral == null ? 0.0 : taskEntity.entity.integral;
            SpannableString spannableString;
            if (integral == 0) {
                spannableString = new SpannableString(describe + "赏金随机");
            } else {
                spannableString = new SpannableString(describe + " 赏金+" + new DecimalFormat("#0.00").format(integral));
            }
            if (taskEntity.entity.type == TYPE_ALIPAY_REDPACKET) {
                spannableString = new SpannableString(describe + " 最高99元");
            }
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.main)), describe.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mItemViewHolder.mTvDesc.setText(spannableString);
            if (taskEntity.entity.status == 1) {//已完成
                mItemViewHolder.mTvGoFinish.setText("已完成");
                mItemViewHolder.mTvGoFinish.setTextColor(mContext.getResources().getColor(R.color.color_b3b3b3));
                mItemViewHolder.mTvGoFinish.setVisibility(View.VISIBLE);
                mItemViewHolder.mTvGetReward.setVisibility(View.GONE);
            } else if (taskEntity.entity.status == 2) {//未完成
                mItemViewHolder.mTvGoFinish.setText("去完成");
                mItemViewHolder.mTvGoFinish.setTextColor(mContext.getResources().getColor(R.color.color_02a0e9));
                mItemViewHolder.mTvGoFinish.setVisibility(View.VISIBLE);
                mItemViewHolder.mTvGetReward.setVisibility(View.GONE);
            } else if (taskEntity.entity.status == 3) {//已完结
                mItemViewHolder.mTvGoFinish.setText("已完结");
                mItemViewHolder.mTvGoFinish.setTextColor(mContext.getResources().getColor(R.color.color_b3b3b3));
                mItemViewHolder.mTvGoFinish.setVisibility(View.VISIBLE);
                mItemViewHolder.mTvGetReward.setVisibility(View.GONE);
            } else if (taskEntity.entity.status == 4) {//可领取
                mItemViewHolder.mTvGoFinish.setVisibility(View.GONE);
                mItemViewHolder.mTvGetReward.setVisibility(View.VISIBLE);
            }
            switch (taskEntity.entity.type) {
                case TYPE_REGIST://注册任务
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_tuijian));
                    break;
                case TYPE_BINDALIPAY://支付宝绑定
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_tuijian));
                    break;
                case TYPE_MARKETCOMMENT://市场评论
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_pinglun));
                    break;
                case TYPE_RECOMMEND://推荐悬赏
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_tuijian));
                    break;
                case TYPE_SHARE://分享朋友圈
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_fengxiang));
                    break;
                case TYPE_SIGN://签到
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_tuijian));
                    break;
                case TYPE_WANTED_REWARD://悬赏奖励
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_jiangli));
                    break;
                case TYPE_GAME_REWARD://游戏奖励
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_youxi));
                    break;
                case TYPE_SHOUTU_REWARD://收徒奖励
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_shuotu));
                    break;
                case TYPE_WENJUAN_REWARD://问卷调查奖励
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_wenjuan));
                    break;
                case TYPE_GUANGGAO://广告列表
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_guanggao));
                    break;
                case TYPE_XINSHOU_FULI://新手福利
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_fuli));
                    break;
                case TYPE_ALIPAY_REDPACKET://支付宝红包奖励红包码
                    mItemViewHolder.mIvIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.daily_redpacket));
                    break;
            }
            mItemViewHolder.mLlItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (taskEntity.entity.status == 2) {//未完成
                        switch (taskEntity.entity.type) {
                            case TYPE_REGIST://注册任务
                                Intent regist = new Intent(mContext, RegisterActivity.class);
                                regist.putExtra(Constant.Activity.Data, "fromNewHand");
                                Utils.StartActivity(mContext, regist);
                                break;
                            case TYPE_BINDALIPAY://支付宝绑定
                                Intent bindalipay = new Intent(mContext, BindAliPayActivity.class);
                                bindalipay.putExtra(Constant.Activity.Type, "fromNewHand");
                                Utils.StartActivity(mContext, bindalipay);
                                break;
                            case TYPE_MARKETCOMMENT://市场评论
                                Utils.StartActivity(mContext, new Intent(mContext, MarketCommentDetailActivity.class));
                                break;
                            case TYPE_RECOMMEND://推荐悬赏
                                if (taskEntity.entity.rwId == 0) {
                                    ToastUtil.showToast(mContext, "暂无推荐悬赏");
                                    return;
                                }
                                Intent intent = new Intent(mContext, HomeGoodsDetailActivity.class);
                                intent.putExtra(Constant.Activity.Data, taskEntity.entity.rwId);
                                Utils.StartActivity(mContext, intent);
                                break;
                            case TYPE_SHARE://分享朋友圈
                                if (mCallBack != null)
                                    mCallBack.onItemClick(TYPE_SHARE, taskEntity.entity.status);
                                break;
                            case TYPE_SIGN://签到
                                if (mCallBack != null)
                                    mCallBack.onItemClick(TYPE_SIGN, taskEntity.entity.status);
                                break;
                            case TYPE_WANTED_REWARD://悬赏奖励
                                if (mCallBack != null)
                                    mCallBack.onItemClick(TYPE_WANTED_REWARD, taskEntity.entity.status);
                                break;
                            case TYPE_GAME_REWARD://游戏奖励
                                if (mCallBack != null)
                                    mCallBack.onItemClick(TYPE_GAME_REWARD, taskEntity.entity.status);
                                break;
                            case TYPE_SHOUTU_REWARD://收徒奖励
                                SkipUtils.SkipToShouTu(mContext);
                                break;
                            case TYPE_WENJUAN_REWARD://问卷调查奖励
                                if (mCallBack != null)
                                    mCallBack.onItemClick(TYPE_WENJUAN_REWARD, taskEntity.entity.status);
                                break;
                            case TYPE_GUANGGAO://广告列表
                                mContext.startActivity(new Intent(mContext, AdvertisementListActivity.class));
                                break;
                            case TYPE_XINSHOU_FULI://新手福利
                                if (mCallBack != null)
                                    mCallBack.onItemClick(TYPE_XINSHOU_FULI, taskEntity.entity.status);
                                break;
                            case TYPE_ALIPAY_REDPACKET:
                                skipToAliPay(taskEntity.entity.url);
                                break;
                        }
                    }
                    if (taskEntity.entity.status == 1) {//已完成
                        switch (taskEntity.entity.type) {
                            case TYPE_SHARE:
                                if (mCallBack != null)
                                    mCallBack.onItemClick(TYPE_SHARE, taskEntity.entity.status);
                                break;
                            case TYPE_SIGN:
                                if (mCallBack != null)
                                    mCallBack.onItemClick(TYPE_SIGN, taskEntity.entity.status);
                                break;
                        }
                    }
                    if (taskEntity.entity.status == 4) {
                        if (mCallBack != null)
                            mCallBack.onItemClick(taskEntity.entity.type, taskEntity.entity.status);
                    }
                }
            });
        }
    }

    private void skipToAliPay(String url) {
        Uri uri;
        if (checkAliPayIsInstall(mContext)) {
            uri = Uri.parse(url);
        } else {
            uri = Uri.parse("https://mobile.alipay.com/index.htm");
        }
        Intent four = new Intent(Intent.ACTION_VIEW, uri);
        mContext.startActivity(four);
    }

    private void initAdv() {
        try {
            if (AppType == Constant.App.Wlgfl) {
                AdvertisementUtils.TencentAdv.getTencentNativeAdv1(mContext, Constant.TencentAdv.advTenCent_ADV_BANNER_ID, new NativeExpressAD.NativeExpressADListener() {

                    @Override
                    public void onNoAD(AdError adError) {
                        Log.i("NativeExpressAD", String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(), adError.getErrorMsg()));
                    }

                    @Override
                    public void onADLoaded(List<NativeExpressADView> list) {
                        Log.i("NativeExpressAD", "onADLoaded Success");
                        if (list != null && list.size() > 0) {
                            NativeExpressADView nativeExpressADView = list.get(0);
                            if (nativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
                                Log.i("NativeExpressAD", "type=AdPatternType.NATIVE_VIDEO");
                            }
                            Log.i("NativeExpressAD", "nativeExpressADView=" + getAdInfo(nativeExpressADView));
                            if (mBaiduViewHolder.mFlContainer != null)
                                mBaiduViewHolder.mFlContainer.addView(nativeExpressADView);
                            nativeExpressADView.render();
                        }
                    }

                    @Override
                    public void onRenderFail(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onRenderFail: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onRenderSuccess: " + nativeExpressADView.toString() + ", adInfo: " + getAdInfo(nativeExpressADView));
                    }

                    @Override
                    public void onADExposure(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADExposure: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onADClicked(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADClicked: " + nativeExpressADView.toString());
                        if (mCallBack != null)
                            mCallBack.onTencentAdvClick();
                    }

                    @Override
                    public void onADClosed(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADClosed: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADLeftApplication: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADOpenOverlay: " + nativeExpressADView.toString());
                    }

                    @Override
                    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                        Log.i("NativeExpressAD", "onADCloseOverlay: " + nativeExpressADView.toString());
                    }
                });
            } else {
                AdView baiDuBanner = AdvertisementUtils.BaiDuAdv.getBaiDuBanner(mContext, Constant.BaiDuAdv.UserCenterBottom, new AdViewListener() {
                    @Override
                    public void onAdReady(AdView adView) {
                        Log.w("BAIDUA", "onAdReady");
                    }

                    @Override
                    public void onAdShow(JSONObject jsonObject) {
                        Log.w("BAIDUA", "onAdShow");
                    }

                    @Override
                    public void onAdClick(JSONObject jsonObject) {
                        Log.w("BAIDUA", "onAdClick" + jsonObject.toString());
                        if (mCallBack != null)
                            mCallBack.onBaiduAdvClick();
                    }

                    @Override
                    public void onAdFailed(String s) {
                        Log.w("BAIDUA", "onAdFailed");
                    }

                    @Override
                    public void onAdSwitch() {
                        Log.w("BAIDUA", "onAdSwitch");
                    }

                    @Override
                    public void onAdClose(JSONObject jsonObject) {
                        Log.w("BAIDUA", "onAdClose" + jsonObject.toString());
                    }
                });
                if (baiDuBanner == null)
                    return;
                DisplayMetrics dm = new DisplayMetrics();
                ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
                int winW = dm.widthPixels;
                int winH = dm.heightPixels;
                int width = Math.min(winW, winH);
                int height = width / 4;
                RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(width, height);
                rllp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                mBaiduViewHolder.mFlContainer.addView(baiDuBanner, rllp);
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

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData == null ? 0 : mData.get(position).type;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView mTitle;

        public HeaderViewHolder(View title) {
            super(title);
            ButterKnife.bind(this, title);
        }
    }

    class BaiduAdvHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fl_container)
        FrameLayout mFlContainer;

        public BaiduAdvHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView    mIvIcon;
        @BindView(R.id.tv_task)
        TextView     mTvTask;
        @BindView(R.id.tv_desc)
        TextView     mTvDesc;
        @BindView(R.id.tv_go_finish)
        TextView     mTvGoFinish;
        @BindView(R.id.tv_get_reward)
        TextView     mTvGetReward;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;

        public ItemViewHolder(View item) {
            super(item);
            ButterKnife.bind(this, item);
        }
    }
}
