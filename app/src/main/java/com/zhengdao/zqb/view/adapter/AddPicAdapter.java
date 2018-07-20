package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.BigImageDialog;
import com.zhengdao.zqb.utils.ViewUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/2/27 09:36
 */
public class AddPicAdapter extends CommonAdapter<String> {

    private CallBack       callBack;
    private BigImageDialog mBigImageDialog;

    public interface CallBack {
        void onPicAdd(int position);

        void onPicDelete(int position);
    }

    public AddPicAdapter(Context context, int layoutId, List<String> datas, CallBack callBack) {
        super(context, layoutId, datas);
        this.callBack = callBack;
    }

    @Override
    protected void convert(ViewHolder holder, final String path, final int position) {
        final ImageView add = holder.getView(R.id.iv_screenshot_add);
        final ImageView imageView = holder.getView(R.id.iv_screenshot);
        final ImageView delete = holder.getView(R.id.iv_delete);
        //显示逻辑
        if (TextUtils.isEmpty(path)) {
            add.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else {
            add.setVisibility(View.GONE);
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path),
                    ViewUtils.dip2px(mContext, 72), ViewUtils.dip2px(mContext, 72));
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }
        //监听事件
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.onPicAdd(position);
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                delete.setVisibility(View.VISIBLE);
                return true;
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(path)) {
                    if (mBigImageDialog == null)
                        mBigImageDialog = new BigImageDialog(mContext);
                    mBigImageDialog.display(path, 0);
                    mBigImageDialog.show();
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.onPicDelete(position);
                delete.setVisibility(View.GONE);
            }
        });
    }
}
