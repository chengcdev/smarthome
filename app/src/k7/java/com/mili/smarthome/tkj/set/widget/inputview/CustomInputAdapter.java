package com.mili.smarthome.tkj.set.widget.inputview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomInputAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context mContext;

    private List<String> mList;

    private int count = 0;

    private String deletReplaceStr = "";

    private int mInputType;

    public static final int INPUT_TYPE_IP = 0xFF;

    public static final int INPUT_TYPE_OTHER = 0x00;

    public static final int INPUT_TYPE_1 = 0x01;

    public static final int INPUT_TYPE_2 = 0x02;

    private boolean isDelete;
    //是否显示*字符
    private boolean isCharXing;
    //第一个是否闪烁
    private boolean isFirstFlash;
    //最后一个是否闪烁
    private boolean isEndFlash;

    @SuppressLint("UseSparseArrays")
    private Map<Integer, RecyclerView.ViewHolder> viewHolderList = new HashMap<>();


    public CustomInputAdapter(Context context, List<String> list, int inputType) {
        mContext = context;
        mList = list;
        mInputType = inputType;
    }

    public CustomInputAdapter(Context context, List<String> list, int inputType, boolean isFirstFlash) {
        mContext = context;
        mList = list;
        mInputType = inputType;
        this.isFirstFlash = isFirstFlash;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {

        viewHolderList.put(position, myViewHolder);

        if (mList.get(position).equals("")) {
            myViewHolder.customNum.setTextLine(mList.get(position));
        } else {
            if (isDelete && count == 0) {
                myViewHolder.customNum.setTextLine("");
                myViewHolder.customNum.setFlash();
            } else {
                myViewHolder.customNum.setTextNoLine(mList.get(position));
            }
        }
        //第一个是否闪
        if (position == 0 && isFirstFlash) {
            myViewHolder.customNum.setFlash();
        }
        //最后一个是否闪
        if (position == mList.size() - 1 && isEndFlash) {
            myViewHolder.customNum.setFlash();
        }

        if (mInputType == INPUT_TYPE_2 && position == mList.size() - 1) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).equals("")) {
                    if (viewHolderList.get(i) != null) {
                        MyViewHolder holder = (MyViewHolder) viewHolderList.get(i);
                        holder.customNum.setFlash();
                        count = i;
                    }
                    break;
                } else if (i == mList.size() - 1 && !mList.get(i).equals("")) {
                    count = mList.size();
                }
            }
        }
        LogUtils.w(" CustomInputAdapter count: " + count);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    void setCharXing(boolean charXing) {
        isCharXing = charXing;
    }

    public boolean isFirstFlash() {
        return isFirstFlash;
    }

    public void setFirstFlash(boolean firstFlash) {
        isFirstFlash = firstFlash;
        if (firstFlash) {
            this.count = 0;
        }
    }

    public void setEndFlash(boolean isEndFlash) {
        this.isEndFlash = isEndFlash;
        if (isEndFlash) {
            this.count = mList.size() - 1;
        }
    }

    public boolean isEndFlash() {
        return isEndFlash;
    }

    /**
     * 添加
     *
     * @param text 添加的字符
     */
    public void addNum(RecyclerView rv, String text) {
        if (count >= mList.size()) {
            if (mInputType == INPUT_TYPE_1 || mInputType == INPUT_TYPE_2) {
                View view = rv.getChildAt(mList.size() - 1);
                if (view != null) {
                    MyViewHolder childViewHolder = (MyViewHolder) rv.getChildViewHolder(view);
                    childViewHolder.customNum.closeAllFalsh();
                }
                return;
            }
        }

        String oldChar = mList.get(count);

        mList.set(count, text);

        View nextView;
        if (mInputType == INPUT_TYPE_IP) {
            //是否合法
            if (ipTointright(getResult()) == 1) {
                if (oldChar != null) {
                    mList.set(count, oldChar);
                } else {
                    mList.set(count, "0");
                }
                return;
            }
        }

        int childCount = rv.getChildCount();

        if (mInputType == INPUT_TYPE_IP) {
            LogUtils.e(" CustomInputAdapter addNum ： " + count);
            if (count == 2 || count == 6 || count == 10) {
                for (int i = 0; i < childCount; i++) {
                    if (i == count + 2) {
                        nextView = rv.getChildAt(count);
                        if (nextView != null) {
                            MyViewHolder nextViewHolder = (MyViewHolder) rv.getChildViewHolder(nextView);
                            nextViewHolder.customNum.setTextNoLine(mList.get(count));
                        }
                        nextView = rv.getChildAt(i);
                        if (nextView != null) {
                            MyViewHolder nextViewHolder = (MyViewHolder) rv.getChildViewHolder(nextView);
                            nextViewHolder.customNum.setFlash();
                        }
                    } else {
                        nextView = rv.getChildAt(i);
                        if (nextView != null) {
                            MyViewHolder nextViewHolder = (MyViewHolder) rv.getChildViewHolder(nextView);
                            nextViewHolder.customNum.closeAllFalsh();
                        }
                    }
                }
            } else {
                setNum(rv, childCount);
            }
        } else {
            setNum(rv, childCount);
        }


        if (mInputType == INPUT_TYPE_IP) {
            if (count == 2 || count == 6 || count == 10) {
                count += 2;
            } else {
                count++;
            }
            if (count >= 15) {
                count = 14;
            }
        } else {
            if (text.equals(mContext.getString(R.string.setting_yes)) || text.equals(mContext.getString(R.string.setting_no))) {
                return;
            }
            count++;
        }

    }

    private void setNum(RecyclerView rv, int childCount) {
        LogUtils.w("  CustomInputAdapter setNum childCount: " + childCount);
        for (int i = 0; i < childCount; i++) {
            if (i == count) {
                View currentView = rv.getChildAt(i);
                if (currentView != null) {
                    MyViewHolder viewHolder = (MyViewHolder) rv.getChildViewHolder(currentView);
                    if (isCharXing) {
                        viewHolder.customNum.setTextNoLine("*");
                    } else {
                        viewHolder.customNum.setTextNoLine(mList.get(count));
                    }
                    if (i == mList.size() - 1) {
                        if (isEndFlash) {
                            viewHolder.customNum.setFlash();
                        } else {
                            showTextLine(viewHolder.customNum, i);
                        }
                        break;
                    }
                }
                View nextView = rv.getChildAt(i + 1);
                if (nextView != null) {
                    MyViewHolder nextViewHolder = (MyViewHolder) rv.getChildViewHolder(nextView);
                    nextViewHolder.customNum.setFlash();
                }
            }
        }
    }

    boolean endNumFlash(RecyclerView rv, boolean isFlash) {
        int childCount = rv.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (i == mList.size() - 1) {
                View preView = rv.getChildAt(i);
                if (preView != null) {
                    MyViewHolder previewHolder = (MyViewHolder) rv.getChildViewHolder(preView);
                    if (isFlash) {
                        previewHolder.customNum.setFlash();
                    } else {
                        previewHolder.customNum.closeAllFalsh();
                    }
                }
                count = mList.size() - 1;
            }
        }
        return true;
    }

    /**
     * 删除字符
     *
     * @param rv   RecyclerView
     * @param text 删除字符填充内容
     */
    public boolean deleteNum(RecyclerView rv, String text) {
        deletReplaceStr = text;

        if (count >= mList.size()) {
            if (mInputType == INPUT_TYPE_1 || mInputType == INPUT_TYPE_2) {
                View currentView = rv.getChildAt(mList.size() - 1);
                if (currentView != null) {
                    MyViewHolder curentViewHolder = (MyViewHolder) rv.getChildViewHolder(currentView);
                    curentViewHolder.customNum.setTextLine(text);
                    curentViewHolder.customNum.setFlash();
                    mList.set(mList.size() - 1, deletReplaceStr);
                }
                count--;
                return false;
            } else {
                count = mList.size() - 1;
            }
        }
        if (mInputType == INPUT_TYPE_1 || mInputType == INPUT_TYPE_2) {
            if (count - 1 <= 0) {
                mList.set(0, deletReplaceStr);
            } else {
                mList.set(count - 1, deletReplaceStr);
            }
        } else {
            mList.set(count, deletReplaceStr);
        }

        int childCount = rv.getChildCount();

        if (deletReplaceStr.equals("")) {
            for (int i = 0; i < childCount; i++) {
                if (i == count) {
                    View currentView = rv.getChildAt(i);
                    MyViewHolder curentViewHolder = (MyViewHolder) rv.getChildViewHolder(currentView);
                    View leftView = rv.getChildAt(i - 1);
                    if (leftView != null) {
                        MyViewHolder leftHolder = (MyViewHolder) rv.getChildViewHolder(leftView);
                        leftHolder.customNum.setTextLine(text);
                        leftHolder.customNum.setFlash();
                    }
                    curentViewHolder.customNum.closeAllFalshLine();
                } else {
                    View view = rv.getChildAt(i);
                    if (view != null) {
                        MyViewHolder viewHolder = (MyViewHolder) rv.getChildViewHolder(view);
                        showTextLine(viewHolder.customNum, i);
                    }
                }
            }

        } else {
            for (int i = 0; i < childCount; i++) {
                if (i == count) {
                    View currentView;
                    View leftView;
                    currentView = rv.getChildAt(i);
                    if (currentView != null) {
                        MyViewHolder viewHolder = (MyViewHolder) rv.getChildViewHolder(currentView);
                        viewHolder.customNum.setTextNoLine(text);
                        if (i == 0) {
                            if (isFirstFlash) {
                                viewHolder.customNum.setFlash();
                            } else {
                                showTextLine(viewHolder.customNum, i);
                            }
                            break;
                        }
                    }
                    if (mInputType == INPUT_TYPE_IP && (i == 4 || i == 8 || i == 12)) {
                        leftView = rv.getChildAt(i - 2);
                    } else {
                        leftView = rv.getChildAt(i - 1);
                    }
                    if (leftView != null) {
                        MyViewHolder viewHolder = (MyViewHolder) rv.getChildViewHolder(leftView);
                        viewHolder.customNum.setFlash();
                    }
                }
            }
        }

        if (mInputType == INPUT_TYPE_IP) {
            if (count == 4 || count == 8 || count == 12) {
                count -= 2;
            } else {
                count--;
            }
        } else {
            count--;
        }

        if (count < 0) {
            count = 0;
        }

        return false;
    }

    /**
     * @return 获取list字符集合
     */
    public String getResult() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String strChar : mList) {
            stringBuilder.append(strChar);
        }
        return stringBuilder.toString();
    }

    /**
     * 替换当前已显示的字符串
     *
     * @param str 新字符串
     */
    void notifyItem(String str) {
        if (str.length() == mList.size()) {
            for (int i = 0; i < mList.size(); i++) {
                mList.set(i, String.valueOf(str.charAt(i)));
            }
            notifyDataSetChanged();
            count = mList.size();
        }
    }

    /**
     * 判断IP字符串格式是否正确
     */
    private static int ipTointright(String strIp) {
        int[] ip = new int[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        if (position1 == -1) {
            return 1;
        } else {
            int position2 = strIp.indexOf(".", position1 + 1);
            if (position2 == -1) {
                return 1;
            } else {
                int position3 = strIp.indexOf(".", position2 + 1);
                if (position3 == -1) {
                    return 1;
                } else {
                    if (!isNum(strIp.substring(0, position1)))
                        return 1;
                    ip[0] = Integer.parseInt(strIp.substring(0, position1));
                    if (!isNum(strIp.substring(position1 + 1, position2)))
                        return 1;
                    ip[1] = Integer.parseInt(strIp.substring(position1 + 1, position2));
                    if (!isNum(strIp.substring(position2 + 1, position3)))
                        return 1;
                    ip[2] = Integer.parseInt(strIp.substring(position2 + 1, position3));
                    if (!isNum(strIp.substring(position3 + 1)))
                        return 1;
                    ip[3] = Integer.parseInt(strIp.substring(position3 + 1));
                    if (ip[0] > 255)
                        return 1;
                    if (ip[1] > 255)
                        return 1;
                    if (ip[2] > 255)
                        return 1;
                    if (ip[3] > 255)
                        return 1;
                }
            }
        }
        return 0;
    }

    private static boolean isNum(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showTextLine(InputItemView textView, int position) {
        if (mList.get(position).equals("")) {
            textView.closeAllFalshLine();
        } else {
            textView.closeAllFalsh();
        }
    }

}




