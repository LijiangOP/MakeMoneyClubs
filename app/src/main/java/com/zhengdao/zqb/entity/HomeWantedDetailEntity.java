package com.zhengdao.zqb.entity;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/19 11:50
 */
public class HomeWantedDetailEntity {
    public String msg;
    public int    code;
    public int    state;  //是否已经领取 0未领取 1已领取
    public int    fid;
    public int    flag;  //是否关注 0未关注 1已关注
    public long   time;
    public String share; //悬赏令
    public Reward reward;


    public class Reward {
        public int                   block;//版块ID 71:理财返利
        public String                condition;
        public String                content; //任务链接
        public String                createTime;
        public int                   classify;//业务分类
        public int                   category;//悬赏分类
        public int                   mode;//审核时间id
        public String                explain;
        public int                   userId;
        public int                   id;
        public int                   type;//1 新品,0默认
        public int                   taskId;
        public Double                money;
        public int                   number;
        public String                picture;
        public List<RewardPics>      rewardPics;
        public List<RewardUserInfos> rewardUserInfos;
        public String                title;
        public int                   joincount;
        public String                keyword;
        public String                upFrameTime;//上架时间
        public String                lowerFrameTime;//下架时间
        public String                discount;//优惠额度
        public User                  user;

    }

    public class User {
        public String avatar;
        public int    id;
        public String nickName;
        public String phone;
    }
}
