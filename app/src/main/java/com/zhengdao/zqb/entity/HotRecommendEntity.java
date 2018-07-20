package com.zhengdao.zqb.entity;

import java.io.Serializable;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/11 16:55
 */
public class HotRecommendEntity implements Serializable {
    /**
     * "appName": "赚钱吧",                       //app名称
     * "content": "万人赚钱就等你来",               //内容
     * "createTime": "2018-05-14 11:26:48",        //创建时间
     * "download": "http://app.zqb88.cn/upload/", //下载链接
     * "id": 2,
     * "logo": "http://app.zqb88.cn/upload/images/",//AppLogo
     */

    public String appName;
    public String content;
    public String createTime;
    public String packageName;
    public String download;
    public String logo;
    public String mD5;
    public int    id;
    public int state = 0;//0是未下载 1是下载中 2下载未安装 3安装未打开

    public void setState(int state) {
        this.state = state;
    }
}
