package com.zhengdao.zqb.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhengdao.zqb.R;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/13 0013 15:49
 */
public class DragFloatButton extends AbastractDragFloatActionButton {

    private ImageView mViewById;

    public DragFloatButton(Context context) {
        this(context, null);
    }

    public DragFloatButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragFloatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.drag_float_button;
    }

    @Override
    public void renderView(View view) {
        mViewById = view.findViewById(R.id.image);
    }

    public void init(Context context){
        Glide.with(context).load(R.drawable.alipay_reb_packet).into(mViewById);
    }
}
