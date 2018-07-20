package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/9 10:25
 */
public class ShareHttpEntity {
    /**
     * {
     * "uploadUrl": "http://app.zqb88.cn/upload/files/app/app-zqb-release.apk",  //下载链接
     * "phone": "1581",                                                             //手机号码
     * "QRcode": "http://192.168.1.102:8088/upload/images/QRcode/L46CEy.jpg",  //分享二维码
     * "inviteCode": "http://zqb88.cn/i/23sDxf",                                      //跳转地址
     * “icon:”http://app.zqb88.cn/upload/images/icons/icon.png”          //APP图标
     * "code": 0,
     * "msg": "操作成功"
     * }
     */
    public int    code;
    public String msg;
    public String uploadUrl;
    public String icon;
    public String phone;
    public String QRcode;
    public String nickName;
    public String inviteCode;
}
