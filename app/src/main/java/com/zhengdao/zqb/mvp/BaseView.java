package com.zhengdao.zqb.mvp;

import android.content.Context;

public interface BaseView {
    Context getContext();

    void showProgress();

    void hideProgress();

    void showErrorMessage(String msg);
}
