package com.mili.smarthome.tkj.widget;

import android.content.Context;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

public class MultiImageView extends View {

    private BitmapAdapter mBitmapAdapter;
    private BitmapFactory.Options mOptions = new BitmapFactory.Options();
    private Bitmap mBitmap;
    private Rect mSrcRect = new Rect();
    private Rect mDstRect = new Rect();

    private float mScale = 1.0f;

    public MultiImageView(Context context) {
        this(context, null, 0);
    }

    public MultiImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MultiImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mBitmapAdapter != null) {
            mOptions.inJustDecodeBounds = true;
            int totalWidth = 0;
            int maxHeight = 0;
            for (int position = 0; position < mBitmapAdapter.getCount(); position++) {
                mBitmapAdapter.getBitmap(position, mOptions);
                totalWidth += mOptions.outWidth;
                if (position != 0) {
                    totalWidth += mBitmapAdapter.getDrawablePadding();
                }
                maxHeight = Math.max(maxHeight, mOptions.outHeight);
            }

            mScale = 1.0f;

            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            if (widthMode == MeasureSpec.AT_MOST) {
                int usedWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
                if (totalWidth > usedWidth) {
                    mScale = usedWidth * 1.0f / totalWidth;
                    totalWidth = usedWidth;
                } else {
                    mScale = 1.0f;
                }
                int widthSize = totalWidth + getPaddingLeft() + getPaddingRight();
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, widthMode);
            }
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (heightMode == MeasureSpec.AT_MOST) {
                int heightSize = (int) (maxHeight * mScale + 0.5f) + getPaddingTop() + getPaddingBottom();
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmapAdapter == null)
            return;

        int saveCount = canvas.getSaveCount();
        canvas.save();

        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (int position = 0; position < mBitmapAdapter.getCount(); position++) {
            mBitmap = mBitmapAdapter.getBitmap(position, null);
            if (mBitmap == null)
                continue;

            mSrcRect.set(0,0, mBitmap.getWidth(), mBitmap.getHeight());

            int right = left + (int) (mBitmap.getWidth() * mScale);
            int bottom = (int) (mBitmap.getHeight() * mScale);
            mDstRect.set(left, top, right, bottom);

            canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, null);

            left = right + mBitmapAdapter.getDrawablePadding();
        }

        canvas.restoreToCount(saveCount);
    }

    public void setAdapter(BitmapAdapter builder) {
        if (mBitmapAdapter == builder) {
            return;
        }
        if (mBitmapAdapter != null) {
            mBitmapAdapter.unregisterDataObserver(mDataObserverImpl);
        }
        builder.registerDataObserver(mDataObserverImpl);
        mBitmapAdapter = builder;
    }

    private DataObserver mDataObserverImpl = new  DataObserver() {
        @Override
        public void onChanged() {
            requestLayout();
        }
    };

    public static abstract class BitmapAdapter {

        private final DataObservable mObservable = new DataObservable();

        public abstract int getCount();
        public abstract Bitmap getBitmap(int position, BitmapFactory.Options options);

        public int getDrawablePadding() {
            return 0;
        }

        public final boolean hasObservers() {
            return mObservable.hasObservers();
        }

        public final void registerDataObserver(@NonNull DataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public final void unregisterDataObserver(@NonNull DataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

        public final void notifyDataSetChanged() {
            mObservable.notifyChanged();
        }
    }

    public static abstract class DataObserver {

        public void onChanged() {
        }
    }

    private static class DataObservable extends Observable<DataObserver> {

        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }
    }

}
