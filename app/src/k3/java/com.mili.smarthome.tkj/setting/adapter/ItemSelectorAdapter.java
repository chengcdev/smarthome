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

import java.util.Arrays;
import java.util.List;

public class ItemSelectorAdapter extends RecyclerView.Adapter<ItemSelectorAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static final int PAGE_SIZE = 5;

    protected Context mContext;
    private List<String> mOptions;
    private int mMaxPage;
    private int mPage;
    private int mSelection = -1;
    private OnItemClickListener mListener;

    public ItemSelectorAdapter(Context context) {
        mContext = context;
    }

    public void setOptions(List<String> options) {
        mOptions = options;
        mPage = 0;
        if (mOptions == null)
            mMaxPage = 0;
        else
            mMaxPage = (mOptions.size() - 1) / PAGE_SIZE;
        notifyDataSetChanged();
    }

    public void setOptions(String[] options) {
        setOptions(Arrays.asList(options));
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

    public void setSelection(int selection) {
        if (mSelection != selection) {
            int unselect = mSelection;
            mSelection = selection;

            int start = mPage * PAGE_SIZE;
            int end = start + PAGE_SIZE;
            end = Math.min(end, mOptions == null ? 0 : mOptions.size());
            if (unselect >= start && unselect < end) {
                notifyItemChanged(unselect);
            }
            if (selection >= start && selection < end) {
                notifyItemChanged(selection);
            }
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
        holder.tvName.setText(mOptions.get(index));
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
        if (mOptions == null)
            return 0;
        int start = mPage * PAGE_SIZE;
        int count = mOptions.size() - start;
        return Math.min(count, PAGE_SIZE);
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView tvName;

        VH(View itemView) {
            super(itemView);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }
    }
}
