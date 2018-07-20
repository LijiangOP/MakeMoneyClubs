package com.zhengdao.zqb.customview;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhengdao.zqb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/4 14:43
 */
public class SignedWindow extends ShapeDialog {

    @BindView(R.id.tv_finish)
    TextView mTvFinish;
    private Context mContext;

    public SignedWindow(Context context) {
        this(context, 0);
    }

    public SignedWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_signed_layout, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.packet_dialog_anim);
    }

    public void setPosition(int x, int y) {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = x; // 新位置X坐标
        lp.y = y; // 新位置Y坐标
        dialogWindow.setAttributes(lp);
    }

    public void initContentView(View.OnClickListener listener) {
        if (listener != null)
            mTvFinish.setOnClickListener(listener);
    }
}
