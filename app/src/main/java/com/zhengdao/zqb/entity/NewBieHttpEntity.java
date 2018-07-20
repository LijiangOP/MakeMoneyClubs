package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/7 17:15
 */
public class NewBieHttpEntity {
    public Double                 sum;
    public int                    code;
    public String                 msg;
    public ArrayList<NewbieTasks> newbieTasks;

    public class NewbieTasks {
        /**
         * "describe": "简单注册即可领取奖励",     //描述
         * "id": 1,
         * "rwId": 0,
         * “integral”,                           //任务奖励
         * "status": "已完成",                     //状态
         * "title": "注册",                         //标题
         * "url": ""                               //跳转链接
         */
        public String describe;
        public int    id;
        public int    rwId;
        public Double integral;
        public int    status;  //状态 1已完成 2未完成
        public String title;
        public String url;
        public int    type; //任务类型 0-注册任务 1-实名认证 2-市场评论 3-推荐悬赏
    }
}
