package com.zhengdao.zqb.controller;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


/**
 * 一个可输入可增加可减少的模块（两个textview与一个editext的组合）的控制器
 *
 * @Author lijiangop
 * @CreateTime 2017/8/1 15:45
 */
public class SubAddItemController implements TextWatcher, TextView.OnEditorActionListener, View.OnLayoutChangeListener, View.OnFocusChangeListener {

    private int                MIN_VALUE;
    private int                MAX_VALUE;
    private View               mViewGroup;
    private Activity           mContext;
    private TextView           mTvSub;
    private TextView           mTvAdd;
    private EditText           mEtCount;
    private InputMethodManager mImm;
    private int                screenHeight;
    private int                keyHeight;
    private textChangeListener mTextChangeListener;

    public void setTextChangeCallBack(textChangeListener listener) {
        mTextChangeListener = listener;
    }

    public interface textChangeListener {
        void onTextChange(String string);
    }

    public SubAddItemController(Activity activity, View viewGroup, TextView tvAmountBtnSub, TextView tvAmountBtnAdd, EditText etAmountBtnCount) {
        this.mContext = activity;
        this.mViewGroup = viewGroup;
        this.mTvSub = tvAmountBtnSub;
        this.mTvAdd = tvAmountBtnAdd;
        this.mEtCount = etAmountBtnCount;
    }

    public void init(int Min, int Max) {
        this.MIN_VALUE = Min;
        this.MAX_VALUE = Max;
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        screenHeight = mContext.getWindowManager().getDefaultDisplay().getHeight();       //获取屏幕高度
        keyHeight = screenHeight / 3;
        initClicListener();
    }

    private void initClicListener() {
        mTvSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCalculation(false);
            }
        });
        mTvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCalculation(true);
            }
        });
        mEtCount.setText(String.valueOf(MIN_VALUE));
        mEtCount.addTextChangedListener(this);
        mEtCount.setOnEditorActionListener(this);
        mEtCount.setOnFocusChangeListener(this);
        mViewGroup.addOnLayoutChangeListener(this);
    }

    public void doCalculation(boolean isAdd) {
        if (mEtCount == null)
            return;
        String value = mEtCount.getText().toString();
        int v;
        if (TextUtils.isEmpty(value))
            v = MIN_VALUE;
        else
            v = Integer.valueOf(value);
        if (isAdd) {
            v++;
            if (v > MAX_VALUE)
                v = MAX_VALUE;
        } else {
            v--;
            if (v < MIN_VALUE)
                v = MIN_VALUE;
        }
        mEtCount.clearFocus();
        hideKeyboard(mEtCount);
        mEtCount.setText(String.valueOf(v));
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String value = editable.toString();
        if (value.length() > 6) {
            value = String.valueOf(MAX_VALUE);
            mEtCount.setText(value);
            mEtCount.setSelection(value.length());
        } else if (TextUtils.isEmpty(value) || Integer.valueOf(value) < 1) {
            value = String.valueOf(MIN_VALUE);
        } else
            value = String.valueOf(Integer.valueOf(editable.toString()));
        if (mTextChangeListener != null)
            mTextChangeListener.onTextChange("" + Integer.valueOf(value) * 100);
    }

    /**
     * 焦点变化监听
     *
     * @param view
     * @param isFocus
     */
    @Override
    public void onFocusChange(View view, boolean isFocus) {
        if (mEtCount == null)
            return;
        if (!isFocus) {
            String value = mEtCount.getText().toString();
            if (TextUtils.isEmpty(value) || Integer.valueOf(value) < MIN_VALUE)
                mEtCount.setText(String.valueOf(MIN_VALUE));
            else if (value.startsWith("0"))
                mEtCount.setText(desFixValue(value));
        }
    }

    private String desFixValue(String value) {
        if (TextUtils.isEmpty(value))
            return String.valueOf(MIN_VALUE);
        if (value.startsWith("0"))
            return desFixValue(value.substring(1));
        else
            return value;
    }

    /**
     * 软键盘回车键点击监听
     *
     * @param textView
     * @param actionId
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        mEtCount.clearFocus();
        hideKeyboard(mEtCount);
        return false;
    }

    /**
     * 间接监听软键盘的调起和关闭（横屏无效）
     */
    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight))  //软键盘弹起
            Log.i("TAG", "软键盘弹起");
        else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) //软键盘关闭
            mEtCount.clearFocus();

    }


    /**
     * 关闭软键盘
     *
     * @param view
     */
    public void hideKeyboard(View view) {
        if (view == null)
            return;
        if (mImm == null)
            return;
        mImm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
