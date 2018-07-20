package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/11 16:30
 */
public class FindEntityHttpResult {
    public String msg;
    public int    code;
    public HotApp hotApp;

    public class HotApp {
        public boolean                       hasNextPage;
        public ArrayList<HotRecommendEntity> list;
    }
}
