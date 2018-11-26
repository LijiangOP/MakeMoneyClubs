package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/11/21 0021 11:53
 */
public class JinChengAdvEntity {
    int at;
    String adl;

    int width;
    int height;

    String clk;
    String ati;
    String atx;
    String pack;
    String appname;

    int deeplinktype;

    String deeplink;

    ArrayList<String> std;
    ArrayList<String> dcp;
    ArrayList<String> ital;
    ArrayList<String> ec;
    ArrayList<String> es;
    ArrayList<String> durls;

    int act;

    public int getAt() {
        return at;
    }

    public String getAdl() {
        return adl;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getClk() {
        return clk;
    }

    public String getAti() {
        return ati;
    }

    public String getAtx() {
        return atx;
    }

    public String getPack() {
        return pack;
    }

    public String getAppname() {
        return appname;
    }

    public int getDeeplinktype() {
        return deeplinktype;
    }

    public String getDeeplink() {
        return deeplink;
    }

    public ArrayList<String> getStd() {
        return std;
    }

    public ArrayList<String> getDcp() {
        return dcp;
    }

    public ArrayList<String> getItal() {
        return ital;
    }

    public ArrayList<String> getEc() {
        return ec;
    }

    public ArrayList<String> getEs() {
        return es;
    }

    public ArrayList<String> getDurls() {
        return durls;
    }

    public int getAct() {
        return act;
    }

    @Override
    public String toString() {
        return "JinChengAdvEntity{" +
                "at=" + at +
                ", adl='" + adl + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", clk='" + clk + '\'' +
                ", ati='" + ati + '\'' +
                ", atx='" + atx + '\'' +
                ", pack='" + pack + '\'' +
                ", appname='" + appname + '\'' +
                ", deeplinktype=" + deeplinktype +
                ", deeplink='" + deeplink + '\'' +
                ", std=" + std +
                ", dcp=" + dcp +
                ", ital=" + ital +
                ", ec=" + ec +
                ", es=" + es +
                ", durls=" + durls +
                ", act=" + act +
                '}';
    }
}
