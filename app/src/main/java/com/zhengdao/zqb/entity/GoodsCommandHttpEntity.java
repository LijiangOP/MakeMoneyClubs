package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/5 11:15
 */
public class GoodsCommandHttpEntity {
    public int    code;
    public String msg;
    public Reward reward;

    public class Reward {
        /**
         * "id": 1,                                //悬赏ID
         * "money": 233.00,                     //悬赏金额
         * "picture": "http://47.98.54.126:8080/upload/images/appImages/201802/201802261535053b5fd43b99dfa29a05a4f1.jpg",                    //悬赏图片
         * "state": 2,              //悬赏状态   0.暂存，1.审核中，2.已发布，3.已结束，4.下架中，5.已下架
         * "title": "轻轻松松赚钱",               //悬赏标题
         * "userId": 19
         */
        public int    id;
        public Double money;
        public String picture;
        public int    state;
        public String title;
        public int    userId;

    }
}
