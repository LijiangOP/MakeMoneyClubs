package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/9 14:59
 */
public class RelevanceAccountEntity {
    public int                  code;
    public String               msg;
    public ArrayList<Relevance> relation;

    public class Relevance {
        public int    id;
        public int    type;
        public int    userId;
        public String openId;
    }
}
