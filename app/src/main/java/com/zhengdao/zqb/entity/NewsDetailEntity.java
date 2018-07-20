package com.zhengdao.zqb.entity;

import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * @Author lijiangop
 * @CreateTime 2018/6/14 12:08
 */
public class NewsDetailEntity {
    public String author_name;
    public String date;
    public int    id;
    public String realtype;
    public String thumbnail_pic_s;
    public String thumbnail_pic_s02;
    public String thumbnail_pic_s03;
    public String title;
    public String uniquekey;
    public String url;
    public String type;
    public String advId;//百度广告ID

    public int showType;//显示类型 1-没有图片 2.一张图片3.一张以上 9,应用下载 10百度广告 11AdView广告

    public NewsDetailEntity(int showType) {
        this.showType = showType;
    }

    public void setAppEntity(HotRecommendEntity appEntity) {
        this.appEntity = appEntity;
    }

    //应用下载
    public HotRecommendEntity appEntity;

    public NewsDetailEntity(int showType, HotRecommendEntity appEntity) {
        this.showType = showType;
        this.appEntity = appEntity;
    }

    //广告
    public transient NativeExpressADView nativeAd;

    public NewsDetailEntity(int showType, String advId) {
        this.showType = showType;
        this.advId = advId;
    }

    @Override
    public String toString() {
        return "NewsDetailEntity{" +
                "author_name='" + author_name + '\'' +
                ", date='" + date + '\'' +
                ", id=" + id +
                ", realtype='" + realtype + '\'' +
                ", thumbnail_pic_s='" + thumbnail_pic_s + '\'' +
                ", thumbnail_pic_s02='" + thumbnail_pic_s02 + '\'' +
                ", thumbnail_pic_s03='" + thumbnail_pic_s03 + '\'' +
                ", title='" + title + '\'' +
                ", uniquekey='" + uniquekey + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", showType=" + showType +
                ", appEntity=" + appEntity +
                '}';
    }
}
