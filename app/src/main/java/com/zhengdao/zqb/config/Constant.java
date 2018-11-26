package com.zhengdao.zqb.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author lijiangop
 * @CreateTime 2017/9/12 17:50
 */
public class Constant {
    public static final String  APP_NAME = "make_money_clubs";
    public static       boolean isDebug  = true;

    /**
     * TODO 服务器地址
     */
    public class Url {
        //        public static final String BASEURL = "http://192.168.1.105:8088/zqb/";//本地测试
        //        public static final String BASEURL = "http://myweb.iok.la:19616/";//本地测试映射
        public static final String BASEURL = "http://app.zqb88.cn/"; //线上URL
    }

    public class SP {
        public static final String IS_FRIST_INSTALL                  = "is_frist_install";
        public static final String IS_LOGIN                          = "isLogin";
        public static final String USER_ID                           = "user_id";
        public static final String USER_TOKEN                        = "userToken";
        public static final String IMG_SETTING                       = "OnlyWifiLoadImg";
        public static final String PHONE_NUM                         = "phoneNum";
        public static final String TOTAL_INCOME                      = "total_income";
        public static final String ACCOUNT                           = "account";
        public static final String ALIPAYACCOUNT                     = "alipayaccount";
        public static final String MESSAGECOUNT                      = "messagecount";
        public static final String REWARDMESSAGECOUNT                = "rewardmessagecount";
        public static final String ACCOUNTTYPE                       = "accounttype";
        public static final String CURRENTCALENDAR                   = "currentcalendar";
        public static final String CURRENT_ALIPAY_CALENDAR           = "current_alipay_calendar";
        public static final String CURRENT_RECOMMEND_REWARD_CALENDAR = "current_recommend_reward_calendar";
        public static final String CURRENT_INTRODUCE_CALENDAR        = "current_introduce_calendar";
        public static final String RECEIVE_COUNT                     = "receive_count";
        public static final String IGNORE                            = "IGNORE";
        public static final String WECHAT_TOKEN                      = "wechat_token";
        public static final String REFRESH_TOKEN                     = "refresh_token";
        public static final String WECHAT_OPEN_ID                    = "wechat_open_id";
        //三属性
        public static final String USER_ADDRESS_SET                  = "user_address_set";
        public static final String USER_SEX                          = "user_sex";
        public static final String USER_BIRTHDAY                     = "user_birthday";
    }

    public class Third {
        public static final int LOGIN_TYPE_QQ     = 1;
        public static final int LOGIN_TYPE_WECHAT = 2;
    }

    public class Assist {
        public static final String mHttpRewardPicPath = "http://app.zqb88.cn/upload/images/rewardImages/.+?\\.\\w{3}";
        public static final String mCommonPicPath     = "/storage/emulated/0/.+?\\.\\w{3}";
    }

    public static ArrayList<Integer> EditableList = new ArrayList<>();


    public class Wallet {
        public static final int TYPE_REDPACKET     = 001;//红包
        public static final int TYPE_BROKERAGE     = 003;//佣金
        public static final int TYPE_INTEGRAL      = 004;//积分
        public static final int TYPE_CHARGERECORD  = 005;//充值记录
        public static final int TYPE_BAR_VALUE     = 006;//吧值
        public static final int TYPE_ADVERTISEMENT = 007;//广告
    }

    public class Download {
        public static final String ID           = "id";
        public static final String URL          = "url";
        public static final String MD5          = "MD5";
        public static final String POSITION     = "position";
        public static final String FILE_NAME    = "file_name";
        public static final String MUST_UPDATE  = "MUST_UPDATE";
        public static final String PACKAGE_NAME = "package_name";
    }

    public class HttpResult {
        public static final int    SUCCEED          = 0;
        public static final int    FAILD            = 1;
        public static final int    RELOGIN          = -2;
        public static final int    DATANULL         = 2;
        public static final int    DATAFAILD        = -1;
        public static final int    TIMEOUT          = -3;
        public static final int    CHARGE_NO_PERMIT = 3;
        public static final int    ELSE             = 233;
        public static final String Collect          = "addColl";
        public static final String Attention        = "addfollow";
        public static final String Browse           = "addBrowse";
    }

