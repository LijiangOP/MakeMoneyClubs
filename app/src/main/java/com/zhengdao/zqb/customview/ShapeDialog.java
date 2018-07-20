package com.zhengdao.zqb.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.zhengdao.zqb.R;


/**
 * 使用这个dialog可以自定义外形
 *
 * @author cairui
 *         2016-3-10
 */

public class ShapeDialog extends Dialog {
    public ShapeDialog(Context context) {
        this(context, R.style.shape_dialog);
    }

    public ShapeDialog(Context context, int themeResId) {
        super(context, themeResId);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
