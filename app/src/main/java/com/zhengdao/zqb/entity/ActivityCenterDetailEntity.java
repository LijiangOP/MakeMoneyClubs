package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/14 10:17
 */
public class ActivityCenterDetailEntity {
    /**
     * "id": 1,                                   //广告ID
     * "imgPath": "123.jpg",                     //广告图片
     * "title": "test",                             //广告标题
     * "type": 0,                                //广告类型
     * "url": "11123"                           //广告链接
     * "startTime": "2018-05-14 11:26:48"      //活动时间
     * "state": 1                              //广告状态 0进行中 1已结束
     */
    public int    id;
    public String imgPath;
    public String title;
    public int    type;
    public String url;
    public String startTime;
    public String endTime;
    public int    state;
}
