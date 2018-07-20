package com.zhengdao.zqb.view.activity.versiondesc;


import android.os.Bundle;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.mvp.MVPBaseActivity;

import butterknife.ButterKnife;


public class VersionDescActivity extends MVPBaseActivity<VersionDescContract.View, VersionDescPresenter> implements VersionDescContract.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_desc);
        ButterKnife.bind(this);
        setTitle("版本说明");
    }

}
