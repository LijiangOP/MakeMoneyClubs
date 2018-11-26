package com.zhengdao.zqb.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.utils.Utils;
import com.zhengdao.zqb.view.activity.homegoodsdetail.HomeGoodsDetailActivity;

import java.text.DecimalFormat;

/**
 * @Author lijiangop
 * @CreateTime 2018/9/7 0007 10:21
 */
public class ExitDialog extends Dialog {

    private Context  mContext;
    private TextView mTvTitle;
    private TextView mTvHint;
    private TextView mTvCancle;
    private TextView mTvConfirm;

    private ImageView    mIvGoodsPic;
    private TextView     mTvContent;
    private TextView     mTvTop;
    private TextView     mTvPrice;
    private LinearLayout mLLGoods;

    public ExitDialog(Context context) {
        super(context, R.style.RuleHintDialog);
        this.mContext = context;
        intiView();
    }

    private void intiView() {
        LayoutInflater from = LayoutInflater.from(mContext);
        View inflate = from.inflate(R.layout.layout_exit_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉默认的title框
        setContentView(inflate);
        mTvTitle = findViewById(R.id.tv_title);
        mTvHint = findViewById(R.id.tv_hint);
        mTvCancle = findViewById(R.id.tv_cancle);
        mTvConfirm = findViewById(R.id.tv_confirm);

        mIvGoodsPic = findViewById(R.id.iv_goods_pic);
        mTvTop = findViewById(R.id.tv_top);
        mTvContent = findViewById(R.id.tv_content);
        mTvPrice = findViewById(R.id.tv_price);

        mLLGoods = findViewById(R.id.ll_goods);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.85);
        dialogWindow.setAttributes(lp);
    }


    public void init(String title, SpannableString content, View.OnClickListener continuelistener, View.OnClickListener exitlistener) {
        init(title, content, "", "", continuelistener, exitlistener);
    }

    public void init(String title, SpannableString content, String leftText, View.OnClickListener continuelistener, View.OnClickListener exitlistener) {
        init(title, content, leftText, "", continuelistener, exitlistener);
    }

    public void init(String title, SpannableString content, String leftText, String rightText, View.OnClickListener continuelistener, View.OnClickListener exitlistener) {
        mTvTitle.setText(TextUtils.isEmpty(title) ? "" : title);
        mTvHint.setText(TextUtils.isEmpty(content) ? "" : content);
        if (!TextUtils.isEmpty(leftText))
            mTvCancle.setText(leftText);
        mTvCancle.setOnClickListener(continuelistener);
        if (!TextUtils.isEmpty(rightText))
            mTvConfirm.setText(rightText);
        mTvConfirm.setOnClickListener(exitlistener);
    }


    public void setGoods(final GoodsCommandHttpEntity httpEntity) {
        if (httpEntity == null) {
            mLLGoods.setVisibility(View.GONE);
        } else if (httpEntity != null && httpEntity.reward != null) {
            try {
                mLLGoods.setVisibility(View.VISIBLE);
                mTvTop.setText("邀请您领取悬赏");
                Glide.with(mContext).load(httpEntity.reward.picture).error(R.drawable.net_less_140).into(mIvGoodsPic);
                mTvContent.setText(TextUtils.isEmpty(httpEntity.reward.title) ? "" : httpEntity.reward.title);
                if (httpEntity.reward != null && httpEntity.reward.money != 0) {
                    Double money = httpEntity.reward.money;
                    String value = money == null ? "0" : new DecimalFormat("#0.00").format(money);
                    SpannableString spannableString = new SpannableString("赚 ¥" + value);
                    spannableString.setSpan(new RelativeSizeSpan(0.8f), 1, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    mTvPrice.setText(spannableString);
                    mTvPrice.setVisibility(View.VISIBLE);
                } else {
                    mTvPrice.setVisibility(View.GONE);
                }
                mLLGoods.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                        Intent intent = new Intent(mContext, HomeGoodsDetailActivity.class);
                        intent.putExtra(Constant.Activity.Data, httpEntity.reward.id);
                        Utils.StartActivity(mContext, intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
