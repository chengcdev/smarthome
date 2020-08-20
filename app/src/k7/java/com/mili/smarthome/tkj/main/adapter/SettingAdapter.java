package com.mili.smarthome.tkj.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.entity.SettingModel;

import java.util.List;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.MyViewHoler>{


    private final Context context;
    private final List<SettingModel> list;
    private int count;
    private ISettingAdapterListener settingAdapterListener;

    public SettingAdapter(Context context, List<SettingModel> list) {
        this.context = context;
        this.list = list;
    }

    public SettingAdapter(Context context, List<SettingModel> list,ISettingAdapterListener settingAdapterListener) {
        this.context = context;
        this.list = list;
        this.settingAdapterListener = settingAdapterListener;
    }


    public SettingAdapter(Context context, List<SettingModel> list,int count) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.rv_setting_item, parent, false);
        return new MyViewHoler(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoler holder, final int position) {

        holder.mTv.setText(list.get(position).getName());

        if (count == position) {
            holder.mIma.setVisibility(View.VISIBLE);
        }else {
            holder.mIma.setVisibility(View.INVISIBLE);
        }

        if (count == 0 && position == list.size()-1) {
            if (settingAdapterListener != null) {
                settingAdapterListener.notifyAdapter();
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHoler extends RecyclerView.ViewHolder{

        private  TextView mTv;
        private  ImageView mIma;

        MyViewHoler(View itemView) {
            super(itemView);
            mTv = (TextView) itemView.findViewById(R.id.tv_item);
            mIma = (ImageView) itemView.findViewById(R.id.img_item);
        }
    }

    public void refreshList(int count){
        this.count = count;
    }

    public interface ISettingAdapterListener {
        void notifyAdapter();
    }

    public void setAdapterListener(ISettingAdapterListener settingAdapterListener) {
        this.settingAdapterListener = settingAdapterListener;
    }

}
