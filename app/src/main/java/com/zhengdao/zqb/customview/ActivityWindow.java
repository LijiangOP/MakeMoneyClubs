package com.zhengdao.zqb.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhengdao.zqb.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * @Author lijiangop
 * @CreateTime 2017/8/9 14:38
 */
public class ActivityWindow extends ShapeDialog implements View.OnClickListener {

    @BindView(R.id.iv_content)
    ImageView mIvContent;
    @BindView(R.id.iv_close)
    ImageView mIvClose;
    @BindView(R.id.iv_get_award)
    ImageView mIvGetAward;
    private Context          mContext;
    private ImgReadyCallBack mCallBack;

    public interface ImgReadyCallBack {
        void onImgReady();
    }

    public ActivityWindow(Context context) {
        this(context, 0);
    }

    public ActivityWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_activity, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.packet_dialog_anim);
        mIvClose.setOnClickListener(this);
    }

    public void initContentView(String imagePath, ImgReadyCallBack callBack, View.OnClickListener listener) {
        mCallBack = callBack;
        if (!TextUtils.isEmpty(imagePath)) {
            Glide.with(mContext).load(imagePath).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if (resource != null) {
                        mIvContent.setImageBitmap(resource);
                        mCallBack.onImgReady();
                    }
                }
            });
        }
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
