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

    public class TaskCount {
        public int    count;
        public int    state;
        public String taskName;
    }

    public class UserInfo {
        public String zfb;
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
