package com.zhengdao.zqb.customview.SwipeBackActivity;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.ShapeDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/22 15:43
 */
public class HotRecommendWindow extends ShapeDialog {

    @BindView(R.id.iv_title)
    ImageView      mIvTitle;
    @BindView(R.id.tv_award)
    TextView       mTvAward;
    @BindView(R.id.re_window)
    RelativeLayout mReWindow;
    private Context mContext;

    public HotRecommendWindow(Context context) {
        this(context, 0);
    }

    public HotRecommendWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_hot_rec_window, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.reward_dialog_anim);
        mReWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void initContentView(String award) {
        if (!TextUtils.isEmpty(award)) {
            SpannableString spannableString = new SpannableString(award + "元");
            spannableString.setSpan(new RelativeSizeSpan(1.8f), 0, award.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTvAward.setText(spannableString);
        }
    }

    public void setPosition(int x, int y) {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = x; // 新位置X坐标
        lp.y = y; // 新位置Y坐标
        dialogWindow.setAttributes(lp);
    }
}
