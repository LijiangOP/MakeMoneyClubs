package com.zhengdao.zqb.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/20 17:48
 */
public class StatementActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView       mTextView;
    @BindView(R.id.iv_title_bar_back)
    ImageView      mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView       mTvTitleBarTitle;
    @BindView(R.id.re_title_bar)
    RelativeLayout mReTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_statement);
        ButterKnife.bind(this);
        initTitle("协议与声明");
        initData();
    }

    private void initTitle(String title) {
        mTvTitleBarTitle.setText(title);
        mIvTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatementActivity.this.finish();
            }
        });
    }

    private void initData() {
        String stringExtra = getIntent().getStringExtra(Constant.Activity.Data);
        mTextView.setText(TextUtils.isEmpty(stringExtra) ? "" : stringExtra);
    }
}
