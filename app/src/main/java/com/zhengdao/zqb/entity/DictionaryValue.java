package com.zhengdao.zqb.entity;

/**
 * @Author lijiangop
 * @CreateTime 2018/5/9 14:52
 */
public class DictionaryValue {
    /**
     * "icon": "",
     * "id": 158,
     * "key": "INTENT_CONSUME",
     * "option1": 0,
     * "option2": "",
     * "option3": "",
     * "rewardList": [],
     * "state": 0,
     * "switchs": 0,
     * "type": 0,
     * "value": "女装"
     */

    public int    id;
    public String key;
    public String value;
    public int    state;
    public int    option1;
    public String option2;
    public String option3;

    @Override
    public String toString() {
        return "Value{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", state=" + state +
                ", option1=" + option1 +
                ", option2='" + option2 + '\'' +
                ", option3='" + option3 + '\'' +
                '}';
    }
}
