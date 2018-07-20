package com.zhengdao.zqb.entity;


/**
 * @Author lijiangop
 * @CreateTime 2018/1/9 16:48
 */
public class WalletListEntity {
    public int    type;
    public Object object;

    public WalletListEntity(int type, Object object) {
        this.type = type;
        this.object = object;
    }
}
