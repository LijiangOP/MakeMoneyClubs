package com.zhengdao.zqb.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhengdao.zqb.R;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/30 18:01
 */
public class CustomAlertDialog extends Dialog implements View.OnClickListener {


    public interface OnDialogButtonClickListener {
        /**
         * 点击按钮的回调方法
         *
         * @param requestCode
         * @param isPositive
         */
        void onDialogButtonClick(int requestCode, boolean isPositive);

    }

    private Context                     context;
    private String                      title;
    private String                      message;
    private String                      strPositive;
    private String                      strNegative;
    private int                         requestCode;
    private OnDialogButtonClickListener listener;

    public CustomAlertDialog(Context context, String title, int requestCode,
                             OnDialogButtonClickListener listener) {
        super(context, R.style.MyDialog);
        this.context = context;
        this.title = title;
        this.requestCode = requestCode;
        this.listener = listener;

    }

    private TextView tvTitle;
    private TextView tvPositive;
    private TextView tvNegative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
        //  setCancelable(false);//设置点击对话框外部和按返回键都不可以取消
        //  setCanceledOnTouchOutside(false);//设置点击对话框外部是否可以取消，默认是不可以取消（但是点返回键可以取消）
        tvTitle = findViewById(R.id.tv_AlertDialogTitle);
        tvPositive = findViewById(R.id.tv_AlertDialogPositive);
        tvNegative = findViewById(R.id.tv_AlertDialogNegative);
        tvTitle.setText(title);
        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_AlertDialogPositive:
                //确定按钮
                listener.onDialogButtonClick(requestCode, true);
                break;
            case R.id.tv_AlertDialogNegative:
                //取消按钮
                listener.onDialogButtonClick(requestCode, false);
                break;
        }
        dismiss();
    }
}
