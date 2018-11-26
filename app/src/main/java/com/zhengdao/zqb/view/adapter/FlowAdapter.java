package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhengdao.zqb.R;
import com.zhengdao.zqb.config.Constant;
import com.zhengdao.zqb.customview.BigImageDialog;
import com.zhengdao.zqb.utils.DensityUtil;
import com.zhengdao.zqb.utils.ImageUtils;
import com.zhengdao.zqb.utils.SelectableTextUtils.SelectableTextHelper;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Author lijiangop
 * @CreateTime 2018/9/1 0001 16:01
 */
public class FlowAdapter extends CommonAdapter<String> {
    private int[] mScreenSize;

    public FlowAdapter(Context context, int resId, List<String> data) {
        super(context, resId, data);
        mScreenSize = DensityUtil.getScreenSize(context);
    }

    @Override
    protected void convert(ViewHolder viewHolder, String value, int i) {
        try {
            if (!TextUtils.isEmpty(value)) {
                TextView textView = viewHolder.getView(R.id.text);
                URLImageParser imageGetter = new URLImageParser(textView);
                textView.setText(Html.fromHtml(value, imageGetter, new DetailTagHandler(mContext, textView.getTextColors())));
                textView.setClickable(true);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                new SelectableTextHelper.Builder(textView)
                        .setSelectedColor(mContext.getResources().getColor(R.color.selected_blue))
                        .setCursorHandleSizeInDp(20)
                        .setCursorHandleColor(mContext.getResources().getColor(R.color.cursor_handle_color))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class URLImageParser implements Html.ImageGetter {
        TextView mTextView;

        public URLImageParser(TextView textView) {
            this.mTextView = textView;
        }

        @Override
        public Drawable getDrawable(String source) {
            final UrlDrawable urlDrawable = new UrlDrawable();
            Glide.with(mContext).load(source).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    int[] imageScale = ImageUtils.getImageScale(resource);
                    int mShowImgWidth = (mScreenSize[0] - DensityUtil.dip2px(mContext, 23) - Constant.Flow.LeftMargin) / 3;
                    int mShowImgHeight = mShowImgWidth * imageScale[1] / imageScale[0];
                    Drawable drawable = new BitmapDrawable(resource);
                    drawable.setBounds(0, 0, mShowImgWidth, mShowImgHeight);
                    urlDrawable.setBounds(0, 0, mShowImgWidth, mShowImgHeight);
                    urlDrawable.setDrawable(drawable);
                    mTextView.invalidate();
                    mTextView.setText(mTextView.getText());
                }
            });
            return urlDrawable;
        }

        public class UrlDrawable extends BitmapDrawable {
            private Drawable drawable;

            @Override
            public void draw(Canvas canvas) {
                if (drawable != null)
                    drawable.draw(canvas);
            }

            public void setDrawable(Drawable drawable) {
                this.drawable = drawable;
            }
        }
    }

    public class DetailTagHandler implements Html.TagHandler {
        private Context           context;
        private ArrayList<String> strings;
        private int startIndex = 0;
        private int stopIndex  = 0;
        private ColorStateList mOriginColors;
        final HashMap<String, String> attributes = new HashMap<String, String>();

