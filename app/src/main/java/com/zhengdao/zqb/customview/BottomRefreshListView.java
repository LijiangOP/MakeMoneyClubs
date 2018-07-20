package com.zhengdao.zqb.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhengdao.zqb.R;
import com.zhengdao.zqb.mvp.MVPBaseActivity;
import com.zhengdao.zqb.utils.KeyBoardUtils;


/**
 * @创建者 cairui
 * @创建时间 2016/12/16 09:54
 * @描述 ${des}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @版本 $Revision$
 * @更新描述 ${des}
 */
public class BottomRefreshListView extends ListView implements AbsListView.OnScrollListener {
    private boolean            isLoadingMore;
    private boolean            isLast;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean enableLoadMore = true;
    private ProgressBar  mFooterPb;
    private TextView     mFooterText;
    private LinearLayout mFooterRoot;
    private View         mFooter;
    private Context      mContext;

    public BottomRefreshListView(Context context) {
        this(context, null);
    }

    public BottomRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init() {
        initFootView();
        setOnScrollListener(this);
    }

    private void initFootView() {
        if (mFooter == null) {
            mFooter = LayoutInflater.from(getContext()).inflate(R.layout.footer_category_detail, null, false);
            mFooterRoot = (LinearLayout) mFooter.findViewById(R.id.footer_ll);
            mFooterPb = (ProgressBar) mFooter.findViewById(R.id.footer_pb);
            mFooterText = (TextView) mFooter.findViewById(R.id.footer_text);
            enableLoadMore(true);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
            if (mContext instanceof MVPBaseActivity) {
                //清除焦点 避免出现异常
                View currentFocus = ((MVPBaseActivity) mContext).getCurrentFocus();
                if (currentFocus != null) {
                    KeyBoardUtils.hideKeyboard(currentFocus, mContext);
                    currentFocus.clearFocus();
                }
            }
        }
    }


    /**
     * 慎用 报错
     */
    public void onLoadMoreFinished() {
        this.isLoadingMore = false;
    }

    public void isLast(boolean isLast) {
        this.isLast = isLast;
        initFootView();
        if (isLast) {
            mFooterText.setText(R.string.no_more_data);
            mFooterPb.setVisibility(View.GONE);
        } else {
            mFooterText.setText(R.string.loading);
            mFooterPb.setVisibility(View.VISIBLE);
        }
    }

    public void enableLoadMore(boolean enable) {
        this.enableLoadMore = enable;
        if (enable) {
            addFooterView(mFooter);
        } else {
            removeFooterView(mFooter);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mOnLoadMoreListener = listener;
    }

    public interface OnTopListener {
        void isTop(boolean isTop);
    }

    private OnTopListener mOnTopListener;

    public void setOnTopListener(OnTopListener listener) {
        this.mOnTopListener = listener;
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (absListView.getFirstVisiblePosition() == 0) {
            final View topChildView = absListView.getChildAt(0);
            if (topChildView == null) {
                return;
            }
            if (topChildView.getTop() == 0) {
                if (mOnTopListener != null) {
                    mOnTopListener.isTop(true);
                }
            } else {
                if (mOnTopListener != null) {
                    mOnTopListener.isTop(false);
                }
            }
        } else {
            if (mOnTopListener != null) {
                mOnTopListener.isTop(false);
            }
        }
        if (!enableLoadMore) {
            return;
        }
        if (absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1) {
            if (isLast) {
                //如果没有更多了直接返回
                Log.e("BottomRefreshListView","没有更多数据 直接返回");
                return;
            }
            if (isLoadingMore) {
                //正在加载直接返回
                return;
            } else {
                isLoadingMore = true;
                if (mOnLoadMoreListener != null) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        }

    }

}
