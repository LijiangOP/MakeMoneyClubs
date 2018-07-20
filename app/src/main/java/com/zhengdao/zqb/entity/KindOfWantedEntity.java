package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/5 14:31
 */
public class KindOfWantedEntity {
    public int    id;
    public String createtime;
    public String remarks;
    public String confirmtime;
    public Reward reward;
    public User   user;
    public long   surplusTime;

    public class Reward {
        public String title;
    }

    public class User {
        public String nickName;
        public String avatar;
    }
}
