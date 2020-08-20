package com.mili.smarthome.tkj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.entity.KeyBoardBean;

import java.util.List;

public class KeyBoardAdapter extends RecyclerView.Adapter<KeyBoardAdapter.MyViewHolder>{

    private Context mContext;
    private List<KeyBoardBean> mList;
    private IKeyBoardListener keyBoardListener;

    public KeyBoardAdapter(Context context, List<KeyBoardBean> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.key_board_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.mIma.setImageResource(mList.get(position).getResId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keyBoardListener != null) {
                    keyBoardListener.onKeyBoardListener(view,position,mList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private final ImageView mIma;

        public MyViewHolder(View itemView) {
            super(itemView);
            mIma = (ImageView) itemView.findViewById(R.id.img_num);
        }
    }

    public interface IKeyBoardListener {
        void onKeyBoardListener(View view, int potion, KeyBoardBean keyBoardBean);
    }

    public void setOnKeyBoardListener(IKeyBoardListener keyBoardListener) {
        this.keyBoardListener = keyBoardListener;
    }

}
