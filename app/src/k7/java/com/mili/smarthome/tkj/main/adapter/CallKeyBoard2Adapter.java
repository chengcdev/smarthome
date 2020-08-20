package com.mili.smarthome.tkj.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mili.smarthome.tkj.R;
import com.mili.smarthome.tkj.main.entity.ResidentListEntity;
import com.mili.smarthome.tkj.main.widget.KeyBoardItemView;
import com.mili.smarthome.tkj.utils.AppManage;

import java.util.List;

public class CallKeyBoard2Adapter extends RecyclerView.Adapter<CallKeyBoard2Adapter.MyViewHolder> {


    private final Context mContext;
    private List<ResidentListEntity> list;
    private ItemClickListener itemClickListener;
    private boolean isEnable;
    private int index;
    private View mChildAt;
    private RecyclerView mRv;


    public CallKeyBoard2Adapter(Context context, List<ResidentListEntity> mResidentList) {
        this.mContext = context;
        this.list = mResidentList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.direct_press_call_item, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String roomName = list.get(position).getRoomName();
        if (roomName != null) {
            if (roomName.length() > 4) {
                holder.tvName.setTextSize(25);
            }else {
                holder.tvName.setTextSize(40);
            }
            holder.tvName.setText(roomName);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgItem;

        MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_title);
            imgItem = (ImageView) itemView.findViewById(R.id.img_item);
        }
    }

    public interface ItemClickListener {
        void setItemDownClick(View view, int position);

        void setItemUpClick(View view, int position);
    }

    public void setKeyBoardListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    public void setOnItemClick() {
        KeyBoardItemView.setOnkeyClickListener(new KeyBoardItemView.IOnKeyClickListener() {
            @Override
            public void OnViewDownClick(int code, View view) {
                if (itemClickListener != null) {
                    int position = AppManage.getInstance().getPosition(code);
                    view.setTag(position);
                    itemClickListener.setItemDownClick(view, position);
                }
            }

            @Override
            public void OnViewUpClick(int code, View view) {
                if (itemClickListener != null) {
                    int position = AppManage.getInstance().getPosition(code);
                    view.setTag(position);
                    itemClickListener.setItemUpClick(view, position);
                }
            }
        });
    }

    /**
     * 直按式图片按下背景
     */
    public void imgDown(RecyclerView rv, int position) {
        mRv = rv;
        mChildAt = rv.getChildAt(position);
        RelativeLayout relativeLayout = getItemView();
        ImageView mIma = (ImageView) relativeLayout.findViewById(R.id.img_item);
        mIma.setScaleType(ImageView.ScaleType.FIT_XY);
        mIma.setImageResource(R.drawable.list_btn_1);
        AppManage.getInstance().playKeySound(mIma);
    }

    /**
     * 直按式图片抬起背景
     */
    public void imgUp() {
        if (mRv != null && mChildAt != null) {
            RelativeLayout relativeLayout = getItemView();
            ImageView mIma = (ImageView) relativeLayout.findViewById(R.id.img_item);
            mIma.setImageResource(R.drawable.list_btn);
        }
    }

    /**
     * 编辑按下背景
     */
    public void ItemBgDown(RecyclerView rv, int position) {
        mRv = rv;
        mChildAt = rv.getChildAt(position);
        RelativeLayout mRlDirect = getItemView();
        mRlDirect.setBackgroundResource(R.drawable.list_back_1);
        AppManage.getInstance().playKeySound(mRlDirect);
    }

    /**
     * 编辑抬起背景
     */
    public void ItemBgUp() {
        if (mRv != null && mChildAt != null) {
            RelativeLayout mRlDirect = getItemView();
            mRlDirect.setBackgroundResource(R.drawable.list_back);
        }
    }

    private RelativeLayout getItemView() {
        MyViewHolder childViewHolder = (MyViewHolder) mRv.getChildViewHolder(mChildAt);
        return (RelativeLayout) childViewHolder.itemView.findViewById(R.id.rl_direct);
    }

}
