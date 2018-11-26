package com.zhengdao.zqb.entity;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/4 15:45
 */
public class UserHomeBean {
    public int             code;
    public String          msg;
    public List<TaskCount> taskCount;
    public UserInfo        userInfo;
    public User            user;
    public Account         account;
    public Double          toDayProfit;//今日收益
    public int             withdraw;//提现次数

    public class TaskCount {
        public int    count;
        public int    state;
        public String taskName;
    }

    public class UserInfo {
        /**
         * zfb:”15818128151”       // 支付宝
         * receiveCount:2                //领取任务次数,
         * submitCount : 3              //提交任务次数',
         * finishCount : 4             //完成任务次数'
         * birthday:”2018-9-10”    //年龄(三属性之一)
         * addressSet:10             //地域编号(三属性之一)
         * sex:1                      //性别(三属性之一)
         * gameInterest:”1,2”      //游戏兴趣,  字典ID
         * rewardPreference:”3,4”   //悬赏偏好,
         * intentionConsumption:”5,6” //意向消费,
         */
        public String zfb;
        public int    receiveCount;
        public int    submitCount;
        public int    finishCount;
        public String birthday;
        public int    addressSet;
        public int    sex;
        public String gameInterest;
        public String rewardPreference;
        public String intentionConsumption;
    }

    public class User {

        public int    vip;
        public String userName;
        public String nickName;
        public String ophone;
        public String inviteCode;
        public String token;
        public String avatar;
        public int    referee;
        public int    editCount;
        public String email;
        public int    welfare;//福利领取状态 0-未领取 1-已领取

    }

    public class Account {

        public int    id;
        public int    userId;
        public Double usableSum;
        public Double integral;
        public Double totalIntegral;
        public Double expValue;
        public Double brokerage;
        public Double frozenfunds;
        public Double totalAmount;
        public Double brokerageSum;
        public Double lowerTotalAmount;
        public Double takenAmount;
        public Double coefficient;
        public Double luckPoint;

    }
}
