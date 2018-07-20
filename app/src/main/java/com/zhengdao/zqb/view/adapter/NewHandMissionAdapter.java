package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.utils.Utils;
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
    private CallBack         mCallBack;
    private Context          mContext;
    private List<TaskEntity> mData;
    private HeaderViewHolder mHeaderViewHolder;
    private ItemViewHolder   mItemViewHolder;
    private BaiduAdvHolder   mBaiduViewHolder;

    public interface CallBack {
        void onSign(boolean state);

        void onShare();

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
        if (viewType == 0) {
            View title = View.inflate(mContext, R.layout.new_hand_mission_1, null);
            holder = new HeaderViewHolder(title);
        } else if (viewType == 10) {
            return new BaiduAdvHolder(View.inflate(mContext, R.layout.item_news_adv, null));
        } else {
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
            initAdv();
        } else {
            mItemViewHolder = (ItemViewHolder) holder;
            mItemViewHolder.mTvTask.setText(TextUtils.isEmpty(taskEntity.entity.title) ? "" : taskEntity.entity.title);
            String describe = TextUtils.isEmpty(taskEntity.entity.describe) ? "" : taskEntity.entity.describe;
            Double integral = taskEntity.entity.integral == null ? 0.0 : taskEntity.entity.integral;
            SpannableString spannableString = new SpannableString(describe + " 赏金+" + new DecimalFormat("#0.00").format(integral));
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.main)), describe.length(), spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mItemViewHolder.mTvDesc.setText(spannableString);
            if (taskEntity.entity.status == 1) {
                if (taskEntity.entity.type == TYPE_SIGN || taskEntity.entity.type == TYPE_SHARE) {
                    mItemViewHolder.mTvAlreadyDone.setVisibility(View.VISIBLE);
                    mItemViewHolder.mTvAlreadyDone.setTextColor(mContext.getResources().getColor(R.color.color_00b7ee));
                    mItemViewHolder.mTvAlreadyDone.setText("去看看");
                    mItemViewHolder.mTvGoFinish.setVisibility(View.GONE);
                } else {
                    mItemViewHolder.mTvAlreadyDone.setVisibility(View.VISIBLE);
                    mItemViewHolder.mTvGoFinish.setVisibility(View.GONE);
                }
            } else if (taskEntity.entity.status == 2) {
                mItemViewHolder.mTvAlreadyDone.setVisibility(View.GONE);
                mItemViewHolder.mTvGoFinish.setVisibility(View.VISIBLE);
            }
            mItemViewHolder.mLlItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (taskEntity.entity.status == 2) {//未完成
                        switch (taskEntity.entity.type) {
                            case TYPE_REGIST:
                                Intent regist = new Intent(mContext, RegisterActivity.class);
                                regist.putExtra(Constant.Activity.Data, "fromNewHand");
                                Utils.StartActivity(mContext, regist);
                                break;
                            case TYPE_BINDALIPAY:
                                Intent bindalipay = new Intent(mContext, BindAliPayActivity.class);
                                bindalipay.putExtra(Constant.Activity.Type, "fromNewHand");
                                Utils.StartActivity(mContext, bindalipay);
                                break;
                            case TYPE_MARKETCOMMENT:
                                Utils.StartActivity(mContext, new Intent(mContext, MarketCommentDetailActivity.class));
                                break;
                            case TYPE_RECOMMEND:
                                if (taskEntity.entity.rwId == 0) {
                                    ToastUtil.showToast(mContext, "暂无推荐悬赏");
                                    return;
                                }
                                Intent intent = new Intent(mContext, HomeGoodsDetailActivity.class);
                                intent.putExtra(Constant.Activity.Data, taskEntity.entity.rwId);
                                Utils.StartActivity(mContext, intent);
                                break;
                            case TYPE_SHARE:
                                if (mCallBack != null)
                                    mCallBack.onShare();
                                break;
                            case TYPE_SIGN:
                                if (mCallBack != null)
                                    mCallBack.onSign(false);
                                break;
                        }
                    }
                    if (taskEntity.entity.status == 1) {//已完成
                        switch (taskEntity.entity.type) {
                            case TYPE_SHARE:
                                if (mCallBack != null)
                                    mCallBack.onShare();
                                break;
                            case TYPE_SIGN:
                                if (mCallBack != null)
                                    mCallBack.onSign(true);
                                break;
                        }
                    }
                }
            });
        }
    }

    private void initAdv() {
        try {
            if (AppType == Constant.App.Wlgfl) {
                AdvertisementUtils.TencentAdv.getTencentNativeAdv1(mContext,Constant.TencentAdv.advTenCent_ADV_BANNER_ID, new NativeExpressAD.NativeExpressADListener() {

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
                AdView baiDuBanner = AdvertisementUtils.BaiDuAdv.getBaiDuBanner(mContext,Constant.BaiDuAdv.UserCenterBottom, new AdViewListener() {
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

        @BindView(R.id.tv_task)
        TextView     mTvTask;
        @BindView(R.id.tv_desc)
        TextView     mTvDesc;
        @BindView(R.id.tv_go_finish)
        TextView     mTvGoFinish;
        @BindView(R.id.tv_already_done)
        TextView     mTvAlreadyDone;
        @BindView(R.id.ll_item)
        LinearLayout mLlItem;

        public ItemViewHolder(View item) {
            super(item);
            ButterKnife.bind(this, item);
        }
    }
}
