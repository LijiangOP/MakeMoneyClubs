package com.zhengdao.zqb.h5;

import android.webkit.JavascriptInterface;

import com.zhengdao.zqb.event.ToShare;
import com.zhengdao.zqb.utils.RxBus;

/**
 * @Author lijiangop
 * @CreateTime 2017/8/23 17:12
 */
public class JsInterface {

    /**
     * 跳添加详情
     *
     * @param url
     */
    @JavascriptInterface
    public void toAddWzRecord(String url) {
        //        RxBus.getDefault().post(new ToAddWzRecord());
    }

    /**
     * 分享类型
     *
     * @param type 1:qq 2qq空间 3微信 4朋友圈
     */
    @JavascriptInterface
    public void toShare(int type) {
        RxBus.getDefault().post(new ToShare(type));
    }
}
