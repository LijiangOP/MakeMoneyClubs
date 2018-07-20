package com.zhengdao.zqb.customview;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/4 15:02
 */
public class UpdataWindow extends ShapeDialog {


    @BindView(R.id.iv_close)
    ImageView    mIvClose;
    @BindView(R.id.iv_top)
    ImageView    mIvTop;
    @BindView(R.id.tv_version)
    TextView     mTvVersion;
    @BindView(R.id.tv_content)
    TextView     mTvContent;
    @BindView(R.id.iv_explain)
    ImageView    mIvExplain;
    @BindView(R.id.tv_cancle)
    TextView     mTvCancle;
    @BindView(R.id.tv_update)
    TextView     mTvUpdate;
    @BindView(R.id.ll_must_update)
    LinearLayout mLlMustUpdate;
    @BindView(R.id.frame_layout)
    FrameLayout  mFrameLayout;

    private Context mContext;

    public UpdataWindow(Context context) {
        this(context, 0);
    }

    public UpdataWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_updata_window, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.packet_dialog_anim);
    }

    public void initContentView(String clientVersion, String cotent, boolean isMustUpdata, View.OnClickListener confirmlistener, View.OnClickListener canclelistener) {
        if (isMustUpdata) {
            mLlMustUpdate.setVisibility(View.VISIBLE);
            mIvClose.setVisibility(View.GONE);
            mIvExplain.setVisibility(View.GONE);
        } else {
            mLlMustUpdate.setVisibility(View.GONE);
            mIvClose.setVisibility(View.VISIBLE);
            mIvExplain.setVisibility(View.VISIBLE);
        }
        mTvVersion.setText("发现新版本！（v " + clientVersion + "）");
        mTvContent.setText(cotent);
        if (confirmlistener != null) {
            mIvExplain.setOnClickListener(confirmlistener);
            mTvUpdate.setOnClickListener(confirmlistener);
        }
        if (canclelistener != null) {
            mIvClose.setOnClickListener(canclelistener);
            mTvCancle.setOnClickListener(canclelistener);
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
