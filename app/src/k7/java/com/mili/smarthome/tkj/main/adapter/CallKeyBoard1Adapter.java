package com.mili.smarthome.tkj.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.entity.KeyBoardMoel;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CallKeyBoard1Adapter extends RecyclerView.Adapter<CallKeyBoard1Adapter.MyViewHolder> {


    private final Context mContext;
    private final List<KeyBoardMoel> list;
    private ItemClickListener itemClickListener;
    private View view;

    public CallKeyBoard1Adapter(Context context, List<KeyBoardMoel> list) {
        this.mContext = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.call_key_board_item1, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder,  int position) {
        holder.keyView.setImgBg(list.get(position).getDrawbleId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

  public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.key_view)
        KeyBoardItemView keyView;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface ItemClickListener {
        void setItemDownClick(View view, int position);
        void setItemUpClick(View view, int position);
    }

    public void setKeyBoardListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnItemClick() {
        KeyBoardItemView.setOnkeyClickListener(new KeyBoardItemView.IOnKeyClickListener() {
            @Override
            public void OnViewDownClick(int code, View view) {
                if (itemClickListener != null) {
                    int position = AppManage.getInstance().getPosition(code);
                    itemClickListener.setItemDownClick(view, position);
                    view.setTag(position);
                }
            }

            @Override
            public void OnViewUpClick(int code, View view) {
                if (itemClickListener != null) {
                    int position = AppManage.getInstance().getPosition(code);
                    itemClickListener.setItemUpClick(view, position);
                    view.setTag(position);
                }
            }
        });
    }
}
