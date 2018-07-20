package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/2 17:51
 */
public class ManagementWantedHttpEntity {
    public int        code;
    public String     msg;
    public RewardPage rewardPage;

    public class RewardPage {
        public boolean                     hasNextPage;
        public int                         code;
        public ArrayList<RewardPageDetail> list;

    }

    public class RewardPageDetail {
        public int    id;
        public Double money;
        public int    joincount;
        public String keyword;
        public String picture;
        public int    state;
        public String title;
        public int    payState;
    }
}
