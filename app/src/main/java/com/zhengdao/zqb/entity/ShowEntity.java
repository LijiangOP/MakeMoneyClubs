package com.zhengdao.zqb.entity;

import android.graphics.Bitmap;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/30 17:43
 */
public class ShowEntity {
    public String desc = "";
    public Bitmap pic  = null;

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public ShowEntity() {

    }

    public ShowEntity(String desc) {
        this.desc = desc;
    }

    public ShowEntity(String desc, Bitmap pic) {
        this.desc = desc;
        this.pic = pic;
    }
}
