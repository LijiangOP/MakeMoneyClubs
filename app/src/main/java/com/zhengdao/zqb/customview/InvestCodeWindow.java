package com.zhengdao.zqb.customview;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/11 16:40
 */
public class InvestCodeWindow extends ShapeDialog {

    @BindView(R.id.et_invest_code)
    EditText  mEtInvestCode;
    @BindView(R.id.tv_take)
    TextView  mTvTake;
    @BindView(R.id.iv_close)
    ImageView mIvClose;

    private Context  mContext;
    private CallBack mCallback;

    public interface CallBack {
        void onTake(String investCode);
    }

    public InvestCodeWindow(Context context, CallBack callback) {
        this(context, 0);
        this.mCallback = callback;
    }

    private InvestCodeWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_invest_code_window, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.reward_dialog_anim);
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtInvestCode.setText("");
                dismiss();
            }
        });
        mTvTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trim = mEtInvestCode.getText().toString().trim();
                if (null != mCallback)
                    mCallback.onTake(trim);
                mEtInvestCode.setText("");
                dismiss();
            }
        });
    }

    public void setPosition(int x, int y) {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int[] screenSize = DensityUtil.getScreenSize(mContext);
        lp.width = (int) (screenSize[0] * 0.65);
        lp.x = x; // 新位置X坐标
        lp.y = y; // 新位置Y坐标
        dialogWindow.setSoftInputMode(lp.SOFT_INPUT_ADJUST_NOTHING);
        dialogWindow.setAttributes(lp);
    }
}
