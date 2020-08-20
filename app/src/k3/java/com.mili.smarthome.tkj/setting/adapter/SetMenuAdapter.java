package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.setting.entities.SettingFunc;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.util.List;

public class SetMenuAdapter extends RecyclerView.Adapter<SetMenuAdapter.VH> {

    public interface OnFuncClickListener {
        void onFuncClick(SettingFunc func);
    }

    public static final int PAGE_SIZE = 5;

    private Context mContext;
    private List<SettingFunc> mList;
    private OnFuncClickListener mListener;
    private int mMaxPage;
    private int mPage;

    public SetMenuAdapter(Context context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnFuncClickListener listener) {
        mListener = listener;
    }

    public void setDataSet(List<SettingFunc> list) {
        mList = list;
        mPage = 0;
        if (mList == null)
            mMaxPage = 0;
        else
            mMaxPage = (mList.size() - 1) / PAGE_SIZE;
        notifyDataSetChanged();
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

    @NonNull
    @Override
    public SetMenuAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_menu, parent, false);
        return new SetMenuAdapter.VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SetMenuAdapter.VH holder, int position) {
        final int index = mPage * PAGE_SIZE + position;
        final SettingFunc func = mList.get(index);

        holder.tvName.setText(func.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFuncClick(func);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        int start = mPage * PAGE_SIZE;
        int count = mList.size() - start;
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
