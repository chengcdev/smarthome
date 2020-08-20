package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.utils.ViewUtils;

public class ItemSelectorAdapter extends RecyclerView.Adapter<ItemSelectorAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    protected Context mContext;
    private String[] mStrArray;
    private int mSelection = 0;
    private int mPage = 0;
    private OnItemClickListener mListener;

    public ItemSelectorAdapter(Context context) {
        mContext = context;
    }

    public void setStringArray(String[] array) {
        mStrArray = array;
        mPage = 0;
        notifyDataSetChanged();
    }

    public void setSelection(int selection) {
        if (selection < 0 || selection >= mStrArray.length)
            return;
        if (mSelection != selection) {
            int unselect = mSelection;
            mSelection = selection;
            notifyItemChanged(unselect);
            notifyItemChanged(selection);
        }
    }

    public int getSelection() {
        return mSelection;
    }

    public void prePage() {
        if (mPage > 0) {
            mPage--;
            notifyDataSetChanged();
        }
    }

    public void nextPage() {
        int page = mStrArray.length/3;
        if (mStrArray.length % 3 != 0) {
            page++;
        }
        if (mPage < page-1) {
            mPage++;
            notifyDataSetChanged();
        }
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
        final int index = position + mPage*3;
        Log.d("", "position=" + mStrArray[position]);
        holder.tvName.setText(mStrArray[index]);
        if (index == mSelection) {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.txt_yellow));
        } else {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.txt_white));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (mListener != null) {
                    mListener.onItemClick(index);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mStrArray == null || mStrArray.length == 0) {
            Log.d("ItemSelectorAdapter", "array is 0");
            return 0;
        }
        int page = mStrArray.length/3;
        if (mStrArray.length % 3 != 0) {
            page++;
        }
        if (mPage < page-1) {
            return 3;
        } else {
            return (mStrArray.length - (page-1)*3);
        }
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView tvName;

        VH(View itemView) {
            super(itemView);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }
    }
}
