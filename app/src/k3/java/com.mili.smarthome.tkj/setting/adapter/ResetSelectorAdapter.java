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

public class ResetSelectorAdapter extends RecyclerView.Adapter<ResetSelectorAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    protected Context mContext;
    private String[] mStrArray;
    private int mSelection = 0;
    private OnItemClickListener mListener;

    public ResetSelectorAdapter(Context context) {
        mContext = context;
    }

    public void setStringArray(String[] array) {
        mStrArray = array;
        notifyDataSetChanged();
    }

    public void setSelection(int selection) {
        if (selection < 0 || selection >= getItemCount())
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

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_reset, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        holder.tvName.setText(mStrArray[position]);
        if (position == mSelection) {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.txt_yellow));
        } else {
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.txt_white));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStrArray == null ? 0 : mStrArray.length;
    }

    class VH extends RecyclerView.ViewHolder {

        private TextView tvName;

        VH(View itemView) {
            super(itemView);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }
    }
}
