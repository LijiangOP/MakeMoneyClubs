package com.zhengdao.zqb.view.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.SideBar;
import com.zhengdao.zqb.entity.PhoneContactEntity;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.view.adapter.PhoneContactAdapter;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/27 10:22
 */
public class PhoneContactActivity extends AppCompatActivity implements PhoneContactAdapter.ItemCallback {

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.sidebar)
    SideBar      mSidebar;

    private static final String[]                            PHONES_PROJECTION         = new String[]{
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};//获取库Phon表字段
    private static final int                                 PHONES_DISPLAY_NAME_INDEX = 0;//联系人显示名称
    private static final int                                 PHONES_NUMBER_INDEX       = 1;//电话号码
    private static final int                                 PHONES_PHOTO_ID_INDEX     = 2;//头像ID
    private static final int                                 PHONES_CONTACT_ID_INDEX   = 3;//联系人的ID
    private              TreeMap<String, PhoneContactEntity> mTreeMap                  = new TreeMap<>(new Comparator<String>() {
        public int compare(String obj1, String obj2) {
            if (obj1.equals("#") && !obj2.equals("#")) {
                return 1;
            } else if (!obj1.equals("#") && obj2.equals("#")) {
                return -1;
            } else if (!obj1.equals("#") && obj2.equals("#")) {
                return 0;
            } else
                return obj1.compareTo(obj2);
        }
    });
    private              ArrayList<PhoneContactEntity>       mData                     = new ArrayList<>();
    private              ArrayList<String>                   mCaseData                 = new ArrayList<>();
    private PhoneContactAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_phone_contact);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        mSidebar.setOnSideChooseListening(new SideBar.OnSideChooseListening() {
            @Override
            public void onChooseListener(int choose, String text) {//0~26
                int scrollPosition = getScrollPosition(text);
                if (mData.size() > scrollPosition)
                    mRecycleView.scrollToPosition(scrollPosition);
            }
        });
        try {
            getPhoneContacts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mAdapter == null)
            mAdapter = new PhoneContactAdapter(this, mData, this);
        mRecycleView.setAdapter(mAdapter);
        mRecycleView.setLayoutManager(new AdvertiseLinearLayoutManager(this));
        mRecycleView.addItemDecoration(new UserListItemDecoration(new GroupListening() {
            @Override
            public String getNameGroup(int position) {
                return mData.get(position).fristcase;
            }
        }));
    }

    @Override
    public void onItemClick(String string) {
        Intent intent = new Intent();
        intent.putExtra(Constant.Activity.FreedBack, string);
        setResult(RESULT_OK, intent);
        PhoneContactActivity.this.finish();
    }

    public int getScrollPosition(String character) {
        if (mCaseData != null && mCaseData.contains(character)) {
            for (int i = 0; i < mData.size(); i++) {
                if (mData.get(i).fristcase.equals(character)) {
                    return i;
                }
            }
        }
        return -1; // -1不会滑动
    }

    /**
     * 得到手机通讯录联系人信息
     **/
    private void getPhoneContacts() throws Exception {
        ContentResolver resolver = getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
                PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                // 得到联系人名称
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);
                // 得到联系人ID
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                // 得到联系人头像ID
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                // 得到联系人头像Bitamp
                Bitmap contactPhoto = null;
                // photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts
                            .openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else {
                    contactPhoto = BitmapFactory.decodeResource(getResources(),
                            R.drawable.contact_photo);
                }
                String name = TextUtils.isEmpty(contactName) ? "" : contactName;
                String firstSpell = TextUtils.isEmpty(getFirstSpell(name)) ? "#" : getFirstSpell(contactName);
                String Spell = TextUtils.isEmpty(getSpell(name)) ? "#" : getSpell(contactName);
                mTreeMap.put(Spell, new PhoneContactEntity(firstSpell, contactName, phoneNumber, contactPhoto));
                mCaseData.add(firstSpell);
            }
            phoneCursor.close();
            if (mTreeMap.size() > 0) {
                Set<String> keySet = mTreeMap.keySet();
                Iterator<String> iter = keySet.iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    mData.add(mTreeMap.get(key));
                }
            }
        }
    }

    /**
     * 名字转拼音,取首字母
     *
     * @param chinese
     * @return
     */
    public static String getFirstSpell(String chinese) {
        try {
            StringBuffer pybf = new StringBuffer();
            char[] arr = chinese.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            if (arr.length > 0) {
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[0], defaultFormat);
                if (temp != null) {
                    pybf.append(temp[0].charAt(0));
                }
            }
            return pybf.toString().replaceAll("\\W", "").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 名字转拼音大写
     *
     * @param chinese
     * @return
     */
    public static String getSpell(String chinese) {
        try {
            StringBuffer pybf = new StringBuffer();
            char[] arr = chinese.toCharArray();
            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
            defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            if (arr.length > 0) {
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[0], defaultFormat);
                if (temp != null && temp.length > 0 && temp[0].length() > 1) {
                    pybf.append(temp[0]);
                }
            }
            return pybf.toString().replaceAll("\\W", "").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class UserListItemDecoration extends RecyclerView.ItemDecoration {

        private       int            mHeightCommon;
        private       int            mHeightClassify;
        private       Paint          mPaint;
        private       GroupListening mGroupListening;
        private final Paint          mTextPaint;

        public UserListItemDecoration(GroupListening groupListening) {
            super();
            mHeightCommon = DensityUtil.dip2px(PhoneContactActivity.this, 2);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setColor(Color.parseColor("#f2f2f2"));
            mGroupListening = groupListening;
            mHeightClassify = DensityUtil.dip2px(PhoneContactActivity.this, 20);

            mTextPaint = new Paint();
            mTextPaint.setTextSize(DensityUtil.dip2px(PhoneContactActivity.this, 12));
            mTextPaint.setColor(Color.parseColor("#424242"));
            mTextPaint.setAntiAlias(true);
        }

        /**
         * 计算偏移量
         *
         * @param outRect 包裹item的矩形，默认为0
         * @param view    item
         * @param parent  recyclerView
         * @param state   recyclerView的状态
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            boolean newGroup = isNewGroup(position);
            if (newGroup) {
                outRect.top = mHeightClassify;
            } else {
                outRect.top = mHeightCommon;
            }
        }

        /**
         * 绘制分割线
         *
         * @param c
         * @param parent
         * @param state
         */
        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int left = parent.getLeft();
            int right = parent.getRight();
            for (int i = 0; i < parent.getChildCount(); i++) {
                View childAt = parent.getChildAt(i);
                int bottom = childAt.getTop();
                int top = bottom - mHeightCommon;
                c.drawLine(left, top, right, bottom, mPaint);
            }
        }

        /**
         * 绘制最上层动画
         *
         * @param c
         * @param parent
         * @param state
         */
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            int childCount = parent.getChildCount();
            int left = parent.getLeft();
            int right = parent.getRight();
            String curGroupName = null;
            String preGroupNam;
            for (int i = 0; i < childCount; i++) {
                View childView = parent.getChildAt(i);
                //position与i不一致，因为当前集合只有现在屏幕中显示的个数，i是集合在屏幕中的索引，索引要转为position
                int position = parent.getChildAdapterPosition(childView);
                preGroupNam = curGroupName;
                curGroupName = mGroupListening.getNameGroup(position);
                //和上一个条目在同一组就不处理，不用isNewGroup()去判断是因为滑动之后该函数重新调用，preGroupNam默认为null，不会continue,(集合是显示在屏幕中的条目，recyclerView的回收复用机制)
                // 可是如果使用isNewGroup，可以得到上一条条目，可能会continue，顶部的吸顶动画就没有了
                //即显示的第一条应该没有上一条数据，才能显示吸顶的item
                if (TextUtils.isEmpty(curGroupName) || curGroupName.equals(preGroupNam)) {
                    continue;
                }
                int bottom = childView.getBottom();
                //使item吸顶的核心方法
                int top = Math.max(mHeightClassify, childView.getTop());
                if (position + 1 < state.getItemCount()) { //必须判断，防止角标越界
                    String nextNameGroup = mGroupListening.getNameGroup(position + 1);
                    if (!curGroupName.equals(nextNameGroup) && bottom < top) {
                        top = bottom;
                    }
                }
                Rect rect = new Rect(left, top - mHeightClassify, right, top);
                c.drawRect(rect, mPaint);
                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                float baseLine = top - (mHeightClassify - (fontMetrics.bottom - fontMetrics.top)) / 2 - fontMetrics.bottom;
                c.drawText(curGroupName, 10 + DensityUtil.px2dip(PhoneContactActivity.this, 40), baseLine, mTextPaint);
            }
        }

        private boolean isNewGroup(int position) {
            if (position == 0) {
                return true;
            }
            String newName = mGroupListening.getNameGroup(position);
            String oldName = mGroupListening.getNameGroup(position - 1);
            return !newName.equals(oldName);
        }
    }

    public interface GroupListening {
        String getNameGroup(int position);
    }

    class AdvertiseLinearSmoothScroller extends LinearSmoothScroller {

        public AdvertiseLinearSmoothScroller(Context context) {
            super(context);
        }

        /**
         * @param viewStart      RecyclerView的top位置
         * @param viewEnd        RecyclerView的Bottom位置
         * @param boxStart       item的top位置
         * @param boxEnd         item的bottom位置
         * @param snapPreference 滑动方向的识别
         * @return
         */
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return boxStart - viewStart;//返回的就是我们item置顶需要的偏移量
        }

        /**
         * 此方法返回滚动每1px需要的时间,可以用来控制滚动速度
         * 即如果返回2ms，则每滚动1000px，需要2秒钟
         *
         * @param displayMetrics
         * @return
         */
        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return super.calculateSpeedPerPixel(displayMetrics);
        }
    }

    class AdvertiseLinearLayoutManager extends LinearLayoutManager {
        public AdvertiseLinearLayoutManager(Context context) {
            super(context);
        }

        public AdvertiseLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public AdvertiseLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
            AdvertiseLinearSmoothScroller linearSmoothScroller =
                    new AdvertiseLinearSmoothScroller(recyclerView.getContext());
            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }
}
