package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/31 16:17
 */
public class WalletHttpEntity {
    public int     code;
    public String  msg;
    public Account account;

    public class Account {
        public Double integral;
        public long   luckPoint;
        public Double usableSum;
        public long   userId;
    }
}