    public class Activity {
        public static final String Skip      = "skip";
        public static final String Url       = "url";
        public static final String Select    = "select";
        public static final String Type      = "type";
        public static final String Type1     = "type1";
        public static final String FreedBack = "freedback";
        public static final String SkipData  = "skipdata";
        public static final String Data      = "data";
        public static final String Data1     = "data1";
        public static final String Data2     = "data2";
        public static final String Data3     = "data3";
        public static final String Price     = "price";
        public static final String Common    = "common";
        public static final String State     = "state";
        public static final String Replace   = "replace";
    }

    public class Fragment {
        public static final String Param  = "param";
        public static final String Param1 = "param_1";
    }

    public class WebView {
        public static final String TITLE = "title";
        public static final String HTML  = "html";
        public static final String URL   = "url";
        public static final String TOKEN = "token";
        public static final String ID    = "id";
    }

    public static class Data {
        public static final Set<Integer>      BrowsingHistoryEditSet = new HashSet<>();
        public static final ArrayList<String> InvitedCodeList        = new ArrayList<>();
    }

    public class IP {
        public static final String Guide   = "";
        public static final String Invited = "http://app.zqb88.cn/recommend/recode";
        public static final String Fiction = "https://m.405169.com/?u=4896286&qp=4896286";
        public static final String Survey  = "http://192.168.1.105:8088/zqb/questionnaire/goQuestionnairePage";
    }

    public class Pay {
        public static final int Balance   = 1;
        public static final int AliPay    = 2;
        public static final int WechatPay = 3;
    }

    public class Block {
        public static final int All           = 0;
        public static final int ZeroEarn      = 69;
        public static final int Discounts     = 70;
        public static final int Rebate        = 71;
        public static final int Entertainment = 72;

    }

    public class Upload {
        public static final int Type_Wanted        = 1;
        public static final int Type_Avator        = 2;
        public static final int Type_Task          = 3;
        public static final int Type_Advice_Report = 4;
        public static final int Type_Comment       = 5;
    }

    public class Wanted {
        public static final int DEFAULT_BRAND_NEW_TYPE = -7;
        public static final int STATE_ALL              = -1;
        public static final int STATE_SAVE             = 0;//暂存
        public static final int STATE_CHECKING         = 1;//审核中
        public static final int STATE_PUBLISHED        = 2;//已发布
        public static final int STATE_FINISHED         = 3;//已结束
        public static final int STATE_SOLDING_OUT      = 4;//下架中
        public static final int STATE_SOLDED_OUT       = 5;//已下架
    }

    public class WechatReq {
        public static final String Loginstate                 = "diandi_wx_login";
        public static final String Bindstate                  = "diandi_wx_bind";
        public static final String DailyShare                 = "daily_share";
        public static final String WelfareShare               = "welfareshare";
        public static final String WELFARESHARE_WECHAT_CIRCLE = "welfareshare_wechat_circle";
        public static final String WELFARESHARE_WECHAT        = "welfareshare_wechat";
        public static final String WELFARESHARE_QQ_CIRCLE     = "welfareshare_qq_circle";
        public static final String WELFARESHARE_QQ            = "welfareshare_qq";
    }

    public class BaiDuAdv {
        public static final String UserCenterTop          = "5818459";//个人中心横幅图文
        public static final String UserCenterBottom       = "5818258";//首页横幅图片
        public static final String Rebate                 = "5818460";//返利横幅图文
        public static final String Splash                 = "5822817";//首页开屏广告
        public static final String Text1                  = "5850871";//文字广告
        public static final String Text2                  = "5927414";//文字广告
        public static final String Text3                  = "5927415";//文字广告
        public static final String PicAndText             = "5846013";//横幅图文类型（看点模块）
        public static final String PicAndText1            = "5850868 ";//横幅图文类型（看点模块）
        public static final String PicAndText2            = "5850869";//横幅图文类型（看点模块）
        public static final String ContentAlliance        = "https://cpu.baidu.com/1001/c4636b86?scid=16003";//内容联盟url
        public static final String ContentAllianceReplace = "http://app.zqb88.cn//zhaoshang";//内容联盟位置的替换url
    }

