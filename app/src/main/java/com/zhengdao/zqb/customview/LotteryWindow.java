package com.zhengdao.zqb.customview;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zhengdao.zqb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/6 14:57
 */
public class LotteryWindow extends ShapeDialog implements View.OnClickListener {

    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.iv_content)
    ImageView mIvContent;
    @BindView(R.id.iv_get_award)
    ImageView mIvGetAward;

    private Context mContext;

    public LotteryWindow(Context context) {
        this(context, 0);
    }

    public LotteryWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_lottery, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.packet_dialog_anim);
        mIvClose.setOnClickListener(this);
    }

    public void initContentView(int state, View.OnClickListener listener) {
        if (state == 1)
            mIvGetAward.setImageDrawable(mContext.getResources().getDrawable(R.drawable.lottery_bt_1));
        if (listener != null)
            mIvGetAward.setOnClickListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                this.dismiss();
                break;
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
