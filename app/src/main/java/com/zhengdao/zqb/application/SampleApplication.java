package com.zhengdao.zqb.application;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * @Author lijiangop
 * @CreateTime 2017/9/15 18:04
 */
public class SampleApplication extends TinkerApplication {

    /**
     * 参数1：tinkerFlags 表示Tinker支持的类型 dex only、library only or all suuport，default: TINKER_ENABLE_ALL
     * 参数2：delegateClassName Application代理类 这里填写你自定义的ApplicationLike
     * 参数3：loaderClassName Tinker的加载器，使用默认即可
     * 参数4：tinkerLoadVerifyFlag 加载dex或者lib是否验证md5，默认为false
     */
    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.zhengdao.zqb.application.ClientAppLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }

    /**
     * 64k的解决方法
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }
}
