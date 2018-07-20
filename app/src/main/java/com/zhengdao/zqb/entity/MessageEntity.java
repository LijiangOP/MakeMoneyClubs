package com.zhengdao.zqb.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Author lijiangop
 * @CreateTime 2018/1/25 17:15
 */
public class MessageEntity {
    public int     code;
    public String  msg;
    public Message message;

    public class Message {
        public int                            total;
        public boolean                        hasNextPage;
        public ArrayList<MessageDeatilEntity> list;
    }

    public class MessageDeatilEntity implements Serializable {
        public String comFromName;
        public String content;
        public String createTime;
        public String title;
        public int    id;
    }
}
