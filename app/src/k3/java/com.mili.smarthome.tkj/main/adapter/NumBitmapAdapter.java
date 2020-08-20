package com.mili.smarthome.tkj.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ResUtils;
import com.mili.smarthome.tkj.widget.MultiImageView;

public class NumBitmapAdapter extends MultiImageView.BitmapAdapter {

    private Context mContext;
    private StringBuilder mText = new StringBuilder();

    public NumBitmapAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mText.length();
    }

    public Bitmap getBitmap(int position, BitmapFactory.Options options) {
        int resId;
        if (isMask(position)) {
            resId = R.drawable.key_mask;
        } else {
            resId = ResUtils.getImageId(mContext, "key_" + mText.charAt(position));
        }
        return BitmapFactory.decodeResource(mContext.getResources(), resId, options);
    }

    public boolean isMask(int position) {
        return false;
    }

    public void setText(CharSequence text) {
        mText.delete(0, mText.length());
        mText.append(text);
        notifyDataSetChanged();
    }

    public void input(int num) {
        char c = (char) ((num % 10) + 48);
        mText.append(c);
        notifyDataSetChanged();
    }

    public void backspace() {
        mText.deleteCharAt(mText.length() - 1);
        notifyDataSetChanged();
    }

    public void clear() {
        mText.delete(0, mText.length());
        notifyDataSetChanged();
    }

    public String getText() {
        return mText.toString();
    }

}
