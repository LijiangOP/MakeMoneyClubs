package com.zhengdao.zqb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhengdao.zqb.event.NetworkChangeEvent;
import com.zhengdao.zqb.utils.NetWorkUtil;
import com.zhengdao.zqb.utils.RxBus;

/**
 * @Author lijiangop
 * @CreateTime 2017/9/14 11:32
 */
public class NetWorkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkChangeEvent event = new NetworkChangeEvent();
        event.isNetWorkAvailable = NetWorkUtil.isNetWorkAvailable(context);
        event.isWifiConnected = NetWorkUtil.isWifiConnected(context);
        event.is3gConnected = NetWorkUtil.is3gConnected(context);
        RxBus.getDefault().post(event);
    }
}
