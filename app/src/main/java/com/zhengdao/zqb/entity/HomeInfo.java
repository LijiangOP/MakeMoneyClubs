package com.zhengdao.zqb.entity;


import java.io.Serializable;
import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/22 17:12
 */
public class HomeInfo implements Serializable {
    public int                    code;
    public String                 msg;
    public String                 GAME_STATE;
    public List<BannerBean>       adverts;
    public InvitationBanner       invitationBanner;
    public HomeGoodsItem          newsRewards;//为你推荐
    public List<AnnouncementBean> platformMsg;

    public class InvitationBanner {
        public int    id;
        public String imgPath;
        public String url;
        public String title;
        public int    type;
    }
}