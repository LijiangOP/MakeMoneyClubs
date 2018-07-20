package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/14 15:13
 */
public class RewardTicketHttpEntity {
    public int     code;
    public String  msg;
    public Coupons coupons;
    public int     notUsedCount;  //未使用数量
    public int     alreadyUsedCount;   //已使用数量
    public int     expiredCount;  //已过期数量

    public class Coupons {
        public boolean                       hasNextPage;
        public ArrayList<RewardTicketEntity> list;
    }
}
