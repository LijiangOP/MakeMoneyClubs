package com.zhengdao.zqb.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/5 17:59
 */
public class MarketCommentHttpEntity {
    public int                      code;
    public String                   msg;
    public ArrayList<MarketComment> marketList;

    public class MarketComment implements Serializable {
        public int    id;
        public int    state;
        public String appName;
        public String title;
        public String goodsIcon;
        public String img1;
        public String img2;
        public String img3;
        public String img4;
    }
}
