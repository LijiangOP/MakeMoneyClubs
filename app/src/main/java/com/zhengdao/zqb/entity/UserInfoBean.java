package com.zhengdao.zqb.entity;

import java.io.Serializable;

/**
 * Created by LiJiangOP on 2017/8/29.
 */
public class UserInfoBean implements Serializable {


    public int      code;
    public String   msg;
    public Userinfo userinfo;
    public User     user;

    public class Userinfo {
        /**
         * "address": "",
         * "birthday": "",                               //出生日期
         * "createTime": "2017-12-29 11:07:35",
         * "finishCount": 0,
         * "hometown": "",
         * "id": 1,
         * "income": -1,
         * "letterSet": 0,
         * "loginAddress": "中国",
         * "loginCount": 0,
         * "loginIP": "0:0:0:0:0:0:0:1",
         * "loginTime": "2017-12-29 11:07:35",
         * "notice": "",
         * "paypwd": "0",
         * "qq": "83520102",
         * "realName": "",
         * "receiveCount": 0,
         * "sex": 0,                            //性别
         * "sfzcard": "",
         * "star": "",
         * "tel": "15131212132",
         * "userId": 1,
         * "zfb": ""
         */
        public String birthday;
        public String zfb;
        public String realName;
        public String qq;
        public int    sex;
    }

    public class User {
        /**
         * "avatar": "",                       //头像
         * "editCount": 1,
         * "email": "835104141@qq.com",
         * "id": 1,
         * "inviteCode": "",
         * "isDel": 0,
         * "nickName": "test",                 //昵称
         * "phone": "1581",
         * "pwd": "",
         * "pwdStrength": 0,
         * "relation": 0,
         * "state": 0,
         * "token": "test",
         * "uid": 0,
         * "userName": "test",                     //用户名
         * "userType": 0,
         * "vip": 0
         */
        public String avatar;
        public int    id;
        public String nickName;
        public String userName;
        public String pwdStrength;
    }
}
