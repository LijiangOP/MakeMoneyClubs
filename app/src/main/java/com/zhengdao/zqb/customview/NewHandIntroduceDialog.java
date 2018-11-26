package com.zhengdao.zqb.customview;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.zhengdao.zqb.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author lijiangop
 * @CreateTime 2018/11/20 0020 16:47
 */
public class NewHandIntroduceDialog extends Dialog {

    @BindView(R.id.image_earn)
    ImageView mImageEarn;
    @BindView(R.id.image_next)
    ImageView mImageNext;
    @BindView(R.id.image_play)
    ImageView mImagePlay;

    private Context mContext;

    public NewHandIntroduceDialog(Context context) {
        this(context, R.style.shape_dialog);
    }

    public NewHandIntroduceDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = context;
        View rootView = View.inflate(context, R.layout.newhand_introduce, null);
        setContentView(rootView);
        ButterKnife.bind(this, rootView);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = width;
        lp.height = height;
        dialogWindow.setAttributes(lp);
        //全屏覆盖状态栏
        dialogWindow.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
        dialogWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //设置背景黑色
        dialogWindow.setBackgroundDrawableResource(android.R.color.black);
    }

    public void setVisibility(int position, int visibility) {
        switch (position) {
            case 0:
                mImagePlay.setVisibility(visibility);
                break;
            case 1:
                mImageNext.setVisibility(visibility);
                break;
            case 2:
                mImageEarn.setVisibility(visibility);
                break;
        }
    }

    @OnClick({R.id.image_earn, R.id.image_next, R.id.image_play})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_earn:
                dismiss();
                break;
            case R.id.image_next:
                mImageNext.setVisibility(View.GONE);
                mImageEarn.setVisibility(View.VISIBLE);
                break;
            case R.id.image_play:
                dismiss();
                break;
        }
    }
}
