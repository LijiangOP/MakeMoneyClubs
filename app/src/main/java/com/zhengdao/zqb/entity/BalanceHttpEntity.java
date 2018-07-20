package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/2 15:15
 */
public class BalanceHttpEntity<T> {
    public int    code;
    public String msg;
    public Double usableSum;
    public T      data;
}
