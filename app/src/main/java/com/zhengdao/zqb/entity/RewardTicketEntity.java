package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/14 14:39
 */
public class RewardTicketEntity {
    /**
     * "endTime": "2018-05-22 10:34:02",    //截止时间
     * "id": 1,
     * "quota": 12.00,                       //额度
     * "state": 0,                           //状态 0-未使用 1-已使用 2-已过期
     * "type": 1,                            //卡券类型 1-流量券 2-话费券 3.现金券
     * "useTime": “2018-05-22 10:34:02”,     //使用时间
     * "userId": 2                           //用户ID
     */
    public String endTime;
    public int    id;
    public Double quota;
    public String money;
    public int    state;
    public int    type;
    public String title;
    public String useTime;
    public String userId;
}
