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
 * @CreateTime 2018/5/11 09:14
 */
public class DailyWindow extends ShapeDialog {

    @BindView(R.id.imageView)
    ImageView mImageView;
    @BindView(R.id.iv_invite)
    ImageView mIvInvite;
    private Context mContext;

    public DailyWindow(Context context) {
        this(context, 0);
    }

    public DailyWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_daily_window, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.reward_dialog_anim);
    }

    public void initContentView(View.OnClickListener onClickListener) {
        if (onClickListener != null)
            mIvInvite.setOnClickListener(onClickListener);
    }

    public void setPosition(int x, int y) {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = x; // 新位置X坐标
        lp.y = y; // 新位置Y坐标
        dialogWindow.setAttributes(lp);
    }
}
