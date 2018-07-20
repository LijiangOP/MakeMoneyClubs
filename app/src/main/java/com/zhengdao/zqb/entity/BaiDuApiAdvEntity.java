package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/7/4 17:32
 */
public class BaiDuApiAdvEntity {
    public Cnf cnf;

    public class Cnf {

        public Dgfly dgfly;
    }

    public class Dgfly {
        public ArrayList<String> a_rpt;
        public ArrayList<String> ad_img;
        public String            ad_pack;
        public String            ad_ver;
        public String            adtype;
        public ArrayList<String> c_rpt;
        public ArrayList<String> d_rpt;
        public ArrayList<String> dc_rpt;
        public String            desc;
        public String            down_url;
        public String            dplnk;
        public ArrayList<String> i_rpt;
        public String            icon_img;
        public String            name;
        public boolean           rtp1;
        public ArrayList<String> s_rpt;
        public String            show_type;
    }
}
