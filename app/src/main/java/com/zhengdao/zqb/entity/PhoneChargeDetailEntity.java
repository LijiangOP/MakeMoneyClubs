package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/2 16:09
 */
public class PhoneChargeDetailEntity {

    public PhoneChargeDetailEntity(int id, String value, String price) {
        this.id = id;
        this.value = value;
        this.price = price;
    }
    public int    id;
    public String value;
    public String price;
}
