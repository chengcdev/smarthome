package com.mili.smarthome.tkj.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.entity.ResidentListEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallKeyBoard3Adapter extends RecyclerView.Adapter<CallKeyBoard3Adapter.MyViewHolder> {


    private final Context mContext;
    private final List<ResidentListEntity> list;
    private ItemClickListener itemClickListener;
    private int count;


    public CallKeyBoard3Adapter(Context context, List<ResidentListEntity> list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.call_key_board_item3, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.tvName.setText(list.get(position).getRoomName());

        if (count == position) {
            holder.tvName.setTextColor(Color.GREEN);
        }else {
            holder.tvName.setTextColor(Color.WHITE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.setItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener {
        void setItemClick(View view, int position);
    }

    public void setKeyBoardListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void refreshList(int count){
        this.count = count;
    }

}
