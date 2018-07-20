package com.zhengdao.zqb.customview;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zhengdao.zqb.R;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/18 11:05
 */
public class WantedShareDialog extends Dialog {

    private Context              mContext;
    private TextView             mTvTitle;
    private TextView             mTvContent;
    private TextView             mTvConfirm;
    private String               mTitle;
    private String               mContent;
    private String               mConfirm;
    private View.OnClickListener mListener;

    public WantedShareDialog(Context context) {
        super(context, R.style.RuleHintDialog);//两个参数的构造方法，第二参数可以改变dialog的显示
        this.mContext = context;
    }

    private void initView() {
        LayoutInflater from = LayoutInflater.from(mContext);
        View inflate = from.inflate(R.layout.layout_wanted_share_dialog, null);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉默认的title框
        setContentView(inflate);
        mTvTitle = findViewById(R.id.tv_title);
        mTvContent = findViewById(R.id.tv_content);
        mTvConfirm = findViewById(R.id.tv_confirm);
        if (!TextUtils.isEmpty(mTitle))
            mTvTitle.setText(mTitle);
        if (!TextUtils.isEmpty(mContent))
            mTvContent.setText(mContent);
        if (!TextUtils.isEmpty(mConfirm))
            mTvConfirm.setText(mConfirm);
        if (mListener != null)
            mTvConfirm.setOnClickListener(mListener);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.85);
        dialogWindow.setAttributes(lp);
    }

    public void initView(String title, String content, String confirm, View.OnClickListener listener) {
        this.mConfirm = confirm;
        initView(title, content, listener);
    }

    public void initView(String title, String content, View.OnClickListener listener) {
        this.mTitle = title;
        initView(content, listener);
    }

    public void initView(String content, View.OnClickListener listener) {
        mContent = content;
        initView(listener);
    }

    public void initView(View.OnClickListener listener) {
        mListener = listener;
        initView();
    }

    public void setMessage(String msg) {
        mTvContent.setText(msg);
    }

    public void setMessage(SpannableString msg) {
        mTvContent.setText(msg);
    }

    public void setOnLongClickListener(View.OnLongClickListener listener){
        if (listener != null)
            mTvContent.setOnLongClickListener(listener);
    }
}
