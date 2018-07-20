package com.zhengdao.zqb.customview.SwipeBackActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.ShapeDialog;
import com.zhengdao.zqb.entity.GoodsCommandHttpEntity;
import com.zhengdao.zqb.entity.HttpLiCaiDetailEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/5 11:39
 */
public class GoodsCommandDialog extends ShapeDialog implements View.OnClickListener {

    @BindView(R.id.iv_goods_pic)
    ImageView mIvGoodsPic;
    @BindView(R.id.tv_content)
    TextView  mTvContent;
    @BindView(R.id.tv_price)
    TextView  mTvPrice;
    @BindView(R.id.tv_cancle)
    TextView  mTvCancle;
    @BindView(R.id.tv_check)
    TextView  mTvCheck;
    @BindView(R.id.tv_top)
    TextView  mTvTop;

    private Context mContext;

    public GoodsCommandDialog(Context context) {
        this(context, 0);
    }

    public GoodsCommandDialog(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_goods_command_dialog, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.packet_dialog_anim);
        mTvCancle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        switch (v.getId()) {
            case R.id.tv_cancle:
                clipboard.setPrimaryClip(ClipData.newPlainText("Label", ""));
                this.dismiss();
                break;
        }
    }

    public void initContentView(GoodsCommandHttpEntity httpEntity, View.OnClickListener listener) {
        if (httpEntity != null && httpEntity.reward != null) {
            mTvTop.setText("邀请你领取悬赏");
            Glide.with(mContext).load(httpEntity.reward.picture).error(R.drawable.net_less_140).into(mIvGoodsPic);
            mTvContent.setText(TextUtils.isEmpty(httpEntity.reward.title) ? "" : httpEntity.reward.title);
            mTvPrice.setText("¥" + httpEntity.reward.money);
        }
        if (listener != null)
            mTvCheck.setOnClickListener(listener);
    }

    public void initContentView(HttpLiCaiDetailEntity httpEntity, View.OnClickListener listener) {
        if (httpEntity != null && httpEntity.wangzhuan != null) {
            mTvTop.setText("邀请你领取奖励");
            Glide.with(mContext).load(httpEntity.wangzhuan.logo).error(R.drawable.net_less_140).into(mIvGoodsPic);
            mTvContent.setText(TextUtils.isEmpty(httpEntity.wangzhuan.title) ? "" : httpEntity.wangzhuan.title);
            String income = "¥" + httpEntity.wangzhuan.incomeTotal;
            SpannableString spannableString = new SpannableString("总收益 " + income);
            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_999999)), 0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvPrice.setText(spannableString);
        }
        if (listener != null)
            mTvCheck.setOnClickListener(listener);
    }


    public void setPosition(int x, int y) {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = x; // 新位置X坐标
        lp.y = y; // 新位置Y坐标
        dialogWindow.setAttributes(lp);
    }
}
