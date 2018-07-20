package com.zhengdao.zqb.entity;

import java.io.Serializable;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/22 17:37
 */
public class BannerBean implements Serializable {
    /**
     * "id": 1,                                   //广告ID
     * "imgPath": "123.jpg",                     //广告图片
     * "title": "test",                             //广告标题
     * "type": 0,                                //广告类型
     * "url": "11123"                           //广告链接
     */
    public int    id;
    public String imgPath;
    public String title;
    public int    type;  //0-跳转URL 1-新手任务 2-赚钱大厅 3-邀请好友 4-网页加载 5-悬赏详情 6-其他
    public String url;

}
