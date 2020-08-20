package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.util.List;

public abstract class ItemSelectorAdapter extends RecyclerView.Adapter<ItemSelectorAdapter.VH> {

    public static final int PAGE_SIZE = 5;

    protected Context mContext;
    private String[] mStrArray;
    private int mMaxPage;
    private int mPage;
    private int mSelection = 0;
    //是否恢复出厂第一次设置界面
    private boolean isResetEnable = false;
    private boolean isTextYellow = true;

    public ItemSelectorAdapter(Context context) {
        mContext = context;
        mStrArray = context.getResources().getStringArray(getStringArrayId());
        onDataSetChanged();
    }

    public ItemSelectorAdapter(Context context,boolean isReset) {
        this.isResetEnable = isReset;
        mContext = context;
        mStrArray = context.getResources().getStringArray(getStringArrayId());
        onDataSetChanged();
    }

    public ItemSelectorAdapter(Context context, List<Integer> list) {
        mContext = context;
        mStrArray = new String[list.size()];
        Integer[] strs = new Integer[list.size()];
        Integer[] integers = list.toArray(strs);
        for (int i = 0; i < integers.length ; i++) {
            mStrArray[i] = context.getResources().getString(integers[i]);
        }
        onDataSetChanged();
    }

    @ArrayRes
    protected abstract int getStringArrayId();

    protected abstract void onItemClick(int position);

    public void setStringArray(String[] array) {
        mStrArray = array;
        onDataSetChanged();
        notifyDataSetChanged();
    }

    public String[] getStringArray() {
        return mStrArray;
    }

    public void setSelection(int selection) {
        if (mSelection != selection) {
            int unselect = mSelection;
            mSelection = selection;

            int start = mPage * PAGE_SIZE;
            int end = start + PAGE_SIZE;
            end = Math.min(end, mStrArray == null ? 0 : mStrArray.length);
            if (unselect >= start && unselect < end) {
                notifyItemChanged(unselect);
            }
            if (selection >= start && selection < end) {
                notifyItemChanged(selection);
            }
        }
    }

    public void setTextYellow(boolean isTextYellow) {
        this.isTextYellow = isTextYellow;
    }

    public boolean isTurnable() {
        return mMaxPage > 0;
    }

    public void prePage() {
        if (mMaxPage == 0)
            return;
        mPage--;
        if (mPage < 0) {
            mPage = mMaxPage;
        }
        notifyDataSetChanged();
    }

    public void nextPage() {
        if (mMaxPage == 0)
            return;
        mPage++;
        if (mPage > mMaxPage) {
            mPage = 0;
        }
        notifyDataSetChanged();
    }

    private void onDataSetChanged() {
        mPage = 0;
        if (mStrArray == null)
            mMaxPage = 0;
        else
            mMaxPage = (mStrArray.length - 1) / PAGE_SIZE;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_menu, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final int index = mPage * PAGE_SIZE + position;
        holder.tvName.setText(mStrArray[index]);
        if (index == mSelection && isTextYellow) {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.txt_yellow));
        } else {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.txt_white));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(index);
                onItemClick(index);
            }
        });

        if (isResetEnable) {
            holder.mImaNext.setVisibility(View.GONE);
            holder.mViewLine.setVisibility(View.GONE);
        }else {
            holder.mImaNext.setVisibility(View.VISIBLE);
            holder.mViewLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        if (mStrArray == null)
            return 0;
        int start = mPage * PAGE_SIZE;
        int count = mStrArray.length - start;
        return Math.min(count, PAGE_SIZE);
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView tvName;
        private View mViewLine;
        private ImageView mImaNext;

        VH(View itemView) {
            super(itemView);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
            mViewLine = ViewUtils.findView(itemView, R.id.view_line);
            mImaNext = ViewUtils.findView(itemView, R.id.img_next);
        }
    }

}
