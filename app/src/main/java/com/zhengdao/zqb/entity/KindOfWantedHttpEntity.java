package com.zhengdao.zqb.entity;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/5 14:33
 */
public class KindOfWantedHttpEntity {
    public int       code;
    public String    msg;
    public int       total;
    public PageTasks pageTasks;

    public class PageTasks {
        public boolean                  hasNextPage;
        public List<KindOfWantedEntity> list;
    }
}
