package com.zhengdao.zqb.view.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.customview.BigImageDialog;
import com.zhengdao.zqb.entity.ShowEntity;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * @Author lijiangop
 * @CreateTime 2018/3/30 17:40
 */
public class HomeDetailAddPicAdapter extends CommonAdapter<ShowEntity> {

    private CallBack       callBack;
    private boolean        isAddShow;
    private BigImageDialog mBigImageDialog;

    public interface CallBack {
        void onPicAdd(int position);

        void onPicDelete(int position);
    }

    public HomeDetailAddPicAdapter(Context context, int layoutId, List<ShowEntity> datas, CallBack callBack) {
        super(context, layoutId, datas);
        this.callBack = callBack;
    }

    @Override
    protected void convert(ViewHolder holder, final ShowEntity bitmap, final int position) {
        final LinearLayout llAdd = holder.getView(R.id.ll_add);
        final ImageView add = holder.getView(R.id.iv_screenshot_add);
        final TextView textView = holder.getView(R.id.tv_pic_desc);
        final ImageView imageView = holder.getView(R.id.iv_screenshot);
        final ImageView delete = holder.getView(R.id.iv_delete);
        if (bitmap.pic == null) {
            llAdd.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else {
            llAdd.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap.pic);
            imageView.setVisibility(View.VISIBLE);
        }
        textView.setText(TextUtils.isEmpty(bitmap.desc) ? "" : bitmap.desc);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.onPicAdd(position);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    if (mBigImageDialog == null)
                        mBigImageDialog = new BigImageDialog(mContext);
                    mBigImageDialog.display(bitmap.pic);
                    mBigImageDialog.show();
                }
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                delete.setVisibility(View.VISIBLE);
                return true;
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null)
                    callBack.onPicDelete(position);
                add.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
            }
        });
    }
}
