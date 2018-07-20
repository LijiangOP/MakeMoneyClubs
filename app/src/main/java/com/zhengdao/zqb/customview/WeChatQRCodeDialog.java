package com.zhengdao.zqb.customview;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhengdao.zqb.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2017/10/10 14:25
 */
public class WeChatQRCodeDialog extends ShapeDialog {

    @BindView(R.id.bt_save)
    Button   mBtSave;
    @BindView(R.id.tv_content)
    TextView mTvContent;

    private Context mContext;

    public WeChatQRCodeDialog(Context context) {
        this(context, 0);
    }

    public WeChatQRCodeDialog(Context context, int themeResId) {
        super(context, themeResId);
        View rootView;
        mContext = context;
        rootView = View.inflate(context, R.layout.wechat_qrcode, null);
        ButterKnife.bind(this, rootView);
        setContentView(rootView);
        initContent("");
    }

    public void initContent(String value) {
        if (!TextUtils.isEmpty(value))
            mTvContent.setText(value);
    }

    public void setSaveListener(View.OnClickListener listener) {
        mBtSave.setOnClickListener(listener);
    }
}
