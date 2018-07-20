package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/31 11:35
 */
public class ScanInfoDetailBean {
    public boolean              isShowCb;
    public ScanInfoDetailEntity entity;

    public ScanInfoDetailBean(boolean isShowCb, ScanInfoDetailEntity entity) {
        this.isShowCb = isShowCb;
        this.entity = entity;
    }
}
