package com.zhengdao.zqb.entity;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/18 16:53
 */
public class RankingListEntity {

    public int           code;
    public String        msg;
    public User          user;
    public List<AllUser> allUser;

    public class User {
        public String avatar;
        public String nickName;
        public String number;
        public Double sunMoney;
        public int    userId;
    }

    public class AllUser {
        public String avatar;
        public String nickName;
        public String number;
        public Double sunMoney;
        public int    userId;
    }
}
