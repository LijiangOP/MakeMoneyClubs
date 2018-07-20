package com.zhengdao.zqb.view.dialogactivity;

import com.zhengdao.zqb.entity.ScreenLoadEntity;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/29 16:04
 */
public interface ISelectedActivityView {

    void showErrorMessage(String message);

    void showView(ScreenLoadEntity result);
}
