package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/20 16:48
 */
public class PlatformHttpEntity {
    public String   msg;
    public int      code;
    public FormList platformList;

    public class FormList {
        public boolean                 hasNextPage;
        public ArrayList<PlatformBean> list;
    }
}
