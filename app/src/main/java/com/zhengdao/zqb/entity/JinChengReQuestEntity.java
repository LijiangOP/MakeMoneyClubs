package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/11/22 0022 16:39
 */
public class JinChengReQuestEntity {
    int      n;
    String   apv;
    String   bid;
    String   aid;
    int      adt;
    int      adsw;
    int      adsh;
    TiEntity ti;

    public JinChengReQuestEntity(int n, String apv, String bid, String aid, int adt, int adsw, int adsh, TiEntity ti) {
        this.n = n;
        this.apv = apv;
        this.bid = bid;
        this.aid = aid;
        this.adt = adt;
        this.adsw = adsw;
        this.adsh = adsh;
        this.ti = ti;
    }
}
