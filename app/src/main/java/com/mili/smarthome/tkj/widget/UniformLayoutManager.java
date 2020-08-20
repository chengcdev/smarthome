package com.mili.smarthome.tkj.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * 等分布局（暂时只支持水平方向）
 */
public class UniformLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() == 0 || state.isPreLayout())
            return;
        detachAndScrapAttachedViews(recycler);

        int screenWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int itemWidth = screenWidth / getItemCount();

        for (int position = 0; position < getItemCount(); position++) {
            View scrap = recycler.getViewForPosition(position);
            addView(scrap);

            RecyclerView.LayoutParams rvlp = (RecyclerView.LayoutParams) scrap.getLayoutParams();
            rvlp.width = itemWidth - getLeftDecorationWidth(scrap) - getRightDecorationWidth(scrap) - rvlp.leftMargin - rvlp.rightMargin;

            measureChildWithMargins(scrap, 0, 0);

            int left = getPaddingLeft() + position * itemWidth + rvlp.leftMargin;
            int top = getPaddingTop();
            int right = left + rvlp.width;
            int bottom = top + rvlp.height;
            layoutDecorated(scrap, left, top, right, bottom);
        }
    }
}
