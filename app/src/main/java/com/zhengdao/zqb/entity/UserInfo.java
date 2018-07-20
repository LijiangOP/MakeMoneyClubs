package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/28 16:18
 */
public class UserInfo {

    /**
     * vip			//等级
     * nickname		//昵称
     * phone			//用户手机号
     * inviteCode		//用户邀请码
     * token			//用户token
     * avatar           //头像
     * referee			//用户渠道
     * editCount        //名字修改次数
     * email            //邮箱
     */
    public String vip;
    public String nickName;
    public String userName;
    public String phone;
    public String inviteCode;
    public String token;
    public String avatar;
    public String referee;
    public String editCount;
    public String email;
    public long   id;
    public int    type;//0普通 1自营
}
