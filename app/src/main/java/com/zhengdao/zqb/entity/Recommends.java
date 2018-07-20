package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/2 16:44
 */
public class Recommends {
    public boolean                          hasNextPage;
    public int                              total;
    public ArrayList<RecommendDetailEntity> list;

    public class RecommendDetailEntity {
        public String createTime;
        public String nickName;
        public int    userType;
    }
}
