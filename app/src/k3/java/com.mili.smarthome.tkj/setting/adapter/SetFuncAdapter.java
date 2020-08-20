package com.mili.smarthome.tkj.setting.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.app.ContextProxy;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class SetFuncAdapter extends RecyclerView.Adapter<SetFuncAdapter.VH> {

    private Context mContext;
    private List<String> mFuncList;

    public SetFuncAdapter(Context context) {
        mContext = context;
    }

    public SetFuncAdapter(Context context, String funcCode) {
        this(context);
        setFuncList(funcCode);
    }

    public SetFuncAdapter(Context context, List<String> funcList) {
        this(context);
        setFuncList(funcList);
    }

    public void setFuncList(String funcCode) {
        int count = funcCode.length() / 2;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(funcCode.substring(0, (i + 1) * 2));
        }
        mFuncList = list;
    }

    public void setFuncList(List<String> funcList) {
        mFuncList = funcList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_setting_caption, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.ivArrow.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        String resName = "setting_" + mFuncList.get(position);
        int resId = ContextProxy.getStringId(resName);
        if (resId == 0) {
            LogUtils.d("缺少字符串资源：" + resName);
            holder.tvName.setText("");
        } else {
            holder.tvName.setText(resId);
        }
    }

    @Override
    public int getItemCount() {
        return mFuncList== null ? 0 : mFuncList.size();
    }

    class VH extends RecyclerView.ViewHolder {

        private ImageView ivArrow;
        private TextView tvName;

        private VH(View itemView) {
            super(itemView);
            ivArrow = ViewUtils.findView(itemView, R.id.iv_arrow);
            tvName = ViewUtils.findView(itemView, R.id.tv_name);
        }
    }

}
