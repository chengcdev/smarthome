package com.mili.smarthome.tkj.set.widget.inputview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CustomInputView extends RecyclerView {

    private Context mContext;
    private CustomInputAdapter customFlahAdapter;
    private List<String> strList;
    private GridLayoutManager gridLayoutManager;
    private boolean isFirstFlash;//第一个item是否闪烁

    public CustomInputView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomInputView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        mContext = context;
    }

    public void init(String str, int maxLen, int inpuType) {
        strList = new ArrayList<>();
        strList.clear();
        if (str == null || str.equals("")) {
            for (int i = 0; i < maxLen; i++) {
                strList.add("");
            }
        } else {
            for (int i = 0; i < str.length(); i++) {
                String charAt = String.valueOf(str.charAt(i));
                strList.add(charAt);
            }
            for (int i = 0; i < maxLen - str.length(); i++) {
                strList.add("");
            }
        }
        customFlahAdapter = new CustomInputAdapter(mContext, strList, inpuType, isFirstFlash);
        if (checkAccountMark(str)) {
            gridLayoutManager = new GridLayoutManager(mContext, 1);
        } else {
            if (inpuType == CustomInputAdapter.INPUT_TYPE_IP) {
                gridLayoutManager = new GridLayoutManager(mContext, 15);
            } else {
                if (maxLen <= 9) {
                    gridLayoutManager = new GridLayoutManager(mContext, maxLen);
                }else {
                    gridLayoutManager = new GridLayoutManager(mContext, 9);
                }
            }
        }
        setLayoutManager(gridLayoutManager);
        setAdapter(customFlahAdapter);
    }

    public void addNum(String text) {
        customFlahAdapter.addNum(this, text);
    }

    public void addNum(String text, boolean isCharxing) {
        customFlahAdapter.setCharXing(isCharxing);
        customFlahAdapter.addNum(this, text);
    }

    public void notifychange() {
        customFlahAdapter.notifyDataSetChanged();
    }

    public void notifychange(boolean isDelete) {
        customFlahAdapter.setDelete(isDelete);
        customFlahAdapter.notifyDataSetChanged();
    }

    public void notifychange(int position) {
        customFlahAdapter.notifyItemChanged(position);
    }

    public boolean setEnd(boolean isEndFlah) {
        return customFlahAdapter.endNumFlash(this, isEndFlah);
    }


    public boolean deleteNum(String text) {
        return customFlahAdapter.deleteNum(this, text);
    }

    public String getNum() {
        return customFlahAdapter == null ? "" : customFlahAdapter.getResult();
    }

    public int getCount() {
        return customFlahAdapter.getCount();
    }

    public void setCount(int count) {
        customFlahAdapter.setCount(count);
    }

    public CustomInputView setFirstFlash(boolean isFlash) {
        if (customFlahAdapter != null) {
            customFlahAdapter.setFirstFlash(isFlash);
        }
        isFirstFlash = isFlash;
        return this;
    }

    public CustomInputView setEndFlash(boolean isFlash) {
        if (customFlahAdapter != null) {
            customFlahAdapter.setEndFlash(isFlash);
        }
        return this;
    }

    public void setText(String str) {
        customFlahAdapter.setFirstFlash(false);
        customFlahAdapter.notifyItem(str);
    }

    /**
     * 验证中文
     */
    private static boolean checkAccountMark(String account) {
        String all = "^[\\u4E00-\\u9FA5]+$";
        return Pattern.matches(all, account);
    }

}
