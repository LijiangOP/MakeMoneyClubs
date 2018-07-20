package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/11 11:09
 */
public class AdvRewardEntity {
    public int    address; //点击位置1-首页 2-发现 3-账户中心
    public String createTime;
    public int    id;
    public String logDesc;  //描述
    public Double outAmount; //用户广告收入
    public Double inAmount;
    public Double totalAmount;  //余额
    public int    userId;
}
