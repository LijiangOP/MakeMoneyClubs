package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/11/22 0022 16:50
 */
public class TiEntity {
    String bn;//手机品牌
    String hm;//手机厂商
    String ht;//手机机型
    int    os;//操作系统
    String ov;//操作系统版本
    int    sw;//设备屏宽
    int    sh;//设备屏高

    String ch;//金橙互动分配
    String ei;//设备的唯一标识
    String si;//手机sim卡标识
    String nt;//设备联网类型

    String mac;//Mac地址
    String andid;//ANDROID_ID
    String ip;//终端设备外网的IP地址
    String ua;//手机实际User-Agent值

    int dpi;//手机dpi

    String pkg;//媒体的包名，即本app的包名
    String apnm;//媒体应用名称

    public TiEntity(String bn, String hm, String ht, int os, String ov, int sw, int sh, String ch, String ei, String si,
                    String nt, String mac, String andid, String ip, String ua, int dpi, String pkg, String apnm) {
        this.bn = bn;
        this.hm = hm;
        this.ht = ht;
        this.os = os;
        this.ov = ov;
        this.sw = sw;
        this.sh = sh;
        this.ch = ch;
        this.ei = ei;
        this.si = si;
        this.nt = nt;
        this.mac = mac;
        this.andid = andid;
        this.ip = ip;
        this.ua = ua;
        this.dpi = dpi;
        this.pkg = pkg;
        this.apnm = apnm;
    }
}
