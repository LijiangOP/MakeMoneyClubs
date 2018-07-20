package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/3 10:35
 */
public class TaskEntity {

    public int                          type;//'任务类型 0-注册任务 1-支付宝绑定 2-市场评论 3-推荐悬赏 4-分享朋友圈 5-签到' 10广告
    public String                       title;
    public NewBieHttpEntity.NewbieTasks entity;

    public TaskEntity(int type) {
        this.type = type;
    }

    public TaskEntity(int type, String title) {
        this.type = type;
        this.title = title;
    }

    public TaskEntity(int type, NewBieHttpEntity.NewbieTasks entity) {
        this.type = type;
        this.entity = entity;
    }
}
