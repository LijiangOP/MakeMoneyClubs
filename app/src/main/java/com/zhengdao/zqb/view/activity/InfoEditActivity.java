package com.zhengdao.zqb.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.view.adapter.AddUserInfoAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.config.Constant.EditableList;
import static com.zhengdao.zqb.config.Constant.Wanted.DEFAULT_BRAND_NEW_TYPE;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/13 17:56
 */
public class InfoEditActivity extends AppCompatActivity implements AddUserInfoAdapter.ClickCallBack, View.OnClickListener {

    @BindView(R.id.iv_title_bar_back)
    ImageView mIvTitleBarBack;
    @BindView(R.id.tv_title_bar_title)
    TextView  mTvTitleBarTitle;
    @BindView(R.id.tv_title_bar_right)
    TextView  mTvTitleBarRight;
    @BindView(R.id.listview)
    ListView  mListview;
    @BindView(R.id.tv_publish)
    TextView  mTvPublish;

    private InputMethodManager mImm;
    private View               decorView;
    private int                mActivityType;
    private AddUserInfoAdapter mAddUserInfoAdapter;
    private int                mActivitySkipType;
    public  ArrayList<String>  mAddUserInfoData;
    private long mCurrentTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info_edit);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        decorView = getWindow().getDecorView();
        mActivityType = getIntent().getIntExtra(Constant.Activity.Type, 0);
        LogUtils.i("" + mActivityType);
        ArrayList<String> data = getIntent().getStringArrayListExtra(Constant.Activity.Data);
        if (data != null)
            LogUtils.i("" + data.size());
        mActivitySkipType = getIntent().getIntExtra(Constant.Activity.Type1, DEFAULT_BRAND_NEW_TYPE);
        LogUtils.i("" + mActivitySkipType);
        String title = "";
        switch (mActivityType) {
            case 0:
                title = "提交文字信息";
                break;
            case 1:
                title = "提交图片信息";
                break;
        }
        mTvTitleBarTitle.setText(title);
        mTvTitleBarRight.setText("新增");
        mIvTitleBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoEditActivity.this.finish();
            }
        });
        mTvTitleBarRight.setOnClickListener(this);
        mTvPublish.setOnClickListener(this);
        if (!EditableList.contains(mActivitySkipType)) {
            mTvPublish.setVisibility(View.GONE);
            mTvTitleBarRight.setVisibility(View.GONE);
        }
        initData(data);
    }

    private void initData(ArrayList<String> data) {
        if (mAddUserInfoData == null)
            mAddUserInfoData = new ArrayList();
        mAddUserInfoData.clear();
        if (data != null && data.size() > 0)
            mAddUserInfoData.addAll(data);
        else
            mAddUserInfoData.add("");
        if (mAddUserInfoAdapter == null) {
            mAddUserInfoAdapter = new AddUserInfoAdapter(InfoEditActivity.this, mAddUserInfoData, InfoEditActivity.this, mActivityType, mActivitySkipType);
            mListview.setAdapter(mAddUserInfoAdapter);
        } else {
            mAddUserInfoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDelete(int position) {
        updateData();
        if (mAddUserInfoData.size() > position)
            mAddUserInfoData.remove(position);
        if (mAddUserInfoAdapter != null)
            mAddUserInfoAdapter.notifyDataSetChanged();
        hideSoftInput();
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 500)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_title_bar_right:
                if (mAddUserInfoData != null) {
                    if (mAddUserInfoData.size() > 4)
                        return;
                    updateData();
                    mAddUserInfoData.add("");
                    mAddUserInfoAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.tv_publish:
                if (mAddUserInfoData != null) {
                    updateData();
                    Intent intent = new Intent();
                    intent.putStringArrayListExtra(Constant.Activity.Data, mAddUserInfoData);
                    setResult(RESULT_OK, intent);
                    InfoEditActivity.this.finish();
                }
                break;
        }
    }

    private void updateData() {
        if (mAddUserInfoAdapter != null) {
            for (int i = 0; i < mAddUserInfoAdapter.getCount(); i++) {
                View childAt = mListview.getChildAt(i);
                if (childAt != null) {
                    EditText editText = childAt.findViewById(R.id.et_add_user_info);
                    String trim = editText.getText().toString().trim();
                    mAddUserInfoData.set(i, trim);
                }
            }
        }
    }

    public void hideSoftInput() {
        if (mImm != null && decorView != null) {
            mImm.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
