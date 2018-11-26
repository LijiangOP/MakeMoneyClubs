package com.zhengdao.zqb.view.activity.advertisementlist;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.entity.AdvertEntity;
import com.zhengdao.zqb.entity.AdvertiseMentEntity;
import com.zhengdao.zqb.entity.AdvertisementHttpEntity;
import com.zhengdao.zqb.entity.HttpResult;
import com.zhengdao.zqb.event.ApkInstallEvent;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.toutiao.TTAdManagerHolder;
import com.zhengdao.zqb.utils.ActivityUtils;
import com.zhengdao.zqb.utils.LogUtils;
import com.zhengdao.zqb.utils.ToastUtil;
import com.zhengdao.zqb.view.activity.login.LoginActivity;
import com.zhengdao.zqb.view.adapter.AdvertisementListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zhengdao.zqb.application.ClientAppLike.AppType;


public class AdvertisementListActivity extends MVPBaseActivity<AdvertisementListContract.View, AdvertisementListPresenter> implements AdvertisementListContract.View {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private ArrayList<AdvertEntity>  mIntegers;
    private AdvertisementListAdapter mAdapter;
    private AdvertiseMentEntity      mHttpAdvert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_list);
        ButterKnife.bind(this);
        setTitle("广告中心");
        init();
    }

    private void init() {
        TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);
        mIntegers = new ArrayList<>();
        //        mIntegers.add(new AdvertEntity(1, AppType == Constant.App.Zqb ? Constant.BaiDuAdv.Text1 : Constant.TencentAdv.advTenCent_ADV_ORIGINAL_ID_1));//百度 腾讯广告
        //        mIntegers.add(new AdvertEntity(1, AppType == Constant.App.Zqb ? Constant.BaiDuAdv.PicAndText2 : Constant.TencentAdv.advTenCent_ADV_ORIGINAL_ID_2));//百度 腾讯广告
        //        mIntegers.add(new AdvertEntity(1, AppType == Constant.App.Zqb ? Constant.BaiDuAdv.PicAndText1 : Constant.TencentAdv.advTenCent_ADV_ORIGINAL_ID_3));//百度 腾讯广告

        //        if (AppType == Constant.App.Zqb)
        mIntegers.add(new AdvertEntity(4, Constant.TouTiaoAdv.XinXiLiu));//头条广告

        mIntegers.add(new AdvertEntity(5, ""));//推啊广告
        //        mIntegers.add(new AdvertEntity(6, ""));//金橙广告

        mAdapter = new AdvertisementListAdapter(AdvertisementListActivity.this, mIntegers, new AdvertisementListAdapter.CallBack() {
            @Override
            public void onAdvClick(int position) {
                switch (mIntegers.get(position).getType()) {
                    case 1:
                        mPresenter.getSeeAdvReward(4, AppType == Constant.App.Zqb ? 1 : 2);
                        break;
                    case 2:
                        mPresenter.getSeeAdvReward(4, 4);
                        break;
                    case 3:
                        if (mHttpAdvert != null)
                            ActivityUtils.doSkip(AdvertisementListActivity.this, mHttpAdvert.type, mHttpAdvert.url, mHttpAdvert.id);
                        break;
                }
            }

            @Override
            public void onAdvInstall(String packageName) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mPresenter.getAdvReplace(4);
    }


    /**
     * 接收到广播 上报广告apk安装成功
     *
     * @param apkInstallEvent
     */
    private void getAPIadv(ApkInstallEvent apkInstallEvent) {
        //        File file = new File(FileUtils.getAppDownloadPath(mFileName));
        //        String packageNameByFile = AppUtils.getPackageNameByFile(getActivity(), file);
        //        if (!TextUtils.isEmpty(apkInstallEvent.uri) && !TextUtils.isEmpty(packageNameByFile) && packageNameByFile.equals(apkInstallEvent.uri)) {
        //            Log.i("Banana", "packageName=" + packageNameByFile);
        //            if (mI_rpt != null && mI_rpt.size() > 0) {
        //                for (String value : mI_rpt) {
        //                    if (!TextUtils.isEmpty(value)) {
        //                        if (value.contains("SZST_CLID") && !TextUtils.isEmpty(mReplaceValue))
        //                            value = value.replace("SZST_CLID", mReplaceValue);
        //                        BaiDuAPiAdvUtils.ReportAdv(value);
        //                    }
        //                }
        //            }
        //        }
    }

    @Override
    public void onGetAdvReward(HttpResult httpResult) {
        if (httpResult.code == Constant.HttpResult.SUCCEED) {
            if (httpResult != null) {
                Double money = httpResult.money;
                if (money == null || money == 0)
                    ToastUtil.showToast(AdvertisementListActivity.this, "获取奖励成功");
                else
                    ToastUtil.showToast(AdvertisementListActivity.this, "奖励" + httpResult.money + "元");
            }
        } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
            LogUtils.e("登录超时");
        } else {
            LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "请求失败" : httpResult.msg);
        }
    }

    @Override
    public void onGetAdvReplace(final AdvertisementHttpEntity httpResult) {
        try {
            if (httpResult.code == Constant.HttpResult.SUCCEED) {
                mHttpAdvert = httpResult.advert;
                if (mHttpAdvert != null) {
                    String imgPath = httpResult.advert.imgPath;
                    if (!TextUtils.isEmpty(imgPath)) {
                        mIntegers.add(new AdvertEntity(3, imgPath));//自己的广告
                        if (mAdapter != null)
                            mAdapter.notifyDataSetChanged();
                    }
                }
            } else if (httpResult.code == Constant.HttpResult.RELOGIN) {
                ToastUtil.showToast(AdvertisementListActivity.this, "登录超时,请重新登录");
                startActivity(new Intent(AdvertisementListActivity.this, LoginActivity.class));
            } else if (httpResult.code == Constant.HttpResult.FAILD) {
                LogUtils.e(TextUtils.isEmpty(httpResult.msg) ? "数据请求失败" : httpResult.msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
