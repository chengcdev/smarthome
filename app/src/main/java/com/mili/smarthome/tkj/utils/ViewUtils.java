package com.mili.smarthome.tkj.utils;

import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.mili.smarthome.tkj.R;

/**
 * View工具类
 */
public final class ViewUtils {

    @SuppressWarnings("unchecked")
    public static <T extends View> T findView(View parent, @IdRes int id) {
        if (parent == null)
            return null;
        return (T) parent.findViewById(id);
    }

    /**
     * 检测View是否被遮住显示不全
     * @return true被遮住，否则完全显示
     */
    public static boolean isCover(View view) {
        Rect outRect = new Rect();
        boolean cover = view.getGlobalVisibleRect(outRect);
        if (cover) {
            if (outRect.width() >= view.getMeasuredWidth()
                    && outRect.height() >= view.getMeasuredHeight()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据比例调整view的宽高
     * @param scale 宽高比
     */
    public static void applyScale(final View view, final double scale) {
        if (view == null)
            return;
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = view.getWidth();
                int height = view.getHeight();
                if ((width) > (height * scale)) {
                    width = (int) (height * scale);
                } else {
                    height = (int) (width / scale);
                }
                LogUtils.d("view size: " + width + "*" + height);
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                lp.width = width;
                lp.height = height;
                view.setLayoutParams(lp);
            }
        });
    }
}
