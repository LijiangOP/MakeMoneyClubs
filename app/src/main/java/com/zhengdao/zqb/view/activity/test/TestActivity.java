package com.zhengdao.zqb.view.activity.test;


import android.os.Bundle;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.application.ClientAppLike;
import com.zhengdao.zqb.application.ExitApplication;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.FileUtils;
import com.zhengdao.zqb.utils.ToastUtil;

import butterknife.ButterKnife;


public class TestActivity extends MVPBaseActivity<TestContract.View, TestPresenter> implements TestContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ExitApplication.getInstance().addActivity(this);
        setSwipeBackEnable(false);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        if (FileUtils.isDownloadFileValable()) {
            ToastUtil.showToast(ClientAppLike.getContext(), "文件创建成功=" + FileUtils.getUpdataApkFilePath());
        } else {
            ToastUtil.showToast(ClientAppLike.getContext(), "文件创建失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
