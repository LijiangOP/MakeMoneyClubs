package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/2 16:38
 */
public class InvitedHttpEntity {
    public int        code;
    public String     msg;
    public Recommends recommends;
    public User       user;
    public String     url;

    public class User {
        public String id;
        public String inviteCode;
        public String nickName;
        public String phone;
        public String icon;
    }
}
