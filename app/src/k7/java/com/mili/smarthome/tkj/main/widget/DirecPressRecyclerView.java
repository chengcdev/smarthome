package com.mili.smarthome.tkj.main.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.mili.smarthome.tkj.utils.LogUtils;

public class DirecPressRecyclerView extends RecyclerView {
    private Context mContext;
    private boolean move;
    private int mIndex;
    private LayoutManager layoutManager;

    public DirecPressRecyclerView(Context context) {
        super(context);
        init(context);
    }


    public DirecPressRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DirecPressRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setFocusable(false);
        setFocusableInTouchMode(false);
        mContext = context;
    }


    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        this.layoutManager = layout;
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
//        if (layoutManager instanceof LinearLayoutManager) {
//            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
//            //在这里进行第二次滚动（最后的100米！）
//            if (move) {
//                move = false;
//                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
//                int n = mIndex - linearLayoutManager.findFirstVisibleItemPosition();
//                if (0 <= n && n < getChildCount()) {
//                    //获取要置顶的项顶部离RecyclerView顶部的距离
//                    int top = getChildAt(n).getTop();
//                    //最后的移动
//                    scrollBy(0, top);
//                    View childAt = linearLayoutManager.getChildAt(n);
//                    View childAt1 = linearLayoutManager.getChildAt(mIndex);
//                }
//            }
//        }

    }

    /**
     * 移动到指定位置
     *
     * @param isLast 当前是否从0开始后退
     */
    public void moveToPosition(int n, boolean isLast) {

        LinearLayoutManager linearLayoutManager = null;
        if (layoutManager instanceof LinearLayoutManager) {
            linearLayoutManager = (LinearLayoutManager) layoutManager;
            mIndex = n;
            int firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            int lastItem = linearLayoutManager.findLastVisibleItemPosition();
            if (isLast) {
                scrollToPosition(n + 3);
                return;
            }
            if (n <= firstItem) {
                scrollToPosition(n);
            } else if (n <= lastItem) {
                int top = getChildAt(n - firstItem).getTop();
                scrollBy(0, top);
            } else {
                scrollToPosition(n + 3);
                move = true;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.w(" DirecPressRecyclerView onKeyDown... ");
        return super.onKeyDown(keyCode, event);
    }
}
