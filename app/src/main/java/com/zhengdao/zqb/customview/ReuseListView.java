package com.zhengdao.zqb.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**具体我也忘了这个listview有啥用了，Reuse吧（看名称的）
 *
 * @Author lijiangop
 * @CreateTime 2017/9/20 15:03
 */
public class ReuseListView extends ListView {

    public ReuseListView(Context context) {
        super(context);
    }

    public ReuseListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReuseListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
