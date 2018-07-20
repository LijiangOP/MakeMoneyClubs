package com.zhengdao.zqb.view.activity.datastatistics;


import android.os.Bundle;
import android.view.View;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.mvp.MVPBaseActivity;

import butterknife.ButterKnife;

public class DataStatisticsActivity extends MVPBaseActivity<DataStatisticsContract.View, DataStatisticsPresenter> implements DataStatisticsContract.View, View.OnClickListener {


    private long mCurrentTimeMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datastatistics);
        ButterKnife.bind(this);
        setTitle("数据统计");
        init();
    }

    private void init() {

    }

    @Override
    public void onClick(View v) {
        if (System.currentTimeMillis() - mCurrentTimeMillis < 1000)
            return;
        mCurrentTimeMillis = System.currentTimeMillis();
        switch (v.getId()) {
            case R.id.ll_can_get_code:
                break;
            case R.id.ll_can_not_get_code:
                break;
        }
    }

}
