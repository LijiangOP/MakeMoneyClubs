package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/25 17:59
 */
public class MyWantedEntity {

    public int      code;
    public String   msg;
    public PageTask pageTask;


    public class PageTask {
        public boolean                         hasNextPage;
        public ArrayList<MyWantedDetailEntity> list;
    }
}
