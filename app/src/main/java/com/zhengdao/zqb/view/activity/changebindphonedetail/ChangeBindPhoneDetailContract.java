package com.zhengdao.zqb.view.activity.changebindphonedetail;

import com.zhengdao.zqb.entity.ConfirmCodeEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.mvp.BasePresenter;
import com.zhengdao.zqb.mvp.BaseView;

public class ChangeBindPhoneDetailContract {
    interface View extends BaseView {
        void onGetConfirmCodeResult(ConfirmCodeEntity httpResult);

        void getConfirmCodeSuccess();

        void onChangePhoneNumResult(HttpResult httpResult);

    }

    interface Presenter extends BasePresenter<View> {
        void getConfirmCode(String phone);

        void BindNewPhone(String phone, String confirmCode);

    }
}
