package com.zhengdao.zqb.customview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhengdao.zqb.R;


/**
 * @创建者 cairui
 * @创建时间 2017/2/24 17:07
 */
public class CustomProgressDialog extends ShapeDialog {

    private ImageView         mIv;
    private TextView          mMsg;
    private AnimationDrawable mAnimation;
    private Context           mContext;

    public CustomProgressDialog(Context context) {
        super(context);
        this.mContext = context;
        initView();
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    private void initView() {
        setContentView(R.layout.layout_loading_dialog);
        mIv = findViewById(R.id.iv_loading_dialog);
        mMsg = findViewById(R.id.tv_loading_dialog);
        mIv.setBackgroundResource(R.drawable.loading_anim);
        mAnimation = (AnimationDrawable) mIv.getBackground();
    }

    public void setMessage(String msg) {
        mMsg.setText(msg);
    }

    public void setTextColor(int colorRes) {
        mMsg.setTextColor(mContext.getResources().getColor(colorRes));//不能使用app的context
    }


    @Override
    public void show() {
        super.show();
        mAnimation.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mAnimation.isRunning()) {
            mAnimation.stop();
        }
    }

}
