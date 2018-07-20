package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/25 18:03
 */
public class MyWantedDetailEntity {
    public String remarks;
    public String createtime;
    public long   deadlineTime;
    public int    id;
    public int    state;
    public Double money;
    public Reward reward;
    public User   user;

    public class Reward {
        public int    id;
        public int    joincount;
        public String keywords;
        public String picture;
        public String title;
        public String keyword;
    }

    public class User {
        public int    userId;
        public String nickName;
        public String avatar;

    }
}
