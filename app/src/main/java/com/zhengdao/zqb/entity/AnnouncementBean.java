package com.zhengdao.zqb.entity;

import java.io.Serializable;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/3 10:52
 */
public class AnnouncementBean implements Serializable {
    /**
     * "addTime": 0,
     * "createTime": "2018-07-12 15:59:25",
     * "findState": "",
     * "fundMode": 0,
     * "id": 0,
     * "inAmount": 0.92,
     * "logDesc": "",
     * "nickName": "Kay",
     * "outAmount": 0,
     * "trader": 0,
     * "usableSum": 0,
     * "userId": 0
     */
    public long   addTime;
    public String createTime;
    public long   agoTime;
    public String findState;
    public long   fundMode;
    public int    id;
    public Double inAmount;
    public Double outAmount;
    public Double usableSum;
    public String logDesc;
    public String nickName;
    public long   trader;
    public int    userId;
}
