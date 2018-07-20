package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/1 11:30
 */
public class WantedDetailHttpEntity {
    public int    code;
    public String msg;
    public Task   task;

    public class Task {

        public String  createtime;
        public long    deadlineTime;
        public int     id;
        public Double  money;
        public String  subtime;
        public String  confirmtime;
        public long    surplusTime;
        public Coupons coupons;
        public Reward  reward;
        public User    user;
    }

    public class Coupons {
        public Double quota;
    }

    public class Reward {
        public int        id;
        public RewardUser user;
        public String     title;
        public String     picture;
        public String     orderNo;
        public String     keyword;
    }

    public class User {
        public int    id;
        public String nickName;
        public String avatar;
    }

    public class RewardUser {
        public int    id;
        public String nickName;
        public String avatar;
        public String phone;
    }
}