        public DetailTagHandler(Context context, ColorStateList originColors) {
            this.context = context;
            strings = new ArrayList<>();
            mOriginColors = originColors;
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            try {
                // 处理标签<img>
                if ("img".equals(tag.toLowerCase(Locale.getDefault()))) {
                    // 获取长度
                    int len = output.length();
                    // 获取图片地址
                    ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                    String imgURL = images[0].getSource();
                    // 记录所有图片地址
                    strings.add(imgURL);
                    // 记录是第几张图片
                    int position = strings.size() - 1;
                    // 使图片可点击并监听点击事件
                    output.setSpan(new ClickableImage(position), (len - 1) < 0 ? 0 : (len - 1), len,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }

                processAttributes(xmlReader);
                if (tag.equalsIgnoreCase("span")) {
                    if (opening) {
                        startSpan(tag, output, xmlReader);
                    } else {
                        endSpan(tag, output, xmlReader);
                        attributes.clear();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void processAttributes(final XMLReader xmlReader) {
            try {
                Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
                elementField.setAccessible(true);
                Object element = elementField.get(xmlReader);
                Field attsField = element.getClass().getDeclaredField("theAtts");
                attsField.setAccessible(true);
                Object atts = attsField.get(element);
                Field dataField = atts.getClass().getDeclaredField("data");
                dataField.setAccessible(true);
                String[] data = (String[]) dataField.get(atts);
                Field lengthField = atts.getClass().getDeclaredField("length");
                lengthField.setAccessible(true);
                int len = (Integer) lengthField.get(atts);
                /**
                 * MSH: Look for supported attributes and add to hash map.
                 * This is as tight as things can get :)
                 * The data index is "just" where the keys and values are stored.
                 */
                for (int i = 0; i < len; i++)
                    attributes.put(data[i * 5 + 1], data[i * 5 + 4]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void startSpan(String tag, Editable output, XMLReader xmlReader) {
            startIndex = output.length();
        }

        public void endSpan(String tag, Editable output, XMLReader xmlReader) {
            stopIndex = output.length();

            String color = attributes.get("color");
            String size = attributes.get("size");
            String style = attributes.get("style");
            if (!TextUtils.isEmpty(style)) {
                analysisStyle(startIndex, stopIndex, output, style);
            }
            if (!TextUtils.isEmpty(size)) {
                size = size.split("px")[0];
            }
            if (!TextUtils.isEmpty(color)) {
                if (color.startsWith("@")) {
                    Resources res = Resources.getSystem();
                    String name = color.substring(1);
                    int colorRes = res.getIdentifier(name, "color", "android");
                    if (colorRes != 0) {
                        output.setSpan(new ForegroundColorSpan(colorRes), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    try {
                        output.setSpan(new ForegroundColorSpan(Color.parseColor(color)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        reductionFontColor(startIndex, stopIndex, output);
                    }
                }
            }
            if (!TextUtils.isEmpty(size)) {
                int fontSizePx = 16;
                if (null != context) {
                    fontSizePx = DensityUtil.dip2px(context, Integer.parseInt(size));
                }
                output.setSpan(new AbsoluteSizeSpan(fontSizePx), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        /**
         * 还原为原来的颜色
         *
         * @param startIndex
         * @param stopIndex
         * @param editable
         */
        private void reductionFontColor(int startIndex, int stopIndex, Editable editable) {
            if (null != mOriginColors) {
                editable.setSpan(new TextAppearanceSpan(null, 0, 0, mOriginColors, null),
                        startIndex, stopIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                editable.setSpan(new ForegroundColorSpan(0xff2b2b2b), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        /**
         * 解析style属性
         *
         * @param startIndex
         * @param stopIndex
         * @param editable
         * @param style
         */
        private void analysisStyle(int startIndex, int stopIndex, Editable editable, String style) {
            String[] attrArray = style.split(";");
            Map<String, String> attrMap = new HashMap<>();
            if (null != attrArray) {
                for (String attr : attrArray) {
                    String[] keyValueArray = attr.split(":");
                    if (null != keyValueArray && keyValueArray.length == 2) {
                        // 记住要去除前后空格
                        attrMap.put(keyValueArray[0].trim(), keyValueArray[1].trim());
                    }
                }
            }
            String color = attrMap.get("color");
            String fontSize = attrMap.get("font-size");
            if (!TextUtils.isEmpty(fontSize)) {
                fontSize = fontSize.split("px")[0];
            }
            if (!TextUtils.isEmpty(color)) {
                if (color.startsWith("@")) {
                    Resources res = Resources.getSystem();
                    String name = color.substring(1);
                    int colorRes = res.getIdentifier(name, "color", "android");
                    if (colorRes != 0) {
                        editable.setSpan(new ForegroundColorSpan(colorRes), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    try {
                        editable.setSpan(new ForegroundColorSpan(Color.parseColor(color)), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        reductionFontColor(startIndex, stopIndex, editable);
                    }
                }
            }
            if (!TextUtils.isEmpty(fontSize)) {
                int fontSizePx = 16;
                if (null != context) {
                    fontSizePx = DensityUtil.dip2px(context, Integer.parseInt(fontSize));
                }
                editable.setSpan(new AbsoluteSizeSpan(fontSizePx), startIndex, stopIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private class ClickableImage extends ClickableSpan {
            private int position;

            public ClickableImage(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View widget) {
                doShowBigImage(strings.get(position));
            }
        }

        private void doShowBigImage(String url) {
            if (!TextUtils.isEmpty(url)) {
                BigImageDialog mBigImageDialog = new BigImageDialog(context);
                mBigImageDialog.display(url);
                mBigImageDialog.show();
            }
        }
    }
}
