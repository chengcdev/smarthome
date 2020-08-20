package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ViewUtils;

/**
 * 分页适配器
 */
public class PagingAdapter extends RecyclerView.Adapter<PagingAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private static final int PAGE_SIZE = 3;

    protected Context mContext;
    private String[] mStrArray;
    private int mMaxPage = 1;
    private int mPage = 0;
    private int mSelection = 0;
    private OnItemClickListener mListener;

    public PagingAdapter(Context context) {
        mContext = context;
    }

    public void setStringArray(String[] array) {
        mStrArray = array;
        mPage = 0;
        if (array == null)
            mMaxPage = 0;
        else
            mMaxPage = (array.length - 1) / PAGE_SIZE;
        notifyDataSetChanged();
    }

    public void prePage() {
        if (mMaxPage == 0 || mPage == 0)
            return;
        mPage--;
        notifyDataSetChanged();
    }

    public void nextPage() {
        if (mMaxPage == 0 || mPage == mMaxPage)
            return;
        mPage++;
        notifyDataSetChanged();
    }

    public void setSelection(int selection) {
        if (mSelection != selection) {
            mSelection = selection;
            notifyDataSetChanged();
        }
    }

    public int getSelection() {
        return mSelection;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_menu, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        final int index = mPage * PAGE_SIZE + position;
        holder.tvName.setText(mStrArray[index]);
        if (index == mSelection) {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.txt_yellow));
        } else {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.txt_white));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(index);
                }
            }
        });
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

        private VH(View itemView) {
            super(itemView);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }
    }
}
