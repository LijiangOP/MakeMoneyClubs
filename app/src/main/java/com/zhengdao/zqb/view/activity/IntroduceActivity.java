package com.zhengdao.zqb.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.umeng.analytics.MobclickAgent;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.animation.AccordionTransformer;
import com.zhengdao.zqb.utils.SettingUtils;
import com.zhengdao.zqb.view.activity.splash.SplashActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/5 10:23
 */
public class IntroduceActivity extends AppCompatActivity {
    public final int ReQuestCode = 007;
    @BindView(R.id.vp)
    ViewPager    mVp;
    @BindView(R.id.ll)
    LinearLayout mLl;
    @BindView(R.id.lan_Iv)
    ImageView    mLanIv;

    private int[] images = {R.mipmap.splash_1, R.mipmap.splash_2,
            R.mipmap.splash_3};
    private List<ImageView>          iamgeList;//图片放置
    public  int                      pointWidth;
    private ScheduledExecutorService scheduledExecutorService;
    private boolean isAlreadyStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_introduce);
        ButterKnife.bind(this);
        doSkip();
        initImageView();
        setCurrentPoint(0);
        mLanIv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                pointWidth = mLl.getChildAt(1).getLeft() - mLl.getChildAt(0).getLeft();
            }
        });
        mVp.setAdapter(new MyAdapter());
        mVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                setCurrentPoint(position);
                doStartAtivity();
            }

            @Override
            public void onPageScrolled(int position, float offset, int arg2) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mLanIv.getLayoutParams();
                layoutParams.leftMargin = (int) (pointWidth * offset + position * pointWidth);
                mLanIv.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mVp.setPageTransformer(true, new AccordionTransformer());
    }

    /**
     *
     */
    private void doSkip() {
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }
        if (!SettingUtils.isFristInstall(this)) {
            startActivity(new Intent(this, SplashActivity.class));
            this.finish();
        }
    }

    private void initImageView() {
        iamgeList = new ArrayList<ImageView>();
        for (int i = 0; i < images.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            iamgeList.add(imageView);
            ImageView huiImageView = new ImageView(this);
            huiImageView.setImageResource(R.drawable.introduce_gray_point);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            huiImageView.setLayoutParams(layoutParams);
            if (i > 0)
                layoutParams.leftMargin = 20;
            mLl.addView(huiImageView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        //        scheduledExecutorService.scheduleWithFixedDelay(new ViewPagerTask(), 2, 2, TimeUnit.SECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void setCurrentPoint(int position) {
        for (int i = 0; i < iamgeList.size(); i++) {
            ImageView imageView = (ImageView) mLl.getChildAt(i);
            if (i == position)
                imageView.setImageResource(R.drawable.introduce_red_point);
            else
                imageView.setImageResource(R.drawable.introduce_gray_point);
        }
    }

    private int     currentItem = 0;
    private Handler handler     = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mVp.setCurrentItem(currentItem);
                    doStartAtivity();
                    break;
            }
        }

    };

    private void doStartAtivity() {
        if (currentItem == (images.length - 1) && !isAlreadyStart) {
            if (scheduledExecutorService != null)
                scheduledExecutorService.shutdown();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(IntroduceActivity.this, SplashActivity.class));
                    IntroduceActivity.this.finish();
                }
            }, 2000);
            isAlreadyStart = true;
        }
    }

    private class ViewPagerTask implements Runnable {

        @Override
        public void run() {
            //更新界面
            currentItem++;
            handler.obtainMessage().sendToTarget();
        }
    }

    /**
     * 适配器
     */
    class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = iamgeList.get(position);
            imageView.setImageResource(images[position]);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
