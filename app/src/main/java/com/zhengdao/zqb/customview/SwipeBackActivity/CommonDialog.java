package com.zhengdao.zqb.customview.SwipeBackActivity;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.utils.DensityUtil;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/27 09:34
 */
public class CommonDialog extends Dialog {

    private Context              mContext;
    private TextView             mTvTitle;
    private TextView             mTvCancle;
    private TextView             mTvConfirm;
    private View.OnClickListener mConfirmListener;
    private View.OnClickListener mCancleListener;
    private String               confirm;
    private String               cancle;
    private String               title;

    public CommonDialog(@NonNull Context context) {
        super(context, R.style.RuleHintDialog);//两个参数的构造方法，第二参数可以改变dialog的显示
        this.mContext = context;
    }

    private void initView() {
        LayoutInflater from = LayoutInflater.from(mContext);
        View inflate = from.inflate(R.layout.layout_common_dialog, null);
        setContentView(inflate);
        mTvTitle = findViewById(R.id.tv_title);
        mTvCancle = findViewById(R.id.tv_cancle);
        mTvConfirm = findViewById(R.id.tv_confirm);
        if (!TextUtils.isEmpty(title))
            mTvTitle.setText(title);
        if (!TextUtils.isEmpty(cancle))
            mTvCancle.setText(cancle);
        if (!TextUtils.isEmpty(confirm))
            mTvConfirm.setText(confirm);
        if (mCancleListener != null)
            mTvCancle.setOnClickListener(mCancleListener);
        if (mConfirmListener != null)
            mTvConfirm.setOnClickListener(mConfirmListener);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.80);
        dialogWindow.setAttributes(lp);
    }

    public void initView(String title, String cancle, String confirm, View.OnClickListener mCancle, View.OnClickListener mConfirm) {
        this.title = title;
        this.cancle = cancle;
        this.confirm = confirm;
        this.mCancleListener = mCancle;
        this.mConfirmListener = mConfirm;
        this.initView();
    }

    public void setTitleSize(int size) {
        int i = DensityUtil.dip2px(mContext, size);
        mTvTitle.setTextSize(i);
    }
}
