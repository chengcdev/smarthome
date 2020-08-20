package com.mili.smarthome.tkj.main.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.android.provider.NetworkHelp;
import com.mili.smarthome.tkj.main.interf.ISetDatasListener;
import com.mili.smarthome.tkj.utils.LogUtils;


public class InputTextView extends LinearLayout {
    private Context mContext;
    private ISetDatasListener setDatasListener;
    private String currentContent = "";
    private final int TEXT_NO_LINE = 0;
    private final int TEXT_SHOW_LINE = 1;
    private final int TEXT_NET = 2;
    private String content;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count = -1;

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
        currentContent = "";
        addText(maxNum, content, type);
    }

    private int maxNum;
    private int type;
    private String TAG = "InputTextView";
    private StringBuilder replace;

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    private int viewId = 0;

    public InputTextView(Context context) {
        super(context);
    }


    public InputTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public InputTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);


    }


    private void init(Context context, AttributeSet attrs) {
        setFocusable(false);
        setFocusableInTouchMode(false);
        mContext = context;
        setOrientation(LinearLayout.HORIZONTAL);



//        @SuppressLint("Recycle") TypedArray attributes = mContext.obtainStyledAttributes(attrs, R.styleable.InputTextView);
//        maxNum = attributes.getInt(R.styleable.InputTextView_maxLen, 9);
//        content = attributes.getString(R.styleable.InputTextView_content);
//        type = attributes.getInt(R.styleable.InputTextView_showType, 0);

        addText(maxNum, content, type);

    }

    /**
     * 添加数量
     */
    public void addText(int maxNum, String content, int type) {

        removeAllViews();

        switch (type) {
            case TEXT_NO_LINE:
                for (int i = 0; i < maxNum; i++) {
                    CustomNum customNum = new CustomNum(mContext);
                    addView(customNum);
                }
                setFlashLine();
                break;
            case TEXT_SHOW_LINE:
                for (int i = 0; i < maxNum; i++) {
                    CustomNum customNum = new CustomNum(mContext);
                    customNum.setTextNoLine(content);
                    addView(customNum);
                }
                firstFlash();
                break;
            //网络类型
            case TEXT_NET:
                for (int i = 0; i < maxNum; i++) {
                    CustomNum customNum = new CustomNum(mContext);
                    if (i == 3 || i == 7 || i == 11) {
                        customNum.setTextNoLine(".");
                    } else {
                        customNum.setTextNoLine(content);
                    }
                    addView(customNum);
                }
                firstFlash();
                break;
        }
    }

    /**
     * 添加内容
     */
    public void setNum(String num, boolean isShowMi) {
        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            if (customNum.isText()) {
                if (isShowMi) {
                    customNum.setTextXing(num);
                } else {
                    customNum.setTextNoLine(num);
                }
                currentContent = currentContent + num;
                if (setDatasListener != null) {
                    setDatasListener.getContent(viewId, currentContent);

                    LogUtils.e("CardControlFragment currentContent.length() : " +currentContent.length() );

                    //是否最后一个
                    if (currentContent.length() == maxNum) {
                        setDatasListener.lastCotent(viewId, true);
                    }
                }
                break;
            }
        }
        setFlashLine();
    }

    /**
     * 修改内容
     */
    public void editNum(String text) {
        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            customNum.setTextNoLine(String.valueOf(text.charAt(i)));
        }
    }


    /**
     * 修改内容
     */
    public void editNum(String num, boolean isFirst) {
        editFlashLine(num, isFirst);
    }

    /**
     * 判断是否闪烁
     */
    private void setFlashLine() {
        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            if (customNum.isText()) {
                customNum.setFlash();
                break;
            } else {
                customNum.hideLine();
            }
        }
    }


    /**
     * 编辑内容闪烁
     */
    private void editFlashLine(String num, boolean isFirst) {
        int index = 0;
        //判断ip是否合法
        if (!isFirst) {
            StringBuilder str_ip = new StringBuilder();
            for (int i = 0; i < getChildCount(); i++) {
                CustomNum customNum = (CustomNum) getChildAt(i);
                String textContent = customNum.getTextContent();
                str_ip.append(textContent);
            }

            index = count;
            if (index < 0) {
                index = -1;
            }
            index++;

            if (index == 3 || index == 7 || index == 11) {
                replace = str_ip.replace(index, index + 1, ".");
            } else {
                replace = str_ip.replace(index, index + 1, num);
            }

            if (NetworkHelp.ipTointright(replace.toString()) == 1) {
//                Log.e(TAG, "当前内容replace" + replace.toString() + "不合法");
                return;
            }

        }

        if (count < 0) {
            count = -1;
        }

        count++;

        if (isFirst && count == maxNum) {
            count = -1;
        } else if (count >= maxNum) {
            count = maxNum - 1;
        }

        for (int i = 0; i < maxNum; i++) {
            CustomNum customNum;
            if (count == i) {
                if (type == TEXT_NET && (i == 3 || i == 7 || i == 11)) {
                    if (isFirst) {
                        customNum = (CustomNum) getChildAt(i + 1);
                        customNum.setFlash();
                        customNum.setTextLine(num);
                    } else {
                        CustomNum c = (CustomNum) getChildAt(i + 2);
                        c.setFlash();
                        customNum = (CustomNum) getChildAt(i + 1);
                        customNum.setTextNoLine(num);
                    }
                    count++;
                } else if (count == maxNum - 1) {
                    customNum = (CustomNum) getChildAt(i);
                    customNum.setTextLine(num);
                    customNum.setFlash();
                } else {
                    if (type == TEXT_NET && isFirst) {
                        customNum = (CustomNum) getChildAt(i);
                        if (count == 0) {
                            customNum.setTextLine(num);
                            customNum.setFlash();
                            count = 0;
                        }
                    } else {
                        if (type == TEXT_NET && (i == 2 || i == 6 || i == 10)) {
                            CustomNum c = (CustomNum) getChildAt(i + 2);
                            c.setFlash();
                        } else {
                            CustomNum c = (CustomNum) getChildAt(i + 1);
                            if (c != null) {
                                c.setFlash();
                            }
                        }
                    }
                    customNum = (CustomNum) getChildAt(i);
                    customNum.setTextNoLine(num);
                }
                break;
            } else {
                if (count == -1) {
                    firstFlash();
                } else if (count == maxNum) {
                    LastFlash();
                }
            }
        }

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            str.append(customNum.getTextContent());
        }

        Log.e("当前的字符串", "当前的字符串" + str.toString());

        if (setDatasListener != null) {
            setDatasListener.getContent(viewId, str.toString());
            //是否最后一个
            if (count == maxNum - 1) {
                setDatasListener.lastCotent(viewId, true);
            }
        }

    }

    /**
     * 第一个内容闪烁 不带下滑线
     */
    public void firstFlash() {

        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            customNum.closeAllFalsh();
        }

        CustomNum customNum = (CustomNum) getChildAt(0);
        customNum.setFlash();
    }

    /**
     * 第一个内容闪烁 带下滑线
     */
    public void firstFlashLine() {

        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            customNum.closeAllFalshLine();
        }

        CustomNum customNum = (CustomNum) getChildAt(0);
        customNum.setFlash();
    }

    /**
     * 最后一个内容闪烁
     */
    public void LastFlash() {

        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            customNum.closeAllFalsh();
        }
        CustomNum customNum = (CustomNum) getChildAt(maxNum - 1);
        customNum.setFlash();
    }

    /**
     * 最后一个内容为0
     */
    public void LastCharZero() {

        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            customNum.closeAllFalsh();
        }
        CustomNum customNum = (CustomNum) getChildAt(maxNum - 1);
        customNum.setTextLine("0");
        customNum.setFlash();
    }

    /**
     * 关闭所有闪烁不带下滑线
     */
    public void closeFlash() {
        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            customNum.closeAllFalsh();
        }
    }

    /**
     * 关闭所有闪烁带下滑线
     */
    public void closeFlashLine() {
        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            customNum.closeAllFalshLine();
        }
    }

    /**
     * 添加后删除
     */
    public void deleteContent() {

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            if (i == currentContent.length() - 1) {
                customNum.setTextNoLine("");
                customNum.showLine();
            }
        }

        if (currentContent.length() == 0) {
            if (setDatasListener != null) {
                setDatasListener.firstCotent(viewId, true);
            }
            return;
        }
        currentContent = currentContent.substring(0, currentContent.length() - 1);
        if (setDatasListener != null) {
            LogUtils.e("  deleteContent currentContent : "+currentContent);
            setDatasListener.getContent(viewId, currentContent);
        }


        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            if (customNum.isText() && i == currentContent.length()) {
                customNum.setFlash();
            } else {
                if (i > currentContent.length()) {
                    customNum.showLine();
                } else {
                    customNum.hideLine();
                }
            }
        }
    }



    /**
     * 编辑后删除
     *
     * @param isShowZero 是否显示0
     */
    public void editDelete(boolean isShowZero) {

        if (count == maxNum) {
            count = count - 1;
        }

        if (count < 0) {
            count = -1;
            CustomNum customNum = (CustomNum) getChildAt(0);
            if (isShowZero) {
                customNum.setTextLine("0");
            }
        } else {
            for (int i = 0; i < getChildCount(); i++) {
                CustomNum customNum = (CustomNum) getChildAt(i);
                if (count == i) {
                    if (type == TEXT_NET && (i == 3 || i == 7 || i == 11)) {
                        customNum = (CustomNum) getChildAt(i - 1);
                        customNum.setTextLine("0");
                        customNum.setFlash();
                        count--;
                    } else {
                        customNum = (CustomNum) getChildAt(i);
                        if (isShowZero) {
                            customNum.setTextLine("0");
                        }
                        customNum.setFlash();
                    }
                } else {
                    customNum.hideLine();
                }


            }
        }

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < getChildCount(); i++) {
            CustomNum customNum = (CustomNum) getChildAt(i);
            str.append(customNum.getTextContent());
        }


        if (setDatasListener != null) {
            setDatasListener.getContent(viewId, str.toString());
            if (count <= 0) {
                setDatasListener.firstCotent(viewId, true);
            }
        }

        count--;
    }


    public void setSetDatasListener(ISetDatasListener setDatasListener) {
        this.setDatasListener = setDatasListener;
    }

    public String  getCurrentContent() {
        return currentContent;
    }


}
