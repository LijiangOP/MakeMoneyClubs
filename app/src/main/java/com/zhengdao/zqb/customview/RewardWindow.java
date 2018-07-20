package com.zhengdao.zqb.customview;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhengdao.zqb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/8 16:19
 */
public class RewardWindow extends ShapeDialog {

    @BindView(R.id.imageView)
    ImageView mImageView;
    private Context mContext;

    public RewardWindow(Context context) {
        this(context, 0);
    }

    public RewardWindow(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.layout_reward, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        init();
    }

    private void init() {
        Window window = getWindow();
        window.setWindowAnimations(R.style.reward_dialog_anim);
    }

    public void initContentView() {
        try {
            Glide.with(mContext).load(R.drawable.reward).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(mImageView);
            MediaPlayer.create(mContext, R.raw.money).start();
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
