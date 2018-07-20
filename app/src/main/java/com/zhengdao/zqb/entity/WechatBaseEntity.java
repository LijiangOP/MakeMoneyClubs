package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/4/23 16:07
 */
public class WechatBaseEntity {
    /**
     * {
     * "access_token": "9_ZI86wLRxcnqTdbDGtd5aYj4VDSQpAN_VID5KMRK93yk0rq29PuvEU40NMDEENftPfrIsSSg7dhUXpNh3pelHo_N2RAxylBVF5C2qNldeiLA",
     * "expires_in": 7200,
     * "openid": "obt081YmDaoMMl410515kLFBPDqc",
     * "refresh_token": "9_bvQdsiJO6ViylGj-mmVzTcmRRO8y1QpbSl6vG09NSfvsAeyvV1IUsSUCLyIxCvLouL74mPJPfH3teHWWHKk8GRvKyB3lvLFBJTLysbRCFiE",
     * "scope": "snsapi_userinfo",
     * "unionid": "o0-G61MCoaeWMfYM-nySuHZtA2vk"
     * }
     */
    public String access_token;
    public String openid;
    public int    expires_in;
    public String refresh_token;
    public String scope;
    public String unionid;

    @Override
    public String toString() {
        return "WechatBaseEntity{" +
                "access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                ", openid='" + openid + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", scope='" + scope + '\'' +
                ", unionid='" + unionid + '\'' +
                '}';
    }
}
