package com.zhengdao.zqb.view.activity.keywords;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fynn.fluidlayout.FluidLayout;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.DictionaryHttpEntity;
import com.zhengdao.zqb.entity.DictionaryValue;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;


public class KeyWordsActivity extends MVPBaseActivity<KeyWordsContract.View, KeyWordsPresenter> implements KeyWordsContract.View, View.OnClickListener {


    @BindView(R.id.tv_close)
    TextView     mTvClose;
    @BindView(R.id.fluid_layout)
    FluidLayout  mFluidLayout;
    @BindView(R.id.tv_add_keyword)
    TextView     mTvAddKeyword;
    @BindView(R.id.iv_delete_keyword)
    ImageView    mIvDeleteKeyword;
    @BindView(R.id.ll_add_keyword)
    LinearLayout mLlAddKeyword;
    @BindView(R.id.et_keyword)
    EditText     mEtKeyword;
    @BindView(R.id.tv_selected)
    TextView     mTvSelected;

    private long mCurrentTimeMillis = 0;
    private ArrayList<TextView>        mData;
    private ArrayList<DictionaryValue> mCheckTimeData;
    private Queue<Integer>             mQueue;
    private String                     mKeyWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_key_words);
        ButterKnife.bind(this);
        initClickListener();
        init();
        mPresenter.getData("KEY_WORD");
    }

    private void initClickListener() {
        mTvClose.setOnClickListener(this);
        mTvAddKeyword.setOnClickListener(this);
        mIvDeleteKeyword.setOnClickListener(this);
        mTvSelected.setOnClickListener(this);
        mEtKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();
                if (!TextUtils.isEmpty(value)) {
                    mLlAddKeyword.setVisibility(View.VISIBLE);
                    mTvAddKeyword.setText(value);
                } else {
                    mTvAddKeyword.setText("");
                    mLlAddKeyword.setVisibility(View.GONE);
                }
            }
        });
        mLlAddKeyword.setVisibility(View.GONE);
    }

    private void init() {
        mKeyWords = getIntent().getStringExtra(Constant.Activity.Data);
        LogUtils.i(mKeyWords);
    }

    @Override
    public void showView(DictionaryHttpEntity result, String key) {
        if (result.code == Constant.HttpResult.SUCCEED) {
            if (!TextUtils.isEmpty(key) && key.equals("KEY_WORD")) {
                if (result.types != null) {
                    mCheckTimeData = result.types;
                    try {
                        buildData(mCheckTimeData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (result.code == Constant.HttpResult.RELOGIN) {
            ToastUtil.showToast(this, "登录超时,请重新登录");
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            ToastUtil.showToast(KeyWordsActivity.this, TextUtils.isEmpty(result.msg) ? "数据加载失败" : result.msg);
        }
    }

    private void buildData(ArrayList<DictionaryValue> mCheckTimeData) throws Exception {
        if (mData == null)
            mData = new ArrayList<>();
        mData.clear();
        if (mQueue == null)
            mQueue = new LinkedList<>();
        mQueue.clear();
        FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 35, 45);
        for (int i = 0; i < mCheckTimeData.size(); i++) {
            doAddkeyWord(mCheckTimeData.get(i).value, i, params);
        }
    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.tv_close:
                KeyWordsActivity.this.finish();
                break;
            case R.id.tv_add_keyword:
                String value = mTvAddKeyword.getText().toString().trim();
                if (!TextUtils.isEmpty(value) && mData != null) {
                    FluidLayout.LayoutParams params = new FluidLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 35, 45);
                    doAddkeyWord(value, mData.size(), params);
                }
                mEtKeyword.setText("");
                //                mTvAddKeyword.setText("");
                //                mLlAddKeyword.setVisibility(View.GONE);
                break;
            case R.id.iv_delete_keyword:
                mEtKeyword.setText("");
                //                mTvAddKeyword.setText("");
                //                mLlAddKeyword.setVisibility(View.GONE);
                break;
            case R.id.tv_selected:
                try {
                    doSelected();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void doAddkeyWord(String value, final int position, FluidLayout.LayoutParams params) {
        if (mData != null) {
            TextView textView = new TextView(this);
            textView.setText(value);
            textView.setTextSize(13);
            textView.setSingleLine(true);
            textView.setPadding(35, 20, 35, 20);
            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            textView.setTextColor(getResources().getColor(R.color.color_418DAE));
            textView.setBackground(getResources().getDrawable(R.drawable.shape_rect_keyword_gray));
            mData.add(textView);
            mFluidLayout.addView(textView, params);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (mQueue.contains(position)) {
                            mQueue.remove(position);
                        } else {
                            if (mQueue.size() == 3)
                                mQueue.poll();
                            mQueue.offer(position);
                        }
                        doSetBg();
                    } catch (Exception ex) {
                        LogUtils.e(ex.getMessage());
                    }
                }
            });
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return true;
                }
            });
        }
    }

    private void doSetBg() {
        for (int i = 0; i < mData.size(); i++) {
            int finalI = i;
            TextView textView = mData.get(i);
            if (mQueue.contains(finalI)) {
                //                textView.setTextColor(getResources().getColor(R.color.color_fc3135));
                textView.setBackground(getResources().getDrawable(R.drawable.shape_rect_keyword_red));
            } else {
                //                textView.setTextColor(getResources().getColor(R.color.color_418DAE));
                textView.setBackground(getResources().getDrawable(R.drawable.shape_rect_keyword_gray));
            }
        }
    }

    private void doSelected() throws Exception {
        Bundle bundle = new Bundle();
        try {
            if (mQueue != null && mQueue.size() > 0) {
                switch (mQueue.size()) {
                    case 1:
                        bundle.putString("keyWord1", mData.get(mQueue.poll()).getText().toString());
                        break;
                    case 2:
                        bundle.putString("keyWord1", mData.get(mQueue.poll()).getText().toString());
                        bundle.putString("keyWord2", mData.get(mQueue.poll()).getText().toString());
                        break;
                    case 3:
                        bundle.putString("keyWord1", mData.get(mQueue.poll()).getText().toString());
                        bundle.putString("keyWord2", mData.get(mQueue.poll()).getText().toString());
                        bundle.putString("keyWord3", mData.get(mQueue.poll()).getText().toString());
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            LogUtils.e(ex.getMessage());
        }
        Intent intent = new Intent();
        intent.putExtra(Constant.Activity.FreedBack, bundle);
        LogUtils.e(bundle.toString());
        setResult(RESULT_OK, intent);
        KeyWordsActivity.this.finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
