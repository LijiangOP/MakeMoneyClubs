package com.zhengdao.zqb.event;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/9 11:27
 */
public class ForceBindPhoneEvent {
    public int type;//0 QQ登录 1 微信登录

    public ForceBindPhoneEvent(int type) {
        this.type = type;
    }
}
