package com.zhengdao.zqb.view.activity.investinfodetailinput;

import com.zhengdao.zqb.entity.PlatformHttpEntity;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

import java.util.ArrayList;

public class InvestInfoDetailInputContract {
    interface View extends BaseView {

        void showPlatformData(PlatformHttpEntity httpEntity);

        void SuccessEdit();

        void SuccessAdd();
    }

    interface  Presenter extends BasePresenter<View> {
        void getData();

        void addData(ArrayList<String> editData);
    }
}
