package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/11 14:55
 */
public class BalanceDetailEntity {

    public long   addTime; //日期
    public String createTime; //日期
    public String findState;  //状态描述
    public Double inAmount;   //存入金额
    public String logDesc;    //日志描述
    public Double outAmount;  //支出金额
    public Double usableSum;  //可用金额

    @Override
    public String toString() {
        return "BalanceDetailEntity{" +
                "addTime=" + addTime +
                ", createTime='" + createTime + '\'' +
                ", findState='" + findState + '\'' +
                ", inAmount=" + inAmount +
                ", logDesc='" + logDesc + '\'' +
                ", outAmount=" + outAmount +
                ", usableSum=" + usableSum +
                '}';
    }
}
