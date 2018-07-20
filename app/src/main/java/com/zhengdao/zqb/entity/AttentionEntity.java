package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/15 16:15
 */
public class AttentionEntity {
    public int    code;
    public String msg;
    public Entity list;

    public class Entity {
        public boolean                          hasNextPage;
        public int                              total;
        public ArrayList<AttentionDetailEntity> list;
    }

    public class AttentionDetailEntity {
        public String avatar;
        public int    fid;
        public int    fuserId;
        public String nickName;
        public String userName;
        public int    vip;
    }
}
