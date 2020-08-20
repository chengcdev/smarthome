package com.mili.smarthome.tkj.set.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class NumView extends RecyclerView {

    public Context mContext;

    public List<String> numList = new ArrayList<>();
    private NumAdapter mAdapter;
    private int count = -1;
    public static final int INPUT_TYP_NUM = 0xF1; //默认数字
    public static final int INPUT_TYP_CHAR = 0xF2; //“*”字符
    private StringBuilder mStringBuilder;
    private boolean isCenterShow; //是否从中心显示
    private boolean isBlend; //是否“*”和数字组合显示（高级密码）


    public NumView(Context context) {
        this(context, null);
    }

    public NumView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        mContext = context;
        setFocusable(false);
        setFilterTouchesWhenObscured(false);
    }

    public void initNumView(int maxLength) {
        for (int i = 0; i < maxLength; i++) {
            numList.add("");
        }
        mStringBuilder = new StringBuilder();
        mAdapter = new NumAdapter(numList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(linearLayoutManager);
        setAdapter(mAdapter);
    }


    public void inputNum(String num,int inputType,boolean isBlend) {
        this.isBlend = isBlend;
        count++;
        if (count < numList.size()) {
            mStringBuilder.append(num);
            if (inputType == INPUT_TYP_CHAR) {
                numList.set(count, "*");
            }else {
                numList.set(count, num);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            count = numList.size() - 1;
        }
    }

    public void backNum() {
        if (count <= -1) {
            count = 0;
        } else {
            numList.set(count, "");
            mStringBuilder.deleteCharAt(count);
            mAdapter.notifyDataSetChanged();
        }
        count--;
    }

    public String getNum() {
        return mStringBuilder.toString();
    }

    public void notifyNumView(int maxLength,boolean isCenterShow) {
        this.isCenterShow = isCenterShow;
        isBlend = false;
        count = -1;
        numList.clear();
        removeAllViews();
        initNumView(maxLength);
    }


    class MyItemView extends RecyclerView.ViewHolder {

        private final TextView tv;
        private final ImageView imageView;

        MyItemView(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            tv = (TextView) itemView.findViewById(R.id.tv);
            LogUtils.w(" isCenterShow: "+isCenterShow);
            if (!isCenterShow) {
                //获取当前每个数字需要的宽度
                int mesureWidth = 490/numList.size();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mesureWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.width = mesureWidth;
                imageView.setLayoutParams(layoutParams);
            }
        }
    }

    class NumAdapter extends RecyclerView.Adapter<MyItemView> {

        private List<String> mList;

        public NumAdapter(List<String> List) {
            mList = List;
        }

        @NonNull
        @Override
        public MyItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.rv_num_view, parent, false);
            return new MyItemView(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull MyItemView holder, int position) {
            if (isBlend) {
                holder.imageView.setVisibility(View.GONE);
                holder.tv.setVisibility(View.VISIBLE);
                holder.tv.setText(mList.get(position));
            }else {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.tv.setVisibility(View.GONE);
                holder.imageView.setImageResource(showImgNum(mList.get(position)));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    public int showImgNum(String num) {
        int resId = 0;
        switch (num) {
            case "0":
                resId = R.drawable.call_0;
                break;
            case "1":
                resId = R.drawable.call_1;
                break;
            case "2":
                resId = R.drawable.call_2;
                break;
            case "3":
                resId = R.drawable.call_3;
                break;
            case "4":
                resId = R.drawable.call_4;
                break;
            case "5":
                resId = R.drawable.call_5;
                break;
            case "6":
                resId = R.drawable.call_6;
                break;
            case "7":
                resId = R.drawable.call_7;
                break;
            case "8":
                resId = R.drawable.call_8;
                break;
            case "9":
                resId = R.drawable.call_9;
                break;
            case "*":
                resId = R.drawable.char_xing2;
                break;
        }
        return resId;
    }

}
