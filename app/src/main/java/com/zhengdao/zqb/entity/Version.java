package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/10 09:46
 */
public class Version {
    /**
     * "id":8,                     //版本id
     * "insideVersion":"4",		//内部版本号
     * "clientVersion":"1",        //版本号
     * "downloadApp":"1",        //安卓下载地址
     * "downloadIos":"1",	//ios下载地址
     * "appId":"4",            //更新编码
     * "updateInstall":0,	//是否强制升级0否，1是
     * "cotent":"1",		//更新内容
     * "addTime":1486979167000},  //添加时间
     * md5 : //md5校验码
     */
    public String addTime;
    public String appId;
    public String clientVersion;
    public String cotent;
    public String downloadApp;
    public int    id;
    public int    insideVersion;
    public String mD5;
    public int    updateInstall;

    public Version() {
    }

    public Version(String addTime, String appId, String clientVersion, String cotent, String downloadApp, int id, int insideVersion, String d5, int updateInstall) {
        this.addTime = addTime;
        this.appId = appId;
        this.clientVersion = clientVersion;
        this.cotent = cotent;
        this.downloadApp = downloadApp;
        this.id = id;
        this.insideVersion = insideVersion;
        mD5 = d5;
        this.updateInstall = updateInstall;
    }
}
