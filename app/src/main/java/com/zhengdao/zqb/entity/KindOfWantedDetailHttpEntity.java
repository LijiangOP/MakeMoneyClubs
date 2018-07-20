package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/5 15:35
 */
public class KindOfWantedDetailHttpEntity {

    public int    code;
    public String msg;
    public Tasks  task;

    public class Tasks {
        public int                      id;
        public int                      rwId;
        public int                      value;  //1已关注 0未关注
        public long                     surplusTime;
        public User                     user;
        public ArrayList<TaskPics>      taskPics;
        public ArrayList<TaskUserInfos> taskUserInfos;
    }

    public class TaskPics {
        public String picture;

    }

    public class TaskUserInfos {
        public String content;

    }

    public class User {
        public String avatar;
        public int    id;
        public String nickName;
        public String userName;

    }
}