    public class AdvPosition {
        /**
         * address  显示位置 0-app首页 1-微站 2-邀请好友 3-活动弹框 4-发现 5-我的 6-账户中心 7签到 8活动中心 9卡券列表 10网盟广告
         */
        public static final int PositionsHome      = 0;
        public static final int PositionsWebPhone  = 1;
        public static final int PositionsInvite    = 2;
        public static final int PositionsActivity  = 3;
        public static final int PositionsDiscovery = 4;
        public static final int PositionsMine      = 5;
        public static final int PositionsAccount   = 6;
        public static final int PositionsSign      = 7;
    }

    /**
     * 跳转类型 type -1-不做跳转 0-跳转URL 1-新手任务 2-赚钱大厅 3-邀请好友 4-浏览器加载 5-悬赏详情 6-活动中心 7-卡券列表 8-签到
     */
    public class Skip {
        public static final int Type_None  = -1;
        public static final int Type_Zero  = 0;
        public static final int Type_One   = 1;
        public static final int Type_Tow   = 2;
        public static final int Type_Three = 3;
        public static final int Type_Four  = 4;
        public static final int Type_Five  = 5;
        public static final int Type_Six   = 6;
        public static final int Type_Seven = 7;
        public static final int Type_Eight = 8;
    }


    public class TencentAdv {
        public static final String advTenCent_APPID             = "1106869363";
        public static final String advTenCent_ADV_SPLASH_ID     = "8020739466098663";
        public static final String advTenCent_ADV_BANNER_ID     = "9090731496993684";
        public static final String advTenCent_ADV_ORIGINAL_ID_1 = "6010439476391602";
        public static final String advTenCent_ADV_ORIGINAL_ID_2 = "6080033477977274";
        public static final String advTenCent_ADV_ORIGINAL_ID_3 = "4020130418751845";
    }

    public class App {
        public static final int Zqb      = 1;
        public static final int Wlgfl    = 2;
        public static final int Wlgfl_hy = 3;
        public static final int Lczj     = 4;
    }

    public class ADVIEW {
        public static final String KeySet = "SDK20180827080645jpg34xju1yceqhc";
        public static final String HTML   = "<meta charset='utf-8'><style type='text/css'>* { padding: 0px; margin: 0px;}a:link { text-decoration: none;}</style><div  style='width: 100%; height: 100%;'><img src=\"image_path\" width=\"100%\" height=\"100%\" ></div>";
    }

    public class Coupons {
        public static final int TaoBao   = 0;
        public static final int JinDong  = 1;
        public static final int PingDD   = 2;
        public static final int SuNingYG = 3;
        public static final int WangYiKL = 4;

        public static final int Head  = 7;
        public static final int Empty = 8;
    }

    /**
     * 闲玩
     */
    public class XianWan {
        //赚钱吧
        public static final String Appid      = "1860";
        public static final String AppSecret  = "348tylimpc3u3hfb";
        //兼职呗
        public static final String Appid1     = "2010";
        public static final String AppSecret1 = "uiwf16rkxk89gujy";
    }

    /**
     * API广告
     */
    public class APIADV {
        public static final String ACCOUNT = "AD058";
    }

    public class AnZhiAdv {
        public static final String AppKey       = "9f346d3926df57f95e7f8c2c7a37dc8a";
        public static final String AdvId_Banner = "1375";
        public static final String AdvId_Native = "1374";

    }

    public class Flow {
        public static final int LeftMargin = 100;
    }

    public class YouLe {
        /**
         * https://wx.iskyshow.cn/youle-api/page/index.html#/FansCenter?channel_id=259357&user_code=862630032882681&post_type=3&keycode=c5f26f566f42410148b02fba1bd56e92&app_user_id=1
         */
        public static final String BASE_URL         = "https://wx.iskyshow.cn/youle-api/page/index.html#/FansCenter?";
        public static final String YOULE_KEY        = "a0a1d410-x55-4240-9b7d-51b669678900";
        public static final String YOULE_CHANNEL_ID = "259357";
        public static final String YOUELE_APPSECRET = "YLUAT1809";
    }

    public class XiangWan {
        public static final String BASE_URL = "http://ifsapp.pceggs.com/Pages/IntegralWall/IW_Awall_adList.aspx?";
        public static final String PID      = "10009";
        public static final String APP_KEY  = "ZQBXW8_TXW_10009";
    }

    public class Vivo {
        public static final String POS_ID = "457f67fea7c74ede99dedd0e38a605df";
    }

    public class TouTiaoAdv {
        public static final String XinXiLiu = "904272638";
    }
}
