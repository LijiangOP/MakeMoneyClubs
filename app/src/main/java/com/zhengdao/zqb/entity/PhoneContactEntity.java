package com.zhengdao.zqb.entity;

import android.graphics.Bitmap;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/27 11:40
 */
public class PhoneContactEntity {
    public String fristcase;
    public String name;
    public String phone;
    public Bitmap icon;

    public PhoneContactEntity(String fristcase, String name, String phone, Bitmap icon) {
        this.fristcase = fristcase;
        this.name = name;
        this.phone = phone;
        this.icon = icon;
    }
}
