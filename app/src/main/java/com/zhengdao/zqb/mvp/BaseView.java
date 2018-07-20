package com.zhengdao.zqb.mvp;

import android.content.Context;

/**
 * MVPPlugin
 *  邮箱 784787081@qq.com
 */

public interface BaseView {
     Context getContext();
     void showProgress();
     void hideProgress();
     void showErrorMessage(String msg);
}
