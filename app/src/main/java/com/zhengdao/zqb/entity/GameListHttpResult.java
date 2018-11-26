package com.zhengdao.zqb.entity;

import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/10/19 0019 15:21
 */
public class GameListHttpResult {
    public int             code;
    public String          msg;
    public ArrayList<Game> games;

    public class Game {
        /**
         * "icon": "http://app.zqb88.cn/upload/icon/menu/icon_game.png",
         * "id": 106,
         * "key": "MENU_INFO",
         * "option1": 0,
         * "option2": "",
         * "option3": "",
         * "rewardList": [],
         * "state": 0,
         * "switchs": 0,
         * "type": 2,
         * "value": "闲玩游戏"
         */
        public String icon;
        public String key;
        public int    id;
        public int    option1;
        public String option2;
        public String option3;
        public int    state;
        public int    switchs;
        public int    type;
        public String value;

    }
}
