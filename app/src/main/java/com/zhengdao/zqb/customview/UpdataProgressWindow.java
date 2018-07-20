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
 * @CreateTime 2018/6/27 17:53
 */
public class UpdataProgressWindow extends ShapeDialog {
    private final Context mContext;
    @BindView(R.id.tv_desc)
    TextView     mTvDesc;
    @BindView(R.id.wave_progress_bar)
    WaveProgress mWaveProgressBar;

    public UpdataProgressWindow(Context context) {
        this(context, 0);
    }

    public UpdataProgressWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_updata_progress_window, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.packet_dialog_anim);
        mTvDesc.setText(mContext.getString(R.string.app_name) + "APP升级中,请稍等片刻");
        mWaveProgressBar.setMaxValue(100);
    }

    public void setProgress(float progress) {
        mWaveProgressBar.setValue(progress);
    }

    public void setPosition(int x, int y) {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = x; // 新位置X坐标
        lp.y = y; // 新位置Y坐标
        dialogWindow.setAttributes(lp);
    }
}
