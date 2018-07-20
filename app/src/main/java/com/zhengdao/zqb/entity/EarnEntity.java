package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/29 11:04
 */
public class EarnEntity {
    /**
     * "rewards": {
     * "hasNextPage": false,                            //是否有下一页
     * "list": [
     * {                                             //悬赏列表
     * "createTime": "2017-12-29 10:43:51",    //创建时间
     * "id": 5,                                 //悬赏ID
     * "joincount": 5,                          //已参与人数
     * "keyWords": [72小时,限首投],             //关键词
     * "money": 5,                              //悬赏金额
     * "picture": "测试数据5.jpg",               //悬赏图片
     * "title": "测试数据5",                      //标题
     * },.....
     * ],
     * },
     * "code": 0,
     * "msg": "操作成功"
     */

    public int     code;
    public String  msg;
    public Rewards rewards;

    public class Rewards {
        public boolean                     hasNextPage;
        public ArrayList<HomeItemEntity> list;
    }
}

