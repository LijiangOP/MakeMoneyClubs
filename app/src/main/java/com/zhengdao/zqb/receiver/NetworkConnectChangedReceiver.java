package com.zhengdao.zqb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zhengdao.zqb.event.NetworkChangeEvent;
import com.zhengdao.zqb.utils.NetWorkUtil;
import com.zhengdao.zqb.utils.RxBus;

/**
 * @创建者 cairui
 * @创建时间 2016/12/1 10:55
 * @描述 ${des}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class NetworkConnectChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkChangeEvent event = new NetworkChangeEvent();
        event.isNetWorkAvailable = NetWorkUtil.isNetWorkAvailable(context);
        event.isWifiConnected = NetWorkUtil.isWifiConnected(context);
        event.is3gConnected = NetWorkUtil.is3gConnected(context);
        RxBus.getDefault().post(event);
    }
}
