package com.zhengdao.zqb.entity;

import java.io.Serializable;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/25 11:17
 */
public class InvestBean implements Serializable {
    /**
     * "id": 16,         //投资id
     * "investmentName": "赵鑫",    //投资姓名
     * "investmentPhone": "15217971261",  //投资电话
     * "amount": 10000,       //投资金额
     * "cycle": 30,            //投资周期
     * "investmentDate": "2017-08-24",    //投资时间
     * "cycleCompany": 1 天 /2 月,    //投资时间单位
     * "state": 0,    //0-待审核 1-审核通过 2-审核未通过
     * insideLogo         //内部logo地址
     * integral	        //奖励乐分值
     * url               //详情链接
     * wzName              //平台名称
     */
    public int    id;
    public String investmentName;
    public String investmentPhone;
    public double amount;
    public int    cycleCompany;
    public String cycle;
    public String investmentTime;
    public int    state;
    public String logo;
    public double integral;
    public String wzName;

    @Override
    public String toString() {
        return "InvestBean{" +
                "id=" + id +
                ", investmentName='" + investmentName + '\'' +
                ", investmentPhone='" + investmentPhone + '\'' +
                ", amount=" + amount +
                ", cycleCompany=" + cycleCompany +
                ", cycle='" + cycle + '\'' +
                ", investmentTime='" + investmentTime + '\'' +
                ", state=" + state +
                ", logo='" + logo + '\'' +
                ", integral=" + integral +
                ", wzName='" + wzName + '\'' +
                '}';
    }
}
