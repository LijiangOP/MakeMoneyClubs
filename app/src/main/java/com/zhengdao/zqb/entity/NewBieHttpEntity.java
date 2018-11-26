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
         * "rwId": 0,                             //悬赏ID
         * “integral”,                           //任务奖励
         * "status": 1,                           //状态 1已完成(可领取) 2未完成)3已完结
         * "title": "注册",                         //标题
         * "url": ""                               //跳转链接
         * “type”:0,              //任务类型 0-注册任务 1-支付宝绑定 2-市场评论 3-推荐悬赏 4-分享朋友圈 5-签到 6-悬赏奖励 7-游戏奖励 8-收徒奖励 9-问卷调查奖励'
         */
        public String describe;
        public int    id;
        public int    rwId;
        public Double integral;
        public int    status;  //状态 1已完成 2未完成3已完结 4可领取
        public String title;
        public String url;
        public int    type; //任务类型 0-注册任务 1-支付宝绑定 2-市场评论 3-推荐悬赏 4-分享朋友圈 5-签到 6-悬赏奖励 7-游戏奖励 8-收徒奖励 9-问卷调查奖励' 10-广告中心
                            //11-新手福利  12-支付宝红包奖励
    }
}
