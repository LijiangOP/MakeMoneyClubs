package com.zhengdao.zqb.view.activity.dailysign;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.NewHandUpDataEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.ActivityUtils;
import com.zhengdao.zqb.utils.AdvertisementUtils;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.RxBus;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.application.ClientAppLike.AppType;


public class DailySignActivity extends MVPBaseActivity<DailySignContract.View, DailySignPresenter> implements DailySignContract.View {

    private static final int REQUESTCODE = 001;
    @BindView(R.id.imageView)
    ImageView      mImageView;
    @BindView(R.id.tv_sign_state)
    TextView       mTvSignState;
    @BindView(R.id.tv_price)
    TextView       mTvPrice;
    @BindView(R.id.tv_desc)
    TextView       mTvDesc;
    @BindView(R.id.ll_advertisement)
    RelativeLayout mLlAdvertisement;
    @BindView(R.id.iv_adv)
    ImageView      mIvAdv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_sign);
        ButterKnife.bind(this);
        setTitle("签到");
        mPresenter.doSign();
        initAdv();
    }

    private void initAdv() {
        try {
            if (AppType == Constant.App.Zqb) {
                AdvertisementUtils.BaiDuAdv.addAdvWithDefClose(this, Constant.BaiDuAdv.UserCenterBottom, mLlAdvertisement);
            } else {
                AdvertisementUtils.TencentAdv.getTencentBannerAdv(DailySignActivity.this, Constant.TencentAdv.advTenCent_ADV_BANNER_ID, mLlAdvertisement, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPresenter.getAdvReplace(Constant.AdvPosition.PositionsSign);
    }

    @Override
    public void onSignResult(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            mImageView.setVisibility(View.VISIBLE);
            mTvSignState.setText("签到成功");
            mTvPrice.setText("+" + httpResult.money);
            mTvDesc.setText("每日签到送现金奖励");
            RxBus.getDefault().post(new NewHandUpDataEvent());
        } else if (httpResult.code == 10) {
            mImageView.setVisibility(View.VISIBLE);
            mTvSignState.setText("签到成功");
            mTvPrice.setText("已签到");
            mTvPrice.setTextSize(18);
            mTvDesc.setText("每日签到送现金奖励");
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivityForResult(new Intent(this, LoginActivity.class), REQUESTCODE);
        } else {
            ToastUtil.showToast(this, TextUtils.isEmpty(httpResult.msg) ? "签到失败" : httpResult.msg);
        }
    }

    @Override
    public void onGetAdvReplace(final AdvertisementHttpEntity httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                if (httpResult.advert != null) {
                    String imgPath = httpResult.advert.imgPath;
                    if (!TextUtils.isEmpty(imgPath))
                        Glide.with(this).load(imgPath).asBitmap().into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                if (resource != null) {
                                    int[] screenSize = DensityUtil.getScreenSize(DailySignActivity.this);
                                    if (screenSize[0] != 0) {
                                        mIvAdv.setImageBitmap(resource);
                                        ViewGroup.LayoutParams layoutParams = mIvAdv.getLayoutParams();
                                        layoutParams.width = screenSize[0];
                                        layoutParams.height = screenSize[0] / 5;
                                        mIvAdv.setLayoutParams(layoutParams);
                                        mIvAdv.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
                    mIvAdv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityUtils.doSkip(DailySignActivity.this, httpResult.advert.type, httpResult.advert.url, httpResult.advert.id);
                        }
                    });
                }
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(this, "登录超时,请重新登录");
                startActivity(new Intent(this, LoginActivity.class));
            } else if (httpResult.code == Constant.HttpResult.FAILD) {
                LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
