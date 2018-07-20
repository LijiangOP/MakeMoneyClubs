package com.zhengdao.zqb.entity;

import java.util.Arrays;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/23 17:13
 */
public class WechatUserEntity {
    /**
     * "openid":"OPENID",
     * "nickname":"NICKNAME",
     * "sex":1,
     * "province":"PROVINCE",
     * "city":"CITY",
     * "country":"COUNTRY",
     * "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     * "privilege":[
     * "PRIVILEGE1",
     * "PRIVILEGE2"
     * ],
     * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     */

    public String   city;
    public String   country;
    public String   headimgurl;
    public String   nickname;
    public String   openid;
    public String[] privilege;
    public String   province;
    public int      sex;
    public String   unionid;

    @Override
    public String toString() {
        return "WechatUserEntity{" +
                "city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", headimgurl='" + headimgurl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", openid='" + openid + '\'' +
                ", privilege=" + Arrays.toString(privilege) +
                ", province='" + province + '\'' +
                ", sex=" + sex +
                ", unionid='" + unionid + '\'' +
                '}';
    }
}
