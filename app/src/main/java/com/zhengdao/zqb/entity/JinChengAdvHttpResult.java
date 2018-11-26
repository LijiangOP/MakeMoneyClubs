package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/11/21 0021 11:52
 */
public class JinChengAdvHttpResult {
    int rcd;
    String msg;
    ArrayList<JinChengAdvEntity> ad;

    public int getRcd() {
        return rcd;
    }

    public String getMsg() {
        return msg;
    }

    public ArrayList<JinChengAdvEntity> getAd() {
        return ad;
    }

    @Override
    public String toString() {
        return "JinChengAdvHttpResult{" +
                "rcd=" + rcd +
                ", msg='" + msg + '\'' +
                ", ad=" + ad +
                '}';
    }
}
